package com.vinylteam.vinyl.entity;

import java.util.Objects;

public class UserPost {

    private long id;
    private long userId;
    private String name;
    private String email;
    private String theme;
    private String message;

    public UserPost() {
    }

    public UserPost(long userId, String name, String email, String theme, String message) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.theme = theme;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPost that = (UserPost) o;
        return id == that.id &&
                userId == that.userId &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(theme, that.theme) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, email, theme, message);
    }

    @Override
    public String toString() {
        return "UserPost{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", subject='" + theme + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}