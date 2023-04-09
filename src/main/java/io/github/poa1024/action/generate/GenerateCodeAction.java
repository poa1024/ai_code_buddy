package io.github.poa1024.action.generate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import io.github.poa1024.Configuration;
import io.github.poa1024.GptException;
import io.github.poa1024.conversation.GptConversationManager;
import io.github.poa1024.conversation.model.Answer;
import io.github.poa1024.conversation.model.Question;
import io.github.poa1024.gpt.GptClient;
import io.github.poa1024.gpt.GptQuestionBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

public class GenerateCodeAction extends AnAction {

    private final GptClient gptClient = Configuration.GPT_CLIENT;
    private final GptQuestionBuilder gptQuestionBuilder = Configuration.GPT_QUESTION_BUILDER;
    private final GptConversationManager conversationManager = Configuration.CONVERSATION_MANAGER;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var project = e.getRequiredData(CommonDataKeys.PROJECT);
        var styleManager = CodeStyleManager.getInstance(project);
        var documentManager = PsiDocumentManager.getInstance(project);
        var document = editor.getDocument();
        var psiFile = e.getData(LangDataKeys.PSI_FILE);
        var caretModel = editor.getCaretModel();

        var selectedText = getSelectedTextOrTheCurrentComment(editor, psiFile);

        conversationManager.startConversation(new Question(selectedText.getText()));

        var fileText = psiFile.getText();

        var res = gptClient
                .ask(gptQuestionBuilder.askToGenerateCode(selectedText.getText(), fileText))
                .getFirstChoice();

        conversationManager.getGptConversation().addAnswer(new Answer(res, "code was generated successfully"));
        conversationManager.updateView();

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
        private final int start;
        private final int end;
    }

}
