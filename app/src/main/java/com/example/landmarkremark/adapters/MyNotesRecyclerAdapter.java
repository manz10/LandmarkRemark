package com.example.landmarkremark.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.landmarkremark.model.Note;
import com.example.landmarkremark.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
Adapter class to display the notes entered by current user in MyNotes Fragment
 */

public class MyNotesRecyclerAdapter extends RecyclerView.Adapter<MyNotesRecyclerAdapter.NotesViewHolder> {

    private List<Note> notes;
    private Context context;

    public MyNotesRecyclerAdapter(Context context, List<Note> notes) {
        this.notes = notes;
        this.context = context;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new NotesViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_mynotes_recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

        //update the UI elements
        holder.location.setText("At Location: "+notes.get(position).getAddress());
        holder.message.setText("Note: "+notes.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class NotesViewHolder extends RecyclerView.ViewHolder {

        TextView location,
                        message;

        private NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.text_location);
            message = itemView.findViewById(R.id.text_notes);
        }


    }


}