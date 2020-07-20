import java.io.File

fun runIde(): Int {
    val command = "./extract-dependencies.sh . ."
    
    val process = Runtime.getRuntime().exec(command)
//    val process = ProcessBuilder(command)
//        .directory(File("/Users/vladimir.kovalenko/work/dependencies-plugin/"))
//        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
//        .redirectError(ProcessBuilder.Redirect.INHERIT)
//        .start()
    
    return process.waitFor()
}

fun readTestFile(): String {
    return File(testOutput).readText()
}