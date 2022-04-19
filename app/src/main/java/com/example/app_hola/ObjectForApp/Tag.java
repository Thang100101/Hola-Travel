package com.example.app_hola.ObjectForApp;

import android.app.Activity;
import android.content.Context;

import com.example.app_hola.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class Tag implements Serializable {
    String ID, Name;
    private Context context;

    public Tag(String ID, String name) {
        this.ID = ID;
        Name = name;
    }

    public Tag() {
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setContext(Context context){ this.context=context;}

    @Override
    public String toString() {
        switch (this.ID){
            case "tag1":
                return context.getResources().getString(R.string.tag1);
            case "tag2":
                return context.getResources().getString(R.string.tag2);
            case "tag3":
                return context.getResources().getString(R.string.tag3);
            case "tag4":
                return context.getResources().getString(R.string.tag4);
            case "tag5":
                return context.getResources().getString(R.string.tag5);
            case "tag6":
                return context.getResources().getString(R.string.tag6);
            case "tag7":
                return context.getResources().getString(R.string.tag7);
            case "tag8":
                return context.getResources().getString(R.string.tag8);
        }
        return this.Name;
    }

    public static void createListTag()
    {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        Tag tag1 = new Tag();
        tag1.setID("tag1");
        tag1.setName("Đồ ăn");
        dataRef.child("Tags").child(tag1.getID()).setValue(tag1);
        Tag tag2 = new Tag();
        tag2.setID("tag2");
        tag2.setName("Tips");
        dataRef.child("Tags").child(tag2.getID()).setValue(tag2);
        Tag tag3 = new Tag();
        tag3.setID("tag3");
        tag3.setName("Kinh nghiệm");
        dataRef.child("Tags").child(tag3.getID()).setValue(tag3);
        Tag tag4 = new Tag();
        tag4.setID("tag4");
        tag4.setName("Khách sạn");
        dataRef.child("Tags").child(tag4.getID()).setValue(tag4);
        Tag tag5 = new Tag();
        tag5.setID("tag5");
        tag5.setName("Đà lạt");
        dataRef.child("Tags").child(tag5.getID()).setValue(tag5);
        Tag tag6 = new Tag();
        tag6.setID("tag6");
        tag6.setName("Vũng Tàu");
        dataRef.child("Tags").child(tag6.getID()).setValue(tag6);
        Tag tag7 = new Tag();
        tag7.setID("tag7");
        tag7.setName("Nha Trang");
        dataRef.child("Tags").child(tag7.getID()).setValue(tag7);
        Tag tag8 = new Tag();
        tag8.setID("tag8");
        tag8.setName("Phú Quốc");
        dataRef.child("Tags").child(tag8.getID()).setValue(tag8);

    }
}
