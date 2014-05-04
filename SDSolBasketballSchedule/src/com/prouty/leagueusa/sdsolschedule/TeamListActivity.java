package com.prouty.leagueusa.sdsolschedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class TeamListActivity extends FragmentActivity {
	private static final String TAG = "TeamListActivity";
	private ConferenceItem   mConferenceItem = new ConferenceItem();
	private DatabaseHelper mHelper;

	protected void launchGameListActivity(TeamItem item) {
		Log.d(TAG, "launchGameListActivity(): "
				+ item.getLeagueId()+"-"
				+ item.getLeagueURL()+"-"
				+ item.getSeasonId()+"-"
				+ item.getSeasonName()+"-"
				+ item.getDivisionId()+"-"
				+ item.getDivisionName()+"-"
				+ item.getConferenceId()+"-"
				+ item.getTeamId()+"-"
				+ item.getTeamName());
		/*Intent i = new Intent (GameListActivity.this, TeamListActivity.class);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		i.putExtra("SeasonId", item.getSeasonId().toString());
		i.putExtra("SeasonName", item.getSeasonName().toString());
		i.putExtra("DivisionId", item.getDivisionId().toString());
		i.putExtra("ConferenceId", item.getConferenceId().toString());
		i.putExtra("TeamId", item.getTeamId().toString());
		i.putExtra("TeamName", item.getTeamName().toString());
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
		String divisionId  = getIntent().getStringExtra("DivisionId");
		String divisionName= getIntent().getStringExtra("DivisionName");
		String conferenceId  = getIntent().getStringExtra("ConferenceId");
		initSetupItem(leagueId, leagueURL, seasonId, seasonName, divisionId, divisionName, conferenceId);

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
	private void initSetupItem (String leagueId, String leagueURL, String seasonId, String seasonName,
								String divisionId, String divisionName, String conferenceId) {
		mConferenceItem.setLeagueId(leagueId);
		mConferenceItem.setLeagueURL(leagueURL);
		mConferenceItem.setSeasonId(seasonId);
		mConferenceItem.setSeasonName(seasonName);
		mConferenceItem.setDivisionId(divisionId);
		mConferenceItem.setDivisionName(divisionName);
		mConferenceItem.setConferenceId(conferenceId);
		Log.d(TAG, "initSetupItem() : "
				+ mConferenceItem.getLeagueId() + " ("
				+ mConferenceItem.getLeagueURL() + "); "
				+ mConferenceItem.getSeasonId() + "-"
				+ mConferenceItem.getSeasonName() + "-"
				+ mConferenceItem.getDivisionId() + "-"
				+ mConferenceItem.getDivisionName() + "-"
				+ mConferenceItem.getConferenceId()
				);
	}
	public ConferenceItem getConferenceItem () {
		return mConferenceItem;
	}
}