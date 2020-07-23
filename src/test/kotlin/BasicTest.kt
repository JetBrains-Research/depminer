import org.jetbrains.research.depminer.actions.getProjectDependencies
import org.jetbrains.research.depminer.model.*
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals


class BasicTest {
    companion object {
        @AfterClass
        @JvmStatic
        fun cleanUp() {
            cleanUpTestFiles()
        }
    }
    
    @Test
    fun `Project Scope is Derived From Project Path Correctly`() {
        val projectPath = System.getProperty("user.dir")
        val projectScope = ProjectScope("$projectPath/src/test/resources/testProjects/kotlinIdea")
        val desiredProjectScope = listOf(
            LocationInfo("$projectPath/src/test/resources/testProjects/kotlinIdea/src/Main.kt", FileRange(null, null)),
            LocationInfo("$projectPath/src/test/resources/testProjects/kotlinIdea/src/Util.kt", FileRange(null, null)))
        Assert.assertTrue(
            "Two kotlin files found: Main.kt and Util.kt",
            projectScope.getLocations() == desiredProjectScope
        )
    }

//    @Test
//    fun `Runner Implementation Executes And Outputs to File`() {
//        val exitCode = runIde(".", ".")
//        assertEquals(0, exitCode, "The IDE should finish with exit code 0")
//        assertEquals(testContent, readTestFile(), "Content of the output should match the expected")
//    }

    @Test
    fun `Java Test Project Runs`() {
        val testInputPath = "src/test/resources/testProjects/javaTestProject"
        val exitCode = runIde(testInputPath, ".")
        assertEquals(0, exitCode, "The IDE should finish terminate with code 0")
    }

    @Test
    fun `Dependency Between Two Files Detected - Java Test Project` () {
        val testInputPath = "src/test/resources/testProjects/javaTestProject"
        runIde(testInputPath, ".")
        Assert.assertFalse("Dependencies list is not empty", readTestFile() == "[]")
        val dependencies = readFromJsonString(readTestFile())
        Assert.assertTrue("Main.java should depend on Util.java",
            dependencies.any { it.from.isInFile("Main.java") && it.to.isInFile("Util.java") })
    }

    @Test
    fun `Dependency Within One File Detected - Java Test Project` () {
        val testInputPath = "src/test/resources/testProjects/javaTestProject"
        runIde(testInputPath, ".")
        Assert.assertFalse("Dependencies list is not empty", readTestFile() == "[]")
        val dependencies = readFromJsonString(readTestFile())
        val innerDependencies = dependencies.filter { it.from.location.path == it.to.location.path }
        Assert.assertFalse("List has dependencies within one file", innerDependencies.isEmpty())
    }

    @Test
    fun `Dependency Between Two Files Detected - Kotlin Idea Project` () {
        val testInputPath = "src/test/resources/testProjects/kotlinIdea"
        val exitCode = runIde(testInputPath, ".")
        assertEquals(0, exitCode, "The IDE should finish terminate with code 0")
        Assert.assertFalse("Dependencies list is not empty", readTestFile() == "[]")
        val dependencies = readFromJsonString(readTestFile())
        Assert.assertTrue("Main.kt should depend on Util.kt",
                dependencies.any { it.from.isInFile("Main.kt") && it.to.isInFile("Util.kt") })
    }
}