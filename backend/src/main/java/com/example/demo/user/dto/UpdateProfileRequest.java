package com.example.demo.user.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    
    @Size(max = 200, message = "个人简介长度不能超过200个字符")
    private String bio;
    
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatarUrl;
    
    // 构造函数
    public UpdateProfileRequest() {}
    
    public UpdateProfileRequest(String nickname, String bio, String avatarUrl) {
        this.nickname = nickname;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }
    
    // Getter和Setter方法
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "nickname='" + nickname + '\'' +
                ", bio='" + bio + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}