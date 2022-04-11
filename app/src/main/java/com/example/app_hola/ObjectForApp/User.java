package com.example.app_hola.ObjectForApp;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String userID, Name, Username, Password, Birth, Sex;
    String Avatar;
    ArrayList<Content> listContent = new ArrayList<>();

    public User(String userID, String username, String password) {
        this.userID = userID;
        Username = username;
        Password = password;
    }

    public User() {
    }

    public void addContent(Content content){
        listContent.add(content);
    }
    public void removeContent(Content content){
        listContent.remove(content);
    }

    ////GET
    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getBirth() {
        return Birth;
    }

    public String getSex() {
        return Sex;
    }

    public String getAvatar() {
        return Avatar;
    }

    public String getName() {
        return Name;
    }


    ///SET


    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setBirth(String birth) {
        Birth = birth;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public void setName(String name) {
        Name = name;
    }
}
