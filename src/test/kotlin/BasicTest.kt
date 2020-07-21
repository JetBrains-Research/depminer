import org.jetbrains.research.depminer.actions.getProjectDependencies
import org.jetbrains.research.depminer.model.CodeElement
import org.jetbrains.research.depminer.model.FileRange
import org.jetbrains.research.depminer.model.LocationInfo
import org.jetbrains.research.depminer.model.ProjectScope
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals

fun LocationInfo.isInFile(fileName: String): Boolean {
    return path.endsWith(fileName)
}

fun CodeElement.isInFile(fileName: String): Boolean {
    return location.isInFile(fileName)
}

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

    @Test
    fun `Runner Implementation Executes And Outputs to File`() {
        val exitCode = runIde(".", ".")
        assertEquals(0, exitCode, "The IDE should finish with exit code 0")
        assertEquals(testContent, readTestFile(), "Content of the output should match the expected")
    }

    @Test
    fun `Dependency Between Two Files Detected - Kotlin Idea Project`() {
        val testInputPath = "src/test/resources/testProjects/kotlinIdea"
        val exitCode = runIde(testInputPath, ".")
        assertEquals(0, exitCode, "The IDE should finish terminate with code 0")
//        Assert.assertFalse("Dependencies list should not be empty",dependencies.isEmpty())
//        Assert.assertTrue("Main.kt should depend on Util.kt",
//            dependencies.any { it.from.isInFile("Main.kt") && it.to.isInFile("Util.kt") })
    }

    @Test
    fun `Java Test Project Runs`() {
        val testInputPath = "src/test/resources/testProjects/javaTestProject"
        val exitCode = runIde(testInputPath, ".")
        assertEquals(0, exitCode, "The IDE should finish terminate with code 0")
    }
}