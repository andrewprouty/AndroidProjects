package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.PhotoCursor;

public class PhotoListActivity extends FragmentActivity
	implements ImageFragment.Callbacks {
	private static final String TAG = "PhotoListActivity";
	private UserItem mUserItem = new UserItem();
	private static ArrayList<PhotoItem> mPhotoItems;
	private int mImageWidth = 0;
	private int mImageHeight = 0;

	private DatabaseHelper mHelper;

	protected void launchPhotoDisplayActivity(PhotoItem photo, int position) {
		Log.d(TAG, "launchPhotoDisplayActivity(): "
				+ photo.getUserId()  + "-"+ photo.getUserName() + "; "
				+ photo.getPhotoId() + "-"+ photo.getPhotoName());
		if (isTwoPane() == false) {
			Log.d(TAG, "launchPhotoDisplayActivity() ["+position+"] via Activity (phone)");
			Intent i = new Intent (PhotoListActivity.this, ImagePagerActivity.class);
			i.putExtra("UserId",   photo.getUserId().toString());
			i.putExtra("UserName", photo.getUserName().toString());
			i.putExtra("position", position);
			startActivity(i);
		}
		else {
			Log.d(TAG, "launchPhotoDisplayActivity() ["+position+"] via Fragment (tablet)");
			createDisplayFrag(position);
		}
	}
	
	private void createDisplayFrag(int position) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
        Fragment oldFrag = fm.findFragmentById(R.id.imageFragmentContainer);
		Fragment newFrag = new ImageFragment().init(position);
		
		if (oldFrag != null) {
			ft.remove(oldFrag);
		}
        ft.add(R.id.imageFragmentContainer, newFrag);
        ft.commit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_masterdetail);
		Log.d(TAG, "onCreate()");

		String id = getIntent().getStringExtra("UserId");
		String name = getIntent().getStringExtra("UserName");
		initUserItem(id, name);

		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new PhotoListFragment();
			fm.beginTransaction()
			.add(R.id.fragmentContainer, fragment)
			.commit();
		}
        mHelper = new DatabaseHelper(getApplicationContext());
	}

	private void initUserItem (String id, String name) {
		mUserItem.setUserId(id);
		mUserItem.setUserName(name);
		Log.d(TAG, "initUserItem() user: "
				+ mUserItem.getUserId() + "-"
				+ mUserItem.getUserName());
	}
	public UserItem getUserItem () {
		return mUserItem;
	}
	
	public Boolean isTwoPane() { //Callback
		//Log.d(TAG, "isTwoPane()");
		if (findViewById(R.id.imageFragmentContainer) == null) {
			return false;
		} else {
			return true;
		}
	}
	public PhotoItem getPhotoItem(int pos) { //Callback
		Log.d(TAG, "getDisplayItem() ["+pos+"] size:"+mPhotoItems.size());
		PhotoItem item = mPhotoItems.get(pos);
		return item;
	}
	// Scenario used: two-pane, view image, exit to user list then see image again. 
	public int handleFieldWidth(int width) { //Callback
		if (width > 0 ) {
			mImageWidth = width;
		}
		return mImageWidth;
	}
	public int handleFieldHeight(int height) { //Callback
		if (height> 0 ) {
			mImageHeight = height;
		}
		return mImageHeight;
	}

	public void setPhotoItems(ArrayList<PhotoItem> items) {
		mPhotoItems = items;
		Log.d(TAG, "setPhotoItems() in:"+items.size()+" set:"+mPhotoItems.size());
		return;
	}
	
	protected void insertPhotoItems(ArrayList<PhotoItem> items, UserItem user) {
		PhotoItem item;
		Log.d(TAG, "insertPhotoItems() user:"+user.getUserId()+"-"+user.getUserName());
		mHelper.deletePhotosforUserId(user.getUserId());
		for (int i=0; i<items.size(); i++) {
			item=items.get(i);
			Log.v(TAG, "insertPhotoItems() user: "
					+ item.getUserId() + "-"  + item.getUserName() + "; "
					+ item.getPhotoId() + "-" + item.getPhotoName());
			mHelper.insertPhoto(item);
		}
		mHelper.close();
		return;
	    }	
	
	protected ArrayList<PhotoItem> queryPhotoItemsforUserId(UserItem user) {
		PhotoCursor cursor;
		ArrayList<PhotoItem> items = new ArrayList<PhotoItem>();
		cursor = mHelper.queryPhotosForUserId(user.getUserId());
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			PhotoItem item = cursor.getPhotoItem();
			items.add(item);
			cursor.moveToNext();
			Log.d(TAG, "queryPhotoItemsforUserId(): "
					+ item.getUserId() + "-"
					+ item.getUserName() + "; "
					+ item.getPhotoId() + "-"
					+ item.getPhotoName());
		}
    	cursor.close();
        mHelper.close();
		return items;
	}
}