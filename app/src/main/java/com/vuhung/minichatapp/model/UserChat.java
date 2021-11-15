package com.vuhung.minichatapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserChat extends User {
    private String content;
    @SerializedName("timestamp")
    private Date timeStamp;

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

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
