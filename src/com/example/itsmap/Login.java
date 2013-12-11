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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private static final String UPDATE_URL = "http://pierrelt.fr/ITSMAP/login.php";

	public ProgressDialog progressDialog;

	private EditText UserEditText;

	private EditText PassEditText;

	public void onCreate(Bundle savedInstanceState) {
		Log.i("Login", "OnCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button createbutton = (Button) findViewById(R.id.createbutton);

		createbutton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(Login.this, AddUser.class);
				startActivity(intent);
			}

		});

		// initialisation d'une progress bar
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Please wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		// Récupération des éléments de la vue définis dans le xml
		UserEditText = (EditText) findViewById(R.id.username);

		PassEditText = (EditText) findViewById(R.id.password);

		Button button = (Button) findViewById(R.id.okbutton);

		// Définition du listener du bouton
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				int usersize = UserEditText.getText().length();

				int passsize = PassEditText.getText().length();
				// si les deux champs sont remplis
				if (usersize > 0 && passsize > 0) {

					progressDialog.show();

					String user = UserEditText.getText().toString();

					String pass = PassEditText.getText().toString();
					// On appelle la fonction doLogin qui va communiquer avec le
					// PHP
					doLogin(user, pass);

				} else
					createDialog("Error", "Please enter Username and Password");

			}

		});

		button = (Button) findViewById(R.id.cancelbutton);
		// Création du listener du bouton cancel (on sort de l'appli)
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				quit(false, null);
			}

		});

	}

	private void quit(boolean success, Intent i) {
		// On envoie un résultat qui va permettre de quitter l'appli
		setResult((success) ? Activity.RESULT_OK : Activity.RESULT_CANCELED, i);
		finish();

	}

	private void createDialog(String title, String text) {
		// Création d'une popup affichant un message
		AlertDialog ad = new AlertDialog.Builder(this)
				.setPositiveButton("Ok", null).setTitle(title).setMessage(text)
				.create();
		ad.show();

	}

	private void doLogin(final String login, final String pass) {

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

					nvps.add(new BasicNameValuePair("login", login));

					nvps.add(new BasicNameValuePair("password", pass));

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
					read(is);
					is.close();

					if (entity != null)
						entity.consumeContent();

				} catch (Exception e) {

					progressDialog.dismiss();
					createDialog("Error", "Couldn't establish a connection");

				}

				Looper.loop();

			}

		};

		t.start();

	}

	private void read(InputStream in) {

		Log.i("Quest cest que j'ai", in.toString());

		String string = convertStreamToString(in);

		Log.i("reponse", string);
		
		int y = Integer.parseInt(string);
		if (y!=0) {

			// Prepare data intent
			Intent data = new Intent();
			data.putExtra("returnKey1", "ConnectionOk ");
			// Activity finished ok, return the data
			setResult(RESULT_CANCELED, data);
			super.finish();
		} else {
			Toast.makeText(getApplicationContext(), "Connection failed",
					Toast.LENGTH_SHORT).show();
			
			Intent intent= new Intent(Login.this, MainActivity.class);
			startActivity(intent);

		}

	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

}
