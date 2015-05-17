package com.example.groupprojectapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dennis on 23/03/2015.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    Context context;
    String[] tabs = {"Water Level","pH","Temperature"};
    public Map<Integer,Fragment> fragmentMap = new HashMap<Integer, Fragment>();
    public TabsPagerAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                Toast.makeText(context, "Fragment 1", Toast.LENGTH_LONG);
                WaterLevelFragment wlf = WaterLevelFragment.newInstance(position + 1);
                fragmentMap.put(position,wlf);
                return wlf;
            case 1:
                Toast.makeText(context,"Fragment 2",Toast.LENGTH_LONG);
                PHFragment phf = PHFragment.newInstance(position+2);
                fragmentMap.put(position,phf);
                return phf;
            case 2:
                Toast.makeText(context,"Fragment 3",Toast.LENGTH_LONG);
                TemperatureFragment tf = TemperatureFragment.newInstance(position+3);
                fragmentMap.put(position,tf);
                return tf;

        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        switch(position){
            case 0:

                fragmentMap.remove(position);

            case 1:

                fragmentMap.remove(position);

            case 2:

                fragmentMap.remove(position);


        }
    }

    public Fragment getFragment(int index){
        return fragmentMap.get(index);
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
