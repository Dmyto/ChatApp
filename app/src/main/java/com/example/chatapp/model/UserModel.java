package com.example.chatapp.model;

public class UserModel {
    private String name;
    private String email;
    private String id;
    private String avatarMockResource;

    public UserModel(String name, String email, String id, String avatarMockResource) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.avatarMockResource = avatarMockResource;
    }

    public UserModel() {
    }

    public String getAvatarMockResource() {
        return avatarMockResource;
    }

    public void setAvatarMockResource(String avatarMockResource) {
        this.avatarMockResource = avatarMockResource;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }
}
