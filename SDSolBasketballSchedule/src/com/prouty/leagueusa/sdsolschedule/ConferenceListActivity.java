package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.ConferenceCursor;

public class ConferenceListActivity extends FragmentActivity {
	private static final String TAG = "ConferenceListActivity";
	private DivisionItem   mDivisionItem = new DivisionItem();
	private DatabaseHelper mHelper;

	protected void launchTeamListActivity(ConferenceItem item) {
		Log.d(TAG, "launchTeamListActivity()");
		Intent i = new Intent (this, TeamListActivity.class);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		i.putExtra("SeasonId", item.getSeasonId().toString());
		i.putExtra("SeasonName", item.getSeasonName().toString());
		i.putExtra("DivisionId", item.getDivisionId().toString());
		i.putExtra("DivisionName", item.getDivisionName().toString());
		i.putExtra("ConferenceId", item.getConferenceId().toString());
		i.putExtra("ConferenceName", item.getConferenceName().toString());
		i.putExtra("ConferenceCount", item.getConferenceCount().toString());
		Log.v(TAG, "launchGameListActivity(): "
				+ " league ID="    + item.getLeagueId()
				+ ", url="         + item.getLeagueURL()
				+ " season ID="    + item.getSeasonId()
				+ ", name="        + item.getSeasonName() 
				+ " division ID="  + item.getDivisionId()
				+ ", name="        + item.getDivisionName()
				+ " conferenceId=" + item.getConferenceId()
				+ ", name="        + item.getConferenceName()
				+ ", count="       + item.getConferenceCount());
		startActivity(i);
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
		String divisionId  = getIntent().getStringExtra("DivisionId");
		String divisionName= getIntent().getStringExtra("DivisionName");
		mDivisionItem.setLeagueId(leagueId);
		mDivisionItem.setLeagueURL(leagueURL);
		mDivisionItem.setSeasonId(seasonId);
		mDivisionItem.setSeasonName(seasonName);
		mDivisionItem.setDivisionId(divisionId);
		mDivisionItem.setDivisionName(divisionName);
		Log.v(TAG, "onCreate() : "
				+ " league ID="    + mDivisionItem.getLeagueId()
				+ ", url="         + mDivisionItem.getLeagueURL()
				+ " season ID="    + mDivisionItem.getSeasonId()
				+ ", name="        + mDivisionItem.getSeasonName()
				+ " division ID="  + mDivisionItem.getDivisionId()
				+ ", name="        + mDivisionItem.getDivisionName());
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		if (fragment == null) {
			fragment = new ConferenceListFragment();
			fm.beginTransaction()
			.add(R.id.fragmentContainer, fragment)
			.commit();
		}
		mHelper = new DatabaseHelper(getApplicationContext());
	}
	public DivisionItem getDivisionItem () {
		return mDivisionItem;
	}
    protected void insertConferenceItems(ArrayList<ConferenceItem> items) {
    	ConferenceItem item;
        Log.d(TAG, "insertConferenceItems()");
		mHelper.deleteConference(); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertConferenceItems() Conference: "
    				+ " league ID="    + item.getLeagueId()
    				+ ", url="         + item.getLeagueURL()
    				+ " season ID="    + item.getSeasonId()
    				+ ", name="        + item.getSeasonName() 
    				+ " division ID="  + item.getDivisionId()
    				+ ", name="        + item.getDivisionName()
    				+ " conferenceId=" + item.getConferenceId()
    				+ ", name="        + item.getConferenceName()
    				+ ", count="       + item.getConferenceCount());
            mHelper.insertConference(item);
            mHelper.close();
        }
        return;
    }
    protected ArrayList<ConferenceItem> queryConferenceByDivisionItem(DivisionItem pk) {
    	ConferenceCursor cursor;
    	ArrayList<ConferenceItem> items = new ArrayList<ConferenceItem>();
    	cursor = mHelper.queryConferenceByDivisionItem(pk);
    	cursor.moveToFirst();
    	while(!cursor.isAfterLast()) {
    		ConferenceItem item = cursor.getConferenceItem();
    		items.add(item);
    		cursor.moveToNext();
    		Log.v(TAG, "queryDivisionItem() Division: "
    				+ " league ID="    + item.getLeagueId()
    				+ ", url="         + item.getLeagueURL()
    				+ " season ID="    + item.getSeasonId()
    				+ ", name="        + item.getSeasonName() 
    				+ " division ID="  + item.getDivisionId()
    				+ ", name="        + item.getDivisionName()
    				+ " conferenceId=" + item.getConferenceId()
    				+ ", name="        + item.getConferenceName()
    				+ ", count="       + item.getConferenceCount());
    	}
    	cursor.close();
        mHelper.close();
    	return items;
    }
}