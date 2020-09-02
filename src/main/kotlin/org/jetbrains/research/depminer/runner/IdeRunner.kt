import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import org.jetbrains.research.depminer.gitutil.cloneRemoteRepository
import org.jetbrains.research.depminer.model.convertToJsonString
import org.jetbrains.research.depminer.model.parseReviewHistory
import org.jetbrains.research.depminer.runner.getProjectDependencies
import org.jetbrains.research.depminer.runner.projectSetup
import java.io.File
import kotlin.system.exitProcess

/**
 * Resulting data will be written to [testOutput] file upon termination
 */
const val testOutput = "test-output"


class IdeRunner : ApplicationStarter {

    private val projectPath: String = System.getProperty("user.dir")

    override fun getCommandName(): String = "mine-dependencies"

    override fun main(args: Array<out String>) {
        println("IDEA instance started. . . \n")
        var inputDir: File? = null
        var sourceRootDir: File? = null
        if (args[1] == "review-mode") {
            val reviewHistoryPath = args[2]
            val reviewHistory = parseReviewHistory(File(projectPath).resolve(reviewHistoryPath).absolutePath)
            reviewHistory.reverse()
            val newReview = reviewHistory[reviewHistory.lastIndex]
            println("Review: $newReview is chosen as the new review for analysis")
            val git = cloneRemoteRepository(newReview)
            inputDir = git.repository.directory
            sourceRootDir = git.repository.directory
        } else {
            println("arg1 ${args[1]} does not apparently equal \"review-mode\"")
            inputDir = File(projectPath).resolve(args[1])
            println(inputDir.absolutePath) //Debug
            sourceRootDir = File(projectPath).resolve(args[2])
            println(sourceRootDir.absolutePath)
        }
        val outputDir = File(projectPath).resolve(args[3])
        println(outputDir.absolutePath) //Debug

        val project = projectSetup(inputDir!!, sourceRootDir!!, outputDir)

        val dumbService= DumbService.getInstance(project)

        if (!dumbService.isDumb) {
            runWhenSmart(inputDir, outputDir, project)
        } else dumbService.runWhenSmart{
            runWhenSmart(inputDir, outputDir, project)
        }

        exitProcess(0)
    }

    private fun runWhenSmart(inputDir: File, outputDir: File, project: Project) {
        println("Indexing finished")
        val dependenciesMap = getProjectDependencies(inputDir.absolutePath, project, outputDir)
        println("writing to file: ${outputDir.resolve(testOutput)}")
        outputDir.resolve(testOutput).writeText(convertToJsonString(dependenciesMap))
    }
}