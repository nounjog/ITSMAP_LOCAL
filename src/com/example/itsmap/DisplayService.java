package com.example.itsmap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.example.itsmap.Map.MapFragment;
import com.example.itsmap.UserManager.Login;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.example.itsmap.Map.GetLoc;

//import com.example.itsmap.Map.MapFragment.GetLoc;

public class DisplayService extends Service {

	private GoogleMap mMap;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("OnCommand", "OnCreate");
		new GetLoc().execute("http://pierrelt.fr/ITSMAP/getLocation.php?id="
				+ Login.iduser);
		return START_REDELIVER_INTENT;
	}

	public class GetLoc extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {
			Log.i("GetLoc", "OnCreate");
			fill(result);
			super.onPostExecute(result);
			// Log.i("start", result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.i("Background", "OnCreate");
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet method = new HttpGet(params[0]);
				HttpResponse response = httpclient.execute(method);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity);
				} else {
					return "No string.";
				}
			} catch (Exception e) {
				return "Network problem";
			}

		}
	}

	public void fill(String o) {
		Log.i("fill", "OnCreate");
		Log.i("Result", o);
		try {
			JSONArray jArray;
			jArray = new JSONArray(o);
		//	Log.i("start", String.valueOf(jArray.length()));

			for (int i = 0; i < jArray.length(); i++) {
				JSONObject tmp = (JSONObject) jArray.get(i);
				String val = tmp.get("name").toString();
				double lon = Double
						.parseDouble(tmp.get("longitude").toString());
				double lat = Double.parseDouble(tmp.get("latitude").toString());

				String time = tmp.get("timestamp").toString();
				//Log.i("start", "AJOUT");

				MapFragment.displayUsers(lat, lon, val, time);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Log.i("start", "JSON FILLED");
	}

	public void onConnectionFailed(ConnectionResult arg0) {

	}

	public void onDisconnected() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
