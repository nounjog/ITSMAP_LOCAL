package com.example.itsmap;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener{

	private static final String TAG = "MapFragment";
	
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
		View rootView = inflater.inflate(R.layout.fragment_map, container, false);
		
		return rootView;
	}
    
    
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
	    Log.d(TAG, "onActivityCreated");
	    
	 // Get a handle to the Map Fragment
        mMap = ((SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        /*LatLng sydney = new LatLng(-33.867, 151.206);

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        mMap.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));*/
        
        lr = LocationRequest.create();
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lc = new LocationClient(this.getActivity().getApplicationContext(),
                this, this);
        lc.connect();
        
	}
	
	public void onLocationChanged(Location l2) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(l2.getLatitude(), l2.getLongitude()), 15);
        mMap.animateCamera(cameraUpdate);
    }

    public void onConnectionFailed(ConnectionResult arg0) {

    }

    public void onConnected(Bundle connectionHint) {
        lc.requestLocationUpdates(lr, this);

    }

    public void onDisconnected() {

    }

}
