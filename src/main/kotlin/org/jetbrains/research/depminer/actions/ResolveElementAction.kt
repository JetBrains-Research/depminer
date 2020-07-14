package org.jetbrains.research.depminer.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType


class ResolveElementAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val elementFinder = GetElementAtCaret()
        val element: PsiElement? = elementFinder.getElement(e)
        var infoContainer = StringBuilder()
        if (element != null) {
            infoContainer
                .append("Element at caret: ")
                .append(element)
                .append("\n")

            val reference: PsiReference = element.findReferenceAt(0)!!
            infoContainer
                .append("Element reference: ")
                .append(reference)
                .append("\n")

            val elementDeclaration: PsiElement = reference.resolve()!!

            infoContainer
                .append("Element declared as: ")
                .append(elementDeclaration)
                .append("\n")

            val containingClass: PsiClass? = elementDeclaration.getStrictParentOfType<PsiClass>()

            infoContainer
                .append("Element declared in class: ")
                .append(containingClass)
                .append("\n")
        }
        else {
            infoContainer
                .append("Cannot resolve an element - something went wrong \n")
        }

        Messages.showMessageDialog(
            e.project,
            infoContainer.toString(),
            "Inspection Report",
            null
        )
    }
}