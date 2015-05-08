package com.example.communication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

public class Communication {
	public static final String TAG = Communication.class.getSimpleName();
	public String URL;
	JSONArray array;
	JSONObject object;
	int statusCode;
	String errorString = "Error cannot connect";
    private static final long MAX_SIZE = 5242880L;
	
	ProgressDialog progressDialog;
	ProgressDialog progressDialog2;
	Context context;
	public Communication(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	public void send (final int communicationId, final CommunicationResponse communicationResponse,String apiUrl,String path,String queryParams){
		StringBuilder builder = new StringBuilder();
		builder.append(apiUrl);
		builder.append(path);
		builder.append(queryParams);
		URL = builder.toString();
		Log.d(TAG, "Url: "+URL);
		
		AsyncTask<String,Object,String> async = new AsyncTask<String,Object,String>(){
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading..");
                progressDialog.show();

			}


			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				String json;
				try{
					StrictMode.ThreadPolicy policy = new StrictMode.
					ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);

                    URL url = new URL(arg0[0]);
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
						Log.d(TAG, "json string: "+json);
						return json;
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						Log.d(TAG, "ERROR",e);
						e.printStackTrace();
						return errorString;
					}catch(IOException e){
						Log.d(TAG, "ERROR",e);
						e.printStackTrace();
						return errorString;
					}catch(Exception e){
						Log.d(TAG, "ERROR",e);
						e.printStackTrace();
						return errorString;
						
					}
				
				}catch(Exception e){
					Log.d(TAG, "ERROR",e);
					e.printStackTrace();
					return  errorString;
					
				}
			}
			;
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				if(result== errorString){
					Toast.makeText(context, result, Toast.LENGTH_LONG);
					Log.d(TAG, result);
                    communicationResponse.onError(communicationId,errorString);
				}else{
				try {
					//array=new JSONArray(result);
					Object json = new JSONTokener(result).nextValue();
					if (json instanceof JSONObject){
						object = new JSONObject(result);
						communicationResponse.onSuccess(communicationId,object);
					}else if (json instanceof JSONArray){
						array = new JSONArray(result);
						communicationResponse.onSuccess(communicationId,array);
					}
					

					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "ERROR",e);
					e.printStackTrace();
				}
				}
				
			}
		};
		async.execute(URL);
	}
	
	







	

	public JSONArray getResult(){
		return this.array;
	}
	
	
	
	public static String getQueryString(HashMap<String,Object> map){
		String query="";
		
		
		for(Map.Entry<String, Object> entry: map.entrySet()){
			if(query==""){
				query=query+"?"+entry.getKey()+"="+entry.getValue();
			}else{
				query=query+"&"+entry.getKey()+"="+entry.getValue();
			}
			
		}
		return query;
	}
		
	

}
