import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import org.eclipse.jgit.lib.ObjectId
import org.jetbrains.research.depminer.gitutil.cloneRemoteRepository
import org.jetbrains.research.depminer.gitutil.openRepoAtPath
import org.jetbrains.research.depminer.model.*
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
        var reviewHistoryPath: String?
        var pathToClonedRepo: String? = null
        if (args[1].endsWith("review-mode")) {
            var accumulateAcc3RF = 0f
            var accumulateAcc3DM = 0f
            var accumulateAcc5RF = 0f
            var accumulateAcc5DM = 0f
            var accumulateAcc10RF = 0f
            var accumulateAcc10DM = 0f
            var isCorrectRF = mutableListOf(0f, 0f ,0f)
            var isCorrectDM = mutableListOf(0f, 0f, 0f)
            var MRRAccumRF = 0f
            var MRRAccumDM = 0f
            mode = "review-mode"
            val pathToRepository = args[1].substringBefore("review-mode")
            reviewHistoryPath = args[2]
            val reviewHistory = parseReviewHistory(File(projectPath).resolve(reviewHistoryPath).absolutePath)
            reviewHistory.reverse()
            val git = cloneRemoteRepository(reviewHistory[0], pathToRepository + clonePath)
//            val git = openRepoAtPath("/Users/Vlad.Repinskiy/IdeaProjects/dependencies-plugin-idea/clonedRepo")
            pathToClonedRepo = git.repository.directory.parent
            inputDir = File(git.repository.directory.parent)
            println(inputDir.absolutePath)
            sourceRootDir = File(git.repository.directory.parent)
            println(sourceRootDir.absolutePath)
            val outputDir = File(projectPath).resolve(args[3])
            println(outputDir.absolutePath)
            val startIndex = (reviewHistory.size*(0.9)).toInt() - 1
            for (reviewIndex in startIndex until reviewHistory.size) {
                val newReview = reviewHistory[reviewIndex]
                println("Inspecting review #${reviewIndex}")
                val currentReviewHistory = reviewHistory.slice(0 until reviewIndex)
                val revFinderReviewers = getReviewers(currentReviewHistory, newReview)
                val commitID = ObjectId.fromString(newReview.commitInfo.commitId)
                val commit = git.repository.parseCommit(commitID)
                git.checkout().setName(newReview.commitInfo.commitId).call()
                val project = projectSetup(inputDir!!, sourceRootDir!!, outputDir)
                val dumbService= DumbService.getInstance(project)
                var dependenciesMap = listOf<Dependency>()
                if (!dumbService.isDumb) {
                    dependenciesMap = runWhenSmart(inputDir!!, outputDir, project, mode, newReview, pathToClonedRepo).toList()
                } else dumbService.runWhenSmart{
                    dependenciesMap = runWhenSmart(inputDir!!, outputDir, project, mode, newReview, pathToClonedRepo).toList()
                }
                println("Dependency analysis done, collected ${dependenciesMap.size} dependency records. \n")
                val depminerReviewers = getDepminerReviewers(dependenciesMap, git, commitID)
                val augmentedReviewers = getAugmentedReviewers(currentReviewHistory, newReview, depminerReviewers)

                /* Accuracy and MRR calculations */
                isCorrectRF[0] += isCorrect(newReview, revFinderReviewers, 10).toFloat()
                isCorrectRF[1] += isCorrect(newReview, revFinderReviewers, 5).toFloat()
                isCorrectRF[2] += isCorrect(newReview, revFinderReviewers, 3).toFloat()
                isCorrectDM[0] += isCorrect(newReview, augmentedReviewers, 10).toFloat()
                isCorrectDM[1] += isCorrect(newReview, augmentedReviewers, 5).toFloat()
                isCorrectDM[2] += isCorrect(newReview, augmentedReviewers, 3).toFloat()
                println(isCorrectRF)
                println(isCorrectDM)
                MRRAccumRF += oneOverRank(newReview, revFinderReviewers)
                MRRAccumDM += oneOverRank(newReview, augmentedReviewers)
                val accuracy10 = getAccuracy(newReview, revFinderReviewers, augmentedReviewers, 10)
                val accuracy5 = getAccuracy(newReview, revFinderReviewers, augmentedReviewers, 5)
                val accuracy3 = getAccuracy(newReview, revFinderReviewers, augmentedReviewers, 3)
                accumulateAcc10RF += accuracy10.first
                accumulateAcc10DM += accuracy10.second
                accumulateAcc5RF += accuracy5.first
                accumulateAcc5DM += accuracy5.second
                accumulateAcc3RF += accuracy3.first
                accumulateAcc3DM += accuracy3.second

                /* Write current review data to file */
                outputDir.resolve(testOutput).appendText("Review #$reviewIndex: \n \n")
                outputDir.resolve(testOutput).appendText(revFinderReviewers.slice(0..15).toString() + "\n \n")
                outputDir.resolve(testOutput).appendText(augmentedReviewers.slice(0..15).toString() + "\n \n")
                outputDir.resolve(testOutput).appendText("Top 10 guess accuracy: ${accuracy10.first}, ${accuracy10.second} \n \n")
                outputDir.resolve(testOutput).appendText("Top 5 guess accuracy: ${accuracy5.first}, ${accuracy5.second} \n \n")
                outputDir.resolve(testOutput).appendText("Top 3 guess accuracy: ${accuracy3.first}, ${accuracy3.second} \n \n")
                outputDir.resolve(testOutput).appendText("\n =============================================================== \n \n \n")

                /* Write final results at the end of a file */
                if (reviewIndex == reviewHistory.size - 1) {
                    val totalIterations = reviewIndex - startIndex
                    outputDir.resolve(testOutput).appendText("Total iteratons: $totalIterations\n")
                    outputDir.resolve(testOutput).appendText("Average top 10 guess accuracies: " +
                            "${accumulateAcc10RF/totalIterations}, ${accumulateAcc10DM/totalIterations} \n")
                    outputDir.resolve(testOutput).appendText("Average top 5 guess accuracies: " +
                            "${accumulateAcc5RF/totalIterations}, ${accumulateAcc5DM/totalIterations} \n")
                    outputDir.resolve(testOutput).appendText("Average top 3 guess accuracies: " +
                            "${accumulateAcc3RF/totalIterations}, ${accumulateAcc3DM/totalIterations} \n")
                    outputDir.resolve(testOutput).appendText("Average top-10, 5 and 3 accuracies for RevFinder, respectively: " +
                            "${isCorrectRF[0]/totalIterations}, ${isCorrectRF[1]/totalIterations}, ${isCorrectRF[2]/totalIterations} \n")
                    outputDir.resolve(testOutput).appendText("Average top-10, 5 and 3 accuracies for augmented method, respectively: " +
                            "${isCorrectDM[0]/totalIterations}, ${isCorrectDM[1]/totalIterations}, ${isCorrectDM[2]/totalIterations} \n")
                    outputDir.resolve(testOutput).appendText("MRR for RevFinder and augmented methods, respectively: " +
                            "${MRRAccumRF/totalIterations}, ${MRRAccumDM/totalIterations} \n")
                }
            }
            // TODO: Cleanup cloned repository
        } else {
            mode = "normal"
            inputDir = File(projectPath).resolve(args[1])
            println(inputDir.absolutePath)
            sourceRootDir = File(projectPath).resolve(args[2])
            println(sourceRootDir.absolutePath)
            val outputDir = File(projectPath).resolve(args[3])
            println(outputDir.absolutePath)
            val project = projectSetup(inputDir!!, sourceRootDir!!, outputDir)
            val dumbService= DumbService.getInstance(project)
            if (!dumbService.isDumb) {
                runWhenSmart(inputDir, outputDir, project, mode, null, pathToClonedRepo)
            } else dumbService.runWhenSmart{
                runWhenSmart(inputDir, outputDir, project, mode, null, pathToClonedRepo)
            }

        }
        println("Terminate normally")
        exitProcess(0)
    }

    private fun runWhenSmart(inputDir: File, outputDir: File, project: Project, mode: String, newReview: Review?, pathToClonedRepo: String?): Collection<Dependency> {
        println("Indexing finished")
        val dependenciesMap = getProjectDependencies(inputDir.absolutePath, project, outputDir, mode, newReview, pathToClonedRepo)
        if (mode != "review-mode") {
            outputDir.resolve(testOutput).writeText(convertToJsonString(dependenciesMap))
        }
        return dependenciesMap
    }
}