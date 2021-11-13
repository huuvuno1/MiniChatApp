package com.vuhung.minichatapp.model;

import java.io.Serializable;
import java.util.List;

public class ChatHistory implements Serializable {
    private String [] members;
    private List<Message> messages;

    public String[] getMembers() {
        return members;
    }

    public void setMembers(String[] members) {
        this.members = members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
