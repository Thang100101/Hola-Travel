package com.example.app_hola.ObjectForApp;

public class Comment {
    String ID, userID, contentID, mainContent, Date;

    public Comment(String ID, String userID, String contentID, String mainContent, String date) {
        this.ID = ID;
        this.userID = userID;
        this.contentID = contentID;
        this.mainContent = mainContent;
        Date = date;
    }

    public Comment() {
    }
    ///GET

    public String getID() {
        return ID;
    }

    public String getUserID() {
        return userID;
    }

    public String getContentID() {
        return contentID;
    }

    public String getMainContent() {
        return mainContent;
    }

    public String getDate() {
        return Date;
    }


    ///SET

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public void setMainContent(String mainContent) {
        this.mainContent = mainContent;
    }

    public void setDate(String date) {
        Date = date;
    }
}
