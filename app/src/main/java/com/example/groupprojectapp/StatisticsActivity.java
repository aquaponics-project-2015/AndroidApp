package com.example.groupprojectapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.communication.APIEndPoints;
import com.example.slidingtablayout.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class StatisticsActivity extends ActionBarActivity implements ActionBar.TabListener {
    private ViewPager pager;
    private TabsPagerAdapter adapter;
    private ActionBar actionBar;
    private SlidingTabLayout slidingTabLayout;
    Context context;
    public JSONObject object;
    public static final String TAG = StatisticsActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        context=this;
        pager = (ViewPager) findViewById(R.id.viewpager);
        slidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
        adapter = new TabsPagerAdapter(context,getSupportFragmentManager());
        actionBar = getSupportActionBar();

        pager.setAdapter(adapter);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(pager);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.darkBlue));

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        scheduleAlarm(1, APIEndPoints.apiUrl, APIEndPoints.getSystemReadings, "");


    }

    public void scheduleAlarm(int id,String apiurl,String path,String parameters) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        StringBuilder builder = new StringBuilder();
        builder.append(apiurl);
        builder.append(path);
        builder.append(parameters);
        String URL = builder.toString();
        Log.d(TAG, "Url: " + URL);

        intent.putExtra("url", URL);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
        int intervalMillis = 120000; // 5 seconds
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                String json = intent.getStringExtra("json");
                int index = pager.getCurrentItem();
                adapter = ((TabsPagerAdapter)pager.getAdapter());
                //Fragment fragment = null;
                switch (index){
                    case 0:
                        WaterLevelFragment wlfragment = (WaterLevelFragment)adapter.getFragment(index);
                        try {
                            Object object = new JSONTokener(json).nextValue();
                            if (object instanceof JSONObject){
                                JSONObject jsonobject = new JSONObject(json);
                                wlfragment.onSuccess(1, jsonobject);
                            }else if (object instanceof JSONArray){
                                JSONArray array = new JSONArray(json);
                                wlfragment.onSuccess(1, array);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        PHFragment phfragment = (PHFragment)adapter.getFragment(index);
                        try {
                            Object object = new JSONTokener(json).nextValue();
                            if (object instanceof JSONObject){
                                JSONObject jsonobject = new JSONObject(json);
                                phfragment.onSuccess(1, jsonobject);
                            }else if (object instanceof JSONArray){
                                JSONArray array = new JSONArray(json);
                                phfragment.onSuccess(1,array);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        TemperatureFragment tfragment = (TemperatureFragment)adapter.getFragment(index);
                        try {
                            Object object = new JSONTokener(json).nextValue();
                            if (object instanceof JSONObject){
                                JSONObject jsonobject = new JSONObject(json);
                                tfragment.onSuccess(1, jsonobject);
                            }else if (object instanceof JSONArray){
                                JSONArray array = new JSONArray(json);
                                tfragment.onSuccess(1,array);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }





            }
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
