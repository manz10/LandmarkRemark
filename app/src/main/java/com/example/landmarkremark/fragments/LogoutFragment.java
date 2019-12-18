package com.example.landmarkremark.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.landmarkremark.R;
import com.example.landmarkremark.activities.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogoutFragment extends Fragment {


    public LogoutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if( context instanceof MainActivity){
            //call signout method from the MainActivity to signout the current user
            ((MainActivity) context).signOut();
        }
    }



}
