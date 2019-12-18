package com.example.landmarkremark.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.landmarkremark.R;
import com.example.landmarkremark.adapters.MyNotesRecyclerAdapter;
import com.example.landmarkremark.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyNotesFragment extends Fragment {

    private MyNotesRecyclerAdapter adapter;

    private FirebaseUser currentUser;
    private List<Note> noteList;

    public MyNotesFragment() {
        // Required empty public constructor
    }

    public static MyNotesFragment newInstance() {
        return new MyNotesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_notes, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            Query query = databaseReference.child("Users").orderByChild("id").equalTo(currentUser.getUid());  //check if there is a data under given userID
            query.addValueEventListener(valueEventListener);        //call a listener to retrieve data from database
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        if(noteList != null){
            adapter = new MyNotesRecyclerAdapter(getActivity(), noteList);
            recyclerView.setAdapter(adapter);
        }

    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            noteList.clear();

            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                DataSnapshot note = userSnapshot.child("notes");        //Access the data unders "notes" node in the database

                for(DataSnapshot snap:note.getChildren()){
                    Note note1 = snap.getValue(Note.class);
                    noteList.add(note1);
                }

            }
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}

