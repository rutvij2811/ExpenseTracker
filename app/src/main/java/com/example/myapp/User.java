package com.example.myapp;

public class User {
    private String username;
    private String password;
    private String dob;
    private String secret;
    private String phone="";
    private String address="";

    public User() {
    }

    public User(String username, String password, String dob, String phone, String address) {
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.phone = phone;
        this.address = address;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
