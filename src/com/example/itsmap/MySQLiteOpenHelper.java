package com.example.itsmap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_FRIEND = "friend";
	
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TIMESTAMP = "timestamp";


	private static final String DATABASE_NAME = "friend.db";
	private static final int DATABASE_VERSION = 5;
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
	      + TABLE_FRIEND + "(" 
		  + COLUMN_ID + " integer primary key autoincrement, " 
		  + COLUMN_NAME + " text not null, "
	      + COLUMN_TIMESTAMP + " text not null );";

	public MySQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.d(MySQLiteOpenHelper.class.getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND);
	        onCreate(db);
	}
	
}
