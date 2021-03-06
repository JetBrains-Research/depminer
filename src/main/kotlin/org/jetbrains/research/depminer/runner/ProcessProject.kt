package org.jetbrains.research.depminer.runner

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.*
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import org.jetbrains.research.depminer.model.*
import java.io.File

fun getProjectDependencies(projectPath: String, project: Project): Collection<Dependency> {
    val scope = ProjectScope(projectPath)
    return getDependenciesSimpleMode(scope, project)
}

private fun getDependenciesSimpleMode(scope: AnalysisScope, project: Project): Collection<Dependency> {
    val psiElements = mutableListOf<PsiElement>()
    for (location in scope.getLocations()) {
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(File(location.path))
        if (virtualFile != null) {
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            if (psiFile != null) {
                val startOffset = 0
                val endOffset = psiFile.textLength
                var caretPosition = startOffset
                while (caretPosition < endOffset) {
                    val psiLeaf = psiFile.findElementAt(caretPosition)
                    if (psiLeaf != null) {
                        val psiElement = psiLeaf.parent
                        psiElements.add(psiElement)
                        if (psiElement.children.isNotEmpty()) {
                            psiElement.accept(object: PsiRecursiveElementVisitor()  {
                                override fun visitElement(element: PsiElement) {
                                    if (element.references.isNotEmpty()) {
                                        psiElements.add(element)
                                    }
                                    super.visitElement(element)
                                }
                            })
                        }
                        caretPosition += psiElement.textLength
                    }
                }
            }
        }
    }
    return findDependenciesInElementsList(psiElements)
}

private fun findDependenciesInElementsList(psiElements: Collection<PsiElement>): Collection<Dependency> {
    val dependencies = mutableListOf<Dependency>()
    for (element in psiElements) {
        dependencies.addAll(visitPsiElement(element))
    }
    return dependencies
}

private fun findDependenciesInFileList(psiFiles: Collection<PsiFile>, outputDir: File, scope: AnalysisScope): Collection<Dependency> {
    val dependenciesMap = mutableListOf<Dependency>()
    for (element in psiFiles) {
        element.accept(object: PsiRecursiveElementVisitor()
        {
            override fun visitElement(element: PsiElement) {
                for (location in scope.getLocations()) {
                    if (element.containingFile.virtualFile.path == location.path) {
                        if (true) {
                            dependenciesMap.addAll(visitPsiElement(element))
                        }
                    }
                }
                super.visitElement(element)
            }

            private fun elementIsWithinRange(element: PsiElement, location: LocationInfo): Boolean {
                if (location.range.start == 0 || location.range.end == 0) {
                    return true
                }
                val elementStartLine = StringUtil.offsetToLineNumber(element.containingFile.text, element.startOffset)
                val elementEndLine = StringUtil.offsetToLineNumber(element.containingFile.text, element.endOffset)
                val elementRange = elementStartLine..elementEndLine
                val locationRange = location.range.start!!..location.range.end!!
                return elementRange.intersect(locationRange).isNotEmpty()
            }
        })
    }
    return dependenciesMap
}

private fun visitPsiElement(psiElement: PsiElement): Collection<Dependency>  {
    val dependenciesMap = mutableListOf<Dependency>()
    val references = psiElement.references
    for (ref in references) {
        val elementDeclaration = ref.resolve()
        if (elementDeclaration != null) {
            val dependency = getDependencyForElements(psiElement, elementDeclaration)
            if (dependency != null) {
                dependenciesMap.add(dependency)
            }
        }
    }

//    Reference search allows to perform the "resolving" of a reference the other way around.
//    By discovering all references pointing to the given element. Note - this action is very computationally expensive.
//    val search = ReferencesSearch.search(psiElement)
//    for (reference in search) {
//        val usage = reference.element
//        val dependency = getDependencyForElements(psiElement, usage)
//        if (dependency != null) {
//            dependenciesMap.add(dependency)
//        }
//    }

    return dependenciesMap
}

fun getDependencyForElements(psiElement: PsiElement, elementDeclaration: PsiElement): Dependency? {
    if (elementDeclaration.containingFile != null && elementDeclaration.containingFile.virtualFile != null) {
        if (elementDeclaration.textRange != null && psiElement.textRange != null) {
            val fromElementBegin = StringUtil.offsetToLineNumber(psiElement.containingFile.text, psiElement.startOffset)
            val fromElementEnd = StringUtil.offsetToLineNumber(psiElement.containingFile.text, psiElement.endOffset)
            val fromElementRange = FileRange(fromElementBegin, fromElementEnd)
            val fromElementType = psiElement.toString().substringBefore(':')
            val codeElement = CodeElement(LocationInfo(psiElement.containingFile.virtualFile.path, fromElementRange), fromElementType)

            val toElementBegin = StringUtil.offsetToLineNumber(elementDeclaration.containingFile.text, elementDeclaration.startOffset)
            val toElementEnd = StringUtil.offsetToLineNumber(elementDeclaration.containingFile.text, elementDeclaration.endOffset)
            val toElementRange = FileRange(toElementBegin, toElementEnd)
            val toElementType = elementDeclaration.toString().substringBefore(':')

            val codeElementDeclaration = CodeElement(LocationInfo(elementDeclaration.containingFile.virtualFile.path, toElementRange), toElementType)
            return Dependency(codeElement, codeElementDeclaration)
        }
    }
    return null
}

