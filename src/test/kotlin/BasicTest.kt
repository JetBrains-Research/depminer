import org.jetbrains.research.depminer.actions.getProjectDependencies
import org.jetbrains.research.depminer.model.CodeElement
import org.jetbrains.research.depminer.model.FileRange
import org.jetbrains.research.depminer.model.LocationInfo
import org.jetbrains.research.depminer.model.ProjectScope
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals

fun LocationInfo.isInFile(fileName: String): Boolean {
    return path.endsWith(fileName)
}

fun CodeElement.isInFile(fileName: String): Boolean {
    return physicalLocation.isInFile(fileName)
}

class BasicTest {
    @Test
    fun projectScopeDefinedCorrectly() {
        val projectPath = System.getProperty("user.dir")
        val projectScope = ProjectScope("$projectPath/src/test/resources/testProjects/kotlinIdea")
        val desiredProjectScope = listOf(
            LocationInfo("$projectPath/src/test/resources/testProjects/kotlinIdea/src/Main.kt", FileRange(null, null)),
            LocationInfo("$projectPath/src/test/resources/testProjects/kotlinIdea/src/Util.kt", FileRange(null, null))
        )
        Assert.assertTrue(
            "Two kotlin files found: Main.kt and Util.kt",
            projectScope.getLocations() == desiredProjectScope
        )
    }

    @Test
    fun kotlinIdeaProject() {
        val projectPath = System.getProperty("user.dir")
        val dependencies = getProjectDependencies("$projectPath/src/test/resources/testProjects/kotlinIdea")
        //Assert.assertNotEquals("The dependencies list should not be empty", dependencies)
        Assert.assertTrue("Main.kt should depend on Util.kt",
            dependencies.any { it.from.isInFile("Main.kt") && it.to.isInFile("Util.kt") })
    }

    @Test
    fun runner() {
        val exitCode = runIde()
        
        assertEquals(0, exitCode, "The IDE should finish with exit code 0")

        assertEquals(testContent, readTestFile(), "Content of the output should match the expected")
    }
}