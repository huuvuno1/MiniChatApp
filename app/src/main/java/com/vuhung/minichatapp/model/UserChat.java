package com.vuhung.minichatapp.model;

import java.io.Serializable;

public class UserChat implements Serializable {
    private String avatar;
    private String name;
    private String username;
    private String content;

    public UserChat() {
    }

    public UserChat(String avatar, String name, String username, String content) {
        this.avatar = avatar;
        this.name = name;
        this.username = username;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
