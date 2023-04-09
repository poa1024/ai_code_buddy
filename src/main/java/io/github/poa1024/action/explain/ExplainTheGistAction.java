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

public class ExplainTheGistAction extends AnAction {

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
                .ask(gptQuestionBuilder.askForShortExplanation(selectedText))
                .getFirstChoice();

        int selectionStart = caretModel.getCurrentCaret().getSelectionStart();
        int explanationStart = selectionStart - 1;

        WriteCommandAction.runWriteCommandAction(
                project,
                () -> {
                    document.insertString(explanationStart, res);
                    documentManager.commitDocument(document);
                    styleManager.reformatText(psiFile, explanationStart, explanationStart + res.length());
                }


        );
        caretModel.getCurrentCaret().removeSelection();
    }

}
