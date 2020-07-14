import org.jetbrains.research.depminer.actions.getProjectDependencies
import org.jetbrains.research.depminer.model.CodeElement
import org.jetbrains.research.depminer.model.LocationInfo
import org.junit.Assert
import org.junit.Test

fun LocationInfo.isInFile(fileName: String): Boolean {
    return path.endsWith(fileName)
}

fun CodeElement.isInFile(fileName: String): Boolean {
    return physicalLocation.isInFile(fileName)
}

class BasicTest {
    @Test
    fun kotlinIdeaProject() {
        val dependencies = getProjectDependencies("src/test/resources/testProjects/kotlinIdea")
        Assert.assertNotEquals("The dependencies list should not be empty", dependencies)
        Assert.assertTrue("Main.kt should depend on Util.kt",
            dependencies.any { it.from.isInFile("Main.kt") && it.to.isInFile("Util.kt") })
    }
}