package org.jetbrains.research.depminer.model

import me.tongfei.progressbar.ProgressBar
import org.eclipse.jgit.api.BlameCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.lib.ObjectId


fun getReviewers(reviewHistory: List<Review>, newReview: Review): List<Pair<UserInfo, Int>> {
    val reviewersBorda = mutableMapOf<UserInfo, Int>()
    val reviewerScores = listOf(
        getReviewersWith(reviewHistory, newReview, "LCSuffix"),
        getReviewersWith(reviewHistory, newReview, "LCPostfix"),
        getReviewersWith(reviewHistory, newReview, "LCSubstring"),
        getReviewersWith(reviewHistory, newReview, "LCSubsequence"))
    //println(reviewerScores)
    for (list in reviewerScores) {
        var score = list.size
        for (reviewer in list) {
            reviewersBorda[reviewer.first] = reviewersBorda.getOrDefault(reviewer.first, 0) + score
            //println("Assigned reviewer: ${reviewer.first.displayName} score: $score")
            score -= 1
        }
    }
    return reviewersBorda.toList().sortedByDescending { it.second }
}

fun getReviewersWith(reviewHistory: List<Review>, newReview: Review, metricType: String): List<Pair<UserInfo, Float>> {
    val reviewers = mutableMapOf<UserInfo, Float>()
    for (pastReview in reviewHistory) {
        val pastReviewFiles = pastReview.files
        val newReviewFiles = newReview.files
        var pastReviewScore = 0f
        for (newReviewFile in newReviewFiles) {
            for (pastReviewFile in pastReviewFiles) {
                when (metricType) {
                    "LCSuffix" -> pastReviewScore += computeLCSuffixScore(newReviewFile, pastReviewFile)
                    "LCPostfix" -> pastReviewScore += computeLCPostfixScore(newReviewFile, pastReviewFile)
                    "LCSubstring" -> pastReviewScore += computeLCSubstringScore(newReviewFile, pastReviewFile)
                    "LCSubsequence" -> pastReviewScore += computeLCSubsequenceScore(newReviewFile, pastReviewFile)
                }
            }
        }
        pastReviewScore /= (pastReviewFiles.size * newReviewFiles.size)
        //println("Past review score: $pastReviewScore")
        for (pastReviewer in pastReview.reviewers) {
            //println("Giving ${pastReviewer.displayName} score: $pastReviewScore")
            reviewers[pastReviewer] = reviewers.getOrDefault(pastReviewer, 0f) + pastReviewScore
        }
    }
    return reviewers.toList().sortedByDescending { it.second }
}

fun String.tokenizePath(): List<String> {
    return this.split('/')
}



fun computeLCPostfixScore(newReviewFile: String, oldReviewFile: String): Float {
    return (computeLCPostfixLength(newReviewFile, oldReviewFile).toFloat()/ Integer.max(
        newReviewFile.length,
        oldReviewFile.length
    ))
}

fun computeLCPostfixLength(newReviewFile: String, oldReviewFile: String): Int {
    val newRevTokens = newReviewFile.tokenizePath()
    val oldRevTokens = oldReviewFile.tokenizePath()
    var commonPathLength = 0
    val minLength = Integer.min(newRevTokens.size, oldRevTokens.size)
    for (i in 0 until minLength) {
        if (newRevTokens[i] == oldRevTokens[i]) {
            commonPathLength += 1
        } else {
            break
        }
    }
    return commonPathLength
}

fun computeLCSuffixScore(newReviewFile: String, oldReviewFile: String): Float {
    return (computeLCSuffixLength(newReviewFile, oldReviewFile).toFloat()/ Integer.max(
        newReviewFile.length,
        oldReviewFile.length
    ))
}

fun computeLCSuffixLength(newReviewFile: String, oldReviewFile: String): Int {
    val newRevTokens = newReviewFile.tokenizePath()
    val oldRevTokens = oldReviewFile.tokenizePath()
    var commonPathLength = 0
    val minLength = Integer.min(newRevTokens.size, oldRevTokens.size)
    for (i in (minLength - 1) downTo 0) {
        if (newRevTokens[i] == oldRevTokens[i]) {
            commonPathLength += 1
        } else {
            break
        }
    }
    return commonPathLength
}

fun computeLCSubstringScore(newReviewFile: String, oldReviewFile: String): Float {
    return (computeLCSubstringLength(newReviewFile, oldReviewFile).toFloat()/ Integer.max(
        newReviewFile.length,
        oldReviewFile.length
    ))
}

fun computeLCSubstringLength(newReviewFile: String, oldReviewFile: String): Int {
    val newRevTokens = newReviewFile.tokenizePath()
    val oldRevTokens = oldReviewFile.tokenizePath()
    var commonPathLength = 0
    if ((newRevTokens.toSet() intersect oldRevTokens.toSet()).isNotEmpty()) {
        val matrix = Array(oldRevTokens.size) {Array(newRevTokens.size) {0}}
        for (i in oldRevTokens.indices) {
            for (j in newRevTokens.indices) {
                if (i == 0 || j == 0) {
                    matrix[i][j] = 0
                } else if (oldRevTokens[i-1] == newRevTokens[j-1]) {
                    matrix[i][j] = matrix[i-1][j-1] + 1
                    commonPathLength = kotlin.math.max(commonPathLength, matrix[i][j])

                } else {
                    matrix[i][j] = 0
                }
            }
        }
    }
    return commonPathLength
}

fun computeLCSubsequenceScore(newReviewFile: String, oldReviewFile: String): Float {
    return (computeLCSubsequenceLength(newReviewFile, oldReviewFile).toFloat()/ Integer.max(
        newReviewFile.length,
        oldReviewFile.length
    ))
}

fun computeLCSubsequenceLength(newReviewFile: String, oldReviewFile: String): Int {
    val newRevTokens = newReviewFile.tokenizePath()
    val oldRevTokens = oldReviewFile.tokenizePath()
    var commonPathLength = 0
    if ((newRevTokens.toSet() intersect oldRevTokens.toSet()).isNotEmpty()) {
        val matrix = Array(oldRevTokens.size) {Array(newRevTokens.size) {0}}
        for (i in oldRevTokens.indices) {
            for (j in newRevTokens.indices) {
                if (i == 0 || j == 0) {
                    matrix[i][j] = 0
                }
                else if (oldRevTokens[i-1] == newRevTokens[j-1]) {
                    matrix[i][j] = matrix[i-1][j-1] + 1
                } else {
                    matrix[i][j] = kotlin.math.max(matrix[i - 1][j], matrix[i][j - 1])
                }
            }
        }
        commonPathLength = matrix[oldRevTokens.size - 1][newRevTokens.size - 1]
    }
    return  commonPathLength
}

fun getDepminerReviewers(dependenciesMap: Collection<Dependency>, git: Git, commitID: ObjectId): List<Pair<String, Int>> {
    val reviewers = mutableMapOf<String, Int>()

    for (dependency in dependenciesMap) {
        print("Inspecting dependency record #${dependenciesMap.indexOf(dependency)} \r")
        val contributors = getContributors(dependency.to.location, git, commitID)
        for (contributor in contributors) {
            reviewers[contributor] = reviewers.getOrDefault(contributor, 0) + 1
        }
    }
    //pb.stepTo(100)
    return reviewers.toList().sortedByDescending { it.second }
}

fun getContributors(location: LocationInfo, git: Git, commitID: ObjectId): List<String> {
    val contributors = mutableListOf<String>()
    val headCommit = git.repository.resolve("HEAD")
    val blamer = BlameCommand(git.repository)
    blamer.setFilePath(location.path.substringAfter(git.repository.directory.parent + "/"))
    blamer.setStartCommit(headCommit)
    blamer.setFollowFileRenames(true)
    blamer.setTextComparator(RawTextComparator.DEFAULT)
    val blameResult = blamer.call()
    //println("Blaming: ${location.path.substringAfter(git.repository.directory.parent + "/")}")
    for (line in location.range.start until location.range.end - 1) {
        if (blameResult != null) {
            if (contributors.contains(blameResult.getSourceCommitter(line).emailAddress)) {
                continue
            } else {
                //println("Adding contributor ${blameResult.getSourceCommitter(line).emailAddress}")
                contributors.add(blameResult.getSourceCommitter(line).emailAddress)
            }
        } else {
            continue
        }
    }
    try {
        contributors.add(blameResult.getSourceCommitter(location.range.end).emailAddress)
    } catch (e: Exception) {
     //   println("tried to check the last line")
    }
    return contributors
}

fun getAugmentedReviewers(reviewHistory: List<Review>, newReview: Review, dpReviewers: List<Pair<String, Int>>): List<Pair<UserInfo, Int>> {
    val augmentedReviewers = mutableMapOf<UserInfo, Int>()
    val dpReviewersWithUserInfo = mutableMapOf<UserInfo, Float>()
    for (dpReviewer in dpReviewers) {
        for (review in reviewHistory) {
            for (reviewer in review.reviewers) {
                if (reviewer.email == dpReviewer.first) {
                    dpReviewersWithUserInfo.putIfAbsent(reviewer, dpReviewer.second.toFloat())
                }
            }
        }
    }
    val dpReviewersList = dpReviewersWithUserInfo.toList().sortedByDescending { it.second }
    val reviewerScores = listOf(
        getReviewersWith(reviewHistory, newReview, "LCSuffix"),
        getReviewersWith(reviewHistory, newReview, "LCPostfix"),
        getReviewersWith(reviewHistory, newReview, "LCSubstring"),
        getReviewersWith(reviewHistory, newReview, "LCSubsequence"),
        dpReviewersList)
    for (list in reviewerScores) {
        var score = list.size
        if (list == dpReviewersList) {
            for (reviewer in list) {
                augmentedReviewers[reviewer.first] = augmentedReviewers.getOrDefault(reviewer.first, 0) + reviewer.second.toInt()
            }
        }
        for (reviewer in list) {
            augmentedReviewers[reviewer.first] = augmentedReviewers.getOrDefault(reviewer.first, 0) + score
            score -= 1
        }
    }
    return augmentedReviewers.toList().sortedByDescending { it.second }
}
