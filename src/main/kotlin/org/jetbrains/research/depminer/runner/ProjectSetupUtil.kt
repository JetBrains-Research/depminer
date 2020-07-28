package org.jetbrains.research.depminer.runner

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import testOutput
import java.io.File
import kotlin.system.exitProcess

fun projectSetup(inputDir: File, outputDir: File): Project {
    val project = loadProject(inputDir.absolutePath, outputDir)

    println("Successfully opened project at inputDir: $project")
    println("Project setup debug info:")

    ProjectRootManager.getInstance(project).contentRoots.forEach { root ->
        println("Project root: $root")
        VfsUtilCore.iterateChildrenRecursively(root, null) {
            println("Virtual file: $it")
            true
        }
    }
    return project
}

fun loadProject(path: String, outputDir: File): Project {
    println("Starting project search at path: $path")
    val project = ProjectUtil.openOrImport(path , null, true)
    if (project == null) {
        outputDir.resolve(testOutput).writeText("Could not load project from $path, execution aborted")
        println("Could not load project from $path")
        println("Aborting execution...")
        exitProcess(1)
    }
    return project
}
