package com.prouty.leagueusa.sdsolschedule;

import java.lang.reflect.Field;
import java.util.ArrayList;

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

import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.LeagueCursor;
import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.SeasonCursor;

public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity";
	private DatabaseHelper mHelper;
	private Menu mMenu;
	private ArrayList<FavoriteItem> mFavoriteItems;
	private FavoriteItem mFavoriteItem;
	private TeamItem mFavoriteTeam;
	private boolean mFavorite = true; //TODO use a orientation-switch safe approach

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
        getOverflowMenu();
        setContentView(R.layout.activity_fragment);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
 
        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();
        }
        mHelper = new DatabaseHelper(getApplicationContext());
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
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu()");
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main_actions, menu);
	    mMenu=menu;
	    return super.onCreateOptionsMenu(menu);
	}*/
    //Gets called every time the user presses the menu button, use for dynamic menus
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		FavoriteListUtil util = new FavoriteListUtil();
		FavoriteItem favItem = new FavoriteItem();
		mFavoriteItems=util.getFavoriteList(getApplicationContext());
		menu.removeGroup(1);
		for (int i=0; i<mFavoriteItems.size(); i++) {
			favItem=mFavoriteItems.get(i);
			Log.v(TAG, "onPrepareOptionsMenu() ["+i+"] "+"fav="
					+favItem.getFavoriteName()+"-"+favItem.getFavoriteURL());
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
	    switch (item.getItemId()) {
	        case R.id.action_refresh:
	    		Log.d(TAG, "onOptionsItemSelected() calling refresh");
	            return true;
	        case R.id.action_choose_important:
	    		Log.d(TAG, "onOptionsItemSelected() muy importante");
	    		MenuItem starred = mMenu.findItem(R.id.action_choose_important);
	    		if (mFavorite) {
	    			starred.setIcon(R.drawable.ic_action_not_important);
	    			mFavorite = false;
	    		}
	    		else {
	    			starred.setIcon(R.drawable.ic_action_important);
	    			mFavorite = true;
	    		}
	            return true;
	        default:
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
	}
	
    public Fragment createFragment() {
		 return new SeasonListFragment();
	}

    protected void insertLeagueItems(ArrayList<LeagueItem> items) {
        LeagueItem item;
        Log.d(TAG, "insertLeagueItems()");
		mHelper.deleteLeague(); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertLeagueItems() league: "+item.getLeagueId()+"-"+item.getOrgName()+item.getLeagueURL());
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
    		Log.d(TAG, "queryLeagueItem() league: "
    				+ item.getLeagueId() + "-"
    				+ item.getOrgName() + "-"
    				+ item.getLeagueURL());
    	}
    	cursor.close();
        mHelper.close();
    	return items;
    }
    protected void insertSeasonItems(ArrayList<SeasonItem> items) {
        SeasonItem item;
        Log.d(TAG, "insertSeasonItems()");
		mHelper.deleteSeason(); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertSeasonItems() league: "+ item.getLeagueId() + "-" + item.getLeagueURL() + "-"
    				+ item.getSeasonId() + "-"+ item.getSeasonName());
            mHelper.insertSeason(item);
            mHelper.close();
        }
        return;
    }
    protected ArrayList<SeasonItem> querySeasonItemsByLeagueId(LeagueItem pk) {
    	SeasonCursor cursor;
    	ArrayList<SeasonItem> items = new ArrayList<SeasonItem>();
    	cursor = mHelper.querySeasonsByLeagueId(pk.getLeagueId());
    	cursor.moveToFirst();
    	while(!cursor.isAfterLast()) {
    		SeasonItem item = cursor.getSeasonItem();
    		items.add(item);
    		cursor.moveToNext();
    		Log.d(TAG, "querySeasonItem() Season: "
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