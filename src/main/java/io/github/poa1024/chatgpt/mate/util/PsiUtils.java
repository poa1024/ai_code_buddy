package io.github.poa1024.chatgpt.mate.util;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

public class PsiUtils {

    @Nullable
    public static SelectedText getSelectedTextOrTheExpectedPsiElement(
            PsiFile psiFile,
            CaretModel caretModel
    ) {
        return getSelectedTextOrTheExpectedPsiElement(psiFile, caretModel, null);
    }

    @Nullable
    public static SelectedText getSelectedTextOrTheExpectedPsiElement(PsiFile psiFile, CaretModel caretModel, @Nullable Class<? extends PsiElement> psiElementClass) {
        var currentCaret = caretModel.getCurrentCaret();

        if (currentCaret.getSelectedText() != null && !currentCaret.getSelectedText().isBlank())
            return new SelectedText(
                    currentCaret.getSelectedText(),
                    currentCaret.getSelectionStart(),
                    currentCaret.getSelectionEnd()
            );

        if (psiElementClass != null) {
            var currentNode = psiFile.getNode().findLeafElementAt(caretModel.getOffset());
            while (currentNode != null) {
                if (psiElementClass.isAssignableFrom(currentNode.getClass())) {
                    return new SelectedText(
                            currentNode.getText(),
                            currentNode.getTextRange().getStartOffset(),
                            currentNode.getTextRange().getEndOffset()
                    );
                }
                currentNode = currentNode.getTreePrev();
            }
        }
        return null;
    }

    @RequiredArgsConstructor
    @Getter
    public static class SelectedText {
        private final String text;
        private final int startOffset;
        private final int endOffset;
    }

}
