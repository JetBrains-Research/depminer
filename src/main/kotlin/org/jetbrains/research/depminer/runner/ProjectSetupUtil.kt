package org.jetbrains.research.depminer.runner

import com.intellij.ide.impl.NewProjectUtil
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import testOutput
import java.io.File
import java.lang.System.*
import kotlin.system.exitProcess

fun projectSetup(inputDir: File, outputDir: File): Project {

    val isKotlin = false //Work in progress

    val project = loadProject(inputDir.absolutePath, outputDir)
    println("Successfully opened project at inputDir: $project")

    println("Project setup debug info:")
    visitProjectFiles(project)

    if (isKotlin) {
        setupKotlinSDK(project)
    }

    return project
}

fun visitProjectFiles(project: Project) {
    ProjectRootManager.getInstance(project).contentRoots.forEach { root ->
        println("Project root: $root")
        VfsUtilCore.iterateChildrenRecursively(root, null) {
            println("Virtual file: $it")
            true
        }
    }
}

fun setupKotlinSDK(project: Project): Project {
    val sdk = JavaSdk.getInstance().createJdk("openjdk", getenv("JAVA_HOME")!!, true)
    ProjectJdkTable.getInstance().addJdk(sdk)
    ProjectRootManager.getInstance(project).projectSdk = sdk
    NewProjectUtil.applyJdkToProject(project, sdk)
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
