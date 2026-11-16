package com.skillswap.model;

public class ExchangeRequest {
    private int id;
    private int fromUserId;
    private int toUserId;
    private String skillOffering;
    private String skillRequesting;
    private String message;
    private String status;
    private String createdAt;

    // Joined fields
    private String fromUserName;
    private String toUserName;

    public ExchangeRequest() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFromUserId() { return fromUserId; }
    public void setFromUserId(int fromUserId) { this.fromUserId = fromUserId; }
    public int getToUserId() { return toUserId; }
    public void setToUserId(int toUserId) { this.toUserId = toUserId; }
    public String getSkillOffering() { return skillOffering; }
    public void setSkillOffering(String skillOffering) { this.skillOffering = skillOffering; }
    public String getSkillRequesting() { return skillRequesting; }
    public void setSkillRequesting(String skillRequesting) { this.skillRequesting = skillRequesting; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }
    public String getToUserName() { return toUserName; }
    public void setToUserName(String toUserName) { this.toUserName = toUserName; }
}
