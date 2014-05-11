package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SeasonListFragment extends Fragment{
	private static final String TAG = "SeasonListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private ArrayList<LeagueItem> mLeagueQuery;
	private ArrayList<LeagueItem> mLeagueFetch;
	private ArrayList<LeagueItem> mLeagueDisplay;
	private LeagueItem mLeagueItem;
	private ArrayList<SeasonItem> mSeasonQuery;
	private ArrayList<SeasonItem> mSeasonFetch;
	private ArrayList<SeasonItem> mSeasonDisplay;
	private SeasonItem mSeasonItem;
	
	View view;
	TextView mSeasonTextView;
	ListView mListView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // survive across Activity re-create (i.e. orientation)
    }
    @Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{       
		Log.d(TAG, "onCreateView()");
		if (mLeagueDisplay != null) {
			mLeagueDisplay.clear(); // reset in case of orientation switch
		}
		if (mSeasonDisplay != null) {
			mSeasonDisplay.clear(); // reset in case of orientation switch
		}
		new QueryLeagueItemsTask().execute();
       
		view = inflater.inflate(R.layout.fragment_season_list, container,false);
        mSeasonTextView = (TextView)view.findViewById(R.id.season_list_season_name);
		mListView = (ListView)view.findViewById(R.id.season_list_view);
		mListView.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textViewItem = ((TextView) view.findViewById(R.id.row_season_name_textView));
				String listItemText = textViewItem.getText().toString();
				Log.d(TAG, "().onItemClick() Season ["+position+"]= "+listItemText);
				returnSeason(position);
			}
		});
		return view;
	}
	private void setupLeague(int choice, int choiceSize) {
		// Really only expect 1:San Diego Sol. Prepared for future extensibility
		if (getActivity() == null || mListView == null) {
    		return;
    	}
		Log.d(TAG, "setupLeague("+choice+") choiceSize="+choiceSize);
		if (mLeagueDisplay == null || mLeagueDisplay.size() == 0) {
			if (choiceSize > 0) {
				if (choice == GET) {						//No results yet, but I have some
					mLeagueDisplay = mLeagueFetch;
					new InsertLeagueItemsTask().execute();	// Most likely Query was fast but empty
				}
				else {
					mLeagueDisplay = mLeagueQuery;
				}
			} //[else] 1st with no results, or 2nd and nobody had results
		}
		else {//else: 1st had results. I am 2nd 
			if (choiceSize > 0) {							// Both had results
				if (!mLeagueFetch.equals(mLeagueQuery)) {
					Log.d(TAG, "setupLeague("+choice+") Fetched != Queried. Size info only: "
							+ mLeagueFetch.size() + " " + mLeagueQuery.size());
					if (choice == GET) {
						new InsertLeagueItemsTask().execute();
						Toast.makeText(getActivity().getApplicationContext(), R.string.try_again_for_update, Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Log.d(TAG, "setupLeague("+choice+") Fetched=Queried");
				}
			}
		}
    	if (choice == QUERY) { // Special League logic, revisit if expand from SD Sol to LeagueUSA
    		if (choiceSize != 1) { // should be 1.  Remove if expand beyond SD Sol to LeagueUSA
        		Log.e(TAG, "setupLeague() 1 league should have been returned, received "+choiceSize+". Use hardcode.");
    			LeagueItem item = new LeagueItem();
    			item.setLeagueId("1");
    			item.setOrgName("San Diego Sol");
    			item.setLeagueURL("http://www.sdsolbasketball.com/mobileschedule.php");
    			mLeagueItem = item;
    		}
    		else {
    			mLeagueItem = mLeagueQuery.get(0);
    		}
    		new QuerySeasonItemsTask().execute(mLeagueItem);
    	}
    }
	private void setupSeason(int choice, int choiceSize) {
    	if (getActivity() == null || mListView == null) {
    		return;
    	}
		Log.d(TAG, "setupSeason("+choice+") choiceSize="+choiceSize);
		if (mSeasonDisplay == null || mSeasonDisplay.size() == 0) {
			if (choiceSize > 0) {
				if (choice == GET) {						//No results yet, but I have some
					mSeasonDisplay = mSeasonFetch;
					new InsertSeasonItemsTask().execute();	// Most likely Query was fast but empty
				}
				else {
					mSeasonDisplay = mSeasonQuery;
				}
				SeasonListAdapter adapter = new SeasonListAdapter(mSeasonDisplay, choice);
				mListView.setAdapter(adapter);
			} //[else] 1st with no results, or 2nd and nobody had results
		}
		else {//else: 1st had results. I am 2nd 
			if (choiceSize > 0) {							// Both had results
				if (!mSeasonFetch.equals(mSeasonQuery)) {
					Log.d(TAG, "setupSeason("+choice+") Fetched != Queried. Sizes only: "
							+ mSeasonFetch.size() + " " + mSeasonQuery.size());
					if (choice == GET) {
						new InsertSeasonItemsTask().execute();
						Toast.makeText(getActivity().getApplicationContext(), R.string.try_again_for_update, Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Log.d(TAG, "setupSeason("+choice+") Fetched=Queried");
				}
			}
		}
    }
    private void returnSeason(int position) {
    	mSeasonItem = mSeasonDisplay.get(position);
		mSeasonTextView.setText(mSeasonItem.getSeasonName());
		Log.i(TAG, "returnSeason()=["+position+"] "
				+ mSeasonItem.getLeagueId() + " ("
				+ mSeasonItem.getLeagueURL() + "); "
				+ mSeasonItem.getSeasonId() + "-"
				+ mSeasonItem.getSeasonName());
		((MainActivity) getActivity()).launchDivisionListActivity(mSeasonItem);
    }
    private class FetchLeagueItemsTask extends AsyncTask<Void,Void,ArrayList<LeagueItem>> {
        @Override
        protected ArrayList<LeagueItem> doInBackground(Void... params) {
        	Log.d(TAG, "FetchLeagueItemsTask.doInBackground()");
    		ArrayList<LeagueItem> items = null;
    		try {
    			// pass context for app dir to cache file
        		items = new LeagueListLeagueUSA().fetchItems(getActivity().getApplicationContext());
    		} catch (Exception e) {
    			Log.e(TAG, "FetchLeagueItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
        }
        @Override
        protected void onPostExecute(ArrayList<LeagueItem> items) {
        	try {
        		Log.d(TAG, "FetchLeagueItemsTask.onPostExecute() fetched=" + items.size());
        		int size;
        		if (items == null || items.size() == 0) {
        			size = 0;
        		} else {
        			size = items.size();
        			mLeagueFetch = items;
        		}
           		setupLeague(GET, size); // show listing
        		cancel(true);
        	} catch (Exception e) {
        		Log.e(TAG, "FetchLeagueItemsTask.doInBackground() Exception.", e);
        	}
        }
    }
    private class InsertLeagueItemsTask extends AsyncTask<Void,Void,Void> {
    	//<x,y,z> params: 1-doInBackground(x); 2-onProgressUpdate(y); 3-onPostExecute(z) 
    	@Override
    	protected Void doInBackground(Void... nada) {
    		Log.d(TAG, "InsertLeagueItemsTask.doInBackground()");
    		try {
    			((MainActivity) getActivity()).insertLeagueItems(mLeagueFetch);
    		} catch (Exception e) {
    			Log.e(TAG, "InsertLeagueItemsTask.doInBackground() Exception.", e);
    		}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void nada) {
    		Log.d(TAG, "InsertLeagueItemsTask.onPostExecute()");
    		cancel(true);
    	}
    }
	private class QueryLeagueItemsTask extends AsyncTask<Void,Void,ArrayList<LeagueItem>> {
		@Override
		protected ArrayList<LeagueItem> doInBackground(Void... nada) {
        	Log.d(TAG, "QueryLeagueItemsTask.doInBackground()");
    		ArrayList<LeagueItem> items = null;
    		try {
    			items = ((MainActivity) getActivity()).queryLeagueItems();
    		} catch (Exception e) {
    			Log.e(TAG, "QueryLeagueItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<LeagueItem> items) {
        	Log.d(TAG, "QueryLeagueItemsTask.onPostExecute() queried=" + items.size());
        	int size;
    		if (items == null || items.size() == 0) {
    			size = 0;
    		} else {
    			size = items.size();
    			mLeagueQuery = items;
    		}
			setupLeague(QUERY, size);
            cancel(true);
    		new FetchSeasonItemsTask().execute(mLeagueItem);
		}
	}
	// SEASON
    private class FetchSeasonItemsTask extends AsyncTask<LeagueItem,Void,ArrayList<SeasonItem>> {
        @Override
        protected ArrayList<SeasonItem> doInBackground(LeagueItem... params) {
        	Log.d(TAG, "FetchSeasonItemsTask.doInBackground()");
    		ArrayList<SeasonItem> items = null;
    		try {
    			// pass context for app dir to cache file
        		items = new SeasonListLeagueUSA().fetchItems(mLeagueItem, getActivity().getApplicationContext());
    		} catch (Exception e) {
    			Log.e(TAG, "FetchSeasonItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
        }
        @Override
        protected void onPostExecute(ArrayList<SeasonItem> items) {
        	try {
        		Log.d(TAG, "FetchSeasonItemsTask.onPostExecute() fetched=" + items.size());
        		int size;
        		if (items == null || items.size() == 0) {
        			size = 0;
        		} else {
        			size = items.size();
            		mSeasonFetch = items;
        		}
           		setupSeason(GET, size);
        		cancel(true);
        	} catch (Exception e) {
        		Log.e(TAG, "FetchSeasonItemsTask.doInBackground() Exception.", e);
        	}
        }
    }
    private class SeasonListAdapter extends ArrayAdapter<SeasonItem> {
        public SeasonListAdapter(ArrayList<SeasonItem> items, int choice) {
            super(getActivity(), 0, items);
			Log.i(TAG, "SeasonListAdapter Constructor ("+choice+")");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.season_list_row, parent, false);
            }
            SeasonItem item = getItem(position);
            TextView seasonTextView = (TextView)convertView.findViewById(R.id.row_season_name_textView);
			Log.v(TAG, "SeasonListAdapter getView(): "+item.getSeasonName());
            seasonTextView.setText(item.getSeasonName());
            return convertView;
        }
    }
    private class InsertSeasonItemsTask extends AsyncTask<Void,Void,Void> {
    	//<x,y,z> params: 1-doInBackground(x); 2-onProgressUpdate(y); 3-onPostExecute(z) 
    	@Override
    	protected Void doInBackground(Void... nada) {
    		Log.d(TAG, "InsertSeasonItemsTask.doInBackground()");
    		try {
    			((MainActivity) getActivity()).insertSeasonItems(mSeasonFetch);
    		} catch (Exception e) {
    			Log.e(TAG, "InsertSeasonItemsTask.doInBackground() Exception.", e);
    		}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void nada) {
    		Log.d(TAG, "InsertSeasonItemsTask.onPostExecute()");
    		cancel(true); // done !
    	}
    }
	private class QuerySeasonItemsTask extends AsyncTask<LeagueItem,Void,ArrayList<SeasonItem>> {
		@Override
		protected ArrayList<SeasonItem> doInBackground(LeagueItem... nada) {
        	Log.d(TAG, "QuerySeasonItemsTask.doInBackground()");
    		ArrayList<SeasonItem> items = null;
    		try {
    			items = ((MainActivity) getActivity()).querySeasonItemsByLeagueId(mLeagueItem);
    		} catch (Exception e) {
    			Log.e(TAG, "QuerySeasonItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<SeasonItem> items) {
        	Log.d(TAG, "QuerySeasonItemsTask.onPostExecute() queried=" + items.size());
    		int size;
    		if (items == null || items.size() == 0) {
    			size = 0;
    		} else {
    			size = items.size();
        		mSeasonQuery = items;
    		}
       		setupSeason(QUERY, size);
            cancel(true);
    		new FetchLeagueItemsTask().execute();
		}
	}
}