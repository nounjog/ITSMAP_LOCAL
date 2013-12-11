package com.example.itsmap;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
		View rootView = inflater.inflate(R.layout.fragment_friendlist, container, false);
		
		return rootView;
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
	    Log.d(TAG, "onActivityCreated");
    	//String[] friends = { "Thomas", "Pierre", "Ivan" };
    	//setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.simple_list, friends));
	    

	    datasource = new FriendsDataSource(getActivity().getApplicationContext());
	    datasource.open();

	    Log.d(TAG, "Getting all friends");
	    friendList = datasource.getAllFriends();
	    Log.d(TAG, "Gotten all friends");
	    
	    Friend friend1 = datasource.createFriend("Thomas");
	    Friend friend2 = datasource.createFriend("Pierre");
		   
		    
	    adapter = new CustomAdapter(getActivity(), friendList);
	    adapter.add(friend1);
	    adapter.add(friend2);

	    Log.d(TAG, "First friend name in friendList: " + friendList.get(0).getName());
	    
	    Log.d(TAG, adapter.getItem(0).toString());
	    
	    setListAdapter(adapter);
	    datasource.close();
	    
	    getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

	        @Override
	        public boolean onItemLongClick(AdapterView<?> parent, View view,
	            int position, long id) {
	            
	            currentPosition = position;
	            listDialog();
	            return true;
	        }
	    });
		
    }
	  
	/**
	 *  Shows dialog when ListView item is clicked
	 *  for a long time.
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
