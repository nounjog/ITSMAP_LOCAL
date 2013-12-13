package com.example.itsmap;

import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.itsmap.Map.MapFragment;
import com.example.itsmap.UserManager.Login;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.GoogleMap;
//import com.example.itsmap.Map.GetLoc;

//import com.example.itsmap.Map.MapFragment.GetLoc;

public class DisplayService extends Service {

	private GoogleMap mMap;
	public String friendLast = "";
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i("OnCommand", "OnCreate");
		
		Intent intent = new Intent(this, DisplayService.class);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(this.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				30 * 1000, pintent);
		
	}
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("SERVICE", "STARTED");

		new GetLoc().execute("http://pierrelt.fr/ITSMAP/getLocation.php?id="
				+ Login.iduser);
		
		/*NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
				.setContentTitle("TA MERE")
				.setContentText("ELLE FAIT DES GAUFFRES");
				//Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(this, MapFragment.class);

				//The stack builder object will contain an artificial back stack for the
				//started Activity.
				//This ensures that navigating backward from the Activity leads out of
				//your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				//Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(MapFragment.class);
				//Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
				    0,
				    PendingIntent.FLAG_UPDATE_CURRENT
				);
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
				(NotificationManager)this.getSystemService(this.NOTIFICATION_SERVICE);
				//mId allows you to update the notification later on.
				mNotificationManager.notify(1, mBuilder.build());*/
		return super.onStartCommand(intent, flags, startId);
	}
	  private void sendNotification(Context context,String friends) {
		  if(!friends.equals("")){
	        Intent notificationIntent = new Intent(context, MapFragment.class);
	        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	        NotificationManager notificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        Notification notification =  new Notification(android.R.drawable.star_on, "Refresh", System.currentTimeMillis());
	        notification.flags |= Notification.FLAG_AUTO_CANCEL;
	        notification.setLatestEventInfo(context, "TA MERE",friends+" a proximité de vous.", contentIntent);
	        notificationMgr.notify(0, notification);}
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
String friends = "";
String curFriend ="";
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject tmp = (JSONObject) jArray.get(i);
				String val = tmp.get("name").toString();
				double lon = Double
						.parseDouble(tmp.get("longitude").toString());
				double lat = Double.parseDouble(tmp.get("latitude").toString());
				String time = tmp.get("timestamp").toString();

			
				if(MapFragment.displayUsers(lat, lon, val, time,i)){
					curFriend+=val+",";
					if(!friendLast.contains(val+",")){
						Log.i("SERVICE",val);
					friends +=val+",";
					} else{
						//friendLast = friendLast+" "+val;
					}
				}
			}
			friendLast = curFriend;
			this.sendNotification(this,friends);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Log.i("start", "JSON FILLED");
	}

	/*public void onConnectionFailed(ConnectionResult arg0) {

	}*/

	/*public void onDisconnected() {

	}*/

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
