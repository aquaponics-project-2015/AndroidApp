package com.example.groupprojectapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

/**
 * Created by Dennis on 23/03/2015.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    Context context;
    String[] tabs = {"Water Level","pH","Temperature"};
    public TabsPagerAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                Toast.makeText(context,"Fragment 1",Toast.LENGTH_LONG);
                return WaterLevelFragment.newInstance(position+1);
            case 1:
                Toast.makeText(context,"Fragment 2",Toast.LENGTH_LONG);
                return PHFragment.newInstance(position+2);
            case 2:
                Toast.makeText(context,"Fragment 3",Toast.LENGTH_LONG);
                return TemperatureFragment.newInstance(position+3);

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }
}
