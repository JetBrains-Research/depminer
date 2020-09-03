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

const val currentGitRepoBase = "https://opendev.org/"
const val clonePath = "/clonedRepo/"


class IdeRunner : ApplicationStarter {

    private val projectPath: String = System.getProperty("user.dir")

    override fun getCommandName(): String = "mine-dependencies"

    override fun main(args: Array<out String>) {
        println("IDEA instance started. . . \n")
        var inputDir: File?
        var sourceRootDir: File?
        var mode: String
        var reviewHistoryPath: String? = null
        var pathToClonedRepo: String? = null
        if (args[1].endsWith("review-mode")) {
            mode = "review-mode"
            val pathToRepository = args[1].substringBefore("review-mode")
            reviewHistoryPath = args[2]
            println("Looking for review history file at:${File(projectPath).resolve(reviewHistoryPath).absolutePath}")
            val reviewHistory = parseReviewHistory(File(projectPath).resolve(reviewHistoryPath).absolutePath)
            reviewHistory.reverse()
            val newReview = reviewHistory[0]
            println("Review: $newReview is chosen as the new review for analysis")
            val git = cloneRemoteRepository(newReview, pathToRepository + clonePath)
            pathToClonedRepo = git.repository.directory.parent
            inputDir = File(git.repository.directory.parent)
            println(inputDir.absolutePath)
            sourceRootDir = File(git.repository.directory.parent)
            println(sourceRootDir.absolutePath)
        } else {
            mode = "normal"
            inputDir = File(projectPath).resolve(args[1])
            println(inputDir.absolutePath)
            sourceRootDir = File(projectPath).resolve(args[2])
            println(sourceRootDir.absolutePath)
        }
        val outputDir = File(projectPath).resolve(args[3])
        println(outputDir.absolutePath)
        val project = projectSetup(inputDir!!, sourceRootDir!!, outputDir)
        val dumbService= DumbService.getInstance(project)
        if (!dumbService.isDumb) {
            runWhenSmart(inputDir, outputDir, project, mode, reviewHistoryPath, pathToClonedRepo)
        } else dumbService.runWhenSmart{
            runWhenSmart(inputDir, outputDir, project, mode, reviewHistoryPath, pathToClonedRepo)
        }
        // TODO: Cleanup cloned repository
        exitProcess(0)
    }

    private fun runWhenSmart(inputDir: File, outputDir: File, project: Project, mode: String, pathToReviewHistory: String?, pathToClonedRepo: String?) {
        println("Indexing finished")
        val dependenciesMap = getProjectDependencies(inputDir.absolutePath, project, outputDir, mode, pathToReviewHistory, pathToClonedRepo)
        println("writing to file: ${outputDir.resolve(testOutput)}")
        outputDir.resolve(testOutput).writeText(convertToJsonString(dependenciesMap))
    }
}