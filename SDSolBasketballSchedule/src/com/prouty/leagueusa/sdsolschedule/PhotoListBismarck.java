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

public class PhotoListBismarck {
	private static final String TAG = "PhotoListBismarck";
	// http://bismarck.sdsu.edu/photoserver/userphotos/2
	private static final String ENDPOINT = "http://bismarck.sdsu.edu/photoserver/userphotos/";
	private UserItem mUserItem;

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

	public ArrayList<PhotoItem> fetchItems(UserItem userItem, Context appContext) {
		Log.d(TAG, "fetchItems()");
		ArrayList<PhotoItem> items = new ArrayList<PhotoItem>();
		mUserItem = userItem; // Sets class variable
		try {
			String jsonString = GETPhotoList();
			if (jsonString == null || jsonString.length() == 0) {
				//Not online - will show an empty list if not in DB
				Log.i(TAG, "fetchItems() Failed to fetch items");
			}
			else {
				parsePhotoList(items, jsonString);
			}
		} catch (Exception e) {
			Log.e(TAG, "fetchItems() Exc:"+e.getMessage(),e);
		}

		return items;
	}
	private String GETPhotoList() {
		String jsonString = "";
		try {
			String url = Uri.parse(ENDPOINT).toString() + mUserItem.getUserId();
			Log.d(TAG, "GETPhotoList():" + url);
			jsonString = getUrl(url);
			Log.d(TAG, "GETPhotoList() Received json: " + jsonString);
		} catch (IOException ioe) {
			Log.e(TAG, "GETUserList() IOException: "+ioe.getMessage()); // skip stack
		}
		catch (Exception e) {
			Log.e(TAG, "GETPhotoList() Exc:"+e.getMessage(),e);
		}
		return jsonString;
	}

	private void parsePhotoList(ArrayList<PhotoItem> items, String stringPhotoList) {
		try {
			JSONArray jsonPhotoList = new JSONArray (stringPhotoList);  
			// {"name":"dog","id":"23"},...
			Log.d(TAG, "parsePhotoList() count of photos: "+jsonPhotoList.length());
			for (int i = 0; i < jsonPhotoList.length(); i++) {
				JSONObject jsonNode = jsonPhotoList.getJSONObject(i);
				String photo_name   = jsonNode.optString("name").toString();
				String photo_id     = jsonNode.optString("id").toString();
				Log.d(TAG, "parsePhotoList() ["+ i + "]: "+photo_id+"-"+photo_name);

				PhotoItem item = new PhotoItem();
				item.setUserId(mUserItem.getUserId());
				item.setUserName(mUserItem.getUserName());
				item.setPhotoName(photo_name);
				item.setPhotoId(photo_id);
				items.add(item);
			}

		} catch (Exception e) {
			Log.e(TAG, "parsePhotoList() Exc:"+e.getMessage(),e);
		}
	}
}