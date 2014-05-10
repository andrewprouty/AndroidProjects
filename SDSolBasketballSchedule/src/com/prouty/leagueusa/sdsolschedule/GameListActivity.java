package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.GameCursor;

public class GameListActivity extends FragmentActivity {
	private static final String TAG = "GameListActivity";
	private TeamItem   mTeamItem = new TeamItem();
	private DatabaseHelper mHelper;

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
		String conferenceId  = getIntent().getStringExtra("ConferenceId");
		String conferenceName = getIntent().getStringExtra("ConferenceName");
		String conferenceCount = getIntent().getStringExtra("ConferenceCount");
		String teamId  = getIntent().getStringExtra("TeamId");
		String teamName= getIntent().getStringExtra("TeamName");
		String teamURL= getIntent().getStringExtra("TeamURL");
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
		mTeamItem.setTeamURL(teamURL);
		Log.v(TAG, "onCreate() "
				+ " league ID="    + mTeamItem.getLeagueId()
				+ ", url="         + mTeamItem.getLeagueURL()
				+ " season ID="    + mTeamItem.getSeasonId()
				+ ", name="        + mTeamItem.getSeasonName() 
				+ " division ID="  + mTeamItem.getDivisionId()
				+ ", name="        + mTeamItem.getDivisionName()
				+ " conference ID="+ mTeamItem.getConferenceId()
				+ ", name="        + mTeamItem.getConferenceName()
				+ ", count="       + mTeamItem.getConferenceCount()
				+ " team ID="      + mTeamItem.getTeamId()
				+ ", name="        + mTeamItem.getTeamName()
				+ ", url="         + mTeamItem.getTeamURL());
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new GameListFragment();
			fm.beginTransaction()
			.add(R.id.fragmentContainer, fragment)
			.commit();
		}
        mHelper = new DatabaseHelper(getApplicationContext());
	}
	public TeamItem getTeamItem () {
		return mTeamItem;
	}
    protected void insertGameItems(ArrayList<GameItem> items) {
    	GameItem item;
		Log.d(TAG, "insertGameItems() to insert count="+items.size());
		long count=0;
		count=mHelper.deleteGameByGameItem(items.get(0)); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
		Log.d(TAG, "insertGameItems() prep deleted=" +count);
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertGameItems() Game: "
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
    				+ ", name="        + item.getTeamName()
    				+ " game ID="      + item.getGameId()
    				+ ", datetime="    + item.getGameDateTime()
    				+ ", hometeam="    + item.getGameHomeTeam()
    				+ ", awayteam="    + item.getGameAwayTeam()
    				+ ", location="    + item.getGameLocation()
    				+ ", starttbd="    + item.getGameStartTBD()
    				+ ", homescore="    + item.getGameHomeScore()
    				+ ", awayscore="    + item.getGameAwayScore()
    				+ ", sort id="+i);
            mHelper.insertGame(item,i);
            mHelper.close();
        }
        return;
    }
    protected ArrayList<GameItem> queryGamesByTeamItem(TeamItem pk) {
    	GameCursor cursor;
    	ArrayList<GameItem> items = new ArrayList<GameItem>();
		Log.d(TAG, "queryGamesByTeamItem() Team PKs: "
				+ " league ID="    + pk.getLeagueId()
				+ " season ID="    + pk.getSeasonId()
				+ " division ID="  + pk.getDivisionId()
				+ " conferenceId=" + pk.getConferenceId()
				+ " team ID="      + pk.getTeamId());
    	cursor = mHelper.queryGamesByTeamItem(pk);
    	cursor.moveToFirst();
    	while(!cursor.isAfterLast()) {
    		GameItem item = cursor.getGameItem();
    		items.add(item);
    		cursor.moveToNext();
    		Log.v(TAG, "queryGamesByTeamItem() Game: "
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
    				+ ", name="        + item.getTeamName()
    				+ " game ID="      + item.getGameId()
    				+ ", datetime="    + item.getGameDateTime()
    				+ ", hometeam="    + item.getGameHomeTeam()
    				+ ", awayteam="    + item.getGameAwayTeam()
    				+ ", location="    + item.getGameLocation()
    				+ ", starttbd="    + item.getGameStartTBD()
    				+ ", homescore="    + item.getGameHomeScore()
    				+ ", awayscore="    + item.getGameAwayScore());
    	}
    	cursor.close();
        mHelper.close();
		Log.e(TAG, "queryGamesByTeamItem() Game Count="+items.size());//TODO e to d
    	return items;
    }

}