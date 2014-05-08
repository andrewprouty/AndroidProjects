package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.TeamCursor;

public class TeamListActivity extends FragmentActivity {
	private static final String TAG = "TeamListActivity";
	private ConferenceItem   mConferenceItem = new ConferenceItem();
	private DatabaseHelper mHelper;

	protected void launchGameListActivity(TeamItem item) {
		Log.d(TAG, "launchGameListActivity()");
		Intent i = new Intent (this, GameListActivity.class);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		i.putExtra("SeasonId", item.getSeasonId().toString());
		i.putExtra("SeasonName", item.getSeasonName().toString());
		i.putExtra("DivisionId", item.getDivisionId().toString());
		i.putExtra("DivisionName", item.getDivisionName().toString());
		i.putExtra("ConferenceId", item.getConferenceId().toString());
		i.putExtra("ConferenceName", item.getConferenceName().toString());
		i.putExtra("ConferenceCount", item.getConferenceCount().toString());
		i.putExtra("TeamId", item.getTeamId().toString());
		i.putExtra("TeamName", item.getTeamName().toString());
		Log.v(TAG, "launchGameListActivity(): "
				+ " league ID="    + item.getLeagueId()
				+ ", url="         + item.getLeagueURL()
				+ " season ID="    + item.getSeasonId()
				+ ", name="        + item.getSeasonName() 
				+ " division ID="  + item.getDivisionId()
				+ ", name="        + item.getDivisionName()
				+ " conferenceId=" + item.getConferenceId()
				+ ", name="        + item.getConferenceName()
				+ ", count="       + item.getConferenceCount()
				+ " team ID="      + item.getTeamId()
				+ ", name="        + item.getTeamName());
		startActivity(i);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		Log.d(TAG, "onCreate()");
		String leagueId  = getIntent().getStringExtra("LeagueId");
		String leagueURL = getIntent().getStringExtra("LeagueURL");
		String seasonId  = getIntent().getStringExtra("SeasonId");
		String seasonName= getIntent().getStringExtra("SeasonName");
		String divisionId  = getIntent().getStringExtra("DivisionId");
		String divisionName= getIntent().getStringExtra("DivisionName");
		String conferenceId   = getIntent().getStringExtra("ConferenceId");
		String conferenceName = getIntent().getStringExtra("ConferenceName");
		String conferenceCount= getIntent().getStringExtra("ConferenceCount");
		mConferenceItem.setLeagueId(leagueId);
		mConferenceItem.setLeagueURL(leagueURL);
		mConferenceItem.setSeasonId(seasonId);
		mConferenceItem.setSeasonName(seasonName);
		mConferenceItem.setDivisionId(divisionId);
		mConferenceItem.setDivisionName(divisionName);
		mConferenceItem.setConferenceId(conferenceId);
		mConferenceItem.setConferenceName(conferenceName);
		mConferenceItem.setConferenceCount(conferenceCount);
		Log.v(TAG, "onCreate() : "
				+ " league ID="    + mConferenceItem.getLeagueId()
				+ ", url="         + mConferenceItem.getLeagueURL()
				+ " season ID="    + mConferenceItem.getSeasonId()
				+ ", name="        + mConferenceItem.getSeasonName() 
				+ " division ID="  + mConferenceItem.getDivisionId()
				+ ", name="        + mConferenceItem.getDivisionName()
				+ " conferenceId=" + mConferenceItem.getConferenceId()
				+ ", name="        + mConferenceItem.getConferenceName()
				+ ", count="       + mConferenceItem.getConferenceCount());

		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new TeamListFragment();
			fm.beginTransaction()
			.add(R.id.fragmentContainer, fragment)
			.commit();
		}
        mHelper = new DatabaseHelper(getApplicationContext());
	}
	public ConferenceItem getConferenceItem () {
		return mConferenceItem;
	}
    protected void insertTeamItems(ArrayList<TeamItem> items) {
    	TeamItem item;
        Log.d(TAG, "insertTeamItems()");
		mHelper.deleteTeam(); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertTeamItems() Team: "
    				+ " league ID="    + item.getLeagueId()
    				+ ", url="         + item.getLeagueURL()
    				+ " season ID="    + item.getSeasonId()
    				+ ", name="        + item.getSeasonName() 
    				+ " division ID="  + item.getDivisionId()
    				+ ", name="        + item.getDivisionName()
    				+ " conferenceId=" + item.getConferenceId()
    				+ ", name="        + item.getConferenceName()
    				+ ", count="       + item.getConferenceCount()
    				+ " team ID="      + item.getTeamId()
    				+ ", name="        + item.getTeamName());

            mHelper.insertTeam(item);
            mHelper.close();
        }
        return;
    }
    protected ArrayList<TeamItem> queryTeamByConferenceItem(ConferenceItem pk) {
    	TeamCursor cursor;
    	ArrayList<TeamItem> items = new ArrayList<TeamItem>();
    	cursor = mHelper.queryTeamsByConferenceItem(pk);
    	cursor.moveToFirst();
    	while(!cursor.isAfterLast()) {
    		TeamItem item = cursor.getTeamItem();
    		items.add(item);
    		cursor.moveToNext();
    		Log.v(TAG, "queryTeamItem() Team: "
    				+ " league ID="    + item.getLeagueId()
    				+ ", url="         + item.getLeagueURL()
    				+ " season ID="    + item.getSeasonId()
    				+ ", name="        + item.getSeasonName() 
    				+ " division ID="  + item.getDivisionId()
    				+ ", name="        + item.getDivisionName()
    				+ " conferenceId=" + item.getConferenceId()
    				+ ", name="        + item.getConferenceName()
    				+ ", count="       + item.getConferenceCount()
    				+ " team ID="      + item.getTeamId()
    				+ ", name="        + item.getTeamName());
    	}
    	cursor.close();
        mHelper.close();
    	return items;
    }
}