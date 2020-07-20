import java.io.File

fun runIde(): Int {
    val command = "./extract-dependencies.sh . ."
    val process = Runtime.getRuntime().exec(command)
    return process.waitFor()
}

fun cleanUpTestFiles() {
    File(testOutput).delete()
}

fun readTestFile(): String {
    return File(testOutput).readText()
}