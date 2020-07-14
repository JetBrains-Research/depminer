import com.intellij.psi.PsiElement

data class DependenciesMapData constructor(val dependenciesMutableMap: MutableMap<PsiElement, PsiElement>){
}