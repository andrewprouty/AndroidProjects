package com.prouty.leagueusa.sdsolschedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class DivisionListActivity extends FragmentActivity {
	private static final String TAG = "DivisionListActivity";
	private SeasonItem   mSeasonItem = new SeasonItem();
	private DatabaseHelper mHelper;

	protected void launchTeamListActivity(ConferenceItem item) {
		Log.d(TAG, "launchTeamListActivity(): "
				+ item.getLeagueId()+"-"
				+ item.getLeagueURL()+"-"
				+ item.getSeasonId()+"-"
				+ item.getSeasonName()+"-"
				+ item.getDivisionId()+"-"
				+ item.getDivisionName()+"-"
				+ item.getConferenceId());
		Intent i = new Intent (this, TeamListActivity.class);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		i.putExtra("SeasonId", item.getSeasonId().toString());
		i.putExtra("SeasonName", item.getSeasonName().toString());
		i.putExtra("DivisionId", item.getDivisionId().toString());
		i.putExtra("DivisionName", item.getDivisionName().toString());
		i.putExtra("ConferenceId", item.getConferenceId().toString());
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
}