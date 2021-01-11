import org.jetbrains.research.depminer.model.*
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import util.*
import kotlin.test.assertEquals

class JavaProjectTests {
    companion object {
        @AfterClass
        @JvmStatic
        fun cleanUp() {
            cleanUpTestFiles()
        }
    }

    @Test
    fun `Java Test Project Runs`() {
        val testInputPath = "/testData/testProjects/javaTestProject"
        val testSrcPath = "/testData/testProjects/javaTestProject/src/"
        val exitCode = runIde(testInputPath, testSrcPath, ".")
        assertEquals(0, exitCode, "The IDE should finish terminate with code 0")
    }

    @Test
    fun `Dependency Between Two Files Detected - Java Test Project` () {
        val testInputPath = "/testData/testProjects/javaTestProject"
        val testSrcPath = "/testData/testProjects/javaTestProject/src/"
        runIde(testInputPath, testSrcPath, ".")
        Assert.assertFalse("Dependencies list is not empty", readTestFile() == "[]")
        val dependencies = readDependenciesDataFromJsonString(readTestFile())
        Assert.assertTrue("Main.java should depend on Util.java",
            dependencies.any { it.betweenFiles("Main.java", "Util.java") })
    }

    @Test
    fun `Dependency Within One File Detected - Java Test Project` () {
        val testInputPath = "/testData/testProjects/javaTestProject"
        val testSrcPath = "/testData/testProjects/javaTestProject/src/"
        runIde(testInputPath, testSrcPath, ".")
        Assert.assertFalse("Dependencies list is not empty", readTestFile() == "[]")
        val dependencies = readDependenciesDataFromJsonString(readTestFile())
        val innerDependencies = dependencies.filter { it.from.location.path == it.to.location.path }
        Assert.assertFalse("List has dependencies within one file", innerDependencies.isEmpty())
    }
}