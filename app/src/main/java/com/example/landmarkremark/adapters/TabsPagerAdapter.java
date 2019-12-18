package com.example.landmarkremark.adapters;

import android.content.Context;

import com.example.landmarkremark.fragments.MyNotesFragment;
import com.example.landmarkremark.fragments.OthersNoteFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/*
Adapter class to implement the TabLayout to setup My Notes and Notes by Others Tabs
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    private static final String[] TAB_TITLE = new String[]{"My Notes", "Notes by Others"};

    private Context mContext;

    public TabsPagerAdapter(Context mContext, FragmentManager fm) {
        super(fm);
        this.mContext = mContext;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MyNotesFragment.newInstance();       //set the destination of first tab
            case 1:
                return OthersNoteFragment.newInstance();        //set the destination of second tab
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TAB_TITLE.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLE[position];
    }
}
