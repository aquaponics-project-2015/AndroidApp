package com.example.groupprojectapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.communication.APIEndPoints;
import com.example.communication.Communication;
import com.example.communication.CommunicationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,CommunicationResponse {
    DrawerLayout drawerLayout;
    ListView navigation;
    String[] navigationOptions;
    Context context;
    Communication comm;
    ActionBarDrawerToggle drawerListener;
    TextView waterLevel;
    TextView phLevel;
    TextView temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        comm = new Communication(context);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigation = (ListView)findViewById(R.id.navigation);
        navigationOptions = getResources().getStringArray(R.array.navigation_options);
        navigation.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,android.R.id.text1,navigationOptions));
        drawerListener = new ActionBarDrawerToggle(this,drawerLayout,0,0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerListener);
        navigation.setOnItemClickListener(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        waterLevel = (TextView)findViewById(R.id.water_level_label);
        phLevel = (TextView)findViewById(R.id.ph_label);
        temperature = (TextView)findViewById(R.id.temperature_label);

        comm.send(1,this, APIEndPoints.apiUrl,APIEndPoints.getSystemReadings,"");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (drawerListener.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.refresh){
            comm.send(1,this, APIEndPoints.apiUrl,APIEndPoints.getSystemReadings,"");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 1:
                Intent i = new Intent(this,StatisticsActivity.class);
                startActivity(i);
        }
    }

    @Override
    public void onSuccess(int communicationId, JSONArray array) {

    }

    @Override
    public void onSuccess(int communicationId, JSONObject object) {
        switch (communicationId){
            case 1:
                try {
                    JSONArray array = object.getJSONArray("data");
                    int length = array.length();
                    JSONObject reading = array.getJSONObject(length-1);
                    double water = reading.getDouble("waterLevel");
                    double ph = reading.getDouble("ph");
                    double temp = reading.getDouble("temperature");

                    waterLevel.setText(Double.toString(water));
                    phLevel.setText(Double.toString(ph));
                    temperature.setText(Double.toString(temp));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

    }

    @Override
    public void onError(int communicationId, String message) {

    }
}
