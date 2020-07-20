import com.intellij.openapi.application.ApplicationStarter
import java.io.File
import kotlin.system.exitProcess

const val testOutput = "test-output"
const val testContent = "test"

class IdeRunner : ApplicationStarter {
    override fun getCommandName(): String = "mine-dependencies"

    override fun main(args: Array<out String>) {
        println("Dependency miner started...")
        val inputDir = File(System.getProperty("user.dir")).resolve(args[1])
        println(inputDir.absolutePath)
        val outputDir = File(System.getProperty("user.dir")).resolve(args[2])
        
        inputDir.resolve(testOutput).writeText(testContent)
        
        exitProcess(0)
    }
}