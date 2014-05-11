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

public class DivisionListLeagueUSA{
	private static final String TAG = "DivisionListLeagueUSA";
	private SeasonItem mSetupItem;

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

	public ArrayList<DivisionItem> fetchItems(SeasonItem setupItem, Context appContext) {
		ArrayList<DivisionItem> items = new ArrayList<DivisionItem>();
		mSetupItem = setupItem; // Sets class variable
		try {
			String jsonString = GETList();
			if (jsonString == null || jsonString.length() == 0) {
				//Not online - will show an empty list if not in DB
				Log.i(TAG, "fetchItems() Failed to fetch items");
			}
			else {
				parseList(items, jsonString);
			}
		} catch (Exception e) {
			Log.e(TAG, "fetchItems() Exc:"+e.getMessage(),e);
		}
		return items;
	}
	private String GETList() {
		String url = "";
		String jsonString = "";
		try {
			// http://www.sdsolbasketball.com/mobileschedule.php?league=1&season=8
			url = Uri.parse(mSetupItem.getLeagueURL()
					+"?league="+mSetupItem.getLeagueId()
					+"&season="+mSetupItem.getSeasonId()).toString();
			Log.d(TAG, "GETList():" + url);
			jsonString = getUrl(url);
			Log.v(TAG, "GETList() Received json: " + jsonString);
		} catch (IOException ioe) {
			Log.e(TAG, "GETList() IOException: "+ioe.getMessage()); // skip stack
		} catch (Exception e) {
			Log.e(TAG, "GETList() Exc:"+e.getMessage(),e);
		}
		return jsonString;
	}
	private void parseList(ArrayList<DivisionItem> items, String stringSeasonList) {
		try {
			JSONArray jsonSeasonList = new JSONArray (stringSeasonList);  
			// [{"divisionid":"107","divisionname":"Boys 3rd"},...
			for (int i = 0; i < jsonSeasonList.length(); i++) {
				JSONObject jsonNode = jsonSeasonList.getJSONObject(i);
				String id  = jsonNode.optString("divisionid").toString();
				String name= jsonNode.optString("divisionname").toString();
				Log.v(TAG, "parseList() ["+ i + "] : "+id+"-"+name);

				DivisionItem item = new DivisionItem();
				item.setLeagueId(mSetupItem.getLeagueId());
				item.setLeagueURL(mSetupItem.getLeagueURL());
				item.setSeasonId(mSetupItem.getSeasonId());
				item.setSeasonName(mSetupItem.getSeasonName());
				
				item.setDivisionId(id);
				item.setDivisionName(name);
				items.add(item);
			}
			Log.d(TAG, "parseList() Items added: "+jsonSeasonList.length());
		} catch (Exception e) {
			Log.e(TAG, "parseList() Exc:"+e.getMessage(),e);
		}
	}
}