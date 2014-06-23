package com.prouty.leagueusa.schedule;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.prouty.leagueusa.schedule.DatabaseHelper.ConferenceCursor;
import com.prouty.leagueusa.schedule.DatabaseHelper.DivisionCursor;

public class DivisionListActivity extends FragmentActivity {
	private static final String TAG = "DivisionListActivity";
	private SeasonItem   mSeasonItem = new SeasonItem();
	private DatabaseHelper mHelper;
	
	private ArrayList<FavoriteItem> mFavoriteItems;
	private FavoriteItem mFavoriteItem;
	private TeamItem mFavoriteTeam;

	protected void launchConferenceListActivity(ConferenceItem item) {
		Log.d(TAG, "launchConferenceListActivity()");
		Intent i = new Intent (this, ConferenceListActivity.class);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		i.putExtra("SeasonId", item.getSeasonId().toString());
		i.putExtra("SeasonName", item.getSeasonName().toString());
		i.putExtra("DivisionId", item.getDivisionId().toString());
		i.putExtra("DivisionName", item.getDivisionName().toString());
		Log.v(TAG, "launchGameListActivity(): "
				+ " league ID="    + item.getLeagueId()
				+ ", url="         + item.getLeagueURL()
				+ " season ID="    + item.getSeasonId()
				+ ", name="        + item.getSeasonName() 
				+ " division ID="  + item.getDivisionId()
				+ ", name="        + item.getDivisionName());
		startActivity(i);
	}

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
		Log.d(TAG, "onCreate()");
		setContentView(R.layout.activity_fragment);
		String leagueId  = getIntent().getStringExtra("LeagueId");
		String leagueURL = getIntent().getStringExtra("LeagueURL");
		String seasonId  = getIntent().getStringExtra("SeasonId");
		String seasonName= getIntent().getStringExtra("SeasonName");
		mSeasonItem.setLeagueId(leagueId);
		mSeasonItem.setLeagueURL(leagueURL);
		mSeasonItem.setSeasonId(seasonId);
		mSeasonItem.setSeasonName(seasonName);
		setActionBarLeagueName();
		Log.v(TAG, "onCreate() : "
				+ " league ID="    + mSeasonItem.getLeagueId()
				+ ", url="         + mSeasonItem.getLeagueURL()
				+ " season ID="    + mSeasonItem.getSeasonId()
				+ ", name="        + mSeasonItem.getSeasonName()); 
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
	public SeasonItem getSeasonItem () {
		return mSeasonItem;
	}
	
	@TargetApi(11)
	private void setActionBarLeagueName() {
		if (android.os.Build.VERSION.SDK_INT >= 11){
			FavoriteListUtil util = new FavoriteListUtil();
			LeagueItem item = new LeagueItem();
			item=util.getHomeLeagueItem(getApplicationContext());
			if (item == null || item.getLeagueId() == null) {
				return;
			}
			else {
				getActionBar().setTitle(item.getOrgName());
			}
		}
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

    protected void insertDivisionItems(ArrayList<DivisionItem> items) {
    	Log.d(TAG, "insertDivisionItems() to insert count="+items.size());
    	DivisionItem item;
        long count=0;
		count=mHelper.deleteDivisionByDivisionItem(items.get(0)); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
		Log.d(TAG, "insertDivisionItems() prep deleted=" +count);
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertDivisionItems() Division: "
    				+ " league ID="    + item.getLeagueId()
    				+ ", url="         + item.getLeagueURL()
    				+ " season ID="    + item.getSeasonId()
    				+ ", name="        + item.getSeasonName() 
    				+ " division ID="  + item.getDivisionId()
    				+ ", name="        + item.getDivisionName());
            mHelper.insertDivision(item);
            mHelper.close();
        }
        return;
    }
    protected ArrayList<DivisionItem> queryDivisionsBySeasonItem(SeasonItem pk) {
    	DivisionCursor cursor;
    	ArrayList<DivisionItem> items = new ArrayList<DivisionItem>();
    	cursor = mHelper.queryDivisionsBySeasonItem(pk);
    	cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
    		DivisionItem item = cursor.getDivisionItem();
    		items.add(item);
    		cursor.moveToNext();
    		Log.v(TAG, "queryDivisionItem() Division: "
    				+ " league ID="    + item.getLeagueId()
    				+ ", url="         + item.getLeagueURL()
    				+ " season ID="    + item.getSeasonId()
    				+ ", name="        + item.getSeasonName() 
    				+ " division ID="  + item.getDivisionId()
    				+ ", name="        + item.getDivisionName());
    	}
    	cursor.close();
        mHelper.close();
    	return items;
    }
    protected void insertConferenceItems(ArrayList<ConferenceItem> items) {
    	Log.d(TAG, "insertConferenceItems() to insert count="+items.size());
    	ConferenceItem item;
        long count=0;
		count=mHelper.deleteConferenceByConferenceItem(items.get(0)); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
		Log.d(TAG, "insertConferenceItems() prep deleted=" +count);
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
    	cursor = mHelper.queryConferencesByDivisionItem(pk);
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