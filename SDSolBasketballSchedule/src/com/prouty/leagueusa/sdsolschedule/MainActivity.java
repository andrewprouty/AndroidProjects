package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.LeagueCursor;
import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.SeasonCursor;

public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity";
	private DatabaseHelper mHelper;

	protected void launchDivisionListActivity(SeasonItem item) {
		Intent i = new Intent (MainActivity.this, DivisionListActivity.class);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		i.putExtra("SeasonId", item.getSeasonId().toString());
		i.putExtra("SeasonName", item.getSeasonName().toString());
		Log.d(TAG, "launchDivisionListActivity() season: "
				+ item.getLeagueId() + " ("
				+ item.getLeagueURL() + "); "
				+ item.getSeasonId() + "-"
				+ item.getSeasonName());
		startActivity(i);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_edit_settings:
	    		Log.d(TAG, "onOptionsItemSelected() calling EditSettingsActivity");
	    		Intent i = new Intent (MainActivity.this, EditSettingsActivity.class);
	    		startActivity(i);
	            return true;
	        case R.id.action_choose_favorite:
	    		Log.d(TAG, "onOptionsItemSelected() calling TBD Popup menu");
	            return true;
	        case 1:
	    		Log.d(TAG, "onOptionsItemSelected() called from #1");
	            return true;
	        case 2:
	    		Log.d(TAG, "onOptionsItemSelected() called from #2");
	            return true;
	        case 3:
	    		Log.d(TAG, "onOptionsItemSelected() called from #3 ");
	            return true;
      	
	        default:
	    		Log.d(TAG, "onOptionsItemSelected() Id: "+item.getItemId());
	            return super.onOptionsItemSelected(item);
	            
	    }
	}
	
	////////////////////////// http://stackoverflow.com/questions/15580111/how-can-i-dynamically-create-menu-items
    /* http://developer.android.com/guide/topics/ui/menus.html
     * http://developer.android.com/reference/android/view/Menu.html#add(int, int, int, int)
     * 
     * groupId	The group identifier that this item should be part of. This can be used to define groups of items for batch state changes. Normally use NONE if an item should not be in a group.
     * itemId	Unique item ID. Use NONE if you do not need a unique ID.
     * order	The order for the item. Use NONE if you do not care about the order. See getOrder().
     * title	The text to display for the item.
     */ 
    //Gets called every time the user presses the menu button, use for dynamic menus
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, 1, Menu.NONE, "First item in the list").setIcon(R.drawable.ic_action_important);
        menu.add(0, 2, Menu.NONE, "Second Team").setIcon(R.drawable.ic_action_important);
        menu.add(0, 3, Menu.NONE, "Nth team").setIcon(R.drawable.ic_action_important);
        return super.onPrepareOptionsMenu(menu);
    }
	//////////////////////////
	
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