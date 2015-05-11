package com.example.groupprojectapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    public static final int REQUEST_CODE= 1234;


    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getStringExtra("url");
        int id = intent.getIntExtra("type",0);
        Intent i = new Intent(context,DataService.class);
        i.putExtra("url",url);
        i.putExtra("type",id);


        context.startService(i);


    }


}