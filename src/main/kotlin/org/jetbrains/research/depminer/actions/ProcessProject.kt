package org.jetbrains.research.depminer.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.*
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import org.jetbrains.research.depminer.model.*
import testOutput
import java.io.File

fun getProjectDependencies(projectPath: String, project: Project, outputDir: File): Collection<Dependency> {
    return getDependencies(ProjectScope(projectPath), project, outputDir)
}

private fun getDependencies(scope: AnalysisScope, project: Project, outputDir: File): Collection<Dependency> {
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
    return findDependenciesInList(psiFiles, outputDir)
}

private fun findDependenciesInList(psiFiles: Collection<PsiFile>, outputDir: File): Collection<Dependency> {
    val dependenciesMap = mutableListOf<Dependency>()
    //outputDir.resolve(testOutput).appendText("[")
    for (element in psiFiles) {
        element.accept(object: PsiRecursiveElementVisitor()
        {
            override fun visitElement(element: PsiElement) {
                dependenciesMap.addAll(visitPsiElement(element, outputDir))
                super.visitElement(element)
            }
        })
    }
    //outputDir.resolve(testOutput).appendText("{}]")
    return dependenciesMap
}

private fun visitPsiElement(psiElement: PsiElement, outputDir: File): Collection<Dependency>  {
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
                     //println("writing current dependency between ${currentDependency.from.location.path} and ${currentDependency.to.location.path} to file: ${outputDir.resolve(testOutput)}")
                     //outputDir.resolve(testOutput).appendText(convertSingleDependencyToJSON(currentDependency) + ", ")
                 }
             }
        } else {
            println()
        }
    }
    return dependenciesMap
}

