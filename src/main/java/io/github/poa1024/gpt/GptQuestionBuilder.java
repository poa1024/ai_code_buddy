package io.github.poa1024.gpt;

public class GptQuestionBuilder {

    public String askForDetailedExplanation(String code) {
        return "Explain the code. Do not change the code, just add explanatory comments. " +
                "Your response should contain this code with your explanatory comments. " +
                "Code starts here: " + code;
    }

    public String askForShortExplanation(String code) {
        return "Give me a short explanation of what's happening in the code. " +
                "Try to be concise. 3-4 sentences." +
                "Wrap your answer as a comment block." +
                "Comment should be in the style of the provided code." +
                "Do not return anything except the comment block." +
                "Your answer should not contain the code itself. Just the explanation." +
                "Every new sentence should start with a new line." +
                "Code starts here: " + code;
    }

    public String askToGenerateCode(String request) {
        return "Generate code based on provided request. " +
                "Your response should contain code only. " +
                "Request starts here: " + request;
    }

}
