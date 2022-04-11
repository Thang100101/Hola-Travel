package com.example.app_hola.ObjectForApp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class Content implements Serializable {
    ImageContent imageContent;
    String mainContent, Title, Location, ID;
    User user;
    String date;
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

    public ArrayList<Content> getAllContent(){
        ArrayList<Content> listContent = new ArrayList<>();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        return  listContent;
    }

    private static int check;
    public static void setContent(Content content){
        check=0;
        dataRef.child("Contents").push().setValue(content);
        dataRef.child("Contents").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (check==0) {
                    check++;
                    content.setID(snapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
