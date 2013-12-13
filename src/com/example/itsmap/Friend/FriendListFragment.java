package com.example.itsmap.Friend;
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
import com.example.itsmap.MySQLiteOpenHelper;
import com.example.itsmap.R;
import com.example.itsmap.ContentProdiver.CustomAdapter;
import com.example.itsmap.DisplayService.GetLoc;
import com.example.itsmap.Friend.AddFriendFragment.AddFriend;
import com.example.itsmap.Friend.AddFriendFragment.GetUser;
import com.example.itsmap.Map.MapFragment;
import com.example.itsmap.R.layout;
import com.example.itsmap.UserManager.Login;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
public class FriendListFragment extends ListFragment {
	private static final String TAG = "FriendsListFragment";
	public static final String DATA_UPDATED = "DATA_UPDATED";
	private FriendsDataSource datasource;
	ArrayList<Friend> friendList;
	LayoutInflater inflater;
	CustomAdapter adapter;
	Friend friend;
	int currentPosition;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_friendlist,
				container, false);
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated");
		new GetLoc().execute("http://pierrelt.fr/ITSMAP/getFriends.php?id="
				+ Login.iduser);
		new NewFriend()
				.execute("http://pierrelt.fr/ITSMAP/getFriendRequest.php?id="
						+ Login.iduser);
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
		public void fill(String o) {
			Log.i("fill", "OnCreate");
			Log.i("Result", o);
			try {
				JSONArray jArray;
				jArray = new JSONArray(o);
				// Log.i("start", String.valueOf(jArray.length()));
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject tmp = (JSONObject) jArray.get(i);
					String val = tmp.get("name").toString();
					int id = tmp.getInt("id");
					Log.i("Nom", val);
					addFriend(val, id, i);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Log.i("start", "JSON FILLED");
		}
		public void addFriend(String name, int id, int flag) {
			datasource = new FriendsDataSource(getActivity()
					.getApplicationContext());
			datasource.open();
			if (flag == 0) {
				datasource.db.delete(MySQLiteOpenHelper.TABLE_FRIEND, null,
						null);
			}
			Log.d(TAG, "Getting all friends");
			friendList = datasource.getAllFriends();
			Log.d(TAG, "Gotten all friends");
			Friend friend1 = datasource.createFriend(name);
			adapter = new CustomAdapter(getActivity(), friendList);
			adapter.add(friend1);
			Log.d(TAG, "First friend name in friendList: "
					+ friendList.get(0).getName());
			Log.d(TAG, adapter.getItem(0).toString());
			setListAdapter(adapter);
			datasource.close();
			getListView().setOnItemLongClickListener(
					new OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							currentPosition = position;
							listDialog();
							return true;
						}
					});
		}
		/**
		 * Shows dialog when ListView item is clicked for a long time.
		 */
		public void listDialog() {
			String[] array = { "Delete" };
			Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Actions");
			builder.setItems(array, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int position) {
					deleteDialog();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		/**
		 * Dialog for deleting a friend
		 */
		public void deleteDialog() {
			Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Do you want to delete this friend?");
			builder.setCancelable(true);
			builder.setNegativeButton("No", new CancelOnClickListener());
			builder.setPositiveButton("Yes", new OkOnClickListener());
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		/**
		 * Cancel button listener that does nothing.
		 */
		private final class CancelOnClickListener implements
				DialogInterface.OnClickListener {
			public void onClick(DialogInterface dialog, int which) {
			}
		}
		/**
		 * Button listener when deleting a friend in a dialog.
		 * 
		 */
		private final class OkOnClickListener implements
				DialogInterface.OnClickListener {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getActivity(), "Friend deleted",
						Toast.LENGTH_LONG).show();
				datasource.open();
				friend = (Friend) getListAdapter().getItem(currentPosition);
				datasource.deleteFriend(friend);
				adapter.remove(friend);
				datasource.close();
				adapter.notifyDataSetChanged();
			}
		}
		/**
		 * Updates ListView.
		 */
		public void updateListView() {
			datasource.open();
			friendList = datasource.getAllFriends();
			datasource.close();
			adapter.updateFriends(friendList);
			adapter.notifyDataSetChanged();
		}
	}
	public class NewFriend extends AsyncTask<String, String, String> {
		
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
	public void fill(String result) {
		int[] idArray;
		JSONArray jArray = null;
		final ArrayList<String> listdata = new ArrayList<String>();
		try {
			jArray = new JSONArray(result);
			if (jArray != null) {
				idArray = new int[jArray.length()];
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject tmp = (JSONObject) jArray.get(i);
					String val = tmp.get("name").toString();
					idArray[i] = Integer.parseInt(tmp.get("id").toString());
					listdata.add(val);
					// listdata.add(tmp.getString("name"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ListView lv = (ListView) getActivity().findViewById(R.id.list2);
		ArrayAdapter<String> clear = null;
		lv.setAdapter(clear);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String name = listdata.get(position);
				// TODO Auto-generated method stub
				Log.i("start", name);
				// new
				run(name);
			}
		});
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1, listdata);
		lv.setAdapter(arrayAdapter);
	}
			public void run(String name) {
				
				final String Iduser = Login.iduser;
				final String UPDATE_URL = "http://pierrelt.fr/ITSMAP/answerRequest.php";
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
					nvps.add(new BasicNameValuePair("idf", name));
					nvps.add(new BasicNameValuePair("action", "acceptFR"));
					Log.i(Iduser, name);
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
	}