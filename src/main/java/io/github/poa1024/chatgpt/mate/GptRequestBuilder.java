package io.github.poa1024.chatgpt.mate;

import io.github.poa1024.chatgpt.mate.session.model.GptInteraction;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GptRequestBuilder {

    public String askForDetailedExplanation() {
        return "\nExplain the code. Inject your notes into the code. " +
                "Your response SHOULD contain this code with your explanatory comments. ";
    }

    public String askForShortExplanation() {
        return "\nGive me a short explanation of what's happening in the code. " +
                "Try to be concise." +
                "Your answer should not contain the code itself. Just the explanation.";
    }

    public String appendCode(String text, String code) {
        return text + "\n\nCode: \n\n```\n\n" + tab(code, 2) + "\n\n```\n\n";
    }

    public String continueDialog(String replica) {
        return "\nI have a new code related question: " + replica;
    }

    public String askToGenerateCode(String request) {
        return "\nGenerate code based on the provided request. " +
               baseCodeGenerationRequest() +
               "\nRequest: [ " + request + " ]";
    }

    public String askToChangeGeneratedCode(String request) {
        return "\nAdjust the provided code. Return only the adjusted version. " +
                "\n" + request + "." +
                baseCodeGenerationRequest();
    }


    private static String baseCodeGenerationRequest() {
        return "\nYor response should contain the code only. " +
               "\nDon't leave any comments or notes. " +
               "\nDon't surround your response with quotes.";
    }


    public String tab(String historyToString, int i) {
        var tab = Stream.generate(() -> " ").limit(i).collect(Collectors.joining());
        return historyToString.lines().map(s -> tab + s).collect(Collectors.joining("\n"));
    }

    public String appendContext(String text, String context) {
        return text + "\n\nConsider that the code is a part of this context: \n\n```\n\n" + tab(context, 2) + "\n\n```\n\n";
    }

    public String appendPreviouslyGenerateCode(String text, List<String> codeGenerateHistory) {
        var prevCode = codeGenerateHistory.get(codeGenerateHistory.size() - 1);
        return text + "\n\nCode to adjust: \n\n```\n\n" + tab(prevCode, 2) + "\n\n```\n\n";
    }

    public String appendHistory(String text, List<GptInteraction> history) {
        var dialog = history.stream()
                .map(qa -> List.of(
                        "  Q: ",
                        qa.getGptRequest().getQuestion().getText(),
                        "\n",
                        "  A: ",
                        qa.getGptResponse().getText(),
                        "\n",
                        "\n"
                )).flatMap(Collection::stream)
                .collect(Collectors.joining());
        return text + "\n\n  Our previous dialog: \n\n" + tab(dialog, 4) + "\n\n";
    }

}
