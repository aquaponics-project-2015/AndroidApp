package com.example.groupprojectapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.communication.APIEndPoints;
import com.example.communication.Communication;
import com.example.communication.CommunicationResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Map;



public class WaterLevelFragment extends Fragment implements CommunicationResponse{
    private LineChart lineChart;
    private int page;
    public static final String TAG = WaterLevelFragment.class.getSimpleName();


    Communication comm;

    public static WaterLevelFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE",page);
        WaterLevelFragment fragment = new WaterLevelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public WaterLevelFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("ARG_PAGE");
        comm = new Communication(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_water_level, container, false);
        lineChart = (LineChart)view.findViewById(R.id.chart);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //comm.send(1,this, APIEndPoints.apiUrl,APIEndPoints.getSystemReadings,"");
        scheduleAlarm(1,APIEndPoints.apiUrl,APIEndPoints.getSystemReadings,"");


    }

    public void scheduleAlarm(int id,String apiurl,String path,String parameters) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
        StringBuilder builder = new StringBuilder();
        builder.append(apiurl);
        builder.append(path);
        builder.append(parameters);
        String URL = builder.toString();
        Log.d(TAG, "Url: " + URL);

        intent.putExtra("url", URL);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(getActivity(), AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
        int intervalMillis = 120000; // 5 seconds
        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, pIntent);
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(testReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DataService.INTENT_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(testReceiver, filter);
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", getActivity().RESULT_CANCELED);
            if (resultCode == getActivity().RESULT_OK) {
                String json = intent.getStringExtra("json");
                //Toast.makeText(getActivity(), "Received Update", Toast.LENGTH_SHORT).show();

                try {
                    Object object = new JSONTokener(json).nextValue();
                    if (object instanceof JSONObject){
                        JSONObject jsonobject = new JSONObject(json);
                        onSuccess(1,jsonobject);
                    }else if (object instanceof JSONArray){
                        JSONArray array = new JSONArray(json);
                        onSuccess(1,array);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    public void loadChart(LineChart chart,JSONArray array){
        ArrayList<Entry> waterVals = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for(int i=0;i<array.length();i++){
            try {
                JSONObject object = array.getJSONObject(i);
                float waterLevel = (float)object.getDouble("waterLevel");
                Entry entry = new Entry(waterLevel,i);
                waterVals.add(entry);
                xVals.add((i*15)+" mins");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }




        LineDataSet setComp1 = new LineDataSet(waterVals, "Water Level");

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);




        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate();

    }

    @Override
    public void onSuccess(int communicationId, JSONArray array) {
        switch(communicationId){
            case 1:
                Log.d(TAG,"Success");
                loadChart(lineChart, array);
        }

    }

    @Override
    public void onSuccess(int communicationId, JSONObject object) {
        switch(communicationId){
            case 1:
                Log.d(TAG,"Success");
                JSONArray array = null;
                try {
                    array = object.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadChart(lineChart, array);
        }

    }

    @Override
    public void onError(int communicationId, String message) {
        Log.d(TAG,"Error");

    }
}
