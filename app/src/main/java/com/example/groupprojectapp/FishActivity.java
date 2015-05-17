package com.example.groupprojectapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entity.Fish;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


public class FishActivity extends ActionBarActivity  {
    ListView listView;
    Button button;
    SharedPreferences prefs;
    Context context;
    List<Fish> fishes = new ArrayList<Fish>();
    //{"Gouramis","Goldfish","Discus","Mollies"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("minph","0.0");
        editor.putString("maxph","14.0");
        editor.commit();
        fishes.add(new Fish(7.0, 3.0, "Gouramis"));
        fishes.add(new Fish(7.5, 6.5, "Goldfish"));
        fishes.add(new Fish(8.6, 7.8, "Tilapia"));
        fishes.add(new Fish(7.5, 5.0, "Trout"));
        fishes.add(new Fish(9.0, 6.5, "Carp"));
        fishes.add(new Fish(7.5, 6.5, "Goldfish"));
        listView = (ListView)findViewById(R.id.listView);
        button = (Button)findViewById(R.id.save);
        FishAdapter adapter = new FishAdapter(fishes,this);
        context = this;
        listView.setAdapter(adapter);


        ActionBar actionBar = getSupportActionBar();



        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = prefs.edit();
                List<Fish> fishes = ((FishAdapter)listView.getAdapter()).fishes;
                double minph = findMinpH(fishes);
                double maxph = findMinpH(fishes);
                editor.putString("minph",Double.toString(minph));
                editor.putString("maxph", Double.toString(maxph));
                editor.commit();
                Intent i = new Intent(context,MainActivity.class);
                startActivity(i);
            }
        });

    }

    public double findMinpH(List<Fish> fishes){
        double min =0.0;
        for (int i=0;i<fishes.size();i++){
            if (i == 0){
                min = fishes.get(i).getMinph();
            }else {
                if (fishes.get(i).getMinph()>min){
                    min = fishes.get(i).getMinph();
                }
            }
        }
        return min;
    };

    public double findMaxpH(List<Fish> fishes){
        double max =0.0;
        for (int i=0;i<fishes.size();i++){
            if (i == 0){
                max = fishes.get(i).getMaxph();
            }else {
                if (fishes.get(i).getMaxph()<max){
                    max = fishes.get(i).getMaxph();
                }
            }
        }
        return max;
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == android.R.id.home){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FishAdapter extends BaseAdapter{
        public List<Fish> fishes;
        Context context;

        public FishAdapter(List<Fish> fishes,Context context) {
            this.fishes = fishes;
            this.context = context;
        }

        @Override
        public int getCount() {
            return fishes.size();
        }

        @Override
        public Object getItem(int i) {
            return fishes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final String fish = fishes.get(i).getName();
            final int pos = i;
            View v = inflater.inflate(R.layout.list_item1, null);
            TextView tv = (TextView)v.findViewById(R.id.fish_name);
            CheckBox cb = (CheckBox)v.findViewById(R.id.checkbox);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b==true) {
                       fishes.get(pos).setChecked();
                    }else{
                        fishes.get(pos).setUnchecked();
                    }
                }
            });
            tv.setText(fish);


            return v;
        }
    }


}
