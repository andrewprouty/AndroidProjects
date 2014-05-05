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
	private void setupLeague(int choice, int choiceSize) {
		// Really only expect 1:San Diego Sol. Prepared for future extensibility
		if (getActivity() == null || mListView == null) {
    		return;
    	}
		Log.d(TAG, "setupLeague("+choice+") choiceSize="+choiceSize);

    	if (choiceSize > 0) {
        	if (choice == GET) {
    			new InsertLeagueItemsTask().execute(); // save fetched to DB
        	}
		}
    	else if (choiceSize > 1) {
    		Log.e(TAG, "setupLeague() 1 league should have been returned, received "+choiceSize+". Providing hardcode.");
			LeagueItem item = new LeagueItem();
			item.setLeagueId("1");
			item.setOrgName("San Diego Sol");
			item.setLeagueURL("http://www.sdsolbasketball.com/mobileschedule.php");
			mLeagueItems.add(item);
    	}
		//Change when more leagues. Currently this is the only/expected value.
    	//Scenario is a new install: QUERY returns nothing (fast) & GET eventually errored as offline
    	if (choice == QUERY && choiceSize == 0 && mLeagueItems == null) {
			Log.e(TAG, "setupLeague() 1 league should have been returned, received zero. Providing hardcode");
			LeagueItem item = new LeagueItem();
			item.setLeagueId("1");
			item.setOrgName("San Diego Sol");
			item.setLeagueURL("http://www.sdsolbasketball.com/mobileschedule.php");
			ArrayList<LeagueItem> items = new ArrayList<LeagueItem>();  
			items.add(item);
			mLeagueItems = items;
    	}

    	//GET & QUERY are in parallel - picking GET as the winner (since faster & hardcode above)
    	if (choice == QUERY) {
			mLeagueItem = mLeagueItems.get(mLeagueItems.size()-1);
			Log.d(TAG, "setupLeague(). [0]:"+mLeagueItem.getLeagueId()+"-"+mLeagueItem.getOrgName()+"-"+mLeagueItem.getLeagueURL());
			new QuerySeasonItemsTask().execute(); // fast
			new FetchSeasonItemsTask().execute(); // add in anything new
    	}
		Log.d(TAG, "setupSeason("+choice+").");
    }
	private void setupSeason(int choice, int choiceSize) {
    	if (getActivity() == null || mListView == null) {
    		return;
    	}
		Log.d(TAG, "setupSeason("+choice+") choiceSize="+choiceSize);
    	if (choiceSize > 0) {
        	if (choice == GET) {
       			new InsertSeasonItemsTask().execute(); // save fetched to DB
        	}
			SeasonListAdapter adapter = new SeasonListAdapter(mSeasonItems);
			mListView.setAdapter(adapter);
		}
		//removed zero/else case: mListView.setAdapter(null);
		Log.d(TAG, "setupSeason("+choice+").");
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
        		Log.d(TAG, "FetchLeagueItemsTask.onPostExecute() fetched=" + items.size());
        		int size;
        		if (items == null || items.size() == 0) {
        			size = 0;
        		} else {
        			size = items.size();
        			mLeagueItems = items;
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
        	Log.d(TAG, "QueryLeagueItemsTask.onPostExecute() queried=" + items.size());
        	int size;
    		if (items == null || items.size() == 0) {
    			size = 0;
    		} else {
    			size = items.size();
    			mLeagueItems = items;
    		}
			setupLeague(QUERY, size);
            cancel(true); // done !
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
        		Log.d(TAG, "FetchSeasonItemsTask.onPostExecute() fetched=" + items.size());
        		int size;
        		if (items == null || items.size() == 0) {
        			size = 0;
        		} else {
        			size = items.size();
            		mSeasonItems = items;
        		}
           		setupSeason(GET, size); // show listing
        		cancel(true);
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
        	Log.d(TAG, "QuerySeasonItemsTask.onPostExecute() queried=" + items.size());
    		int size;
    		if (items == null || items.size() == 0) {
    			size = 0;
    		} else {
    			size = items.size();
        		mSeasonItems = items;
    		}
       		setupSeason(QUERY, size);
            cancel(true);
		}
	}
}