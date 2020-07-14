import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class ResolveElementAtCaretAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) return;

        final StringBuilder infoContainer = new StringBuilder();

        // Get a PsiElement at current caret location
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        if (element != null) {
            infoContainer
                    .append("Element at caret: ")
                    .append(element)
                    .append("\n");

            PsiReference elementRef = element.findReferenceAt(0);
            infoContainer
                    .append("Reference to the element: ")
                    .append(elementRef)
                    .append("\n");

            PsiElement refResolved = elementRef.resolve();
            infoContainer
                    .append("Reference resolves to: ")
                    .append(refResolved)
                    .append("\n");

            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(refResolved, PsiMethod.class);
            infoContainer
                    .append("Containing method: ")
                    .append(containingMethod != null ? containingMethod.getName() : "none")
                    .append("\n");

            if (containingMethod != null) {
                PsiClass containingClass = containingMethod.getContainingClass();
                infoContainer
                        .append("Containing class: ")
                        .append(containingClass != null ? containingClass.getName() : "none")
                        .append("\n");
            }
        } else {
            infoContainer
                    .append("Element at caret not found!")
                    .append("\n");
        }
        Messages.showMessageDialog(e.getProject(), infoContainer.toString(), "Inspection Report", null);
    }
}
