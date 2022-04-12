package com.example.app_hola.ObjectForApp;

import java.io.Serializable;

public class ImageContent implements Serializable {
    String Link, Name;

    public ImageContent(String link, String name) {
        Link = link;
        Name = name;
    }

    public ImageContent() {
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
