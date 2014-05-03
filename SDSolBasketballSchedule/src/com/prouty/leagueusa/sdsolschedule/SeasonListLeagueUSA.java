package com.prouty.leagueusa.sdsolschedule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class SeasonListLeagueUSA{
	private static final String TAG = "SeasonListLeagueUSA";
	private LeagueItem mLeagueItem;

	public byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}

			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}

	String getUrl(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}

	public ArrayList<SeasonItem> fetchItems(LeagueItem leagueItem, Context appContext) {
		ArrayList<SeasonItem> items = new ArrayList<SeasonItem>();
		mLeagueItem = leagueItem; // Sets class variable
		try {
			String jsonString = GETSeasonList();
			if (jsonString == null || jsonString.length() == 0) {
				//Not online - will show an empty list if not in DB
				Log.i(TAG, "fetchItems() Failed to fetch items");
			}
			else {
				parseSeasonList(items, jsonString);
			}
		} catch (Exception e) {
			Log.e(TAG, "fetchItems() Exc:"+e.getMessage(),e);
		}
		return items;
	}
	private String GETSeasonList() {
		String url = "";
		String jsonString = "";
		try {
			url = Uri.parse(mLeagueItem.getLeagueURL()+"?league="+mLeagueItem.getLeagueId()).toString();
			Log.d(TAG, "GETSeasonList():" + url);
			jsonString = getUrl(url);
			Log.d(TAG, "GETSeasonList() Received json: " + jsonString);
		} catch (IOException ioe) {
			Log.e(TAG, "GETSeasonList() IOException: "+ioe.getMessage()); // skip stack
		} catch (Exception e) {
			Log.e(TAG, "GETSeasonList() Exc:"+e.getMessage(),e);
		}
		return jsonString;
	}
	private void parseSeasonList(ArrayList<SeasonItem> items, String stringSeasonList) {
		try {
			JSONArray jsonSeasonList = new JSONArray (stringSeasonList);  
			// [{"seasonid":"7","seasonname":"Breakaway League 2014"},
			for (int i = 0; i < jsonSeasonList.length(); i++) {
				JSONObject jsonNode = jsonSeasonList.getJSONObject(i);
				String season_id  = jsonNode.optString("seasonid").toString();
				String season_name   = jsonNode.optString("seasonname").toString();
				Log.d(TAG, "parseSeasonList() ["+ i + "] : "+season_id+"-"+season_name);

				SeasonItem item = new SeasonItem();
				item.setLeagueId(mLeagueItem.getLeagueId());
				item.setSeasonId(season_id);
				item.setSeasonName(season_name);
				items.add(item);
			}
			Log.d(TAG, "parseSeasonList() SeasonItem added: "+jsonSeasonList.length());
		} catch (Exception e) {
			Log.e(TAG, "parseSeasonList() Exc:"+e.getMessage(),e);
		}
	}
}