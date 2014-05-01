package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.PhotoCursor;

public class ImagePagerActivity extends FragmentActivity 
	implements ImageFragment.Callbacks {
	private static final String TAG = "ImagePagerActivity";
	private static ArrayList<PhotoItem> mQueryPhotos;
	private static PhotoItem mQueryPhoto = new PhotoItem();
	private UserItem mUserItem = new UserItem();
	private int mImageWidth = 0;
	private int mImageHeight = 0;

	private DatabaseHelper mHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		String uId   = getIntent().getStringExtra("UserId");
		String uName = getIntent().getStringExtra("UserName");
		int position = getIntent().getIntExtra ("position",0);
		Log.d(TAG, "onCreate().position: "+position);
		initUserItem(uId, uName);

		mHelper = new DatabaseHelper(getApplicationContext());
		mQueryPhotos=queryPhotoItemsforUserId(mUserItem);
		mQueryPhoto=mQueryPhotos.get(position);
		Log.d(TAG, "onCreate() mQueryPhoto: "
				+ mQueryPhoto.getUserId() + "-"
				+ mQueryPhoto.getUserName() + "; "
				+ mQueryPhoto.getPhotoId() + "-"
				+ mQueryPhoto.getPhotoName());

		ViewPager mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			@Override
			public int getCount() {
				return mQueryPhotos.size();
			}
			@Override
			public Fragment getItem(int pos) {
				mQueryPhoto=mQueryPhotos.get(pos);
				Log.d(TAG, "OnCreate().setAdapter().getItem ["+pos+"] "
						+ mQueryPhoto.getPhotoId() + "-"
						+ mQueryPhoto.getPhotoName());
				return new ImageFragment().init(pos);
			}
		});
		mViewPager.setCurrentItem(position);
	}

	private void initUserItem (String uId, String uName) {
		mUserItem.setUserId(uId);
		mUserItem.setUserName(uName);
		Log.d(TAG, "initUserItem(): "
				+ mUserItem.getUserId() + "-"
				+ mUserItem.getUserName());
	}

	public Boolean isTwoPane() { //Callback
		return false;
	}
	public PhotoItem getPhotoItem(int pos) { //Callback
		Log.d(TAG, "getPhotoItem() ["+pos+"] size:"+mQueryPhotos.size());
		PhotoItem item = mQueryPhotos.get(pos);
		return item;
	}

	protected ArrayList<PhotoItem> queryPhotoItemsforUserId(UserItem user) {
		PhotoCursor cursor;
		Log.d(TAG, "queryPhotoItemsforUser() UserId: "+user.getUserId());
		ArrayList<PhotoItem> items = new ArrayList<PhotoItem>();
		cursor = mHelper.queryPhotosForUserId(user.getUserId());
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			PhotoItem item = cursor.getPhotoItem();
			items.add(item);
			cursor.moveToNext();
			Log.d(TAG, "fetchPhotoItemsforUserId(): "
					+ item.getUserId() + "-"
					+ item.getUserName() + "; "
					+ item.getPhotoId() + "-"
					+ item.getPhotoName());
		}
		cursor.close();
        mHelper.close();
		return items;
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
}