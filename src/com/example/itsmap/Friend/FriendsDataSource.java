package com.example.itsmap.Friend;

import java.util.ArrayList;

import com.example.itsmap.MySQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FriendsDataSource {

	  // Database fields
	  private SQLiteDatabase db;
	  private MySQLiteOpenHelper dbHelper;
	  private String[] allColumns = { 
	          MySQLiteOpenHelper.COLUMN_ID,
    	      MySQLiteOpenHelper.COLUMN_NAME,
    	      MySQLiteOpenHelper.COLUMN_TIMESTAMP };

	  public FriendsDataSource(Context context) {
	    dbHelper = new MySQLiteOpenHelper(context);
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
	 *  Closes database connection.
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
	    values.put(MySQLiteOpenHelper.COLUMN_TIMESTAMP, "timestamp");
	    long insertId = db.insert(MySQLiteOpenHelper.TABLE_FRIEND, null,
	        values);
	    Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_FRIEND,
	        allColumns, MySQLiteOpenHelper.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Friend friend = cursorToFriend(cursor);
	    cursor.close();
	    return friend;
	  }
	  
	/**
	 * Updates an existing friend in the database and returns the modified friend.
	 */
	public Friend updateFriend(long rowId, String friendName, String timestamp) {
	      Log.d("HabitsDataSource", "updateFriend()");
	      ContentValues values = new ContentValues();
	      values.put(MySQLiteOpenHelper.COLUMN_NAME, friendName);
	      values.put(MySQLiteOpenHelper.COLUMN_TIMESTAMP, timestamp);
	      db.update(MySQLiteOpenHelper.TABLE_FRIEND, values, MySQLiteOpenHelper.COLUMN_ID + "=" + rowId, null);
	      Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_FRIEND,
	              allColumns, MySQLiteOpenHelper.COLUMN_ID + " = " + rowId, null,
	              null, null, null);
	      cursor.moveToFirst();
	      Friend friend = cursorToFriend(cursor);
	      cursor.close();
	      return friend;
	  }

	/** 
	 * Deletes friend from the database.
	 * @param friend
	 */
	public void deleteFriend(Friend friend) {
	    Log.d("FriendsDataSource", "deleteFriend()");
	    long id = friend.getId();
	    System.out.println("Friend deleted with id: " + id);
	    db.delete(MySQLiteOpenHelper.TABLE_FRIEND, MySQLiteOpenHelper.COLUMN_ID
	        + " = " + id, null);
	  }

	/**
	 * Gets all the friends from the database.
	 * @return List of friends
	 */
	public ArrayList<Friend> getAllFriends() {
	    Log.d("FriendsDataSource", "getAllFriends()");
	    ArrayList<Friend> friends = new ArrayList<Friend>();

	    Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_FRIEND,
	        allColumns, null, null, null, null, null);

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