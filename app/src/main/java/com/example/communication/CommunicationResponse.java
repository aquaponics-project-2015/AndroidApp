package com.example.communication;

import org.json.JSONArray;
import org.json.JSONObject;

public interface CommunicationResponse {
	void onSuccess(int communicationId, JSONArray array);
	void onSuccess(int communicationId, JSONObject object);
	void onError(int communicationId, String message);

}
