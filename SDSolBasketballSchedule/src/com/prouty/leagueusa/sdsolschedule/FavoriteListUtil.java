package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

public class FavoriteListUtil {
	public static final String PREFS_NAME = "MyPrefsFile";

	public void addFavoriteItem(Context context, FavoriteItem item, int position) {
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
}