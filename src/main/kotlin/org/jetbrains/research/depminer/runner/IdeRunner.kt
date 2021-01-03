import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import org.jetbrains.research.depminer.model.convertToJsonString
import org.jetbrains.research.depminer.runner.getProjectDependencies
import org.jetbrains.research.depminer.runner.projectSetup
import java.io.File
import kotlin.system.exitProcess

/**
 * Resulting data will be written to [testOutput] file upon termination
 */
const val testOutput = "depminer_output"



class IdeRunner : ApplicationStarter {
    private val projectPath: String = System.getProperty("user.dir")

    override fun getCommandName(): String = "mine-dependencies"

    override fun main(args: Array<out String>) {
        println("IDEA instance started. . . \n")
        val inputDir: File?
        val sourceRootDir: File?
        inputDir = File(projectPath).resolve(args[1])
        sourceRootDir = File(projectPath).resolve(args[2])
        val outputDir = File(projectPath).resolve(args[3])
        val project = projectSetup(inputDir, sourceRootDir, outputDir)
        /* Now wait for indexing to finish, so that references can be resolved */
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
        println("Writing to file: ${outputDir.resolve(testOutput)}")
        outputDir.resolve(testOutput).writeText(convertToJsonString(dependenciesMap))
    }
}