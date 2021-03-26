package com.vinylteam.vinyl.entity;

public class User {

    private String email;
    private String password;
    private String salt;
    private int iterations;
    private Role role;

    public User(String email, String password, String salt, int iterations, Role role) {
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.iterations = iterations;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", iterations=" + iterations +
                ", role=" + role.toString() +
                '}';
    }
}
