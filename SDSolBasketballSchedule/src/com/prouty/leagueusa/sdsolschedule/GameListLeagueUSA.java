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

public class GameListLeagueUSA{
	private static final String TAG = "GameListLeagueUSA";
	private TeamItem mSetupItem;

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

	public ArrayList<GameItem> fetchItems(TeamItem setupItem, Context appContext) {
		ArrayList<GameItem> items = new ArrayList<GameItem>();
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
			// http://www.sdsolbasketball.com/mobileschedule.php?league=1&season=8&division=123&conference=127&team=933
			url = Uri.parse(mSetupItem.getLeagueURL()
					+"?league="+mSetupItem.getLeagueId()
					+"&season="+mSetupItem.getSeasonId()
					+"&division="+mSetupItem.getDivisionId()
					+"&conference="+mSetupItem.getConferenceId()
					+"&team="+mSetupItem.getTeamId()).toString();
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
	private void parseList(ArrayList<GameItem> items, String stringList) {
		try {
			JSONArray jsonSeasonList = new JSONArray (stringList);  
			/* [{"gameid":"5269","meetname":"","starttbd":"1","awayteamid":"933","hometeamid":"930",
			 * "gamemonth":"04","gameyear":"2014","gamedate":"Sun, 04\/27 at 3:00 PM","gamedate2":"Sun, 04\/27",
			 * "fieldname":"AIU Court 1","homescore":"42","awayscore":"24",
			 * "hometeamname":"North County Prospects","awayteamname":"SD Sol-Johnson",
			 * "locationname":"AIU Court 1","locationid":"15"},... 
			 */
			
			for (int i = 0; i < jsonSeasonList.length(); i++) {
				JSONObject jsonNode = jsonSeasonList.getJSONObject(i);
				String gameId  = jsonNode.optString("gameid").toString();
				String gameDate= jsonNode.optString("gamedate").toString();
				String homeTeamId= jsonNode.optString("hometeamid").toString();
				String homeTeamName= jsonNode.optString("hometeamname").toString();
				String awayTeamName= jsonNode.optString("awayteamname").toString();
				String fieldName= jsonNode.optString("fieldname").toString();
				String locationName= jsonNode.optString("locationname").toString();
				String homeScore= jsonNode.optString("homescore").toString();
				String awayScore= jsonNode.optString("awayscore").toString();
				Log.v(TAG, "parseList() ["+ i + "] : "+gameId+"-"+gameDate+"-"+awayTeamName+"-"+homeTeamId+"-"
						+homeTeamName+"-"+fieldName+"-"+locationName+"-"+homeScore+"-"+awayScore+"-");

				GameItem item = new GameItem();
				item.setLeagueId(mSetupItem.getLeagueId());
				item.setLeagueURL(mSetupItem.getLeagueURL());
				item.setSeasonId(mSetupItem.getSeasonId());
				item.setSeasonName(mSetupItem.getSeasonName());
				item.setDivisionId(mSetupItem.getDivisionId());
				item.setDivisionName(mSetupItem.getDivisionName());
				item.setConferenceId(mSetupItem.getConferenceId());
				item.setTeamId(mSetupItem.getTeamId());
				item.setTeamName(mSetupItem.getTeamName());

				String GameDateTime=gameDate; // TODO trim
				String GameOpponent;
				String GameScore;
				if (mSetupItem.getTeamId() == homeTeamId) {
					GameOpponent=awayTeamName;
					GameScore=homeScore+"-"+awayScore; //TODO skip "null" score
				}
				else {
					GameOpponent=homeTeamName;
					GameScore=awayScore+"-"+homeScore;
				}
				String GameLocation=locationName; // TODO compare

				Log.v(TAG, "parseList() time="+GameDateTime+" opponent="+GameOpponent+
						" location="+GameLocation+" score="+GameScore);
				item.setGameId(gameId);
				item.setGameDateTime(GameDateTime);
				item.setGameOpponent(GameOpponent);
				item.setGameLocation(GameLocation);
				item.setGameScore(GameScore);
				items.add(item);
			}
			Log.d(TAG, "parseList() added: "+jsonSeasonList.length());
		} catch (Exception e) {
			Log.e(TAG, "parseList() Exc:"+e.getMessage(),e);
		}
	}
}