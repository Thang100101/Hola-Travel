package com.example.app_hola.ObjectForApp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Content {
    ImageContent imageContent;
    String mainContent, Title, userID, Location, ID;
    String date;
    ArrayList<ImageContent> listImage = new ArrayList<ImageContent>();

    public Content() {
    }

    public Content(ImageContent imageContent, String mainContent, String title, String userID, String ID, String date) {
        this.imageContent = imageContent;
        this.mainContent = mainContent;
        this.Title = title;
        this.userID = userID;
        this.ID = ID;
        this.date = date;
    }

    ///Thêm ảnh

    public void addImage(ImageContent image)
    {
        listImage.add(image);
    }
    public void removeImage(ImageContent image)
    {
        listImage.remove(image);
    }


    ////GET
    public ImageContent getImageContent() {
        return imageContent;
    }
    public String getMainContent() {
        return mainContent;
    }
    public String getDate() {
        return date;
    }
    public String getTitle() { return Title; }
    public String getUserID() { return userID; }
    public String getLocation() { return Location; }
    public String getID() { return ID; }
    ////SET


    public void setImageContent(ImageContent imageContent) {
        this.imageContent = imageContent;
    }

    public void setMainContent(String mainContent) {
        this.mainContent = mainContent;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Content> getAllContent(){
        ArrayList<Content> listContent = new ArrayList<>();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        return  listContent;
    }

    private int check=0;
    public String setContent(){
        return "";
    }
}
