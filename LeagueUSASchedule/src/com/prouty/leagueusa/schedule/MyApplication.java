package com.prouty.leagueusa.schedule;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

public class MyApplication extends Application {
	//Based largely on this-- http://www.javacodegeeks.com/2014/04/working-with-google-analytics-api-v4-for-android.html
	private static final String TAG = "MyApplication";

	// The following line should be changed to include the correct property id.
	private static final String PROPERTY_ID = "UA-55871696-2"; // unused global & money tracker

	private enum UserIDTypes {
		UNKNOWN,// Initialized, don't know current preference value
		EMPTY,	// Confirmed empty (first start-up, or re-install the application)
		ADID,	// An Advertising ID.  Requires BOTH the ID available AND the user NOT opting out
		UUID	// If Advertising ID not available, generate & store a UUID  
		};
	private static String mUserUUID=null;
	private static String mUserADID=null;
	private static boolean mADThread_Waiting=false;
	private static UserIDTypes mUserIDType=UserIDTypes.UNKNOWN;

	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		MONEY_TRACKER, // Tracker used for all money across applications
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
	}
	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	public MyApplication() {
		super();
	}

	public synchronized Tracker getTracker(TrackerName trackerId) {
		Log.d(TAG, "getTracker()");
		if (!mTrackers.containsKey(trackerId)) {
			Log.d(TAG, "getTracker() CREATED TRACKER");
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER)  ? analytics.newTracker(R.xml.app_tracker)
					: (trackerId == TrackerName.MONEY_TRACKER)? analytics.newTracker(R.xml.money_tracker)
					: analytics.newTracker(PROPERTY_ID);	//else GLOBAL_TRACKER
			mTrackers.put(trackerId, t);
		}
		prepareTracker(mTrackers.get(trackerId));
		return mTrackers.get(trackerId);
	}
	
	private String prepareTracker(Tracker t) {
		if (mUserIDType == UserIDTypes.UNKNOWN) {
			Log.w(TAG, "prepareTracker() UNKNOWN. retrieveUserInfoPrefs() and continue");
			retrieveUserInfoPrefs();
		}
		String user=null;
		if (mUserIDType == UserIDTypes.UNKNOWN) {
			Log.e(TAG, "prepareTracker() UNKNOWN again! Set to WIP & get(). Event no user, no Add Collection");
			setUserID();
			t.set("&uid", "");
			t.enableAdvertisingIdCollection(false);
		}
		else if (mUserIDType == UserIDTypes.EMPTY) {
			Log.w(TAG, "prepareTracker() EMPTY. If first usage then OK. Set to WIP & get(). Event no user, no Add Collection");
			setUserID();
			t.set("&uid", "");
			t.enableAdvertisingIdCollection(false);
		}
		else if (mUserIDType == UserIDTypes.ADID) {
			Log.d(TAG, "prepareTracker() User=ADID="+mUserADID);
			user=mUserADID;
			t.set("&uid", user);
			t.enableAdvertisingIdCollection(true);
		}
		else if (mUserIDType == UserIDTypes.UUID) {
			Log.d(TAG, "prepareTracker() User=UUID="+mUserUUID);
			user=mUserUUID;
			t.set("&uid", user);
			//"Not to use Advertising ID..." instead using a local UUID, not the Advertising ID
			t.enableAdvertisingIdCollection(true);
		}

		return user;
	}
	public String getUserID() {
		String user=null;
		if (mUserIDType == UserIDTypes.ADID) {
			user=mUserADID;
			Log.d(TAG, "getUserID() ID ADID="+user);
		}
		else if (mUserIDType == UserIDTypes.UUID) {
			user=mUserUUID;
			Log.d(TAG, "getUserID() ID UUID="+user);
		}
		else {
			Log.w(TAG, "getUserID() not set, returning user="+user);
		}
		return user;
	}
	public synchronized void setUserID() {
		if (mADThread_Waiting == true) {
			Log.w(TAG, "setUserID() ID Waiting, parallel? Exiting.");
		}
		else {
			mADThread_Waiting=true;
			if (mUserIDType == UserIDTypes.UNKNOWN) {
				Log.d(TAG, "setUserID() ID was UNKNOWN, OK, should be starting up. Set to Waiting & getThread()");
			}
			else if (mUserIDType == UserIDTypes.EMPTY) {
				Log.w(TAG, "setUserID() ID was EMPTY. If first usage then OK. Set to Waiting & getThread()");
			}
			else if (mUserIDType == UserIDTypes.ADID) {
				Log.d(TAG, "setUserID() ID was ADID. Very thorough but set to Waiting & getThread()");
			}
			else if (mUserIDType == UserIDTypes.UUID) {
				Log.d(TAG, "setUserID() ID was UUID. Very thorough but set to Waiting & getThread()");
			}
			retrieveUserInfoPrefs();  // use something while waiting
			getAdvertisingIDThread(); // will retrieve/reset & set for next usage
		}

		return;
	}
	private void getAdvertisingIDThread(){
		Log.d(TAG, "getAdvertisingIDThread()");
		new Thread(new Runnable() {
			@Override public void run() {
				// See sample code at http://developer.android.com/google/play-services/id.html
				UserIDTypes setType=UserIDTypes.UUID; // initialize rather than set for each error condition
				String adID;
				boolean adOptOut;
				boolean otherChange=false;
				try {
					Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
					adID = adInfo.getId();
					adOptOut = adInfo.isLimitAdTrackingEnabled();

					if (adID == null) {
						Log.d(TAG, "getAdvertisingIDThread() adID is null => UUID");
					} else if (adOptOut != false) {
						Log.d(TAG, "getAdvertisingIDThread() OptOut is true (or null) => UUID");
					} else {
						setType=UserIDTypes.ADID;
						Log.d(TAG, "getAdvertisingIDThread() YES ADID="+adID);
						if (mUserADID != null && !mUserADID.equals(adID)) { // a Different adID
							Log.d(TAG, "getAdvertisingIDThread() ADID changed old="+mUserADID+".");
							Log.d(TAG, "getAdvertisingIDThread() ADID changed new="+adID+".");
							otherChange = true;
						}
						mUserADID = adID;
					}
				} catch (IOException e) {
					Log.e(TAG, "getIDThread() IOException " + e);
					// Unrecoverable error connecting to Google Play services (e.g.,
					// the old version of the service doesn't support getting AdvertisingId).
				} catch (GooglePlayServicesNotAvailableException e) {
					Log.e(TAG, "getIDThread() GooglePlayServicesNotAvailableException " + e);
					// Google Play services is not available entirely.
				} catch (GooglePlayServicesRepairableException e) {
					Log.e(TAG, "getIDThread() GooglePlayServicesRepairableException " + e);
					// Encountered a recoverable error connecting to Google Play services.
				} catch (NullPointerException e) {
					Log.e(TAG, "getIDThread() NullPointerException " + e);
					// getId() is sometimes null
				}

				if(setType != mUserIDType) {
					Log.d(TAG, "getAdvertisingIDThread() save user ID type");
					saveUserInfo(setType);
				}
				else if (otherChange) {
					Log.d(TAG, "getAdvertisingIDThread() save new Advertising ID");
					saveUserInfo(setType);
				} else {
					Log.d(TAG, "getAdvertisingIDThread() done. No changes");
				}
				mADThread_Waiting=false;
			}
		}).start();
	return;
	}
	private void retrieveUserInfoPrefs() {
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(FavoriteListUtil.PREFS_NAME, Context.MODE_PRIVATE);
		String userType=prefs.getString(FavoriteListUtil.USER_TYPE, "EMPTY");
		mUserADID=prefs.getString(FavoriteListUtil.USER_ADID, null);
		mUserUUID=prefs.getString(FavoriteListUtil.USER_UUID, null);
		switch (userType) {
		case "EMPTY":
			Log.d(TAG, "retrieveUserInfo() "+userType+", ADID=" + mUserADID+", UUID="+mUserUUID);
			mUserIDType = UserIDTypes.EMPTY;
			break;
		case "ADID":
			Log.d(TAG, "retrieveUserInfo() "+userType+", ADID=" + mUserADID+", UUID="+mUserUUID);
			mUserIDType = UserIDTypes.ADID;
			break;
		case "UUID":
			Log.d(TAG, "retrieveUserInfo() "+userType+", ADID=" + mUserADID+", UUID="+mUserUUID);
			mUserIDType = UserIDTypes.UUID;
			// Preserve, rather than re-generate a new UUID... since 
			break;
		default:
			Log.e(TAG, "retrieveUserInfo() "+userType+", ADID=" + mUserADID+", UUID="+mUserUUID);
			mUserIDType = UserIDTypes.UNKNOWN;
			break;
		}
		return;
	}
	
	public void saveUserInfo(UserIDTypes userType) {
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(FavoriteListUtil.PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(FavoriteListUtil.USER_TYPE);
		if (userType == UserIDTypes.ADID) {
			if (mUserADID == null) { // Crap - should have been set already... switch to UUID
				Log.e(TAG, "saveUserInfo() ADID requested, but no ADID - rediret to UUID");
				userType=UserIDTypes.UUID;
			}
			else {
				mUserIDType = UserIDTypes.ADID;
				editor.putString(FavoriteListUtil.USER_ADID,mUserADID);
				editor.putString(FavoriteListUtil.USER_TYPE,"ADID");
				editor.commit();
				Log.d(TAG, "saveUserInfo() ADID saved mUserADID=" + mUserADID);
			}
		}
		if (userType == UserIDTypes.UUID) {
			mUserIDType = UserIDTypes.UUID;
			if (mUserUUID == null) { // if exists, re-use
				editor.remove(FavoriteListUtil.USER_UUID);
				mUserUUID = UUID.randomUUID().toString();
				Log.d(TAG, "saveUserInfo() UUID GENERATED mUserUUID=" + mUserUUID);
				editor.putString(FavoriteListUtil.USER_UUID,mUserUUID);
			}
			else {	//USER_UUID is already set
				Log.d(TAG, "saveUserInfo() UUID existing mUserUUID=" + mUserUUID);
			}
			editor.putString(FavoriteListUtil.USER_TYPE,"UUID");
			editor.commit();
		}
		else if (userType == UserIDTypes.EMPTY) {
			Log.e(TAG, "saveUserInfo() EMPTY requested... should set to UUID or ADID");
		}
		else if (userType == UserIDTypes.UNKNOWN) {
			Log.e(TAG, "saveUserInfo() UNKNOWN requested... should set to UUID or ADID");
		}
		return;
	}
}