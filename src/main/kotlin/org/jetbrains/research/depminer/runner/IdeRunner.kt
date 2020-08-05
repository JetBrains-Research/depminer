import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import org.jetbrains.research.depminer.actions.getProjectDependencies
import org.jetbrains.research.depminer.model.Dependency
import org.jetbrains.research.depminer.model.convertToJsonString
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


        DumbService.getInstance(project).runWhenSmart {
            println("Indexing finished")
            val dependenciesMap = getProjectDependencies(inputDir.absolutePath, project)
            outputDir.resolve(testOutput).writeText(convertToJsonString(dependenciesMap))
        }

        exitProcess(0)
    }
}