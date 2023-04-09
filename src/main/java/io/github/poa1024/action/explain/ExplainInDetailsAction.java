package io.github.poa1024.action.explain;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import io.github.poa1024.Configuration;
import io.github.poa1024.gpt.GptClient;
import io.github.poa1024.gpt.GptQuestionBuilder;
import org.jetbrains.annotations.NotNull;

public class ExplainInDetailsAction extends AnAction {

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
        var selectedText = caretModel.getCurrentCaret().getSelectedText();

        var res = gptClient
                .ask(gptQuestionBuilder.askForDetailedExplanation(selectedText))
                .getFirstChoice();

        int selectionStart = caretModel.getCurrentCaret().getSelectionStart();
        int selectionEnd = caretModel.getCurrentCaret().getSelectionEnd();

        WriteCommandAction.runWriteCommandAction(
                project,
                () -> {
                    document.replaceString(selectionStart, selectionEnd, res);
                    documentManager.commitDocument(document);
                    styleManager.reformatText(psiFile, selectionStart, selectionStart + res.length());
                }


        );
        caretModel.getCurrentCaret().removeSelection();
    }

}
