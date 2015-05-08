package com.example.communication;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Response {
	private int status;
	private String message;
	public static final String TAG = Response.class.getSimpleName();
	public Response(JSONObject object) {
		try {
			this.status = object.getInt("status");
			this.message = object.getString("message");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "ERROR",e);
		}
	}
	
	public int getStatus(){
		return this.status;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public boolean isSuccess(){
		if(this.status==200){
			return true;
		}else{
			return false;
		}
	}

}
