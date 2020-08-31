import org.jetbrains.research.depminer.model.*
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import util.cleanUpTestFiles
import util.runIde


class BasicTest {
    companion object {
        @AfterClass
        @JvmStatic
        fun cleanUp() {
            cleanUpTestFiles()
        }
    }

    @Test
    fun `Null Safety In Project Setup`() {
        val testInputPath = "/testData/testProjects/NoSuchProject"
        val testSrcPath = "/testData/testProjects/NoSuchProject/src/"
        val exitCode = runIde(testInputPath, testSrcPath, ".")
        Assert.assertNotEquals("IDE finished with non zero code", 0, exitCode)
    }
    
    @Test
    fun `Project Scope is Derived From Project Path Correctly`() {
        val projectPath = System.getProperty("user.dir")
        val projectScope = ProjectScope("$projectPath/testData/testProjects/kotlinTestProject")
        val desiredProjectScope = listOf(
            LocationInfo("$projectPath/testData/testProjects/kotlinTestProject/src/Main.kt", FileRange(null, null)),
            LocationInfo("$projectPath/testData/testProjects/kotlinTestProject/src/Utility.kt", FileRange(null, null)))
        Assert.assertTrue(
            "Two kotlin files found: Main.kt and Utility.kt",
            projectScope.getLocations() == desiredProjectScope
        )
    }
}