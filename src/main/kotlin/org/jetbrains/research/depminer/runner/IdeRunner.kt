import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import org.jetbrains.research.depminer.model.convertToJsonString
import org.jetbrains.research.depminer.runner.getProjectDependencies
import org.jetbrains.research.depminer.runner.projectSetup
import java.io.File
import kotlin.system.exitProcess

/**
 * Resulting data [testContent] will be output to this [testOutput] file upon termination
 */
const val testOutput = "test-output"
const val testContent = "test"


class IdeRunner : ApplicationStarter {

    private val projectPath: String = System.getProperty("user.dir")

    override fun getCommandName(): String = "mine-dependencies"

    override fun main(args: Array<out String>) {

        // Arguments number check isn't necessary and is handled in extract-dependencies.sh

        println("IDEA instance started. . . \n")
        val inputDir = File(projectPath).resolve(args[1])
        println(inputDir.absolutePath) //Debug

        val sourceRootDir = File(projectPath).resolve(args[2])
        println(sourceRootDir.absolutePath)

        val outputDir = File(projectPath).resolve(args[3])
        println(outputDir.absolutePath) //Debug

        val project = projectSetup(inputDir, sourceRootDir, outputDir)

        val dumbService= DumbService.getInstance(project)

        if (!dumbService.isDumb) {
            runWhenSmart(inputDir, outputDir, project)
        } else dumbService.runWhenSmart{
            runWhenSmart(inputDir, outputDir, project)
        }

        exitProcess(0)
    }

    private fun runWhenSmart(inputDir: File, outputDir: File, project: Project) {
        println("Indexing finished")
        val dependenciesMap = getProjectDependencies(inputDir.absolutePath, project, outputDir)
        println("writing to file: ${outputDir.resolve(testOutput)}")
        outputDir.resolve(testOutput).writeText(convertToJsonString(dependenciesMap))
    }
}