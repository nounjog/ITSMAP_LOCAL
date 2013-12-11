package com.example.itsmap.ContentProdiver;

import com.example.itsmap.Friend.AddFriendFragment;
import com.example.itsmap.Friend.FriendListFragment;
import com.example.itsmap.Map.MapFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class TabsPagerAdapter extends FragmentStatePagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new MapFragment();
		case 1:
			// Games fragment activity
			return new FriendListFragment();
		case 2:
			// Movies fragment activity
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
