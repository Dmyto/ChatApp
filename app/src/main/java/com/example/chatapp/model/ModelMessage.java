package com.example.chatapp.model;

public class ModelMessage {

    private String text;
    private String name;
    private String sender;
    private String recipient;
    private String imageUrl;
    private String avatarMockResourceMsg;
    private String timeSent;


    public ModelMessage(){

    }

    public ModelMessage(String text, String name, String sender, String recipient, String imageUrl) {
        this.text = text;
        this.name = name;
        this.sender = sender;
        this.recipient = recipient;
        this.imageUrl = imageUrl;
    }

    public String getAvatarMockResourceMsg() {
        return avatarMockResourceMsg;
    }

    public void setAvatarMockResourceMsg(String avatarMockResourceMsg) {
        this.avatarMockResourceMsg = avatarMockResourceMsg;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String dateSent) {
        this.timeSent = dateSent;
    }
}
