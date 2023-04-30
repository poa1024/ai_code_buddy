package io.github.poa1024.ai.code.buddy.context;

public abstract class AICBContextHolder {

    private static final GptAICBContext GPT_AICB_CONTEXT = new GptAICBContext();

    //can be switched to different implementation
    public static GptAICBContext getContext() {
        return GPT_AICB_CONTEXT;
    }


}
