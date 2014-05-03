package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.PhotoCursor;

public class DivisionListActivity extends FragmentActivity {
	private static final String TAG = "DivisionListActivity";
	private SeasonItem   mSeasonItem = new SeasonItem();
	//TODO old/delete
	private static ArrayList<PhotoItem> mPhotoItems;
	//private int mImageWidth = 0;
	//private int mImageHeight = 0;

	private DatabaseHelper mHelper;

	protected void launchTeamListActivity(ConferenceItem item) {
		Log.d(TAG, "launchTeamListActivity(): "
				+ item.getLeagueId()+"-"
				+ item.getLeagueURL()+"-"
				+ item.getSeasonId()+"-"
				+ item.getSeasonName()+"-"
				+ item.getDivisionId()+"-"
				+ item.getConferenceId()+"-"
				+ item.getConferenceName());
		/*Intent i = new Intent (DisplayListActivity.this, TeamListActivity.class);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		i.putExtra("SeasonId", item.getSeasonId().toString());
		i.putExtra("SeasonName", item.getSeasonName().toString());
		i.putExtra("DivisionId", item.getDivisionId().toString());
		i.putExtra("ConferenceId", item.getConferenceId().toString());
		startActivity(i);*/
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_masterdetail);
		Log.d(TAG, "onCreate()");

		String leagueId  = getIntent().getStringExtra("LeagueId");
		String leagueURL = getIntent().getStringExtra("LeagueURL");
		String seasonId  = getIntent().getStringExtra("SeasonId");
		String seasonName= getIntent().getStringExtra("SeasonName");
		initSetupItem(leagueId, leagueURL, seasonId, seasonName);

		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new DivisionListFragment();
			fm.beginTransaction()
			.add(R.id.fragmentContainer, fragment)
			.commit();
		}
        mHelper = new DatabaseHelper(getApplicationContext());
	}

	private void initSetupItem (String leagueId, String leagueURL, String seasonId, String seasonName) {
		mSeasonItem.setLeagueId(leagueId);
		mSeasonItem.setLeagueURL(leagueURL);
		mSeasonItem.setSeasonId(seasonId);
		mSeasonItem.setSeasonName(seasonName);
		Log.d(TAG, "initSetupItem() : "
				+ mSeasonItem.getLeagueId() + " ("
				+ mSeasonItem.getLeagueURL() + "); "
				+ mSeasonItem.getSeasonId() + "-"
				+ mSeasonItem.getSeasonName());
	}
	public SeasonItem getSeasonItem () {
		return mSeasonItem;
	}
	
	public Boolean isTwoPane() { //Callback
		//Log.d(TAG, "isTwoPane()");
		if (findViewById(R.id.imageFragmentContainer) == null) {
			return false;
		} else {
			return true;
		}
	}
	public PhotoItem getPhotoItem(int pos) { //Callback
		Log.d(TAG, "getDisplayItem() ["+pos+"] size:"+mPhotoItems.size());
		PhotoItem item = mPhotoItems.get(pos);
		return item;
	}

	public void setPhotoItems(ArrayList<PhotoItem> items) {
		mPhotoItems = items;
		Log.d(TAG, "setPhotoItems() in:"+items.size()+" set:"+mPhotoItems.size());
		return;
	}
	
	protected void insertPhotoItems(ArrayList<PhotoItem> items, UserItem user) {
		PhotoItem item;
		Log.d(TAG, "insertPhotoItems() user:"+user.getUserId()+"-"+user.getUserName());
		mHelper.deletePhotosforUserId(user.getUserId());
		for (int i=0; i<items.size(); i++) {
			item=items.get(i);
			Log.v(TAG, "insertPhotoItems() user: "
					+ item.getUserId() + "-"  + item.getUserName() + "; "
					+ item.getPhotoId() + "-" + item.getPhotoName());
			mHelper.insertPhoto(item);
		}
		mHelper.close();
		return;
	    }	
	
	protected ArrayList<PhotoItem> queryPhotoItemsforUserId(UserItem user) {
		PhotoCursor cursor;
		ArrayList<PhotoItem> items = new ArrayList<PhotoItem>();
		cursor = mHelper.queryPhotosForUserId(user.getUserId());
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			PhotoItem item = cursor.getPhotoItem();
			items.add(item);
			cursor.moveToNext();
			Log.d(TAG, "queryPhotoItemsforUserId(): "
					+ item.getUserId() + "-"
					+ item.getUserName() + "; "
					+ item.getPhotoId() + "-"
					+ item.getPhotoName());
		}
    	cursor.close();
        mHelper.close();
		return items;
	}
}