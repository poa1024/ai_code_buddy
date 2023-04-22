package io.github.poa1024.ai.code.buddy;

import io.github.poa1024.ai.code.buddy.session.model.AIInteraction;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AIRequestBuilder {

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


    public String tab(String historyToString, int i) {
        var tab = Stream.generate(() -> " ").limit(i).collect(Collectors.joining());
        return historyToString.lines().map(s -> tab + s).collect(Collectors.joining("\n"));
    }

    public String appendContext(String text, String context) {
        return text + "\n\nConsider that the code is a part of this context: \n\n```\n\n" + tab(context, 2) + "\n\n```\n\n";
    }

    public String appendHistory(String text, List<AIInteraction> history) {
        var dialog = history.stream()
                .map(qa -> List.of(
                        "  Q: ",
                        qa.getRequest().getQuestion().text(),
                        "\n",
                        "  A: ",
                        qa.getResponse().getText(),
                        "\n",
                        "\n"
                )).flatMap(Collection::stream)
                .collect(Collectors.joining());
        return text + "\n\n  Our previous dialog: \n\n" + tab(dialog, 4) + "\n\n";
    }

}
