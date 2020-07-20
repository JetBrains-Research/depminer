import com.intellij.openapi.application.ApplicationStarter
import java.io.File
import kotlin.system.exitProcess

const val testOutput = "test-output"
const val testContent = "test"

class IdeRunner : ApplicationStarter {
    override fun getCommandName(): String = "mine-dependencies"

    override fun main(args: Array<out String>) {
        println("Dependency miner started...")
        File(testOutput).writeText(testContent)
        exitProcess(0)
    }
}