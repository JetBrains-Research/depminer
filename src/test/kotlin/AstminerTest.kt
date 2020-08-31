import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import util.*
import kotlin.test.assertTrue

class AstminerTest {
    companion object {
        @AfterClass
        @JvmStatic
        fun cleanUp() {
            cleanUpTestFiles()
        }
    }

    @Test
    fun `Astminer-master-dev Granularity_kt Dependencies Mining Test`() {
        val testInputPath = "testData/testProjects/astminer-master-dev/"
        val testSrcPath = "testData/testProjects/astminer-master-dev/src/"

        println("Run IDE:")
        val exitCode = runIde(testInputPath, testSrcPath, ".")
        Assert.assertEquals("The IDE should finish terminate with code 0",0, exitCode)

        val consideredFile = "Granularity.kt"
        val targetFiles = listOf("FilterPredicates.kt", "TreeUtil.kt", "ParsingModel.kt", "TreeSplittingModel.kt", "SimpleNode.kt",
                "JavaMethodSplitter.kt", "PythonMethodSplitter.kt", "FuzzyMethodSplitter.kt", "FuzzyNode.kt", "GumTreeJavaNode.kt", "GumTreeMethodSplitter.kt")

        val dependencies = readDependenciesDataFromJsonString(readTestFile())
        val dependenciesGranularityKt = dependencies.filter {it.from.location.isInFile(consideredFile)}
        println(dependenciesGranularityKt)
        for (dependency in dependenciesGranularityKt) {
            assertTrue {/* dependency.from.location.path.substringAfterLast('/') in targetFiles || */dependency.to.location.path.substringAfterLast('/') in targetFiles || dependency.to.location.path.substringAfterLast('/') == consideredFile }
        }
    }

    @Test
    fun `Astminer-master-dev ParsingModel_kt Dependencies Mining Test`() {
        val testInputPath = "testData/testProjects/astminer-master-dev/"
        val testSrcPath = "testData/testProjects/astminer-master-dev/src/"

        println("Run IDE:")
        val exitCode = runIde(testInputPath, testSrcPath, ".")
        Assert.assertEquals("The IDE should finish terminate with code 0",0, exitCode)

        val consideredFile = "ParsingModel.kt"
        val targetFiles = listOf("kotlin.kotlin_builtins", "StandardKt.class", "CollectionsKt.class", "collections.kotlin_builtins", "ConsoleKt.class", "FilesKt.class", "SequencesKt.class", "StringsKt.class")

        val dependencies = readDependenciesDataFromJsonString(readTestFile())
        val dependenciesGranularityKt = dependencies.filter {it.from.location.isInFile(consideredFile)}
        println(dependenciesGranularityKt)
        for (dependency in dependenciesGranularityKt) {
            assertTrue {/* dependency.from.location.path.substringAfterLast('/') in targetFiles || */dependency.to.location.path.substringAfterLast('/') in targetFiles || dependency.to.location.path.substringAfterLast('/') == consideredFile }
        }
    }
}