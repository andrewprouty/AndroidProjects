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
	private ArrayList<UserItem> mUserItems;
	private UserItem mUserItem;
	FetchUserItemsTask mFetchUserItemsTask = new FetchUserItemsTask();
	FetchLeagueItemsTask mFetchLeagueItemsTask = new FetchLeagueItemsTask();
	FetchSeasonItemsTask mFetchSeasonItemsTask = new FetchSeasonItemsTask();
	
	View view;
	TextView mUserTextView;
	ListView mListView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // survive across Activity re-create (i.e. orientation)
        mFetchUserItemsTask.execute();
        mFetchLeagueItemsTask.execute();
    }
	
    @Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{       
		Log.d(TAG, "onCreateView()");
		view = inflater.inflate(R.layout.fragment_season_list, container,false);
        mUserTextView = (TextView)view.findViewById(R.id.user_list_textView);
		mListView = (ListView)view.findViewById(R.id.user_list_view);
		mListView.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textViewItem = ((TextView) view.findViewById(R.id.row_user_name_textView));
				String listItemText = textViewItem.getText().toString();
				Log.d(TAG, "().onItemClick() User ["+position+"]= "+listItemText);
				returnSelection(position);
			}
		});
		return view;
	}
	
	private void knowLeague(int choice) {
		Log.d(TAG, "returnLeague("+choice+")");
		mLeagueItem = mLeagueItems.get(0);
		
		Log.d(TAG, "knowLeague() " + mLeagueItem.getLeagueId() + "-" + mLeagueItem.getOrgName());
        mFetchSeasonItemsTask.execute();
    	return;
    }

	private void knowSeason(int choice) {
		Log.d(TAG, "returnSeason("+choice+")");
		mSeasonItem = mSeasonItems.get(1);
		
		Log.d(TAG, "returnSeason() " + mSeasonItem.getLeagueId() + "-" + mSeasonItem.getSeasonId()+ "-" + mSeasonItem.getSeasonName());
    	return;
    }

	private void setupAdapter(int choice) {
    	if (getActivity() == null || mListView == null) {
    		return;
    	}
    	Log.d(TAG, "setupAdapter("+choice+")");

    	if (choice == GET) {
    		if (mUserItems != null && mUserItems.size()>0) {
    			// Async to save the fetched list to DB
    			new InsertUserItemsTask().execute(); // save fetched to DB
    		}
    		else {
    			// none. If in DB can populate from there
    			new QueryUserItemsTask().execute();
    		}
    	}
		if (mUserItems != null) {
			UserListAdapter adapter = new UserListAdapter(mUserItems);
			mListView.setAdapter(adapter);
		}
		else {
			mListView.setAdapter(null);
		}
    }

    private void returnSelection(int position) {
		mFetchUserItemsTask.cancel(true);
    	mUserItem = mUserItems.get(position);
    	Log.i(TAG, "returnSelection()=["+position+"] "+mUserItem.getUserId()+": "+mUserItem.getUserName());
		mUserTextView.setText(mUserItem.getUserName());
		((MainActivity) getActivity()).launchPhotoListActivity(mUserItem);
    }
    private class FetchUserItemsTask extends AsyncTask<Void,Void,ArrayList<UserItem>> {
        @Override
        protected ArrayList<UserItem> doInBackground(Void... params) {
        	Log.d(TAG, "FetchUserItemsTask.doInBackground()");
    		ArrayList<UserItem> items = null;
    		try {
    			// pass context for app dir to cache file
        		items = new UserListBismarck().fetchItems(getActivity().getApplicationContext());
    		} catch (Exception e) {
    			Log.e(TAG, "FetchUserItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
        }
        @Override
        protected void onPostExecute(ArrayList<UserItem> items) {
        	try {
        		mUserItems = items;
        		Log.d(TAG, "FetchUserItemsTask.onPostExecute()");
        		setupAdapter(GET); // show listing
        		cancel(true); // done !
        	} catch (Exception e) {
        		Log.e(TAG, "FetchUserItemsTask.doInBackground() Exception.", e);
        	}
        }
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
        		knowLeague(GET); // show listing
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
        		knowSeason(GET); // show listing
        		cancel(true); // done !
        	} catch (Exception e) {
        		Log.e(TAG, "FetchSeasonItemsTask.doInBackground() Exception.", e);
        	}
        }
    }
    private class UserListAdapter extends ArrayAdapter<UserItem> {
        public UserListAdapter(ArrayList<UserItem> userItems) {
            super(getActivity(), 0, userItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.user_list_row, parent, false);
            }
            
            UserItem item = getItem(position);
            TextView userTextView = (TextView)convertView.findViewById(R.id.row_user_name_textView);
			Log.v(TAG, "adapter.getView() item.getUserName(): "+item.getUserName());
            userTextView.setText(item.getUserName());
            
            return convertView;
        }
    }
    private class InsertUserItemsTask extends AsyncTask<Void,Void,Void> {
    	//<x,y,z> params: 1-doInBackground(x); 2-onProgressUpdate(y); 3-onPostExecute(z) 
    	@Override
    	protected Void doInBackground(Void... nada) {
    		Log.d(TAG, "InsertUserItemsTask.doInBackground()");
    		try {
        		 ((MainActivity) getActivity()).insertUserItems(mUserItems);
    		} catch (Exception e) {
    			Log.e(TAG, "InsertUserItemsTask.doInBackground() Exception.", e);
    		}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void nada) {
    		Log.d(TAG, "InsertUserItemsTask.onPostExecute()");
    		cancel(true); // done !
    	}
    }
	private class QueryUserItemsTask extends AsyncTask<Void,Void,ArrayList<UserItem>> {
		@Override
		protected ArrayList<UserItem> doInBackground(Void... nada) {
        	Log.d(TAG, "QueryUserItemsTask.doInBackground()");
    		ArrayList<UserItem> items = null;
    		try {
    			items = ((MainActivity) getActivity()).queryUserItems();
    		} catch (Exception e) {
    			Log.e(TAG, "QueryUserItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<UserItem> userItems) {
			mUserItems = userItems;
			setupAdapter(QUERY);
            cancel(true); // done !
        	Log.d(TAG, "QueryUserItemsTask.onPostExecute()");
		}
	}
}