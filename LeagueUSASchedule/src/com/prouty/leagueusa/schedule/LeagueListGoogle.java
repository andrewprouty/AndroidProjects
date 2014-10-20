package com.prouty.leagueusa.schedule;

import java.io.IOException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.ServiceException;

public class LeagueListGoogle{
	private static final String TAG = "LeagueListGoogle";

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
		 * 3-"Name" must be the header of this column 
		 * 4-"URL" must be the header of this column
		 * 5-"URL" formatting/cleanup rules:
		 *     A-Trim to the left of the first "." <REMOVED THIS>
		 *     B-Right trim after any "/"
		 *     C-Prepend: "http://"            <NO LONGER PREPEND 'www.', only 'http://'
		 *     D-Append: "/mobileschedule.php"
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
			Log.d(TAG, "GETListFeed() Title="+feed.getTitle()+ " TotalResults="+feed.getTotalResults()+" Updated="+feed.getUpdated());

			// Each row in the spreadsheet
			// http://developer.android.com/reference/java/text/Collator.html
			// TODO revisit this site if need to handle more internationally
			Collator usCollator = Collator.getInstance(Locale.US);
			usCollator.setStrength(Collator.PRIMARY);
			
			String prevName=null;
			int result=0;
			for (ListEntry entry : feed.getEntries()) {
				CustomElementCollection elements = entry.getCustomElements();
				String name = elements.getValue("Name");
				String origUrl = elements.getValue("URL");
				String active =  elements.getValue("Active");
				int position;

				//Do NOT show leagues that begin with Active = "No"...
				if (active != null) {
					if (active.indexOf("No") == 0 || active.indexOf("NO") == 0) {
						Log.v(TAG, "GETListFeed() NOT Active: Name="+name+ " origUrl="+origUrl+" active="+active);
						continue;
					}
				}
				else {
					Log.w(TAG, "GETListFeed() Active=NULL: Name="+name+ " origUrl="+origUrl+" active="+active);
				}

				if (prevName != null) {
					//result = prevName.compareTo(name)
					result = usCollator.compare (prevName, name);
					if (result == 0)
						Log.e(TAG, "GETListFeed() Duplicate name, prevName("+prevName+") = current name("+name+")");
					else if (result > 0 )
						Log.e(TAG, "GETListFeed() Non-alpha sort, prevName("+prevName+") > current name ("+name+")");
				}
				prevName=name;
				//A-Trim to the first "."
		    	//int position=origUrl.indexOf(".");
		    	//String baseUrl=origUrl.substring(position+1);
				//B-Right trim from "/"
		    	String newUrl=origUrl;
				position=origUrl.indexOf("/");
		    	if (position != -1) {
		    		newUrl=origUrl.substring(0,position);
		    	}
				//C-Prepend: "http://"
				//D-Append: "/mobileschedule.php"
		    	newUrl = "http://"+origUrl+"/mobileschedule.php";

				LeagueItem item = new LeagueItem();
				item.setLeagueId("1");  // ENH If multiple leagues share the site - likely have to adjust something
				item.setOrgName(name);
				item.setLeagueURL(newUrl);
				items.add(item);
				Log.v(TAG, "GETListFeed() Name="+name+ " origUrl="+origUrl+" newUrl="+newUrl);
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