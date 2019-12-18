package com.example.landmarkremark.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.landmarkremark.R;
import com.example.landmarkremark.adapters.OthersNotesRecyclerAdapter;
import com.example.landmarkremark.helper.Constants;
import com.example.landmarkremark.model.Note;
import com.example.landmarkremark.model.UserAddedNote;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OthersNoteFragment extends Fragment implements SearchView.OnQueryTextListener {

    private DatabaseReference databaseReference;
    private List<UserAddedNote> userAddedNotesList;

    private FirebaseUser currentUser;  //to check if the user is currently logged in or not

    private OthersNotesRecyclerAdapter adapter;

    public OthersNoteFragment() {
        // Required empty public constructor
    }

    public static OthersNoteFragment newInstance() {
        return new OthersNoteFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_others_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchView = view.findViewById(R.id.searchView);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if (userAddedNotesList != null) {
            adapter = new OthersNotesRecyclerAdapter(getActivity(), userAddedNotesList);
            recyclerView.setAdapter(adapter);
        }

        searchView.setOnQueryTextListener(this);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userAddedNotesList = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (currentUser != null)
            databaseReference.addValueEventListener(valueEventListener);    //read data from Firebase Realtime Database

    }


    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            userAddedNotesList.clear();
            //get list of data for all users
            for (DataSnapshot mainSnapshot : dataSnapshot.getChildren()) {

                //mapping to the child of the data set
                for (DataSnapshot userSnapshot : mainSnapshot.getChildren()) {

                    String userId = userSnapshot.child("id").getValue(String.class);
                    String name = userSnapshot.child("userName").getValue(String.class);
                    String user_email = userSnapshot.child("userEmail").getValue(String.class);

                    DataSnapshot noteSnapshot = userSnapshot.child("notes");
                    //mapping to list of note items for individual users
                    for (DataSnapshot notes : noteSnapshot.getChildren()) {

                        Note note = notes.getValue(Note.class);

                        //add the individual user with its note
                        UserAddedNote addedNote = new UserAddedNote(userId, name, user_email, note.getAddress(), note.getMessage());

                        userAddedNotesList.add(addedNote);
                    }
                }

            }
            adapter.notifyDataSetChanged();     //notify the recycleview of the data change to update the UI
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        //implement the method if user enters in a search widget
        adapter.getFilter().filter(newText);
        return true;
    }
}
