package com.example.app_hola.ObjectForApp;

import java.io.Serializable;

public class ImageContent implements Serializable {
    String ID, Link, ContentID;

    public ImageContent(String id, String link, String contentID) {
        Link = link;
        ContentID = contentID;
        this.ID = id;
    }

    public ImageContent() {
    }

    public String getLink() {
        return Link;
    }

    public String getContentID() {
        return ContentID;
    }

    public void setLink(String link) {
        Link = link;
    }

    public void setContentID(String contentID) {
        ContentID = contentID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
