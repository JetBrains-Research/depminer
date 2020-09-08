package org.jetbrains.research.depminer.runner

import com.intellij.ide.impl.NewProjectUtil
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VfsUtilCore.isEqualOrAncestor
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jps.model.serialization.PathMacroUtil
import testOutput
import java.io.File
import java.lang.System.getenv
import kotlin.system.exitProcess

fun projectSetup(inputDir: File, sourceRootDir: File, outputDir: File): Project {
    val project = loadProject(inputDir.absolutePath, outputDir)
    val projectVirtualFiles = getProjectFiles(project)
    return if (projectVirtualFiles.any() {it.path.endsWith(".iml")}) {
        project
    } else {
        visitProjectFiles(project, sourceRootDir)
        project
    }
}

fun getProjectFiles(project: Project): Collection<VirtualFile> {
    val virtualFiles = mutableListOf<VirtualFile>()
    ProjectRootManager.getInstance(project).contentRoots.forEach { root ->
        VfsUtilCore.iterateChildrenRecursively(root, null) {
            virtualFiles.add(it)
            true
        }
    }
    return virtualFiles
}

fun visitProjectFiles(project: Project, sourceRootDir: File) {
    var sourceRootSet = false
    ProjectRootManager.getInstance(project).contentRoots.forEach { root ->
        VfsUtilCore.iterateChildrenRecursively(root, null) { it ->
            if (it.path.endsWith(sourceRootDir.path) && !sourceRootSet) {
                sourceRootSet = true
                setSourceRootForSingleModule(project, it)
            }
            true
        }
    }
}

fun setSourceRootForSingleModule(project: Project, vfile: VirtualFile) {
    val module = ModuleManager.getInstance(project).modules[0]
    ApplicationManager.getApplication().runWriteAction {
        addSourceFolder(vfile.path, module)
    }
    true
}

fun addSourceFolder(relativePath: String, module: Module) {
    val rootModel = ModuleRootManager.getInstance(module).modifiableModel
    val directory = File(PathMacroUtil.getModuleDir(module.moduleFilePath)).resolve(relativePath)
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(directory)
    getContentRootFor(virtualFile, rootModel)?.addSourceFolder(virtualFile!!.url, false)
    rootModel.commit()
}

fun getContentRootFor(url: VirtualFile?, rootModel: ModifiableRootModel): ContentEntry? {
    for (e in rootModel.contentEntries) {
        if (url != null) {
            if (isEqualOrAncestor(e.url, url.url)) return e
        }
    }
    return null
}

fun loadProject(path: String, outputDir: File): Project {
    //println("Starting project search at path: $path")
    val project = ProjectUtil.openOrImport(path , null, true)
    if (project == null) {
        outputDir.resolve(testOutput).writeText("Could not load project from $path, execution aborted")
        println("Could not load project from $path")
        println("Aborting execution...")
        exitProcess(1)
    }
    return project
}
