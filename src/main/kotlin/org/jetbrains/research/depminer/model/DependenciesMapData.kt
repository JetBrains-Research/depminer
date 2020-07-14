package org.jetbrains.research.depminer.model

import com.intellij.psi.PsiElement

data class DependenciesMapData constructor(val dependenciesMutableMap: MutableMap<PsiElement, PsiElement>){
}