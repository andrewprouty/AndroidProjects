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

public class SeasonListFragment extends Fragment{
	private static final String TAG = "SeasonListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private ArrayList<LeagueItem> mLeagueItems;
	private LeagueItem mLeagueItem;
	private ArrayList<SeasonItem> mSeasonItems;
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
		new QueryLeagueItemsTask().execute(); // fast
		new FetchLeagueItemsTask().execute(); // add in anything new
       
		view = inflater.inflate(R.layout.fragment_season_list, container,false);
        mSeasonTextView = (TextView)view.findViewById(R.id.season_list_textView);
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
	private void setupLeague(int choice) {
		// Really only expect 1:San Diego Sol. Prepared for future extensibility
		if (getActivity() == null || mListView == null) {
    		return;
    	}
		Log.d(TAG, "setupLeague("+choice+")");

    	if (choice == GET) {
    		if (mLeagueItems != null && mLeagueItems.size()>0) {
    			// Async to save the fetched list to DB
    			new InsertLeagueItemsTask().execute(); // save fetched to DB
    		}
    		else {	// none. If in DB can populate from there
    			//new QueryLeagueItemsTask().execute();
    		}
    	}
		if (mLeagueItems != null) {
			int size = mLeagueItems.size();
			if (size > 1) {
				Log.e(TAG, "setupLeague() 1 league should have been returned, received. Will use [0] "+ size);
			}
		}
		else {
			Log.e(TAG, "setupLeague() 1 league should have been returned, received zero. Providing hardcode");
			LeagueItem item = new LeagueItem();
			item.setLeagueId("1");
			item.setOrgName("San Diego Sol");
			item.setLeagueURL("http://www.sdsolbasketball.com/mobileschedule.php");
			mLeagueItems.add(item);
		}
		if (mLeagueItems != null && mLeagueItems.size() > 0
				&& mSeasonItems == null) { // League does GET/QUERY in parallel - only initiate once
			mLeagueItem = mLeagueItems.get(0);
			Log.d(TAG, "setupLeague() [0]:"+mLeagueItem.getLeagueId()+"-"+mLeagueItem.getOrgName()+"-"+mLeagueItem.getLeagueURL());
			new QuerySeasonItemsTask().execute(); // fast
			new FetchSeasonItemsTask().execute(); // add in anything new
		}
    }
	private void setupSeason(int choice) {
    	if (getActivity() == null || mListView == null) {
    		return;
    	}
		Log.d(TAG, "setupSeason("+choice+")");
    	if (choice == GET) {
    		if (mSeasonItems != null && mSeasonItems.size()>0) {
    			new InsertSeasonItemsTask().execute(); // save fetched to DB
    		}
    	}
    	if (mSeasonItems != null && mSeasonItems.size() > 0) {
			SeasonListAdapter adapter = new SeasonListAdapter(mSeasonItems);
			mListView.setAdapter(adapter);
		}
		/*else {
			mListView.setAdapter(null);
		}*/
		Log.d(TAG, "setupSeason().");
    }
    private void returnSeason(int position) {
    	mSeasonItem = mSeasonItems.get(position);
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
        		Log.d(TAG, "FetchLeagueItemsTask.onPostExecute()");
        		if (items != null && items.size() > 0) {
        			mLeagueItems = items;
        		}
        		setupLeague(GET); // show listing
        		cancel(true); // done !
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
    			((MainActivity) getActivity()).insertLeagueItems(mLeagueItems);
    		} catch (Exception e) {
    			Log.e(TAG, "InsertLeagueItemsTask.doInBackground() Exception.", e);
    		}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void nada) {
    		Log.d(TAG, "InsertLeagueItemsTask.onPostExecute()");
    		cancel(true); // done !
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
			mLeagueItems = items;
			setupLeague(QUERY);
            cancel(true); // done !
        	Log.d(TAG, "QueryLeagueItemsTask.onPostExecute()");
		}
	}
	// SEASON
    private class FetchSeasonItemsTask extends AsyncTask<Void,Void,ArrayList<SeasonItem>> {
        @Override
        protected ArrayList<SeasonItem> doInBackground(Void... params) {
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
        		if (items != null && items.size() > 0) {
            		mSeasonItems = items;
        		}
        		Log.d(TAG, "FetchSeasonItemsTask.onPostExecute()");
        		setupSeason(GET); // show listing
        		cancel(true); // done !
        	} catch (Exception e) {
        		Log.e(TAG, "FetchSeasonItemsTask.doInBackground() Exception.", e);
        	}
        }
    }
    private class SeasonListAdapter extends ArrayAdapter<SeasonItem> {
        public SeasonListAdapter(ArrayList<SeasonItem> seasonItems) {
            super(getActivity(), 0, seasonItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.season_list_row, parent, false);
            }
            
            SeasonItem item = getItem(position);
            TextView seasonTextView = (TextView)convertView.findViewById(R.id.row_season_name_textView);
			Log.v(TAG, "adapter.getView() item.getSeasonName(): "+item.getSeasonName());
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
    			((MainActivity) getActivity()).insertSeasonItems(mSeasonItems);
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
	private class QuerySeasonItemsTask extends AsyncTask<Void,Void,ArrayList<SeasonItem>> {
		@Override
		protected ArrayList<SeasonItem> doInBackground(Void... nada) {
        	Log.d(TAG, "QuerySeasonItemsTask.doInBackground()");
    		ArrayList<SeasonItem> items = null;
    		try {
    			items = ((MainActivity) getActivity()).querySeasonItemsbyLeagueId(mLeagueItem);
    		} catch (Exception e) {
    			Log.e(TAG, "QuerySeasonItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<SeasonItem> items) {
        	Log.d(TAG, "QuerySeasonItemsTask.onPostExecute() fetched: " + items.size());
			mSeasonItems = items;
			setupSeason(QUERY);
            cancel(true); // done !
		}
	}
}