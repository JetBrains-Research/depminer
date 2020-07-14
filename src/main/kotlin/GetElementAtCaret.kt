import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.Nullable

class GetElementAtCaret {
    fun getElement (event: AnActionEvent): PsiElement? {
        val editor: Editor? = event.getData(CommonDataKeys.EDITOR)
        val psiFile: PsiFile? = event.getData(CommonDataKeys.PSI_FILE)
        val offset: Int = editor?.caretModel!!.offset
        return psiFile?.findElementAt(offset)
    }
}