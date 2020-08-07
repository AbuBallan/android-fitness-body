package com.jordan.fitnessbody.model;

import com.google.firebase.firestore.Exclude;

public class User {

    private String id;

    private String name;

    private String phone;

    private boolean admin;

    public User() {
    }

    public User(String name, String phone, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.admin = isAdmin;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", admin=" + admin +
                '}';
    }
}
