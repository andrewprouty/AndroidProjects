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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.prouty.leagueusa.schedule.DatabaseHelper.LeagueCursor;

public class LeagueListActivity extends FragmentActivity {
	private static final String TAG = "LeagueListActivity";
	private DatabaseHelper mHelper;
	
	private ArrayList<FavoriteItem> mFavoriteItems;
	private FavoriteItem mFavoriteItem;
	private TeamItem mFavoriteTeam;

	protected void launchSeasonListActivity(LeagueItem item) {
		Log.d(TAG, "launchSeasonListActivity()");
		
		if (item.getLeagueId() != null) {

			Tracker t = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);

			FavoriteListUtil util = new FavoriteListUtil();
			util.setHomeLeagueItem(getApplicationContext(),item, t);
		}
	
		Intent i = new Intent (this, MainActivity.class);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("OrgName", item.getOrgName().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		Log.v(TAG, "launchSeasonListActivity(): "
				+ " league ID="    + item.getLeagueId()
				+ " name="         + item.getOrgName()
				+ ", url="         + item.getLeagueURL());
		startActivity(i);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
		setContentView(R.layout.activity_fragment);
		setActionBarVicid();
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new LeagueListFragment();
			fm.beginTransaction()
			.add(R.id.fragmentContainer, fragment)
			.commit();
		}
		mHelper = new DatabaseHelper(getApplicationContext());
	}
	private void setActionBarVicid() {
		Log.d(TAG, "setActionBarVicid() API Level="+android.os.Build.VERSION.SDK_INT);
		setActionBarVicidName();
		setActionBarVicidIcon();
	}
	@TargetApi(11)
	private void setActionBarVicidName() {
		if (android.os.Build.VERSION.SDK_INT >= 11){
			getActionBar().setTitle("Vicid Schedules");
		}
	}
	@TargetApi(14)
	private void setActionBarVicidIcon() {
		if (android.os.Build.VERSION.SDK_INT >= 14){
			getActionBar().setIcon(R.drawable.vicid_logo);
		}
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
		supportInvalidateOptionsMenu();
	}
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart()");
		Tracker t = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
		//t.setScreenName(TAG);
        t.send(new HitBuilders.AppViewBuilder().build());
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
				Tracker t = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
				util.launchGameListActivity(getApplicationContext(), mFavoriteTeam, t);
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.broken_must_navigate, Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
    protected void insertLeagueItems(ArrayList<LeagueItem> items) {
    	Log.d(TAG, "insertLeagueItems() to insert count="+items.size());
        LeagueItem item;
        long count=0;
		count=mHelper.deleteLeague(); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
		Log.d(TAG, "insertLeagueItems() prep deleted=" +count);
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertLeagueItems() league: "+item.getLeagueId()+"-"+item.getOrgName()+"-"+item.getLeagueURL());
            mHelper.insertLeague(item);
            mHelper.close();
        }
        return;
    }
    protected ArrayList<LeagueItem> queryLeagueItems() {
    	LeagueCursor cursor;
    	ArrayList<LeagueItem> items = new ArrayList<LeagueItem>();
    	cursor = mHelper.queryLeagues();
    	cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
    		LeagueItem item = cursor.getLeagueItem();
    		items.add(item);
    		cursor.moveToNext();
    		Log.v(TAG, "queryLeagueItem() league: "
    				+ item.getLeagueId() + "-"
    				+ item.getOrgName() + "-"
    				+ item.getLeagueURL());
    	}
    	cursor.close();
        mHelper.close();
    	return items;
    }
}