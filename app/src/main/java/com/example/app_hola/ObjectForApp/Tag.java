package com.example.app_hola.ObjectForApp;

public class Tag {
    String ID, Name;

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
}
