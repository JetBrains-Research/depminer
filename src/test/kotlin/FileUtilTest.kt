import org.jetbrains.research.depminer.util.countLines
import org.junit.Assert
import org.junit.Test

class FileUtilTest {
    @Test
    fun `Lines in Files Computed Correctly`() {
        val projectPath = System.getProperty("user.dir")
        val fileAPath = "$projectPath/testData/testProjects/javaTestProject/src/Util.java"
        val fileBPath = "$projectPath/testData/testProjects/astminer-master-dev/src/main/kotlin/astminer/Main.kt"
        Assert.assertEquals("Lines in Util.java: 6", 6, countLines(fileAPath))
        Assert.assertEquals("Lines in Main.kt: 20", 20, countLines(fileBPath))
    }
}