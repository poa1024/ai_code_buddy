package io.github.poa1024.action.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import io.github.poa1024.Configuration;
import io.github.poa1024.exception.GptException;
import io.github.poa1024.session.GptGenerationCodeSession;
import io.github.poa1024.session.GptSessionManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

public class GenerateCodeAction extends AnAction {


    private final GptSessionManager gptSessionManager = Configuration.GPT_SESSION_MANAGER;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var psiFile = e.getData(LangDataKeys.PSI_FILE);
        var caretModel = editor.getCaretModel();

        var selectedText = getSelectedTextOrTheCurrentComment(editor, psiFile);
        var userInput = selectedText.getText();

        var session = new GptGenerationCodeSession(
                psiFile,
                selectedText.getStartOffset(),
                selectedText.getEndOffset()
        );

        gptSessionManager.openNewSession(editor.getProject(), session);
        gptSessionManager.proceed(userInput);

        caretModel.getCurrentCaret().removeSelection();
    }

    private static SelectedText getSelectedTextOrTheCurrentComment(Editor editor, PsiFile psiFile) {

        var caretModel = editor.getCaretModel();
        var currentCaret = caretModel.getCurrentCaret();

        if (currentCaret.getSelectedText() != null && !currentCaret.getSelectedText().isBlank()) {
            return new SelectedText(
                    currentCaret.getSelectedText(),
                    currentCaret.getSelectionStart(),
                    currentCaret.getSelectionEnd()
            );
        }

        var currentNode = psiFile.getNode().findLeafElementAt(caretModel.getOffset());

        while (currentNode instanceof PsiWhiteSpace && !(currentNode instanceof PsiComment)) {
            currentNode = currentNode.getTreePrev();
        }

        if (!(currentNode instanceof PsiComment)) {
            throw new GptException("Didn't find a suitable place for the code insertion");
        }

        return new SelectedText(
                currentNode.getText(),
                currentNode.getTextRange().getStartOffset(),
                currentNode.getTextRange().getEndOffset()
        );
    }

    @RequiredArgsConstructor
    @Getter
    private static class SelectedText {
        private final String text;
        private final int startOffset;
        private final int endOffset;
    }

}
