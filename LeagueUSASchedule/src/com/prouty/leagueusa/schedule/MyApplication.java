package com.prouty.leagueusa.schedule;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

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

	public String prepareTracker(Tracker t) {
		if (mUserIDType == UserIDTypes.UNKNOWN) {
			Log.w(TAG, "prepareTracker() UNKNOWN. retrieveUserInfoPrefs() and continue");
			retrieveUserInfoPrefs();
		}
		String user=null;
		String debug= null; //TODO remove
		if (mUserIDType == UserIDTypes.UNKNOWN) {
			Log.e(TAG, "prepareTracker() UNKNOWN again! Set to WIP & get(). Event no user, no Add Collection");
			getUserID();
			t.set("&uid", "");
			t.enableAdvertisingIdCollection(false);
			debug="UNKNOWN";
		}
		else if (mUserIDType == UserIDTypes.EMPTY) {
			Log.w(TAG, "prepareTracker() EMPTY. If first usage then OK. Set to WIP & get(). Event no user, no Add Collection");
			getUserID();
			t.set("&uid", "");
			t.enableAdvertisingIdCollection(false);
			debug="EMPTY";
		}
		else if (mUserIDType == UserIDTypes.ADID) {
			Log.d(TAG, "prepareTracker() User=ADID="+mUserADID);
			user=mUserADID;
			t.set("&uid", user);
			t.enableAdvertisingIdCollection(true);
			debug="ADID";
		}
		else if (mUserIDType == UserIDTypes.UUID) {
			Log.d(TAG, "prepareTracker() User=UUID="+mUserUUID);
			user=mUserUUID;
			t.set("&uid", user);
			//"Not to use Advertising ID..." Either they don't want to use it or missing anyway, so set to false.
			t.enableAdvertisingIdCollection(false);
			debug="UUID";
		}
		debug=debug+" user="+user;
		Toast.makeText(getApplicationContext(),debug,Toast.LENGTH_LONG).show();

		return user;
	}
	public synchronized void getUserID() {
		if (mADThread_Waiting == true) {
			Log.w(TAG, "getUserID() ID Waiting. Parallel? Exiting");
		}
		else {
			mADThread_Waiting=true;
			if (mUserIDType == UserIDTypes.UNKNOWN) {
				Log.d(TAG, "getUserID() ID was UNKNOWN, OK, should be starting up. Set to Waiting & getThread()"); //TODO RESET to debug logging?
			}
			else if (mUserIDType == UserIDTypes.EMPTY) {
				Log.w(TAG, "getUserID() ID was EMPTY. If first usage then OK. Set to Waiting & getThread()");
			}
			else if (mUserIDType == UserIDTypes.ADID) {
				Log.w(TAG, "getUserID() ID was ADID. Wasteful to deep check again?");
			}
			else if (mUserIDType == UserIDTypes.UUID) {
				Log.w(TAG, "getUserID() ID was UUID. Wasteful to deep check again?");
			}
			retrieveUserInfoPrefs();  // use something for now
			getAdvertisingIDThread(); // will retrieve/reset & set for next usage
		}

		return;
	}
	private void getAdvertisingIDThread(){
		Log.e(TAG, "getAdvertisingIDThread()");	//TODO reset to Log.debug
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
						Log.w(TAG, "getAdvertisingIDThread() adID is null => UUID");
					} else if (adOptOut != false) {
						Log.w(TAG, "getAdvertisingIDThread() OptOut is true (or null) => UUID");
					} else {
						setType=UserIDTypes.ADID;
						Log.e(TAG, "getAdvertisingIDThread() YES ADID="+adID);	//TODO change back to Log.debug
						if (mUserADID != null && !mUserADID.equals(adID)) { // a Different adID
							Log.d(TAG, "getAdvertisingIDThread() ADID old="+mUserADID+".");
							Log.d(TAG, "getAdvertisingIDThread() ADID new="+adID+".");
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

				if(setType == mUserIDType && !otherChange) {
					Log.e(TAG, "getAdvertisingIDThread() done. No changes");	//TODO change back to Log.debug
				} else {
					Log.e(TAG, "getAdvertisingIDThread() calling save, otherChange="+otherChange);	//TODO change back to Log.debug
					saveUserInfo(setType);
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
				Log.w(TAG, "saveUserInfo() UUID GENERATED mUserUUID=" + mUserUUID);		//TODO normal so return to Log.debug
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


	public synchronized Tracker getTracker(TrackerName trackerId) {
		Log.d(TAG, "getTracker()");
		if (!mTrackers.containsKey(trackerId)) {
			Log.e(TAG, "getTracker() CREATED TRACKER");	//TODO reset to Log.debug
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER)  ? analytics.newTracker(R.xml.app_tracker)
					: (trackerId == TrackerName.MONEY_TRACKER)? analytics.newTracker(R.xml.money_tracker)
					: analytics.newTracker(PROPERTY_ID);	//else GLOBAL_TRACKER
			mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}
}