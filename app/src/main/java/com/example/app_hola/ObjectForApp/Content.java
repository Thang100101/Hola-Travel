package com.example.app_hola.ObjectForApp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class Content implements Serializable {
    ImageContent imageContent;
    String mainContent, Title, Location, ID;
    User user;
    String date;
    ArrayList<Like> Likes = new ArrayList<Like>();
    ArrayList<ImageContent> listImage = new ArrayList<ImageContent>();
    static DatabaseReference dataRef;
    public Content() {
        dataRef = FirebaseDatabase.getInstance().getReference();
    }

    public Content(ImageContent imageContent, String mainContent, String title, User userID, String ID, String date) {
        this.imageContent = imageContent;
        this.mainContent = mainContent;
        this.Title = title;
        this.user = userID;
        this.ID = ID;
        this.date = date;
        dataRef = FirebaseDatabase.getInstance().getReference();
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
    public User getUser() { return user; }
    public String getLocation() { return Location; }
    public String getID() { return ID; }
    public ArrayList<ImageContent> getListImage() {
        return listImage;
    }

    public ArrayList<Like> getLikes() {
        return Likes;
    }
    ////SET


    public void setListImage(ArrayList<ImageContent> listImage) {
        this.listImage = listImage;
    }

    public void setImageContent(ImageContent imageContent) {
        this.imageContent = imageContent;
    }

    public void setMainContent(String mainContent) {
        this.mainContent = mainContent;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void setLikes(ArrayList<Like> likes) {
        Likes = likes;
    }

    public ArrayList<Content> getAllContent(){
        ArrayList<Content> listContent = new ArrayList<>();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        return  listContent;
    }


    public static void setContent(Content content, String id){
        dataRef.child("Contents").child(id).setValue(content);
    }

}
