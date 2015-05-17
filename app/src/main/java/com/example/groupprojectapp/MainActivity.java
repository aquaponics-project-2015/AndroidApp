package com.example.groupprojectapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communication.APIEndPoints;
import com.example.communication.Communication;
import com.example.communication.CommunicationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Date;


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
    SharedPreferences prefs;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);

        waterLevel = (TextView)findViewById(R.id.water_level_label);
        phLevel = (TextView)findViewById(R.id.ph_label);
        temperature = (TextView)findViewById(R.id.temperature_label);

        //comm.send(1,this, APIEndPoints.apiUrl,APIEndPoints.getSystemReadings,"");
        scheduleAlarm(1,APIEndPoints.apiUrl,APIEndPoints.getSystemReadings,"");
        scheduleAlarm(2, APIEndPoints.apiUrl, APIEndPoints.getPumpReadings, "");
        setNetStatus();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();

    }

    public void setNetStatus() {
        boolean stat = prefs.getBoolean("netstat", false);
        if(stat==false){
            View v = getSupportActionBar().getCustomView();
            ImageView image = (ImageView)v.findViewById(R.id.status_image);
            TextView text = (TextView)v.findViewById(R.id.status_text);
            image.setImageResource(R.drawable.offline);
            text.setText("Offline");
        }else{
            View v = getSupportActionBar().getCustomView();
            ImageView image = (ImageView)v.findViewById(R.id.status_image);
            TextView text = (TextView)v.findViewById(R.id.status_text);
            image.setImageResource(R.drawable.online);
            text.setText("Online");
        }
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
        intent.putExtra("type", id);

        if(id ==1) {
            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Setup periodic alarm every 5 seconds
            long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
            int intervalMillis = 120000; // 5 seconds
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);
        }else{
            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver2.REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Setup periodic alarm every 5 seconds
            long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
            int intervalMillis = 120000; // 5 seconds
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);

        }

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
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DataService.INTENT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
    }

    // Define the callback for what to do when data is received
    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {

                String json = intent.getStringExtra("json");
                int type = intent.getIntExtra("type",0);
                Log.d(TAG,"Response type: "+type);
                //Toast.makeText(context, "Received Update", Toast.LENGTH_SHORT).show();

                    try {
                        Object object = new JSONTokener(json).nextValue();
                        if (object instanceof JSONObject){
                            JSONObject jsonobject = new JSONObject(json);
                            onSuccess(type, jsonobject);
                        }else if (object instanceof JSONArray){
                            JSONArray array = new JSONArray(json);
                            onSuccess(type, array);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 1:
                Intent i = new Intent(this,StatisticsActivity.class);
                startActivity(i);
            break;
            case 2:
                Intent i2 = new Intent(this,FishActivity.class);
                startActivity(i2);

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
                    JSONObject reading = array.getJSONObject(length - 1);
                    double water = reading.getDouble("waterLevel");
                    double ph = reading.getDouble("ph");
                    double temp = reading.getDouble("temperature");
                    Date datetime = new Date(reading.getLong("datetime"));

                    waterLevel.setText(Double.toString(water));
                    phLevel.setText(Double.toString(ph));
                    //temperature.setText(Double.toString(temp));
                    setTemperatureText(temp);
                    setpHText(ph);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            break;
            case 2:
                /*JSONArray data2 = null;
                try {
                    data2 = object.getJSONArray("data");
                    int length2 = data2.length();
                    JSONObject pumpReading = data2.getJSONObject(length2-1);
                    boolean status = pumpReading.getBoolean("status");
                    Date datetime = new Date(pumpReading.getLong("datetime"));
                    if(status == false){
                        Date current = new Date();

                        long diff = current.getTime() - datetime.getTime();
                        long diffSeconds = diff / 1000 % 60;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffHours = diff / (60 * 60 * 1000);
                        int diffInDays = (int) ((current.getTime() - datetime.getTime()) / (1000 * 60 * 60 * 24));
                        View v = getSupportActionBar().getCustomView();
                        ImageView image = (ImageView)v.findViewById(R.id.status_image);
                        TextView text = (TextView)v.findViewById(R.id.status_text);
                        if(diffMinutes>30){

                            image.setImageResource(R.drawable.offline);
                            text.setText("Offline");


                        }
                    }else{


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/





        }


    }

    @Override
    public void onError(int communicationId, String message) {


    }
    public void setpHText(double ph){
        double minph = Double.parseDouble(prefs.getString("minph","0.0"));
        double maxph = Double.parseDouble(prefs.getString("maxph", "14.0"));
        double maxdiff = Math.abs(ph - maxph);
        double mindiff = Math.abs(ph - minph);
        if(ph < mindiff|| ph >maxph){
            if(maxdiff > 3 || mindiff >3) {
                phLevel.setTextColor(getResources().getColor(R.color.red));
            }else{
                phLevel.setTextColor(getResources().getColor(R.color.yellow));
                }
        }
        phLevel.setText(Double.toString(ph));


    }
    public void setTemperatureText(double temp){
        if(temp < 24.0 || temp > 27.0){
            temperature.setTextColor(getResources().getColor(R.color.red));
        }
        temperature.setText(Double.toString(temp));


    }
}
