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
import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.UserCursor;

public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity";
	private int mPosition;
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

	protected void launchPhotoListActivity(UserItem user) {
		Intent i = new Intent (MainActivity.this, PhotoListActivity.class);
		i.putExtra("UserId", user.getUserId().toString());
		i.putExtra("UserName", user.getUserName().toString());
		Log.d(TAG, "launchPhotoListActivity() user: "
				+ user.getUserId() + "-"
				+ user.getUserName());
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
	        case R.id.action_upload:
	    		Log.d(TAG, "onOptionsItemSelected() calling UploadFileActivity");
	    		Intent i = new Intent (MainActivity.this, UploadFileActivity.class);
	    		startActivity(i);
	            return true;
	        default:
	    		Log.d(TAG, "onOptionsItemSelected() Id: "+item.getItemId());
	            return super.onOptionsItemSelected(item);
	    }
	}
	
    public Fragment createFragment() {
		 return new SeasonListFragment();
	}

	public int getPosition () {
		return mPosition;
	}
    protected void insertLeagueItems(ArrayList<LeagueItem> items) {
        LeagueItem item;
        Log.d(TAG, "insertLeagueItems()");
		mHelper.deleteLeague(); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertLeagueItems() league: "+ item.getLeagueId() + "-"+ item.getOrgName());
            mHelper.insertLeague(item);
            mHelper.close();
        }
        return;
    }
    protected void insertSeasonItems(ArrayList<SeasonItem> items) {
        SeasonItem item;
        Log.d(TAG, "insertSeasonItems()");
		mHelper.deleteSeason(); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertSeasonItems() league: "+ item.getLeagueId() + "-"+ item.getSeasonId() + "-"+ item.getSeasonName());
            mHelper.insertSeason(item);
            mHelper.close();
        }
        return;
    }
    protected void insertUserItems(ArrayList<UserItem> items) {
        UserItem item;
        Log.d(TAG, "insertUserItems()");
		mHelper.deleteUsers(); // By default parent key is not "RESTRICT" from delete (http://www.sqlite.org/foreignkeys.html)
        for (int i=0; i<items.size(); i++) {
    		item=items.get(i);
    		Log.v(TAG, "insertUserItems() user: "+ item.getUserId() + "-"+ item.getUserName());
            mHelper.insertUser(item);
            mHelper.close();
        }
        return;
    }
    protected ArrayList<UserItem> queryUserItems() {
    	UserCursor cursor;
    	ArrayList<UserItem> items = new ArrayList<UserItem>();
    	cursor = mHelper.queryUsers();
    	cursor.moveToFirst();
    	while(!cursor.isAfterLast()) {
			UserItem item = cursor.getUserItem();
    		items.add(item);
    		cursor.moveToNext();
    		Log.d(TAG, "queryUserItem() user: "
    				+ item.getUserId() + "-"
    				+ item.getUserName());
    	}
    	cursor.close();
        mHelper.close();
    	return items;
    }
}
