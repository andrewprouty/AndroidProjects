package com.prouty.leagueusa.schedule;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.ServiceException;

public class LeagueListGoogle{
	private static final String TAG = "LeagueListLeagueUSA";

	public ArrayList<LeagueItem> fetchItems(Context appContext) {
		ArrayList<LeagueItem> items = new ArrayList<LeagueItem>();
		try {
			GETListFeed(items);
			if (items == null || items.size() == 0) {
				//Not online - will show an empty list if not in DB
				Log.i(TAG, "fetchItems() Failed to fetch items");
			}
		} catch (Exception e) {
			Log.e(TAG, "fetchItems() Exc:"+e.getMessage(),e);
		}
		return items;
	}
	private void GETListFeed(ArrayList<LeagueItem> items) {
		Log.w(TAG, "GETListFeed()");
		/* VERY Specific, but easiest way I could find for a public spreadsheet
		 * http://blog.restphone.com/2011/05/very-simple-google-spreadsheet-code.html
		 * 1-spreadsheet with the key below
		 * 2-list feed must be the first work sheet
		 * 3-"Name" must be the header of column 1
		 * 4-"URL"  must be the header of column 2
		 */
		SpreadsheetService service = new SpreadsheetService("LeagueSchedule");
		try {
			// Notice that the url ends
			// with default/public/values.
			// That wasn't obvious (at least to me)
			// from the documentation.

			String urlString = "https://spreadsheets.google.com/feeds/list/0ArhVkXs6F3Y4cDllVzRqUE5PYnc3TkNkVG8xRzhSTGc/default/public/values";
			URL url = new URL(urlString);
			ListFeed feed = service.getFeed(url, ListFeed.class);

			for (ListEntry entry : feed.getEntries()) {
				CustomElementCollection elements = entry.getCustomElements();
				String name = elements.getValue("Name");
				String baseUrl = "http://"+elements.getValue("URL")+"/mobileschedule.php";

				LeagueItem item = new LeagueItem();
				item.setLeagueId("1");  // ENH Will leagues ever share the same site?
				item.setOrgName(name);
				item.setLeagueURL(baseUrl);
				items.add(item);

				Log.v(TAG, "GETListFeed() Name="+name+ " baseUrl="+baseUrl);
			}
			Log.d(TAG, "GETListFeed() Items added: "+items.size());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return;
	}

}