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
            println("And it resolves to: ${elementDeclaration.toString()} \n")
             if (elementDeclaration.containingFile != null && elementDeclaration.containingFile.virtualFile != null) {

                 if (elementDeclaration.textRange != null && psiElement.textRange != null) {
                     val fromElementRange = FileRange(psiElement.startOffset, psiElement.endOffset)

                     val fromElementType = ref.toString().substringBefore(':')
                     val codeElement = CodeElement(LocationInfo(psiElement.containingFile.virtualFile.path, fromElementRange), fromElementType)


                     val toElementRange = FileRange(elementDeclaration.startOffset, elementDeclaration.endOffset)
                     val toElementType = elementDeclaration.toString().substringBefore(':')
                     val codeElementDeclaration = CodeElement(LocationInfo(elementDeclaration.containingFile.virtualFile.path, toElementRange), toElementType)

                     val currentDependency = Dependency(ConnectionType.USAGE, codeElement, codeElementDeclaration) //Usage only? Consider getting rid of
                     dependenciesMap.add(currentDependency)
                 }
             }
        } else {
            println()
        }
    }
    return dependenciesMap
}

