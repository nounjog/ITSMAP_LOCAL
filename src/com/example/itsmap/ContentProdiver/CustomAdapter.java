package com.example.itsmap.ContentProdiver;

import java.util.ArrayList;

import com.example.itsmap.R;
import com.example.itsmap.Friend.Friend;
import com.example.itsmap.R.id;
import com.example.itsmap.R.layout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CustomAdapter extends ArrayAdapter<Friend> {
	 private final Context context;
	 private ArrayList<Friend> friendList;
	
	 private static final String TAG = "CustomAdapter";
	 
	/**
	 * Sets up the list of Friends.
	 * 
	 * @param context
	 * @param friendList List of Friends used in ListView
	 */
	public CustomAdapter(Context context, ArrayList<Friend> friendList) {
		super(context, R.layout.list_friendlist, friendList);
		this.context = context;
		this.friendList = friendList;
	}
	
	private class ViewHolder {
		TextView name, timestamp;
	}
	
	public void updateFriend(Friend friend) {    
	    for(int i = 0; i < friendList.size(); i++) {
	        if(friendList.get(i).getId() == friend.getId()) {
	            friendList.set(i, friend);
	        }
	    }
	}
	
	public void updateFriends(ArrayList<Friend> friendList) {
	    this.friendList = friendList;
	}

	@Override
	public void add(Friend Friend) {
	    friendList.add(Friend);
	}
	
	@Override
	public void remove(Friend friend) {
	    friendList.remove(friend);
	}

   @Override
   public int getCount() {
       return friendList.size();
   }

   @Override
   public Friend getItem(int position) {
	   if(friendList.get(position) != null) {
		   return friendList.get(position);
	   } else {
		   Log.d(TAG, "getItem() Out of bounds");
		   return null;
	   }
   }

	/**
	 * Updates the elements in the ListView.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder holder;
	    
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_friendlist, null);
			
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
			convertView.setTag(holder);
		} else {
		    holder = (ViewHolder) convertView.getTag();
		}

       //Log.d("CustomAdapter", "Current position: " + String.valueOf(position));
		holder.name.setText(friendList.get(position).getName());

		holder.timestamp.setText(friendList.get(position).getTimestamp());
			
		return convertView;
	}

}
