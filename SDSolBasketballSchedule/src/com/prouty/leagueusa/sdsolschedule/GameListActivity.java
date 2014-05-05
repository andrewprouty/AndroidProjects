package com.prouty.leagueusa.sdsolschedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class GameListActivity extends FragmentActivity {
	private static final String TAG = "GameListActivity";
	private TeamItem   mTeamItem = new TeamItem();
	//private DatabaseHelper mHelper;

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
		String conferenceId  = getIntent().getStringExtra("ConferenceId");
		String conferenceName = getIntent().getStringExtra("ConferenceName");
		String conferenceCount = getIntent().getStringExtra("ConferenceCount");
		String teamId  = getIntent().getStringExtra("TeamId");
		String teamName= getIntent().getStringExtra("TeamName");
		mTeamItem.setLeagueId(leagueId);
		mTeamItem.setLeagueURL(leagueURL);
		mTeamItem.setSeasonId(seasonId);
		mTeamItem.setSeasonName(seasonName);
		mTeamItem.setDivisionId(divisionId);
		mTeamItem.setDivisionName(divisionName);
		mTeamItem.setConferenceId(conferenceId);
		mTeamItem.setConferenceName(conferenceName);
		mTeamItem.setConferenceCount(conferenceCount);
		mTeamItem.setTeamId(teamId);
		mTeamItem.setTeamName(teamName);
		Log.v(TAG, "onCreate() "
				+ " league ID="    + mTeamItem.getLeagueId()
				+ ", url="         + mTeamItem.getLeagueURL()
				+ " season ID="    + mTeamItem.getSeasonId()
				+ ", name="        + mTeamItem.getSeasonName() 
				+ " division ID="  + mTeamItem.getDivisionId()
				+ ", name="        + mTeamItem.getDivisionName()
				+ " conferenceId=" + mTeamItem.getConferenceId()
				+ ", name="        + mTeamItem.getConferenceName()
				+ ", count="       + mTeamItem.getConferenceCount()
				+ " team ID="      + mTeamItem.getTeamId()
				+ ", name="        + mTeamItem.getTeamName());

		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new GameListFragment();
			fm.beginTransaction()
			.add(R.id.fragmentContainer, fragment)
			.commit();
		}
        //mHelper = new DatabaseHelper(getApplicationContext());
	}
	public TeamItem getTeamItem () {
		return mTeamItem;
	}
}