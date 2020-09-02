package org.jetbrains.research.depminer.gitutil

import org.eclipse.jgit.api.Git
import org.jetbrains.research.depminer.model.Review
import java.io.File

const val clonePath = "clonedRepos/"

fun cloneRemoteRepository(newReview: Review): Git {
    val remoteURI = newReview.commitInfo.project + ".git"
    println("Cloning repository from%${remoteURI}")
    val git = Git.cloneRepository()
        .setURI(remoteURI)
        .setDirectory(File(System.getProperty("user.dir") + clonePath))
        .call()
    println("Cloned repository into:${git.repository.directory.absolutePath}")
    return git
}