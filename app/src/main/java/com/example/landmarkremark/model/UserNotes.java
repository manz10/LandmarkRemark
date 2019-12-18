package com.example.landmarkremark.model;

import java.util.List;

/*
This class model holds an array of notes stored by specific user
 */
public class UserNotes {

    private String id;
    private String userName;
    private String userEmail;
    private List<Note> notes;

    public UserNotes(String id,String userName, String userEmail, List<Note> notes) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.notes = notes;
    }

    public UserNotes() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
