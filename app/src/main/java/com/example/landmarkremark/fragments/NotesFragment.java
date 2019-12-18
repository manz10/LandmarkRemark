package com.example.landmarkremark.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.landmarkremark.R;
import com.example.landmarkremark.adapters.TabsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class NotesFragment extends Fragment {

    private TabLayout tabs;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        tabs = root.findViewById(R.id.tabs);
        viewPager = root.findViewById(R.id.view_pager);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //implement the TabsPagerAdapter to setup the tabs
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getContext(), getFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter );
        tabs.setupWithViewPager(viewPager);

    }

}