package util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.research.depminer.model.CodeElement
import org.jetbrains.research.depminer.model.Dependency
import org.jetbrains.research.depminer.model.LocationInfo
import testOutput
import java.io.File
import java.util.concurrent.TimeUnit

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
 * Runs external command handling input and output streams
 * https://gist.github.com/seanf/58b76e278f4b7ec0a2920d8e5870eed6
 *
 * @param workingDir current working directory
 */
fun String.runCommand(workingDir: File? = null): Int {
    val process = ProcessBuilder(*split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
    if (!process.waitFor(120, TimeUnit.SECONDS)) {
        process.destroy()
        throw RuntimeException("execution timed out: $this")
    }
    if (process.exitValue() != 0) {
        println("execution failed with code ${process.exitValue()}: $this")
    }
    return process.exitValue()
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
    return command.runCommand(File(System.getProperty("user.dir")))
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

fun readDependenciesDataFromJsonString(jsonString: String): Collection<Dependency> {
    val gson = Gson()
    val collectionDependeciesType = object : TypeToken<Collection<Dependency>>() {}.type
    val dependenciesMap: Collection<Dependency> = gson.fromJson(jsonString, collectionDependeciesType)
    return dependenciesMap
}