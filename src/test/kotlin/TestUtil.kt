import org.jetbrains.research.depminer.model.CodeElement
import org.jetbrains.research.depminer.model.Dependency
import org.jetbrains.research.depminer.model.LocationInfo
import java.io.File

/**
 * Checks if locationInfo is in file [fileName]
 *
 * @param fileName:
 */
fun LocationInfo.isInFile(fileName: String): Boolean {
    return path.endsWith(fileName)
}

/**
 * Checks if the code element is in given file [fileName]
 */
fun CodeElement.isInFile(fileName: String): Boolean {
    return location.isInFile(fileName)
}

/**
 * Checks if the dependency is between two given files
 */
fun Dependency.betweenFiles(fromFileName: String, toFileName: String): Boolean {
    return (from.location.isInFile(fromFileName) && to.location.isInFile(toFileName)) || (from.location.isInFile(toFileName) && to.location.isInFile(fromFileName))
}

/**
 * Executes extract-dependencies shell script
 *
 * @param inputDir input directory for the inspection
 * @param outputDir directory where an output data should be placed
 *
 * @return launched process' return code
 */
fun runIde(inputDir: String, sourceDir: String, outputDir: String): Int {
    val command = "./extract-dependencies.sh $inputDir $sourceDir $outputDir"
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