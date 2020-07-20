import java.io.File

/**
 * Executes extract-dependencies shell script
 *
 * @param inputDir input directory for the inspection
 * @param outputDir directory where an output data should be placed
 *
 * @return launched process' return code
 */
fun runIde(inputDir: String, outputDir: String): Int {
    val command = "./extract-dependencies.sh $inputDir $outputDir"
    val process = Runtime.getRuntime().exec(command)
    return process.waitFor()
}

/**
 * Utility - delete the testOutput file - called before reexecuting the runner test
 */
fun cleanUpTestFiles() {
    File(testOutput).delete()
}

/**
 * Utility - read the content of the testOutput file
 *
 * @return testOutput file contents
 */
fun readTestFile(): String {
    return File(testOutput).readText()
}