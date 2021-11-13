package com.vuhung.minichatapp.model;

import java.io.Serializable;

public class UserChat extends User implements Serializable {
    private String content;

    public UserChat() {
    }

    public UserChat(String avatar, String name, String username, String content) {
        this.setAvatar(avatar);
        this.setFullName(name);
        this.setUsername(username);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
