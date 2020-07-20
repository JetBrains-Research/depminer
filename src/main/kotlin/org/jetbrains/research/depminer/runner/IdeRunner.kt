import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.application.ApplicationStarter
import org.jetbrains.research.depminer.model.Dependency
import java.io.File
import kotlin.system.exitProcess

/**
 * Resulting data [testContent] will be output to this [testOutput] file upon termination
 */
const val testOutput = "test-output"
const val testContent = "test"


class IdeRunner : ApplicationStarter {

    override fun getCommandName(): String = "mine-dependencies"

    override fun main(args: Array<out String>) {

        val dependencies = mutableListOf<Dependency>()

        if (args.size != 3) {
            println("Incorrect number of arguments: " + args.size + " found, 3 expected")
            exitProcess(0)
        }

        println("IDEA instance started")
        val inputDir = File(System.getProperty("user.dir")).resolve(args[1])
        println(inputDir.absolutePath)
        val outputDir = File(System.getProperty("user.dir")).resolve(args[2])
        println(outputDir.absolutePath)
        val project = ProjectUtil.openOrImport(inputDir.absolutePath, null, true)

        if (project == null) {
            println("Could not load project from $inputDir")
            exitProcess(0)
        }

        println("Successfully opened project at inputDir: $project")

        outputDir.resolve(testOutput).writeText(testContent)
        exitProcess(0)
    }
}