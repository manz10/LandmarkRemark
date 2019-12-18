package com.example.landmarkremark.helper;


import com.example.landmarkremark.model.Note;
import com.example.landmarkremark.model.UserNotes;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/*
This class handle the Firebase database operations
 */
public class DatabaseHelper {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private AlertDialog dialog;

    public DatabaseHelper(FirebaseAuth mAuth, DatabaseReference databaseReference, AlertDialog dialog) {
        this.mAuth = mAuth;
        this.databaseReference = databaseReference;
        this.dialog = dialog;
    }

    //this method add the data to Firebase database
    public void addNoteToDB(String enteredNote, LatLng currentLocation, String currentAddress) {

        final String uId = mAuth.getCurrentUser().getUid();                 //get the userId of the current user logged in using current FirebaseAuth instance

        final String user_Name = mAuth.getCurrentUser().getDisplayName();
        final String user_Email = mAuth.getCurrentUser().getEmail();

        final Note note = new Note(enteredNote,
                currentLocation.latitude,
                currentLocation.longitude,
                currentAddress);


        Query query = databaseReference.child("Users").orderByChild("id").equalTo(uId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //add every data if the database under that user doesn't exist
                if (!dataSnapshot.exists()) {
                    List<Note> notes = new ArrayList<Note>();
                    notes.add(note);
                    UserNotes userNotes = new UserNotes(uId, user_Name, user_Email, notes);
                    databaseReference.child("Users").push().setValue(userNotes);    //invoking push() method to add the list of data
                    dialog.cancel();
                    return;
                }
                //if the user database exists, just update the notes value
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String key = snap.getKey();
                    databaseReference.child("Users").child(key).child("notes").push().setValue(note);
                }
                dialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.cancel();
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
    }

}

