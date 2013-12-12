package com.example.itsmap;

import com.example.itsmap.Map.MapFragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class NewFriendAround extends BroadcastReceiver {
	public NewFriendAround() {
	}
//			registerReceiver(myBroadcastReceiver, new IntentFilter("update"));

	@Override
	public void onReceive(Context context, Intent intent) {
Log.i("broadcast","RECEIVE");
NotificationCompat.Builder mBuilder =
new NotificationCompat.Builder(context)
.setContentTitle("My notification")
.setContentText("Hello World!");
//Creates an explicit intent for an Activity in your app
Intent resultIntent = new Intent(context, MapFragment.class);

//The stack builder object will contain an artificial back stack for the
//started Activity.
//This ensures that navigating backward from the Activity leads out of
//your application to the Home screen.
TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
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
(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//mId allows you to update the notification later on.
mNotificationManager.notify(1, mBuilder.build());
	}
}
