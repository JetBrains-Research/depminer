package org.jetbrains.research.depminer.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.*
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import org.jetbrains.research.depminer.model.*
import java.io.File

fun getProjectDependencies(projectPath: String, project: Project): Collection<Dependency> {
    return getDependencies(ProjectScope(projectPath), project)
}

fun getFileDependencies(filePath: String, project: Project): Collection<Dependency> {
    TODO("Implement FileScope approach")
    return getDependencies(FileScope(filePath), project)
}

private fun getDependencies(scope: AnalysisScope, project: Project): Collection<Dependency> {
    val psiFiles = mutableListOf<PsiFile>()
    for (element in scope.getLocations()) {
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(File(element.path))
        if (virtualFile != null) {
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            if (psiFile != null) {
                psiFiles.add(psiFile)
            }
        }
    }
    return findDependenciesInList(psiFiles)
}

private fun findDependenciesInList(psiFiles: Collection<PsiFile>): Collection<Dependency> {
    val dependenciesMap = mutableListOf<Dependency>()
    for (element in psiFiles) {
        element.accept(object: PsiRecursiveElementVisitor()
        {
            override fun visitElement(element: PsiElement) {
                dependenciesMap.addAll(visitPsiElement(element))
                super.visitElement(element)
            }
        })
    }
    return dependenciesMap
}

private fun visitPsiElement(psiElement: PsiElement): Collection<Dependency> {
    val dependenciesMap = mutableListOf<Dependency>()
    val references = psiElement.references
    for (ref in references) {
        println("Inspecting element: $psiElement")
        println("Element reference: $ref")
        val elementDeclaration = ref.resolve()
        if (elementDeclaration != null) {
            println("And it resolves to: ${elementDeclaration.toString()}")
             if (elementDeclaration.containingFile != null && elementDeclaration.containingFile.virtualFile != null)
            {
                val fromElementRange = FileRange(psiElement.startOffset, psiElement.endOffset)
                val codeElement = CodeElement(LocationInfo(psiElement.containingFile.virtualFile.path, fromElementRange), ElementType.FIELD)
                val toElementRange = FileRange(elementDeclaration.startOffset, elementDeclaration.endOffset)
                val codeElementDeclaration = CodeElement(LocationInfo(elementDeclaration.containingFile.virtualFile.path, toElementRange), ElementType.FIELD)
                val currentDependency = Dependency(ConnectionType.USAGE, codeElement, codeElementDeclaration)
                dependenciesMap.add(currentDependency)
            }
        }
    }
    return dependenciesMap
}