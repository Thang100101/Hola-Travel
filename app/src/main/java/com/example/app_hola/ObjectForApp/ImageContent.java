package com.example.app_hola.ObjectForApp;

import java.io.Serializable;

public class ImageContent implements Serializable {
    String Link;

    public ImageContent(String id, String link, String contentID) {
        Link = link;
    }

    public ImageContent() {
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }


}
