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

public class LeagueListLeagueUSA{
	private static final String TAG = "LeagueListLeagueUSA";
	private static final String ENDPOINT = "http://www.sdsolbasketball.com/mobileschedule.php";

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

	public ArrayList<LeagueItem> fetchItems(Context appContext) {
		ArrayList<LeagueItem> items = new ArrayList<LeagueItem>();
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
			url = Uri.parse(ENDPOINT).toString();
			Log.d(TAG, "GETList():" + url);
			jsonString = getUrl(url);
			Log.d(TAG, "GETList() Received json: " + jsonString);
		} catch (IOException ioe) {
			Log.e(TAG, "GETList() IOException: "+ioe.getMessage()); // skip stack
		} catch (Exception e) {
			Log.e(TAG, "GETList() Exc:"+e.getMessage(),e);
		}
		return jsonString;
	}
	private void parseList(ArrayList<LeagueItem> items, String stringLeagueList) {
		try {
			JSONArray jsonLeagueList = new JSONArray (stringLeagueList);  
			// {"name":"Roger Whitney","id":"1"},...
			// [{"leagueid":"1","orgname":"San Diego Sol"}]
			for (int i = 0; i < jsonLeagueList.length(); i++) {
				JSONObject jsonNode = jsonLeagueList.getJSONObject(i);
				String id  = jsonNode.optString("leagueid").toString();
				String name   = jsonNode.optString("orgname").toString();
				String url = "http://www.sdsolbasketball.com/mobileschedule.php";
				Log.v(TAG, "parseList() ["+ i + "] : "+id+"-"+name+"-"+url);

				LeagueItem item = new LeagueItem();
				item.setLeagueId(id);
				item.setOrgName(name);
				item.setLeagueURL(url);
					// If this is enhanced for other leagues, URL likely to be returned in JSON result
				items.add(item);
			}
			Log.d(TAG, "parseList() Items added: "+jsonLeagueList.length());
		} catch (Exception e) {
			Log.e(TAG, "parseList() Exc:"+e.getMessage(),e);
		}
	}
}