package com.example.app_hola;

public class Content {
    int imageContent;
    String mainContent;
    String date;

    public Content(int imageContent, String mainContent, String date) {
        this.imageContent = imageContent;
        this.mainContent = mainContent;
        this.date = date;
    }

    public int getImageContent() {
        return imageContent;
    }

    public String getMainContent() {
        return mainContent;
    }

    public String getDate() {
        return date;
    }
}
