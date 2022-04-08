package com.example.app_hola.ObjectForApp;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app_hola.HomeActivity;
import com.example.app_hola.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.List;

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
