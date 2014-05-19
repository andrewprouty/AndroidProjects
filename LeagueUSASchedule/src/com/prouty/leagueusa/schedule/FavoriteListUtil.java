package com.prouty.leagueusa.schedule;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.prouty.leagueusa.schedule.DatabaseHelper.TeamCursor;

public class FavoriteListUtil {
	private static final String TAG = "FavoriteListUtil";
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String HOME_ID = "HOME_ID";
	public static final String HOME_NAME = "HOME_NAME";
	public static final String HOME_URL = "HOME_URL";
	private DatabaseHelper mHelper;

	public FavoriteItem addFavoriteItem(Context context, TeamItem team) {
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
		//editor.clear(); //truncate preferences file
		//URL is absolute, the name could change (if team goes from single to multiple conferences division)
		editor.putString(fav.getFavoriteURL(),fav.getFavoriteName());
		editor.commit();
		return fav;
	}
	public void setHomeLeageItem(Context context, LeagueItem item) {
		Log.d(TAG, "setHomeLeageItem() "
				+ item.getLeagueId() + "-"
				+ item.getOrgName() + " ("
				+ item.getLeagueURL() + ")");
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
	public void removeFavoriteItem(Context context, String keyTeamURL) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(keyTeamURL);
		editor.commit();
		Log.d(TAG, "removeFavoriteItem() Key="+keyTeamURL);
		editor.commit();
		return;
	}
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

	public ArrayList<FavoriteItem> getFavoriteList(Context context) {
		ArrayList<FavoriteItem> items = new ArrayList<FavoriteItem>();
		FavoriteItem item;
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Boolean empty = false;
		for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            Object val = entry.getValue();
            if (!entry.getKey().equals(HOME_ID) && !entry.getKey().equals(HOME_NAME) && !entry.getKey().equals(HOME_URL)) {
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
    	Uri uri = Uri.parse(item.getFavoriteURL());
    	item.setLeagueId(uri.getQueryParameter("league"));
    	item.setSeasonId(uri.getQueryParameter("season"));
    	item.setDivisionId(uri.getQueryParameter("division"));
    	item.setConferenceId(uri.getQueryParameter("conference"));
    	item.setTeamId(uri.getQueryParameter("team"));
		return item; 
	}
	private TeamItem getFieldsFromURL (TeamItem item) {
    	Uri uri = Uri.parse(item.getTeamURL());
    	item.setLeagueId(uri.getQueryParameter("league"));
    	item.setSeasonId(uri.getQueryParameter("season"));
    	item.setDivisionId(uri.getQueryParameter("division"));
    	item.setConferenceId(uri.getQueryParameter("conference"));
    	item.setTeamId(uri.getQueryParameter("team"));
		return item; 
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
	protected void launchGameListActivity(Context context, TeamItem item) {
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