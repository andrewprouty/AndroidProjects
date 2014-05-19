package com.prouty.leagueusa.schedule;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.prouty.leagueusa.schedule.DatabaseHelper.TeamCursor;

public class TeamListActivity extends FragmentActivity {
	private static final String TAG = "TeamListActivity";
	private ConferenceItem   mConferenceItem = new ConferenceItem();
	private DatabaseHelper mHelper;
	
	private ArrayList<FavoriteItem> mFavoriteItems;
	private FavoriteItem mFavoriteItem;
	private TeamItem mFavoriteTeam;

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
		i.putExtra("TeamURL", item.getTeamURL().toString());
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
				+ ", name="        + item.getTeamName()
				+ ", url="         + item.getTeamURL());
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
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
		supportInvalidateOptionsMenu();
	}
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		FavoriteListUtil util = new FavoriteListUtil();
		FavoriteItem favItem = new FavoriteItem();
		mFavoriteItems=util.getFavoriteList(getApplicationContext());
		menu.removeGroup(1);
		for (int i=0; i<mFavoriteItems.size(); i++) {
			favItem=mFavoriteItems.get(i);
			Log.v(TAG, "onPrepareOptionsMenu() ["+i+"] "+"fav="+favItem.getFavoriteName()+"-"+favItem.getFavoriteURL());
	        menu.add(1,i,i, favItem.getFavoriteName());
		}
        return super.onPrepareOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected() Id: "+item.getItemId());
		if (mFavoriteItems != null && mFavoriteItems.size() > 0) {
			mFavoriteItem = mFavoriteItems.get(item.getItemId());
			Log.d(TAG, "onOptionsItemSelected() FavItem Key="+mFavoriteItem.getFavoriteURL()
					+" Value="+mFavoriteItem.getFavoriteName());
			FavoriteListUtil util = new FavoriteListUtil();
			mFavoriteTeam=util.queryTeamByTeamURL(getApplicationContext(),mFavoriteItem.getFavoriteURL());
			if (mFavoriteTeam != null ) {
				Log.d(TAG, "onOptionsItemSelected() FavTeam: " + mFavoriteTeam.getTeamName());
				util.launchGameListActivity(getApplicationContext(), mFavoriteTeam);
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.broken_must_navigate, Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}

    protected void insertTeamItems(ArrayList<TeamItem> items) {
		Log.d(TAG, "insertTeamItems() to insert count="+items.size());
    	TeamItem item;
        long count=0;
		count=mHelper.deleteTeamsByTeamItem(items.get(0)); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
		Log.d(TAG, "insertTeamItems() prep deleted=" +count);
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
    				+ ", name="        + item.getTeamName()
    				+ ", url="         + item.getTeamURL());
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
    				+ ", name="        + item.getTeamName()
    				+ ", url="         + item.getTeamURL());
    	}
    	cursor.close();
        mHelper.close();
    	return items;
    }
}