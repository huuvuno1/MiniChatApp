package com.vuhung.minichatapp.model;

import java.util.Date;

public class Message {
    private String sender;
    private String receiver;
    private String content;
    private Date time;

    public Message() {
    }

    public Message(String content) {
        this.content = content;
    }

    public Message(String sender, String receiver, String content, Date time) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
