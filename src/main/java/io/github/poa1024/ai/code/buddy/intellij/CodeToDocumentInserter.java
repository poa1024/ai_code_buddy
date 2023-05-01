package io.github.poa1024.ai.code.buddy.intellij;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class CodeToDocumentInserter implements Consumer<String> {

    private final PsiFile psiFile;
    private final String before;
    private final String after;

    @Override
    public void accept(String code) {
        var project = psiFile.getProject();
        var documentManager = PsiDocumentManager.getInstance(project);
        var document = documentManager.getDocument(psiFile);
        var styleManager = CodeStyleManager.getInstance(project);
        WriteCommandAction.runWriteCommandAction(
                project,
                () -> {
                    document.setText(before + code + after);
                    documentManager.commitDocument(document);
                    styleManager.reformatText(psiFile, before.length(), before.length() + code.length() + 1);
                }
        );
    }
}
