package com.vuhung.minichatapp.model;

public class JwtResponse {
    private String token;
    private String message;
    private int status;

    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='" + token + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
