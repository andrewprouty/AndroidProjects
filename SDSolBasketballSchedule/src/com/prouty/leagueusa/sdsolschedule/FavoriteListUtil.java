package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.prouty.leagueusa.sdsolschedule.DatabaseHelper.TeamCursor;

public class FavoriteListUtil {
	private static final String TAG = "FavoriteListUtil";
	public static final String PREFS_NAME = "MyPrefsFile";
	private DatabaseHelper mHelper;


	public void addFavoriteItem(Context context, FavoriteItem item, int position) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("FavoriteName"+position, item.getFavoriteName());
		editor.putString("FavoriteURL"+position, item.getFavoriteURL());
		editor.commit();
		return;
	}
	public void removeFavoriteItem(Context context, FavoriteItem item, int position) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("FavoriteName"+position, item.getFavoriteName());
		editor.putString("FavoriteURL"+position, item.getFavoriteURL());
		editor.commit();
		return;
	}
	public ArrayList<FavoriteItem> getFavoriteList(Context context) {
		ArrayList<FavoriteItem> items = new ArrayList<FavoriteItem>();
		FavoriteItem item = new FavoriteItem();
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		int position = 0;
		String name = settings.getString("FavoriteName"+position,"NONE");
		String url = settings.getString("FavoriteURL"+position,"NONE");
 		while (!name.equals("NONE")){
 			item.setFavoriteName(name);
 			item.setFavoriteURL(url);
 			items.add(item);
 			position++;
 			item = new FavoriteItem();
 			name = settings.getString("FavoriteName"+position,"NONE");
 			url = settings.getString("FavoriteURL"+position,"NONE");
		}
		return items;
	}
    protected TeamItem queryTeamByTeamURL(Context context, String teamURL) {
    	//String leagueId, String seasonId, String DivisionId, String ConferenceId
		// www...mobileschedule.php?league=1&season=8&division=123&conference=127&team=933
        mHelper = new DatabaseHelper(context);
    	Uri uri = Uri.parse(teamURL);
    	TeamItem item = new TeamItem();
    	item.setLeagueId(uri.getQueryParameter("league"));
    	item.setSeasonId(uri.getQueryParameter("season"));
    	item.setDivisionId(uri.getQueryParameter("division"));
    	item.setConferenceId(uri.getQueryParameter("conference"));
    	item.setTeamId(uri.getQueryParameter("team"));
		Log.v(TAG, "queryTeamByTeamURL() Team PK: "
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
}