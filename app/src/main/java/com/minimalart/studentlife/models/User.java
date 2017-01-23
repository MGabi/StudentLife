package com.minimalart.studentlife.models;

/**
 * Created by ytgab on 11.12.2016.
 */

public class User {
    public String email;
    public String name;
    public String secName;
    public String age;

    public User() {

    }

    public User(String email, String name, String secName, String age) {
        this.email = email;
        this.name = name;
        this.secName = secName;
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSecName() {
        return secName;
    }

    public String getAge() {
        return age;
    }
}
