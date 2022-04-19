package com.example.app_hola.ObjectForApp;

public class NotificationContent {
    String ID,userID, contentID, readerName, Type, mainContent, Date;
    int countContact;
    ImageContent img;
    boolean Read;
    public NotificationContent() {
    }

    public NotificationContent(String userID, String contentID, String readerName, String type, int countContact, boolean read) {
        this.userID = userID;
        this.contentID = contentID;
        this.readerName = readerName;
        Type = type;
        this.countContact = countContact;
        this.Read=read;
//        settingMainContent();
    }
    private void settingMainContent(){
        if(Type.equals("like"))
        {
            String s = readerName+ " và "+countContact+" người khác đã thích bài viết của bạn";
            mainContent=s;
        }
        else
        {
            String s = readerName+ " đã bình luận về bài viết của bạn";
            mainContent=s;
        }
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerID) {
        this.readerName = readerID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMainContent() {
        return mainContent;
    }

    public void setMainContent(String mainContent) {
        this.mainContent = mainContent;
    }

    public int getCountContact() {
        return countContact;
    }

    public void setCountContact(int countContact) {
        this.countContact = countContact;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public ImageContent getImg() {
        return img;
    }

    public void setImg(ImageContent img) {
        this.img = img;
    }

    public boolean isRead() {
        return Read;
    }

    public void setRead(boolean read) {
        Read = read;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
