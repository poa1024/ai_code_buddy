package io.github.poa1024.action.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import io.github.poa1024.Configuration;
import io.github.poa1024.GptException;
import io.github.poa1024.sesssion.GptCodeGenerationSessionManager;
import io.github.poa1024.sesssion.AnswerHandler;
import io.github.poa1024.sesssion.model.Answer;
import io.github.poa1024.sesssion.model.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

public class GenerateCodeAction extends AnAction {


    private final GptCodeGenerationSessionManager gptCodeGenerationSessionManager = Configuration.GPT_CODE_GENERATION_SESSION_MANAGER;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var psiFile = e.getData(LangDataKeys.PSI_FILE);
        var caretModel = editor.getCaretModel();

        var selectedText = getSelectedTextOrTheCurrentComment(editor, psiFile);

        var context = psiFile.getText();
        gptCodeGenerationSessionManager.openNewSession(
                context,
                new InsertCodeToEditorAnswerHandler(psiFile, selectedText.getStartOffset(), selectedText.getEndOffset())
        );

        gptCodeGenerationSessionManager.askQuestion(new Question(selectedText.getText()));

        caretModel.getCurrentCaret().removeSelection();
    }

    @AllArgsConstructor
    private static class InsertCodeToEditorAnswerHandler implements AnswerHandler {

        private final PsiFile psiFile;
        private int start;
        private int end;

        @Override
        public void call(Answer answer) {

            var project = psiFile.getProject();
            var documentManager = PsiDocumentManager.getInstance(project);
            var document = documentManager.getDocument(psiFile);
            var styleManager = CodeStyleManager.getInstance(project);

            WriteCommandAction.runWriteCommandAction(
                    project,
                    () -> {
                        var answerText = answer.getText();
                        document.replaceString(start, end, answerText);
                        documentManager.commitDocument(document);

                        var leftPsi = psiFile.findElementAt(start);
                        var rightPsi = findNotWhiteSpaceElementAt(start + answerText.length());

                        styleManager.reformatText(psiFile, start, start + answerText.length() + 1);

                        start = leftPsi.getTextOffset();
                        end = rightPsi.getTextRange().getEndOffset();
                    }
            );
        }

        private PsiElement findNotWhiteSpaceElementAt(int offset) {
            var res = psiFile.findElementAt(offset);
            while (res instanceof PsiWhiteSpace) {
                res = res.getPrevSibling();
            }
            return res;
        }
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
            throw new GptException("Didn't found a suitable place for the code insertion");
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
