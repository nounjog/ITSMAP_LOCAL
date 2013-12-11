package com.example.itsmap.Map;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.itsmap.R;
import com.example.itsmap.R.id;
import com.example.itsmap.R.layout;
import com.example.itsmap.UserManager.Login;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private static final String TAG = "MapFragment";
	private static final String UPDATE_URL = "http://pierrelt.fr/ITSMAP/location.php";
	private static final String MAP_FRAGMENT_TAG = "map";
	private GoogleMap mMap;
	private SupportMapFragment mMapFragment;
	LocationManager lm;
	private LocationRequest lr;
	private LocationClient lc;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_map, container,
				false);

		Log.i("start", "START");
		return rootView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated");

		// Get a handle to the Map Fragment
		mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setAllGesturesEnabled(true);
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);

		/*
		 * LatLng sydney = new LatLng(-33.867, 151.206);
		 * 
		 * mMap.setMyLocationEnabled(true);
		 * mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
		 * 
		 * mMap.addMarker(new MarkerOptions() .title("Sydney")
		 * .snippet("The most populous city in Australia.") .position(sydney));
		 */

		lr = LocationRequest.create();
		lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		lc = new LocationClient(this.getActivity().getApplicationContext(),
				this, this);
		lc.connect();

	}

	public void onLocationChanged(Location l2) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				new LatLng(l2.getLatitude(), l2.getLongitude()), 15);
		double lat = l2.getLatitude();
		double lon = l2.getLongitude();
		String lat2 = String.valueOf(lat);
		String lon2 = String.valueOf(lon);
		mMap.animateCamera(cameraUpdate);

		String id = Login.iduser;

		new GetLoc()
				.execute("http://pierrelt.fr/ITSMAP/getLocation.php?id="+Login.iduser);
		// doAddLocation(lat2, lon2, id);

		// displayUsers(lat, lon, id, "minuit");

	}

	public void fill(String o) {
		try {
			JSONArray jArray;
			jArray = new JSONArray(o);
Log.i("start",String.valueOf(jArray.length()));


			for (int i = 0; i < jArray.length(); i++) {
				JSONObject tmp = (JSONObject) jArray.get(i);
				String val = tmp.get("name").toString();
				double lon = Double
						.parseDouble(tmp.get("longitude").toString());
				double lat = Double.parseDouble(tmp.get("latitude").toString());

				String time = tmp.get("timestamp").toString();
			Log.i("start","AJOUT");
				displayUsers(lat,lon,val,time);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.i("start", "JSON FILLED");
	}

	private void doAddLocation(final String latitude, final String longitude,
			final String id) {

		// final String pw = md5(pass);
		// Cr�ation d'un thread
		Thread t = new Thread() {

			public void run() {

				Looper.prepare();
				// On se connecte au serveur afin de communiquer avec le PHP
				DefaultHttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(),
						15000);

				HttpResponse response;
				HttpEntity entity;

				try {
					// On �tablit un lien avec le script PHP
					HttpPost post = new HttpPost(UPDATE_URL);

					List<NameValuePair> nvps = new ArrayList<NameValuePair>();

					nvps.add(new BasicNameValuePair("id", id));

					nvps.add(new BasicNameValuePair("lon", longitude));

					nvps.add(new BasicNameValuePair("lat", latitude));

					post.setHeader("Content-Type",
							"application/x-www-form-urlencoded");

					post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					response = client.execute(post);
					entity = response.getEntity();

					InputStream is = entity.getContent();
					// On appelle une fonction d�finie plus bas pour traduire la
					// r�ponse

					/*
					 * JSONArray jArray = new
					 * JSONArray(Login.convertStreamToString(is)); JSONObject tm
					 * = (JSONObject) jArray.get(0); tm.get("time").toString();
					 * 
					 * for (int i=1;i<jArray.length();i++){ JSONObject tmp =
					 * (JSONObject) jArray.get(i); String val =
					 * tmp.get("name").toString(); double lon =
					 * Double.parseDouble(tmp.get("longitude").toString());
					 * double lat =
					 * Double.parseDouble(tmp.get("latitude").toString());
					 * 
					 * String time = "DANS LE FUTUR";
					 * 
					 * }
					 */
					is.close();

					if (entity != null)
						entity.consumeContent();

				} catch (Exception e) {

				}

				Looper.loop();

			}

		};

		t.start();

	}


	public void displayUsers(double longitude, double latitude, String name,
			String timestamp) {

		mMap.addMarker(
				new MarkerOptions().position(new LatLng(longitude, latitude))
						.title(name)).setSnippet(timestamp);
	}

	public void onConnectionFailed(ConnectionResult arg0) {

	}

	public void onConnected(Bundle connectionHint) {
		lc.requestLocationUpdates(lr, this);

	}

	public void onDisconnected() {

	}

	public class GetLoc extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {
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

}