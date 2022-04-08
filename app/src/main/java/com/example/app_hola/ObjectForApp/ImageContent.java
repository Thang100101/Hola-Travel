package com.example.app_hola.ObjectForApp;

public class ImageContent {
    String Link, ContentID;

    public ImageContent(String link, String contentID) {
        Link = link;
        ContentID = contentID;
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
}
