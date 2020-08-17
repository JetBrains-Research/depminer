import org.jetbrains.research.depminer.model.*
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals

class KotlinProjectTest {
    companion object {
        @AfterClass
        @JvmStatic
        fun cleanUp() {
            cleanUpTestFiles()
        }
    }

    @Test
    fun `Dependency Between Two Files Detected - Kotlin Test Project` () {
        val testInputPath = "/testData/testProjects/kotlinTestProject"
        val testSrcPath = "/testData/testProjects/kotlinTestProject/src/"
        val exitCode = runIde(testInputPath, testSrcPath, ".")
        assertEquals(0, exitCode, "The IDE should finish terminate with code 0")
        Assert.assertFalse("Dependencies list is not empty", readTestFile() == "[]")
        println(readTestFile())
        val dependencies = readFromJsonString(readTestFile())
        Assert.assertTrue("Main.kt should depend on Utility.kt",
            dependencies.any { it.betweenFiles("Main.kt", "Utility.kt") })
    }

    @Test
    fun `Dependency Within One File Detected - Kotlin Test Project` () {
        val testInputPath = "/testData/testProjects/kotlinTestProject"
        val testSrcPath = "/testData/testProjects/kotlinTestProject/src/"
        val exitCode = runIde(testInputPath, testSrcPath, ".")
        assertEquals(0, exitCode, "The IDE should finish terminate with code 0")
        Assert.assertFalse("Dependencies list is not empty", readTestFile() == "[]")
        val dependencies = readFromJsonString(readTestFile())
        val innerDependencies = dependencies.filter { it.from.location.path == it.to.location.path }
        Assert.assertFalse("List has dependencies within one file", innerDependencies.isEmpty())
    }
}