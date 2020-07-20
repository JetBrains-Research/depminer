import org.jetbrains.research.depminer.runner.testContent
import org.jetbrains.research.depminer.runner.testOutput
import java.io.File

fun runIde() {
    val command = "./gradlew extractDependencies"
    
    Runtime.getRuntime().exec(command)
}

fun readTestFile(): String {
    return File(testOutput).readText()
}