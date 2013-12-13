package com.example.itsmap.Friend;

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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;

import com.example.itsmap.MySQLiteOpenHelper;
import com.example.itsmap.UserManager.Login;

public class FriendsDataSource {

	// Database fields
	public SQLiteDatabase db;
	private MySQLiteOpenHelper dbHelper;
	private String[] allColumns = { MySQLiteOpenHelper.COLUMN_ID,
			MySQLiteOpenHelper.COLUMN_NAME, MySQLiteOpenHelper.COLUMN_TIMESTAMP };

	public FriendsDataSource(Context context) {
		dbHelper = new MySQLiteOpenHelper(context);

		//db.delete(MySQLiteOpenHelper.TABLE_FRIEND, null, null);
	}

	/**
	 * Opens database connection.
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();

	}

	/**
	 * Closes database connection.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Creates a new friend to database and returns it.
	 * 
	 * @param friendName
	 * @return Created friend
	 */
	public Friend createFriend(String friendName) {
		Log.d("HabitsDataSource", "createFriend()");
		ContentValues values = new ContentValues();
		values.put(MySQLiteOpenHelper.COLUMN_NAME, friendName);
		values.put(MySQLiteOpenHelper.COLUMN_TIMESTAMP, "Friend");
		long insertId = db
				.insert(MySQLiteOpenHelper.TABLE_FRIEND, null, values);
		Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_FRIEND, allColumns,
				MySQLiteOpenHelper.COLUMN_ID + " = " + insertId, null, null,
				null, null);
		cursor.moveToFirst();
		Friend friend = cursorToFriend(cursor);
		cursor.close();
		return friend;
	}

	/**
	 * Updates an existing friend in the database and returns the modified
	 * friend.
	 */
	public Friend updateFriend(long rowId, String friendName, String timestamp) {
		Log.d("HabitsDataSource", "updateFriend()");
		ContentValues values = new ContentValues();
		values.put(MySQLiteOpenHelper.COLUMN_NAME, friendName);
		values.put(MySQLiteOpenHelper.COLUMN_TIMESTAMP, timestamp);
		db.update(MySQLiteOpenHelper.TABLE_FRIEND, values,
				MySQLiteOpenHelper.COLUMN_ID + "=" + rowId, null);
		Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_FRIEND, allColumns,
				MySQLiteOpenHelper.COLUMN_ID + " = " + rowId, null, null, null,
				null);
		cursor.moveToFirst();
		Friend friend = cursorToFriend(cursor);
		cursor.close();
		return friend;
	}

	/**
	 * Deletes friend from the database.
	 * 
	 * @param friend
	 */
	public void deleteFriend(Friend friend) {
		Log.d("FriendsDataSource", "deleteFriend()");
		long id = friend.getId();
		final String UPDATE_URL = "http://pierrelt.fr/ITSMAP/manageFriends.php";
		final String Id = String.valueOf(id);
		final String Iduser = Login.iduser;
		System.out.println("Friend deleted with id: " + id);
		db.delete(MySQLiteOpenHelper.TABLE_FRIEND, MySQLiteOpenHelper.COLUMN_ID
				+ " = " + id, null);

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

					nvps.add(new BasicNameValuePair("id", Iduser));
					nvps.add(new BasicNameValuePair("idf", Id));
					nvps.add(new BasicNameValuePair("action", "deleteF"));
					Log.i(Iduser, Id);
					post.setHeader("Content-Type",
							"application/x-www-form-urlencoded");
					// On passe les paramètres login et password qui vont être
					// récupérés
					// par le script PHP en post
					post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					// On récupère le résultat du script
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

	/**
	 * Gets all the friends from the database.
	 * 
	 * @return List of friends
	 */
	public ArrayList<Friend> getAllFriends() {
		Log.d("FriendsDataSource", "getAllFriends()");
		ArrayList<Friend> friends = new ArrayList<Friend>();

		Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_FRIEND, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Friend friend = cursorToFriend(cursor);
			friends.add(friend);
			cursor.moveToNext();
		}

		cursor.close();
		
		return friends;
	}

	private Friend cursorToFriend(Cursor cursor) {
		Friend friend = new Friend();
		friend.setId(cursor.getLong(0));
		friend.setName(cursor.getString(1));
		friend.setTimestamp(cursor.getString(2));
		return friend;
	}
}