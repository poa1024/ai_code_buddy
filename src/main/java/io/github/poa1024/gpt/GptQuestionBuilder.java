package io.github.poa1024.gpt;

public class GptQuestionBuilder {

    public String askForDetailedExplanation(String code) {
        return "Explain the code. Do not change the code, just add explanatory comments. Your response should contains this code with your explanatory comments. Code starts here: " + code;
    }

}
