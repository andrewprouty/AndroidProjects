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

public class UserListBismarck {
	private static final String TAG = "UserListBismarck";
	private static final String ENDPOINT = "http://bismarck.sdsu.edu/photoserver/userlist/";

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

	public ArrayList<UserItem> fetchItems(Context appContext) {
		ArrayList<UserItem> items = new ArrayList<UserItem>();
		try {
			String jsonString = GETUserList();
			if (jsonString == null || jsonString.length() == 0) {
				//Not online - will show an empty list if not in DB
				Log.i(TAG, "fetchItems() Failed to fetch items");
			}
			else {
				parseUserList(items, jsonString);
			}
		} catch (Exception e) {
			Log.e(TAG, "fetchItems() Exc:"+e.getMessage(),e);
		}
		return items;
	}
	private String GETUserList() {
		String url = "";
		String jsonString = "";
		try {
			url = Uri.parse(ENDPOINT).toString();
			Log.d(TAG, "GETUserList():" + url);
			jsonString = getUrl(url);
			Log.d(TAG, "GETUserList() Received json: " + jsonString);
		} catch (IOException ioe) {
			Log.e(TAG, "GETUserList() IOException: "+ioe.getMessage()); // skip stack
		} catch (Exception e) {
			Log.e(TAG, "GETUserList() Exc:"+e.getMessage(),e);
		}
		return jsonString;
	}
	private void parseUserList(ArrayList<UserItem> items, String stringUserList) {
		try {
			JSONArray jsonUserList = new JSONArray (stringUserList);  
			// {"name":"Roger Whitney","id":"1"},...
			for (int i = 0; i < jsonUserList.length(); i++) {
				JSONObject jsonNode = jsonUserList.getJSONObject(i);
				String user_name   = jsonNode.optString("name").toString();
				String user_id     = jsonNode.optString("id").toString();
				Log.d(TAG, "parseUserList() ["+ i + "] : "+user_id+"-"+user_name);

				UserItem item = new UserItem();
				item.setUserName(user_name);
				item.setUserId(user_id);
				items.add(item);
			}
			Log.d(TAG, "parseUserList() UserItem added: "+jsonUserList.length());
		} catch (Exception e) {
			Log.e(TAG, "parseUserList() Exc:"+e.getMessage(),e);
		}
	}
}