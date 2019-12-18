package com.example.landmarkremark.model;


/*
This model is to hold one note of one user which will be displayed in the Note Fragments
 */
public class UserAddedNote {

    private String uId,
            user_name,
            user_Email,
            address,
            addedNote;

    public UserAddedNote(String uId, String user_name, String user_Email, String address, String addedNote) {
        this.uId = uId;
        this.user_name = user_name;
        this.user_Email = user_Email;
        this.address = address;
        this.addedNote = addedNote;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_Email() {
        return user_Email;
    }

    public void setUser_Email(String user_Email) {
        this.user_Email = user_Email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddedNote() {
        return addedNote;
    }

    public void setAddedNote(String addedNote) {
        this.addedNote = addedNote;
    }
}
