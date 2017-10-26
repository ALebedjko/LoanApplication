package io.fourfinanceit.exception;

import java.util.ArrayList;
import java.util.List;

public class ExceptionJSONInfo {
    private String url;
    private List<String> messages;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String Url, String message) {
        this.url = Url;
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
    }

    public void addMessage(String message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
    }
}
