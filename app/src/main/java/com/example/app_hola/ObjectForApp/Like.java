package com.example.app_hola.ObjectForApp;

import java.io.Serializable;

public class Like implements Serializable {
    String ID, userID, contentID;

    public Like(String ID, String userID, String contentID) {
        this.ID = ID;
        this.userID = userID;
        this.contentID = contentID;
    }

    public Like() {
    }

    public String getID() {
        return ID;
    }

    public String getUserID() {
        return userID;
    }

    public String getContentID() {
        return contentID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }
}
