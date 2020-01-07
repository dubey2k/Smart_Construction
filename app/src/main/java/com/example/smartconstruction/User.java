package com.example.smartconstruction;

import java.net.URL;

public class User {
    private String name;
    private String email;
    private String pass;
    private String phoneNumber;
    private String profilePicture;
    private String memberType;

    public User() {
    }

    public User(String name, String email, String pass, String phoneNumber, String profilePicture, String Member) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.memberType = Member;
    }

    public User(String name, String email, String pass, String phoneNumber, String Member) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.phoneNumber = phoneNumber;
        this.memberType = Member;
    }

    public User(String name, String email, String pass) {
        this.name = name;
        this.email = email;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public String getMemberType() {
        return memberType;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
