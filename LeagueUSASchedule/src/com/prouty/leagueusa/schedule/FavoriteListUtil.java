package com.prouty.leagueusa.schedule;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.prouty.leagueusa.schedule.DatabaseHelper.LeagueCursor;
import com.prouty.leagueusa.schedule.DatabaseHelper.TeamCursor;

public class FavoriteListUtil {
	private static final String TAG = "FavoriteListUtil";
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String HOME_ID = "HOME_ID";
	public static final String HOME_NAME = "HOME_NAME";
	public static final String HOME_URL = "HOME_URL";
	public static final String USER_TYPE = "USER_TYPE";
	public static final String USER_UUID = "USER_UUID";
	public static final String USER_ADID = "USER_ADID";
	//WARNING- if adding a preference file, search for "Fragile" to add to exclusion list below... or will break favorites
	private DatabaseHelper mHelper;
	private ArrayList<FavoriteItem> mFavoriteItems;
	
	private LeagueItem mLeagueItem = new LeagueItem();

	public LeagueItem getHomeLeagueItem(Context context) {
		Log.d(TAG, "getHomeLeageItem()");
		LeagueItem item = new LeagueItem();
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		item.setLeagueId(prefs.getString(HOME_ID, null));
		item.setOrgName(prefs.getString(HOME_NAME, null));
		item.setLeagueURL(prefs.getString(HOME_URL, null));
		Log.d(TAG, "getHomeLeageItem() "
				+ item.getLeagueId() + "-"
				+ item.getOrgName() + " ("
				+ item.getLeagueURL() + ")");
		return item;
	}
	public void setHomeLeagueItem(Context context, LeagueItem item, Tracker t) {
		Log.d(TAG, "setHomeLeagueItem() "
				+ item.getLeagueId() + "-"
				+ item.getOrgName() + " ("
				+ item.getLeagueURL() + ")");
		
		mLeagueItem=getHomeLeagueItem(context);
		//TODO remove t.set("&uid", FavoriteListUtil.getClientID(context));
		//TODO remove t.enableAdvertisingIdCollection(true);
		if (mLeagueItem == null || mLeagueItem.getLeagueId() == null) {
			// ADD, nothing currently
			t.send(new HitBuilders.EventBuilder()
			    .setCategory("HomeLeague")
			    .setAction("LeagueUSA")
			    .setLabel(item.getOrgName())
			    .setValue(1)
			    .build());
		}
		else { // REMOVE old homeleague, ADD the new one
			t.send(new HitBuilders.EventBuilder()
				.setCategory("HomeLeague")
				.setAction("LeagueUSA")
				.setLabel(mLeagueItem.getOrgName())
				.setValue(-1)
				.build());
			t.send(new HitBuilders.EventBuilder()
				.setCategory("HomeLeague")
				.setAction("LeagueUSA")
				.setLabel(item.getOrgName())
				.setValue(1)
				.build());
		}
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(HOME_ID);
		editor.remove(HOME_NAME);
		editor.remove(HOME_URL);
		editor.putString(HOME_ID,item.getLeagueId());
		editor.putString(HOME_NAME,item.getOrgName());
		editor.putString(HOME_URL,item.getLeagueURL());
		editor.commit();
		return;
	}

	private void eventFavoriteTeamTotal(Context context, Tracker t, String userID) {
		mFavoriteItems=getFavoriteList(context); // reusing to count
		int count = mFavoriteItems.size();
		Log.d(TAG, "eventFavoriteCount() favorite count="+count);
		//uid is set for this tracker already
		t.enableAdvertisingIdCollection(true);
		t.send(new HitBuilders.EventBuilder()
		.setCategory("FavoriteTeamTotal")
		.setAction("LeagueUSA")
		.setLabel(userID)
		.setValue(count) //Total # of favorites
		.build());
		return;
	}
	public FavoriteItem addFavoriteTeamItem(Context context, TeamItem team, Tracker t, String user) {
		FavoriteItem fav = new FavoriteItem();
		if(team.getConferenceCount().equals("one")) {
			fav.setFavoriteName(team.getTeamName()+"/"
					+ team.getDivisionName()+"/"
					+ team.getSeasonName());
		}
		else {
			fav.setFavoriteName(team.getTeamName()+"/"
					+ team.getConferenceName()+"/"
					+ team.getDivisionName()+"/"
					+ team.getSeasonName());
		}	    				
		fav.setFavoriteURL(team.getTeamURL());
		Log.d(TAG, "addFavoriteItem() Key="+fav.getFavoriteURL()+" Value="+fav.getFavoriteName());
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		//To truncate preferences file use: editor.clear();
		//URL is absolute, the name could change (if team goes from single to multiple conferences division)
		editor.putString(fav.getFavoriteURL(),fav.getFavoriteName());
		editor.commit();

		String fullName = queryOrgNameByTeamItem(context, team);
		if(team.getConferenceCount().equals("one")) {
			fullName = fullName+"/"+team.getSeasonName()+"/"
					+team.getDivisionName()+"/"+"/"+team.getTeamName();
		}
		else {
			fullName = fullName+"/"+team.getSeasonName()+"/"
					+team.getDivisionName()+"/"+team.getConferenceName()+"/"+team.getTeamName();
		}
		//TODO remove String user = FavoriteListUtil.getClientID(context);
		//TODO remove t.set("&uid", user);
		//TODO remove t.enableAdvertisingIdCollection(true);
		t.send(new HitBuilders.EventBuilder()
		.setCategory("FavoriteTeamByTeam")
		.setAction("LeagueUSA")
		.setLabel(fullName)
		.setValue(1) // Add 1
		.build());
		t.send(new HitBuilders.EventBuilder()
		.setCategory("FavoriteTeamByUser")
		.setAction("LeagueUSA")
		.setLabel(user)
		.setValue(1) // Add 1
		.build());
		eventFavoriteTeamTotal(context, t, user);
		return fav;
	}
	public void removeFavoriteTeamItem(Context context, TeamItem team, Tracker t, String user) {
		mLeagueItem=getHomeLeagueItem(context);
		String keyTeamURL=team.getTeamURL();
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(keyTeamURL);
		editor.commit();
		Log.d(TAG, "removeFavoriteItem() Key="+keyTeamURL);

		String fullName = queryOrgNameByTeamItem(context, team);
		if(team.getConferenceCount().equals("one")) {
			fullName = fullName+"/"+team.getSeasonName()+"/"
					+team.getDivisionName()+"/"+"/"+team.getTeamName();
		}
		else {
			fullName = fullName+"/"+team.getSeasonName()+"/"
					+team.getDivisionName()+"/"+team.getConferenceName()+"/"+team.getTeamName();
		}
		//TODO remove String user = FavoriteListUtil.getClientID(context);
		//TODO remove t.set("&uid", user);
		//TODO remove t.enableAdvertisingIdCollection(true);
		t.send(new HitBuilders.EventBuilder()
		.setCategory("FavoriteTeamByTeam")
		.setAction("LeagueUSA")
		.setLabel(fullName)
		.setValue(-1) // Subtract 1
		.build());
		t.send(new HitBuilders.EventBuilder()
		.setCategory("FavoriteTeamByUser")
		.setAction("LeagueUSA")
		.setLabel(user)
		.setValue(-1) // Subtract 1
		.build());
		eventFavoriteTeamTotal(context, t, user);
		return;
	}

	public ArrayList<FavoriteItem> getFavoriteList(Context context) {
		ArrayList<FavoriteItem> items = new ArrayList<FavoriteItem>();
		FavoriteItem item;
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Boolean empty = false;
		for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            Object val = entry.getValue();
            //Fragile
            if (!entry.getKey().equals(HOME_ID)
            		&& !entry.getKey().equals(HOME_NAME)
            		&& !entry.getKey().equals(HOME_URL)
            		&& !entry.getKey().equals(USER_TYPE)
            		&& !entry.getKey().equals(USER_UUID)
            		&& !entry.getKey().equals(USER_ADID)) {
     			item = new FavoriteItem();
     			item.setFavoriteURL(entry.getKey());
                item=getFieldsFromURL(item);
                if (val == null) {
        			Log.e(TAG, "getFavoriteList() NULL Key="+entry.getKey()+" Value="+entry.getValue());
        			empty = true;
                }
                else {
            		//Log.v(TAG, "getFavoriteList() Entry Key="+entry.getKey()+       " Value="+entry.getValue());
         			item.setFavoriteName(entry.getValue().toString());
            		Log.v(TAG, "getFavoriteList() Key="+item.getFavoriteURL()+" Value="+item.getFavoriteName());
                }
     			items.add(item);
            }
        }
		if (empty) {
			SharedPreferences.Editor editor = prefs.edit();
	        for (int i=0; i<items.size(); i++) {
	    		item=items.get(i);
	    		if(item.getFavoriteName() == null){
	    			editor.remove(item.getFavoriteURL());
	    			Log.w(TAG, "getFavoriteList() removed Key="+item.getFavoriteURL()+" Value="+item.getFavoriteName());
	    		}
	        }
	        editor.commit();
		}
		return items;
	}
	private FavoriteItem getFieldsFromURL (FavoriteItem item) {
		Log.d(TAG, "getFieldsFromURL() FavoriteItem");
    	Uri uri = Uri.parse(item.getFavoriteURL());
    	item.setLeagueId(uri.getQueryParameter("league"));
    	item.setSeasonId(uri.getQueryParameter("season"));
    	item.setDivisionId(uri.getQueryParameter("division"));
    	item.setConferenceId(uri.getQueryParameter("conference"));
    	item.setTeamId(uri.getQueryParameter("team"));
    	int position=item.getFavoriteURL().indexOf("?");
    	item.setLeagueURL(item.getFavoriteURL().substring(0,position));
		Log.d(TAG, "getFieldsFromURL() FavoriteItem: "
				+ " league ID="    + item.getLeagueId()
				+ ", url="         + item.getLeagueURL()
				+ " season ID="    + item.getSeasonId()
				+ " division ID="  + item.getDivisionId()
				+ " conferenceId=" + item.getConferenceId()
				+ " team ID="      + item.getTeamId());
		return item; 
	}
	private TeamItem getFieldsFromURL (TeamItem item) {
		Log.d(TAG, "getFieldsFromURL() TeamItem");
    	Uri uri = Uri.parse(item.getTeamURL());
    	item.setLeagueId(uri.getQueryParameter("league"));
    	item.setSeasonId(uri.getQueryParameter("season"));
    	item.setDivisionId(uri.getQueryParameter("division"));
    	item.setConferenceId(uri.getQueryParameter("conference"));
    	item.setTeamId(uri.getQueryParameter("team"));
    	int position=item.getTeamURL().indexOf("?");
    	item.setLeagueURL(item.getTeamURL().substring(0,position));
		return item; 
	}
    protected String queryOrgNameByTeamItem(Context context, TeamItem team) {
		Log.d(TAG, "queryOrgNameByTeamItem() League PK (from team): "
				+ " league ID="    + team.getLeagueId()
				+ ", url="         + team.getLeagueURL());
        mHelper = new DatabaseHelper(context);
    	LeagueCursor cursor;
    	cursor = mHelper.queryLeagueByTeamItem(team);
    	cursor.moveToFirst();
    	String orgName=null;
    	ArrayList<LeagueItem> items = new ArrayList<LeagueItem>();
		while(!cursor.isAfterLast()) {
    		LeagueItem item = cursor.getLeagueItem();
    		items.add(item);
    		orgName=item.getOrgName();
    		cursor.moveToNext();
    		Log.v(TAG, "queryOrgNameByTeamItem() league: "
    				+ item.getLeagueId() + "-"
    				+ item.getOrgName() + "-"
    				+ item.getLeagueURL());
    	}
    	cursor.close();
        mHelper.close();
    	return orgName;
    }
    protected TeamItem queryTeamByTeamURL(Context context, String teamURL) {
		// www...mobileschedule.php?league=1&season=8&division=123&conference=127&team=933
    	TeamItem item = new TeamItem();
		item.setTeamURL(teamURL);
        item=getFieldsFromURL(item);
		Log.d(TAG, "queryTeamByTeamURL() Team PK: "
				+ " league ID="    + item.getLeagueId()
				+ ", url="         + item.getLeagueURL()
				+ " season ID="    + item.getSeasonId()
				+ ", name="        + item.getSeasonName() 
				+ " division ID="  + item.getDivisionId()
				+ ", name="        + item.getDivisionName()
				+ " conferenceId=" + item.getConferenceId()
				+ ", name="        + item.getConferenceName()
				+ ", count="       + item.getConferenceCount()
				+ " team ID="      + item.getTeamId()
				+ ", name="        + item.getTeamName()
				+ ", url="         + item.getTeamURL());
        mHelper = new DatabaseHelper(context);
    	TeamCursor cursor;
    	ArrayList<TeamItem> items = new ArrayList<TeamItem>();
    	cursor = mHelper.queryTeamByTeamItem(item);
    	cursor.moveToFirst();
    	while(!cursor.isAfterLast()) {
    		item = cursor.getTeamItem();
    		items.add(item);
    		cursor.moveToNext();
    		Log.v(TAG, "queryTeamByTeamURL() Team: "
    				+ " league ID="    + item.getLeagueId()
    				+ ", url="         + item.getLeagueURL()
    				+ " season ID="    + item.getSeasonId()
    				+ ", name="        + item.getSeasonName() 
    				+ " division ID="  + item.getDivisionId()
    				+ ", name="        + item.getDivisionName()
    				+ " conferenceId=" + item.getConferenceId()
    				+ ", name="        + item.getConferenceName()
    				+ ", count="       + item.getConferenceCount()
    				+ " team ID="      + item.getTeamId()
    				+ ", name="        + item.getTeamName()
    				+ ", url="         + item.getTeamURL());
    	}
    	cursor.close();
        mHelper.close();
        if (items.size() != 1) {
        	Log.e(TAG, "queryTeamByTeamURL() Count="+items.size());
        	item=null;
        }
        else {
        	item=items.get(0);
        }
    	return item;
    }
	protected void launchGameListActivity(Context context, TeamItem item, Tracker t) {
		Log.d(TAG, "launchGameListActivity()");
		Intent i = new Intent (context, GameListActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("LeagueId", item.getLeagueId().toString());
		i.putExtra("LeagueURL", item.getLeagueURL().toString());
		i.putExtra("SeasonId", item.getSeasonId().toString());
		i.putExtra("SeasonName", item.getSeasonName().toString());
		i.putExtra("DivisionId", item.getDivisionId().toString());
		i.putExtra("DivisionName", item.getDivisionName().toString());
		i.putExtra("ConferenceId", item.getConferenceId().toString());
		i.putExtra("ConferenceName", item.getConferenceName().toString());
		i.putExtra("ConferenceCount", item.getConferenceCount().toString());
		i.putExtra("TeamId", item.getTeamId().toString());
		i.putExtra("TeamName", item.getTeamName().toString());
		i.putExtra("TeamURL", item.getTeamURL().toString());
		
		String fullName = queryOrgNameByTeamItem(context, item);
		if(item.getConferenceCount().equals("one")) {
			fullName = fullName+"/"+item.getSeasonName()+"/"
					+item.getDivisionName()+"/"+"/"+item.getTeamName();
		}
		else {
			fullName = fullName+"/"+item.getSeasonName()+"/"
					+item.getDivisionName()+"/"+item.getConferenceName()+"/"+item.getTeamName();
		}
		//TODO remove t.set("&uid", FavoriteListUtil.getClientID(context));
		//TODO remove t.enableAdvertisingIdCollection(true);
		t.send(new HitBuilders.EventBuilder()
		.setCategory("GameListing")
		.setAction("favoriteListing")
		.setLabel(fullName)
		.setValue(1) // Add 1
		.build());
		Log.v(TAG, "launchGameListActivity(): "
				+ " league ID="    + item.getLeagueId()
				+ ", url="         + item.getLeagueURL()
				+ " season ID="    + item.getSeasonId()
				+ ", name="        + item.getSeasonName() 
				+ " division ID="  + item.getDivisionId()
				+ ", name="        + item.getDivisionName()
				+ " conference ID=" + item.getConferenceId()
				+ ", name="        + item.getConferenceName()
				+ ", count="       + item.getConferenceCount()
				+ " team ID="      + item.getTeamId()
				+ ", name="        + item.getTeamName()
				+ ", url="         + item.getTeamURL());

		context.startActivity(i);
	}
}