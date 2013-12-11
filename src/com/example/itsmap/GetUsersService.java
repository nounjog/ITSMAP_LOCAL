package com.example.itsmap;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

public class GetUsersService extends Service {
	
	private GoogleMap mMap;

	private static final String UPDATE_URL = "http://pierrelt.fr/ITSMAP/getuser.php";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		new Thread(new Runnable() {
			public void run() {

				String string = "Get";

				Looper.prepare();
				// On se connecte au serveur afin de communiquer avec le PHP
				DefaultHttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(),
						15000);

				HttpResponse response;
				HttpEntity entity;

				try {
					// On établit un lien avec le script PHP
					HttpPost post = new HttpPost(UPDATE_URL);

					List<NameValuePair> nvps = new ArrayList<NameValuePair>();

					nvps.add(new BasicNameValuePair("get", string));

					post.setHeader("Content-Type",
							"application/x-www-form-urlencoded");

					post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					response = client.execute(post);
					entity = response.getEntity();

					InputStream is = entity.getContent();
					// On appelle une fonction définie plus bas pour traduire la
					// réponse

					JSONArray jArray = new JSONArray(
							Login.convertStreamToString(is));
					JSONObject tm = (JSONObject) jArray.get(0);
					tm.get("time").toString();

					for (int i = 1; i < jArray.length(); i++) {
						JSONObject tmp = (JSONObject) jArray.get(i);
						String val = tmp.get("name").toString();
						double lon = Double.parseDouble(tmp.get("longitude")
								.toString());
						double lat = Double.parseDouble(tmp.get("latitude")
								.toString());

						String time = "DANS LE FUTUR";
						displayUsers(lon, lat, val, time);
					}
					is.close();

					if (entity != null)
						entity.consumeContent();

				} catch (Exception e) {

				}

				Looper.loop();
				while (true) {
					try {
						Thread.sleep(60000);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}
	
public void displayUsers(double longitude, double latitude, String name, String timestamp){
		
		mMap.addMarker(new MarkerOptions()
        .position(new LatLng(longitude, latitude))
        .title(name)).setSnippet(timestamp);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
