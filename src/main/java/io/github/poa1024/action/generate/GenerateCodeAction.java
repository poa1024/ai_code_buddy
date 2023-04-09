package io.github.poa1024.action.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import io.github.poa1024.Configuration;
import io.github.poa1024.gpt.GptClient;
import io.github.poa1024.gpt.GptQuestionBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

public class GenerateCodeAction extends AnAction {

    private final GptClient gptClient = Configuration.GPT_CLIENT;
    private final GptQuestionBuilder gptQuestionBuilder = Configuration.GPT_QUESTION_BUILDER;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var project = e.getRequiredData(CommonDataKeys.PROJECT);
        var styleManager = CodeStyleManager.getInstance(project);
        var documentManager = PsiDocumentManager.getInstance(project);
        var document = editor.getDocument();
        var psiFile = e.getData(LangDataKeys.PSI_FILE);
        var caretModel = editor.getCaretModel();

        var selectedText = getSelectedText(editor, psiFile);

        var res = gptClient
                .ask(gptQuestionBuilder.askToGenerateCode(selectedText.getText()))
                .getFirstChoice();

        WriteCommandAction.runWriteCommandAction(
                project,
                () -> {
                    document.replaceString(selectedText.getStart(), selectedText.getEnd(), res);
                    documentManager.commitDocument(document);
                    styleManager.reformatText(psiFile, selectedText.getStart(), selectedText.getEnd() + res.length());
                }


        );
        caretModel.getCurrentCaret().removeSelection();
    }

    private static SelectedText getSelectedText(Editor editor, PsiFile psiFile) {

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
        private final int start;
        private final int end;
    }

}
