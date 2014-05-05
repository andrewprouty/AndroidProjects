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
		new FetchLeagueItemsTask().execute();
       
		view = inflater.inflate(R.layout.fragment_season_list, container,false);
        mSeasonTextView = (TextView)view.findViewById(R.id.season_list_textView);
		mListView = (ListView)view.findViewById(R.id.season_list_view);
		mListView.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textViewItem = ((TextView) view.findViewById(R.id.row_season_name_textView));
				//TextView textViewItem = ((TextView) view.findViewById(R.id.row_user_name_textView));
				String listItemText = textViewItem.getText().toString();
				Log.d(TAG, "().onItemClick() Season ["+position+"]= "+listItemText);
				returnSelection(position);
			}
		});
		return view;
	}

	private void setupSeason(int choice) {
    	if (getActivity() == null || mListView == null) {
    		return;
    	}
		Log.d(TAG, "setupSeason("+choice+")");
    	if (choice == GET) {
    		if (mSeasonItems != null && mSeasonItems.size()>0) {
    			// Async to save the fetched list to DB
    			//new InsertSeasonItemsTask().execute(); // save fetched to DB
    			Log.w(TAG, "setupSeason() replace with insert/save to DB"); //TODO League insert DB
    		}
    		else {
    			// none. If in DB can populate from there
    			//new QuerySeasonItemsTask().execute();
    			Log.e(TAG, "setupSeason() replace with query from DB"); //TODO League query DB
    		}
    	}
		//mSeasonItem = mSeasonItems.get(1); //TODO Add user's selection
		//Log.d(TAG, "setupSeason() " + mSeasonItem.getLeagueId() + "-" + mSeasonItem.getSeasonId()+ "-" + mSeasonItem.getSeasonName());
    	if (mSeasonItems != null) {
			SeasonListAdapter adapter = new SeasonListAdapter(mSeasonItems);
			mListView.setAdapter(adapter);
		}
		else {
			mListView.setAdapter(null);
		}
		Log.d(TAG, "setupSeason().");
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
    			Log.w(TAG, "setupLeague() replace with insert/save to DB"); //TODO Season query DB
    			//new InsertLeagueItemsTask().execute(); // save fetched to DB
    		}
    		else {	// none. If in DB can populate from there
    			Log.e(TAG, "setupLeague() replace with query from DB"); //TODO Season query DB
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
		mLeagueItem = mLeagueItems.get(0);
		Log.d(TAG, "setupLeague() [0]:"+mLeagueItem.getLeagueId()+"-"+mLeagueItem.getOrgName()+"-"+mLeagueItem.getLeagueURL());
		new FetchSeasonItemsTask().execute();
    }
    private void returnSelection(int position) {
    	mSeasonItem = mSeasonItems.get(position);
		mSeasonTextView.setText(mSeasonItem.getSeasonName());
		Log.i(TAG, "returnSelection()=["+position+"] "
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
        		mLeagueItems = items;
        		Log.d(TAG, "FetchLeagueItemsTask.onPostExecute()");
        		setupLeague(GET); // show listing
        		cancel(true); // done !
        	} catch (Exception e) {
        		Log.e(TAG, "FetchLeagueItemsTask.doInBackground() Exception.", e);
        	}
        }
    }
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
        		mSeasonItems = items;
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
}