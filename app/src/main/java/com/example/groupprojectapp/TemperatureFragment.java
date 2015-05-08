package com.example.groupprojectapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.communication.APIEndPoints;
import com.example.communication.Communication;
import com.example.communication.CommunicationResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TemperatureFragment extends Fragment implements CommunicationResponse {
    private LineChart lineChart;
    private int page;
    Communication comm;
    public static final String TAG = TemperatureFragment.class.getSimpleName();


    public static TemperatureFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("TEMP_PAGE",page);
        TemperatureFragment fragment = new TemperatureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TemperatureFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("TEMP_PAGE");

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
        comm = new Communication(getActivity());
        comm.send(1,this, APIEndPoints.apiUrl,APIEndPoints.getSystemReadings,"");


    }
    public void loadChart(LineChart chart,JSONArray array){
        ArrayList<Entry> tempVals = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for(int i=0;i<array.length();i++){
            try {
                JSONObject object = array.getJSONObject(i);
                float temperature = (float)object.getDouble("temperature");
                Entry entry = new Entry(temperature,i);
                tempVals.add(entry);
                xVals.add((i*15)+" mins");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }




        LineDataSet setComp1 = new LineDataSet(tempVals, "Temperature");

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);




        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate();

    }

    @Override
    public void onSuccess(int communicationId, JSONArray array) {

    }

    @Override
    public void onSuccess(int communicationId, JSONObject object) {
        switch(communicationId){
            case 1:
                Log.d(TAG, "Success");
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

    }
}
