package com.prouty.leagueusa.schedule;

import java.util.HashMap;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class MyApplication extends Application {
	//Based largely on this-- http://www.javacodegeeks.com/2014/04/working-with-google-analytics-api-v4-for-android.html

	// The following line should be changed to include the correct property id.
	private static final String PROPERTY_ID = "UA-55871696-2";

	//Logging TAG
	private static final String TAG = "MyApplication";

	public static int GENERAL_TRACKER = 0;

	public enum TrackerName {
		SEASON_LIST_TRACKER, // Tracker used only in this app.
		GAME_LIST_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	public MyApplication() {
		super();
	}

	synchronized Tracker getTracker(TrackerName trackerId) {
		Log.v(TAG, "getTracker()");
		if (!mTrackers.containsKey(trackerId)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.SEASON_LIST_TRACKER) ? analytics.newTracker(R.xml.season_list_tracker)
					  : (trackerId == TrackerName.GAME_LIST_TRACKER)   ? analytics.newTracker(R.xml.game_list_tracker)
					  : analytics.newTracker(PROPERTY_ID);	//else GLOBAL_TRACKER
					mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}
}