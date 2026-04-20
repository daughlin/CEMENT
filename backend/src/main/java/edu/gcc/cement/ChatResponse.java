package edu.gcc.cement;

public class ChatResponse {
    private String reply;
    private String intent;
    private Object data;

    public ChatResponse() {}

    public ChatResponse(String reply, String intent, Object data) {
        this.reply = reply;
        this.intent = intent;
        this.data = data;
    }

    public String getReply() {
        return reply;
    }

    public String getIntent() {
        return intent;
    }

    public Object getData() {
        return data;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public void setData(Object data) {
        this.data = data;
    }
}