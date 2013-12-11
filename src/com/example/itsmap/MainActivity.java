package com.example.itsmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	private TextView tv;
	public static final int RESULT_Main = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("MainActivity", "OnCreate");
		
		startActivityForResult(new Intent(MainActivity.this, Login.class), RESULT_Main);
		
		tv = new TextView(this);
        setContentView(tv);
	}

	 private void startup(Intent i) 
		{
			// Récupère l'identifiant        
			int user = i.getIntExtra("userid",-1);
			 
			//Affiche les identifiants de l'utilisateur
	        tv.setText("UserID: "+String.valueOf(user)+" logged in");
	        
	    }
	 
	 
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
		{ 
	        if(requestCode == RESULT_Main && resultCode == RESULT_CANCELED)  
	            finish();
	        if (data.hasExtra("returnKey1")) {
	            Toast.makeText(this, data.getExtras().getString("returnKey1"),
	              Toast.LENGTH_SHORT).show();
	            finish();
	            startActivity(new Intent(MainActivity.this, ContentActivity.class));
	            
	          }
	        else 
	            startup(data);
	    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
