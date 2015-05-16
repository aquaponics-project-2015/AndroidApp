package com.example.groupprojectapp;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class DataService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String TAG = DataService.class.getSimpleName();
    private static final String COMM_ID = "communicationId";
    private static final String URL = "url";
    private static final String TYPE = "type";
   // public int type = 0;
    public int SIZE =5;
    SharedPreferences prefs;

    public static final String INTENT_ACTION = "com.example.groupprojectapp";




    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DataService.class);
        //intent.setAction(ACTION_FOO);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, DataService.class);
        //intent.setAction(ACTION_BAZ);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public DataService() {
        super("DataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (intent != null) {
           String intenturl = intent.getStringExtra(URL);
           int type = intent.getIntExtra(TYPE,0);

            String json;
            try{
                StrictMode.ThreadPolicy policy = new StrictMode.
                        ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                java.net.URL url = new URL(intenturl);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream is = url.openStream();

                try {

                    InputStreamReader isReader = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isReader);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine())!=null){
                        sb.append(line+"\n");

                    }
                    is.close();
                    json = sb.toString();
                    Log.d(TAG, "json string: " + json);
                    //return json;
                    Intent i = new Intent(INTENT_ACTION);
                    i.putExtra("json",json);
                    i.putExtra("type",type);
                    i.putExtra("resultCode", Activity.RESULT_OK);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(i);
                    sendNotif(json,type);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "ERROR",e);
                    e.printStackTrace();
                    //return errorString;
                }catch(IOException e){
                    Log.d(TAG, "ERROR",e);
                    e.printStackTrace();
                   // return errorString;
                }catch(Exception e){
                    Log.d(TAG, "ERROR",e);
                    e.printStackTrace();
                   // return errorString;

                }

            }catch(Exception e){
                Log.d(TAG, "ERROR",e);
                e.printStackTrace();
                //return  errorString;

            }
        }
    }

    public void sendNotif(String json,int type){
        // prepare intent which is triggered if the
        // notification is selected
        JSONObject jsonobject;
        JSONArray array;
        try {
            Object object = new JSONTokener(json).nextValue();
            if (object instanceof JSONObject){
                switch (type){
                    case 1:
                        Log.d(TAG,"Analyze System");
                        jsonobject = new JSONObject(json);

                        double []temps = new double[SIZE];
                        double []phs = new double[SIZE];


                        JSONArray data = jsonobject.getJSONArray("data");
                        int length = data.length();
                        long latesttime = data.getJSONObject(length-1).getLong("datetime");
                        setNetStatus(new Date(latesttime));


                        for(int i = ((length-1)-SIZE); i<length-1; i++){
                            JSONObject reading = data.getJSONObject(i);
                            int index = i - ((length-1)-SIZE);
                            double water = reading.getDouble("waterLevel");

                            double ph = reading.getDouble("ph");
                            double temp = reading.getDouble("temperature");
                            temps[index] = temp;
                            phs[index] = ph;

                        }

                        // analyzeWater(water);
                        analyzeTemp(temps);
                        // analyzepH(phs);
                    break;
                    case 2:
                        Log.d(TAG,"Analyze Pump");
                        jsonobject = new JSONObject(json);



                        JSONArray data2 = jsonobject.getJSONArray("data");
                        int length2 = data2.length();
                        JSONObject pumpReading = data2.getJSONObject(length2-1);
                        boolean status = pumpReading.getBoolean("status");
                        Date datetime = new Date(pumpReading.getLong("datetime"));
                        analyzeWaterLevel(status, datetime);



                }

            }else if (object instanceof JSONArray){
                array = new JSONArray(json);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    public void setNetStatus(Date datetime){
        Date current = new Date();
        Log.d(TAG, "current date: " + current.toString());
        Log.d(TAG, "timestamp date: " + datetime.toString());

        long diffMinutes = getDateDiff(datetime,current,TimeUnit.MINUTES);
        Log.d(TAG,"Time difference: "+diffMinutes);
        if(diffMinutes>30){

            //image.setImageResource(R.drawable.offline);
            //text.setText("Offline");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("netstat",false);
            editor.commit();




        }else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("netstat", true);
            editor.commit();
            Log.d(TAG,"Net status is now: "+prefs.getBoolean("netstat",false));
        }


    }
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public void analyzeTemp(double[] temps){
        boolean stat = prefs.getBoolean("netstat",false);
        if(stat == true) {
            double sum = 0;
            for (int i = 0; i < SIZE; i++) {
                sum = sum + temps[i];

            }
            double avg = sum / SIZE;
            if (avg < 24.0) {
                Intent notifintent = new Intent(this, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, notifintent, 0);

                // build notification
                // the addAction re-use the same intent to keep the example short
                Notification n = new Notification.Builder(this)
                        .setContentTitle("System Temperature Inadequate")
                        .setContentText("Temperature may be too low")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .build();


                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);

            } else if (avg > 27.0) {
                Intent notifintent = new Intent(this, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, notifintent, 0);

                // build notification
                // the addAction re-use the same intent to keep the example short
                Notification n = new Notification.Builder(this)
                        .setContentTitle("System Temperature Inadequate")
                        .setContentText("Temperature may be too high")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .build();


                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);

            }
        }else {

        }
    }

    public void analyzeWaterLevel(boolean status,Date datetime){
        boolean stat = prefs.getBoolean("netstat",false);
        if(stat == true) {
            if (status == false) {
                Date current = new Date();


                long diffMinutes= getDateDiff(datetime,current,TimeUnit.MINUTES);

                if (diffMinutes > 30) {
                    Intent notifintent = new Intent(this, MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(this, 0, notifintent, 0);

                    // build notification
                    // the addAction re-use the same intent to keep the example short
                    Notification n = new Notification.Builder(this)
                            .setContentTitle("System Water Level Inadequate")
                            .setContentText("The water level may be too low")
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .build();


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(0, n);
                }

            }
        }else {

        }

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
