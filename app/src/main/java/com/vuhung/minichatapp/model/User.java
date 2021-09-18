package com.vuhung.minichatapp.model;


import java.io.Serializable;

public class User implements Serializable {
    private int resourceId;
    private String name;
    private String email;

    public User(int resourceId, String email, String name) {
        this.resourceId = resourceId;
        this.email = email;
        this.name = name;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
