package com.example.groupprojectapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver2 extends BroadcastReceiver {
    public static final int REQUEST_CODE= 2345;
    public AlarmReceiver2() {
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
