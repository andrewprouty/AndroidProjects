package com.prouty.leagueusa.schedule;

import java.lang.reflect.Field;
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
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.prouty.leagueusa.schedule.DatabaseHelper.SeasonCursor;

public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity";
	private LeagueItem   mLeagueItem = new LeagueItem();

	private DatabaseHelper mHelper;
	private ArrayList<FavoriteItem> mFavoriteItems;
	private FavoriteItem mFavoriteItem;
	private TeamItem mFavoriteTeam;

	protected void launchLeagueListActivity() {
		Log.d(TAG, "launchLeagueListActivity()"); 
		Intent i = new Intent (MainActivity.this, LeagueListActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
	}
	protected void launchDivisionListActivity(SeasonItem item) {
		Intent i = new Intent (MainActivity.this, DivisionListActivity.class);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		i.putExtra("SeasonId", item.getSeasonId().toString());
		i.putExtra("SeasonName", item.getSeasonName().toString());
		Log.d(TAG, "launchDivisionListActivity() season: "
				+ " league ID="    + item.getLeagueId()
				+ ", url="         + item.getLeagueURL()
				+ " season ID="    + item.getSeasonId()
				+ ", name="        + item.getSeasonName()); 
		startActivity(i);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		if (needLeague()) {
			launchLeagueListActivity();
			finish();
		}
		else {
			getOverflowMenu();
			setContentView(R.layout.activity_fragment);
			setActionBarLeague(mLeagueItem);
			FragmentManager manager = getSupportFragmentManager();
			Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

			if (fragment == null) {
				fragment = new SeasonListFragment();
				manager.beginTransaction()
				.add(R.id.fragmentContainer, fragment)
				.commit();
			}
			mHelper = new DatabaseHelper(getApplicationContext());
		}
	}
	private boolean needLeague() {
		FavoriteListUtil util = new FavoriteListUtil();
		mLeagueItem=util.getHomeLeagueItem(getApplicationContext());
		if (mLeagueItem == null || mLeagueItem.getLeagueId() == null) {
			return true;
		}
		else {
			return false;
		}
	}
	public LeagueItem getLeagueItem () {
		return mLeagueItem;
	}
	private void setActionBarLeague(LeagueItem item) {
		Log.d(TAG, "setActionBarLeague() API Level="+android.os.Build.VERSION.SDK_INT);
		setActionBarLeagueName(item);
		//ENH setActionBarLeagueIcon(item);
	}
	@TargetApi(11)
	private void setActionBarLeagueName(LeagueItem item) {
		if (android.os.Build.VERSION.SDK_INT >= 11){
			getActionBar().setTitle(item.getOrgName());
		}
	}
	@TargetApi(14)
	private void setActionBarLeagueIcon(LeagueItem item) {
		if (android.os.Build.VERSION.SDK_INT >= 14){
			getActionBar().setIcon(R.drawable.sd_sol_icon);
		}
	}
	private void getOverflowMenu() {
		// had a problem with 1-phone (http://stackoverflow.com/questions/9739498/android-action-bar-not-showing-overflow)
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
		supportInvalidateOptionsMenu();
		//invalidateOptionsMenu(); API 11+
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
			/* http://developer.android.com/reference/android/view/Menu.html#add(int, int, int, int)
			 * groupId	The group identifier that this item should be part of. This can be used to define groups of items for batch state changes. Normally use NONE if an item should not be in a group.
			 * itemId	Unique item ID. Use NONE if you do not need a unique ID.
			 * order	The order for the item. Use NONE if you do not care about the order. See getOrder().
			 * title	The text to display for the item. */
		}
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected() Id: "+item.getItemId());

		if (item.getItemId() == android.R.id.home) {
			launchLeagueListActivity();
		}
		else {
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
		}
		return super.onOptionsItemSelected(item);
	}
	protected void insertSeasonItems(ArrayList<SeasonItem> items) {
		Log.d(TAG, "insertSeasonItems() to insert count="+items.size());
		SeasonItem item;
		long count=0;
		count=mHelper.deleteSeasonBySeasonItem(items.get(0)); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
		Log.d(TAG, "insertSeasonItems() prep deleted=" +count);
		for (int i=0; i<items.size(); i++) {
			item=items.get(i);
			Log.v(TAG, "insertSeasonItems() league: "+ item.getLeagueId() + "-" + item.getLeagueURL() + "-"
					+ item.getSeasonId() + "-"+ item.getSeasonName());
			mHelper.insertSeason(item);
			mHelper.close();
		}
		return;
	}
	protected ArrayList<SeasonItem> querySeasonItemsByLeagueItem(LeagueItem pk) {
		SeasonCursor cursor;
		ArrayList<SeasonItem> items = new ArrayList<SeasonItem>();
		cursor = mHelper.querySeasonsByLeagueItem(pk);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			SeasonItem item = cursor.getSeasonItem();
			items.add(item);
			cursor.moveToNext();
			Log.v(TAG, "querySeasonItem() Season: "
					+ item.getLeagueId() + "-"
					+ item.getLeagueURL() + "-"
					+ item.getSeasonId() + "-"
					+ item.getSeasonName());
		}
		cursor.close();
		mHelper.close();
		return items;
	}
}