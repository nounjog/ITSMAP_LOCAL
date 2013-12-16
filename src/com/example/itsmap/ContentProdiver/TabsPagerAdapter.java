package com.example.itsmap.ContentProdiver;

import com.example.itsmap.Friend.AddFriendFragment;
import com.example.itsmap.Friend.FriendListFragment;
import com.example.itsmap.Map.MapFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;


public class TabsPagerAdapter extends FragmentStatePagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
Log.i("get Item Tabs", String.valueOf(index));
		switch (index) {
		case 0:
			return new MapFragment();
		case 1:
			return new FriendListFragment();
		case 2:
			return new AddFriendFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
