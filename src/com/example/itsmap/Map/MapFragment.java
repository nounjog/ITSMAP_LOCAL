package com.example.itsmap.Map;

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

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.itsmap.DisplayService;
import com.example.itsmap.R;
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
	private static GoogleMap mMap;
	private SupportMapFragment mMapFragment;
	LocationManager lm;
	private LocationRequest lr;
	private LocationClient lc;
	public static Location location = null;
	private static View view;
	public static double lonuser = 0;
	public static double latuser = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment_map, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}

		Log.i("start", "START");
		return view;
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

		lr = LocationRequest.create();
		lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		lc = new LocationClient(this.getActivity().getApplicationContext(),
				this, this);
		lc.connect();
		getActivity().startService(
				new Intent(getActivity(), DisplayService.class));

		/*final Handler handler = new Handler();
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				getActivity().startService(
						new Intent(getActivity(), DisplayService.class));
				handler.postDelayed(this, 10000);
			}
		};
		handler.postDelayed(runnable, 10000);
		
		handler.removeCallbacks(runnable);*/

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
		latuser = lat;
		lonuser = lon;
		doAddLocation(lat2, lon2, id);

	}

	private void doAddLocation(final String latitude, final String longitude,
			final String id) {

		// final String pw = md5(pass);
		// Création d'un thread
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
					// On établit un lien avec le script PHP
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
					// On appelle une fonction définie plus bas pour traduire la
					// réponse

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

	public static void displayUsers(double latitude, double longitude,
			String name, String timestamp) {
		Log.i("DisplayUser", "OnCreate");
		Log.i("longitude", String.valueOf(longitude));
		Log.i("latitude", String.valueOf(latitude));
		Log.i("name", String.valueOf(name));
		Log.i("timestamp", String.valueOf(timestamp));
		double d = 0;
		Location locationA = new Location("A");
		locationA.setLatitude(latitude);
		locationA.setLongitude(longitude);
		Location locationB = new Location("B");
		locationB.setLatitude(latuser);
		locationB.setLongitude(lonuser);
		d = locationA.distanceTo(locationB);
		Log.i("start", "D : " + String.valueOf(d));
		// Log.i("start","LON : "+String.valueOf(longitude));

		if (d < 500) {
			mMap.addMarker(
					new MarkerOptions().position(
							new LatLng(latitude, longitude)).title(name))
					.setSnippet(timestamp);
		}

	}

	public void onConnectionFailed(ConnectionResult arg0) {

	}

	public void onConnected(Bundle connectionHint) {
		lc.requestLocationUpdates(lr, this);

	}

	public void onDisconnected() {

	}

	@Override
	public void onDestroy() {
		Log.i("onDestroy", "OnCreate");
		// TODO Auto-generated method stub
		

		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.i("onPause", "OnCreate");
		
		super.onPause();
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i("onStop", "OnCreate");
		//getActivity().getSupportFragmentManager().popBackStack();

		super.onStop();
	}

	@Override
	public void onAttach(Activity activity) {
		Log.i("onAttach", "OnCreate");
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		Log.i("onHidden", "OnCreate");
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
	}

}
