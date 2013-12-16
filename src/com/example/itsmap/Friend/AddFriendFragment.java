package com.example.itsmap.Friend;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.itsmap.R;
//import com.example.itsmap.Friend.FriendListFragment.CancelOnClickListener;
//import com.example.itsmap.Friend.FriendListFragment.OkOnClickListener;
import com.example.itsmap.R.layout;
import com.example.itsmap.UserManager.Login;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AddFriendFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		new GetUser().execute("http://pierrelt.fr/ITSMAP/getUser.php?id="
				+ Login.iduser);

		View rootView = inflater.inflate(R.layout.fragment_addfriend,
				container, false);
		Log.i("AddFriend","onCreateView");
		// new
		// GetUser().execute("http://pierrelt.fr/ITSMAP/getUser.php?id="+Login.iduser);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.i("AddFriend","onActivitycreated");
	}

	public static String name = "";

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
if(getActivity()!=null){
		ListView lv = (ListView) getActivity().findViewById(R.id.list);
		ArrayAdapter<String> clear = null;
		lv.setAdapter(clear);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String Name = listdata.get(position);
				name = Name;
				// TODO Auto-generated method stub
				Log.i("start", name);
				listDialog3();

			}
		});

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1, listdata);
		lv.setAdapter(arrayAdapter);
		Log.i("AddFriend","fill method");
}
	}

	public void listDialog3() {
		String[] array = { "ADD" };
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Actions");
		builder.setItems(array, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int position) {
				AcceptDialog();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * Dialog for deleting a friend
	 */
	public void AcceptDialog() {
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Do you want to add this friend?");
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
			Toast.makeText(getActivity(), "Request Send", Toast.LENGTH_LONG)
					.show();
			new AddFriend()
					.execute("http://pierrelt.fr/ITSMAP/addFriend.php?id="
							+ Login.iduser + "&name=" + name);
		}
	}

	public void updateListView2() {

	}

	public class GetUser extends AsyncTask<String, String, String> {

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

	public class AddFriend extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {
			Log.i("start", result);
			Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
			new GetUser().execute("http://pierrelt.fr/ITSMAP/getUser.php?id="
					+ Login.iduser);
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
}
