package com.example.app_hola.ObjectForApp;

public class TagContent {
    String ID, tagId, contentID;

    public TagContent(String ID, String tagId, String contentID) {
        this.ID = ID;
        this.tagId = tagId;
        this.contentID = contentID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }
}
