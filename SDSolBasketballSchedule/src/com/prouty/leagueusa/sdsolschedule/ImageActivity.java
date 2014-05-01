package com.prouty.leagueusa.sdsolschedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class ImageActivity extends FragmentActivity{

	private static final String TAG = "ImageActivity";
	private PhotoItem mPhotoItem = new PhotoItem();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		Log.d(TAG, "onCreate()");

		String uId = getIntent().getStringExtra("UserId");
		String uName = getIntent().getStringExtra("UserName");
		String pId = getIntent().getStringExtra("PhotoId");
		String pName = getIntent().getStringExtra("PhotoName");
		initPhotoItem(uId, uName, pId, pName);

		FragmentManager manager = getSupportFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = createFragment();
			manager.beginTransaction()
			.add(R.id.fragmentContainer, fragment)
			.commit();
		}
	}

	public Fragment createFragment() {
		return new ImageFragment();
	}

	public void initPhotoItem (String uId, String uName, String pId, String pName) {
		mPhotoItem.setUserId(uId);
		mPhotoItem.setUserName(uName);
		mPhotoItem.setPhotoId(pId);
		mPhotoItem.setPhotoName(pName);
		Log.d(TAG, "initPhotoItem(): "
				  + mPhotoItem.getUserId() + "-"
				  + mPhotoItem.getUserName() + "; "
				  + mPhotoItem.getPhotoId() + "-"
				  + mPhotoItem.getPhotoName());
	}

	public PhotoItem getPhotoItem () {
		return mPhotoItem;
	}
}