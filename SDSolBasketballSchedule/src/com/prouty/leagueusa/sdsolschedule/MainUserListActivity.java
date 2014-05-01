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

public class MainUserListActivity extends FragmentActivity {
	private static final String TAG = "MainUserListActivity";
	private int mPosition;
	private DatabaseHelper mHelper;
	
	protected void launchPhotoListActivity(UserItem user) {
		Intent i = new Intent (MainUserListActivity.this, PhotoListActivity.class);
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
	    		Intent i = new Intent (MainUserListActivity.this, UploadFileActivity.class);
	    		startActivity(i);
	            return true;
	        default:
	    		Log.d(TAG, "onOptionsItemSelected() Id: "+item.getItemId());
	            return super.onOptionsItemSelected(item);
	    }
	}
	
    public Fragment createFragment() {
		 return new UserListFragment();
	}

	public int getPosition () {
		return mPosition;
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
