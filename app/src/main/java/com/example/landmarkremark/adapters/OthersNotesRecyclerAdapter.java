package com.example.landmarkremark.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.landmarkremark.R;
import com.example.landmarkremark.model.UserAddedNote;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
Adapter class to display the notes entered by all users in OthersNote Fragment
 */

public class OthersNotesRecyclerAdapter extends RecyclerView.Adapter<OthersNotesRecyclerAdapter.NotesViewHolder> implements Filterable {

    private List<UserAddedNote> userAddedNotes;
    private Context context;

    private List<UserAddedNote> temp = new ArrayList<>();//temp variable to hold original data

    public OthersNotesRecyclerAdapter(Context context, List<UserAddedNote> userAddedNotes) {
        this.userAddedNotes = userAddedNotes;
        this.context = context;
        temp = userAddedNotes;
    }


    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new NotesViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_othersnote_recycler_view, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

        holder.user.setText("Added By: " + userAddedNotes.get(position).getUser_name() + " (" + userAddedNotes.get(position).getUser_Email() + ") ");
        holder.location.setText("At Location: " + userAddedNotes.get(position).getAddress());
        holder.message.setText("Note: " + userAddedNotes.get(position).getAddedNote());

    }

    @Override
    public int getItemCount() {
        return userAddedNotes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //This override methods filters the list based on the string entered in the search view
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                final FilterResults onReturn = new FilterResults();
                final List<UserAddedNote> results = new ArrayList<UserAddedNote>();

                if (temp == null)
                    temp = userAddedNotes;

                if (constraint != null) {
                    String filteredText = constraint.toString().toLowerCase(Locale.getDefault());
                    if (temp != null && temp.size() > 0) {
                        //check if the entered char in searchwidget matches the values in the user note
                        for (final UserAddedNote filteredNotes : temp) {
                            if (filteredNotes.getUser_name().toLowerCase(Locale.getDefault()).contains(filteredText) ||
                                    filteredNotes.getAddedNote().toLowerCase(Locale.getDefault()).contains(filteredText) ||
                                    filteredNotes.getAddress().toLowerCase(Locale.getDefault()).contains(filteredText)) {
                                results.add(filteredNotes);
                            }
                        }
                    }
                    onReturn.values = results;
                }
                return onReturn;
            }


            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userAddedNotes = (List<UserAddedNote>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class NotesViewHolder extends RecyclerView.ViewHolder {

        TextView user,
                location,
                message;

        private NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.text_user);
            location = itemView.findViewById(R.id.text_location);
            message = itemView.findViewById(R.id.text_notes);
        }


    }


}