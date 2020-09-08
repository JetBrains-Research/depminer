package org.jetbrains.research.depminer.gitutil

import currentGitRepoBase
import org.eclipse.jgit.api.Git
import org.jetbrains.research.depminer.model.Review
import java.io.File


fun cloneRemoteRepository(newReview: Review, dirPath: String): Git {
    val remoteURI = currentGitRepoBase + newReview.commitInfo.project + ".git"
    println("Cloning repository from%${remoteURI}")
    val git = Git.cloneRepository()
        .setURI(remoteURI)
        .setDirectory(File(dirPath))
        .setCloneAllBranches(true)
        .call()
    println("Cloned repository into:${git.repository.directory.absolutePath}")
    return git
}

fun openRepoAtPath(pathToRepo: String): Git {
    return Git.open(File("$pathToRepo/.git"))
}