package com.prouty.leagueusa.schedule;

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

public class LeagueListFragment extends Fragment{
	private static final String TAG = "LeagueListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private ArrayList<LeagueItem> mLeagueQuery;
	private ArrayList<LeagueItem> mLeagueFetch;
	private ArrayList<LeagueItem> mLeagueDisplay;
	private LeagueItem mLeagueItem;
	LeagueListAdapter adapter;

	View view;
	TextView mLeagueTextView;
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
		FavoriteListUtil util = new FavoriteListUtil();
		mLeagueItem=util.getHomeLeagueItem(getActivity().getApplicationContext());
		Log.d(TAG, "onCreateView() execute QueryLeagueItemsTask()");
		new QueryLeagueItemsTask().execute();

		view = inflater.inflate(R.layout.fragment_league_list, container,false);
		mLeagueTextView = (TextView)view.findViewById(R.id.league_list_league_name);
		mListView = (ListView)view.findViewById(R.id.league_list_view);
		if (mLeagueItem != null && mLeagueItem.getOrgName() != null) {
			mLeagueTextView.setText(mLeagueItem.getOrgName());
		}

		mListView.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textViewItem = ((TextView) view.findViewById(R.id.row_league_name_textView));
				String listItemText = textViewItem.getText().toString();
				Log.d(TAG, "().onItemClick() League ["+position+"]= "+listItemText);
				returnLeague(position);
			}
		});
		return view;
	}

	private void setupLeague(int choice, int choiceSize) {
		if (getActivity() == null || mListView == null) {
			return;
		}
		Log.d(TAG, "setupLeague("+choice+") choiceSize="+choiceSize);
		if (mLeagueDisplay == null || mLeagueDisplay.size() == 0) {
			if (choiceSize > 0) {
				if (choice == GET) {
					Log.d(TAG, "setupLeague("+choice+") (2nd/GET) has the only results so insert them");
					mLeagueDisplay = mLeagueFetch;
					new InsertLeagueItemsTask().execute();
				}
				else {
					mLeagueDisplay = mLeagueQuery;
				}
				adapter = new LeagueListAdapter(mLeagueDisplay, choice);
				mListView.setAdapter(adapter);
			}
			else {
				if (choice == QUERY) {
					Log.d(TAG, "setupLeague("+choice+") (1st/QUERY) has no results");
				}
				else {
					Log.w(TAG, "setupLeague("+choice+") (2nd/GET) also has no results");
					String msg = getActivity().getApplicationContext().getResources().getString(R.string.name_league);
					msg = getActivity().getApplicationContext().getResources().getString(R.string.no_information_available, msg);
					Toast.makeText(getActivity().getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
				}
			}
		}
		else {
			if (choiceSize == 0) {
				Log.w(TAG, "setupLeague("+choice+") (1st/QUERY) had results (2nd/GET) had none. Offline?");
			}
			else {
				if (!mLeagueFetch.equals(mLeagueQuery)) {
					Log.w(TAG, "setupLeague("+choice+") Fetched != Queried. Sizes only: "
							+ mLeagueFetch.size() + " " + mLeagueQuery.size());
					if (choice == GET) {
						Log.d(TAG, "setupLeague("+choice+") (2nd/GET) difference so inserting");
						mLeagueDisplay.clear();
						mLeagueDisplay.addAll(mLeagueFetch);
						adapter.notifyDataSetChanged();
						Toast.makeText(getActivity().getApplicationContext(), R.string.new_information_available, Toast.LENGTH_SHORT).show();
						new InsertLeagueItemsTask().execute();
					}
				}
				else {
					Log.d(TAG, "setupLeague("+choice+") Fetched=Queried");
				}
			}
		}
	}
	private void returnLeague(int position) {
		mLeagueItem = mLeagueDisplay.get(position);
		mLeagueTextView.setText(mLeagueItem.getOrgName());
		Log.i(TAG, "returnLeague()=["+position+"] "
				+ mLeagueItem.getLeagueId() + "-"
				+ mLeagueItem.getOrgName() + " ("
				+ mLeagueItem.getLeagueURL() + ")");
		((LeagueListActivity) getActivity()).launchSeasonListActivity(mLeagueItem);
	}
	private class FetchLeagueItemsTask extends AsyncTask<Void,Void,ArrayList<LeagueItem>> {
		@Override
		protected ArrayList<LeagueItem> doInBackground(Void... params) {
			Log.d(TAG, "FetchLeagueItemsTask.doInBackground()");
			ArrayList<LeagueItem> items = null;
			try {
				// pass context for app dir to cache file
				items = new LeagueListGoogle().fetchItems(getActivity().getApplicationContext());
			} catch (Exception e) {
				Log.e(TAG, "FetchLeagueItemsTask.doInBackground() Exception.", e);
			}
			return items;
		}
		@Override
		protected void onPostExecute(ArrayList<LeagueItem> items) {
			try {
				if (items == null) {
					Log.d(TAG, "FetchLeagueItemsTask.onPostExecute() fetched=NULL");
				}
				else {
					Log.d(TAG, "FetchLeagueItemsTask.onPostExecute() fetched=" + items.size());
				}
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
				((LeagueListActivity) getActivity()).insertLeagueItems(mLeagueFetch);
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
				items = ((LeagueListActivity) getActivity()).queryLeagueItems();
			} catch (Exception e) {
				Log.e(TAG, "QueryLeagueItemsTask.doInBackground() Exception.", e);
			}
			return items;
		}
		@Override
		protected void onPostExecute(ArrayList<LeagueItem> items) {
			if (items == null) {
				Log.d(TAG, "QueryLeagueItemsTask.onPostExecute() queried=NULL");
			}
			else {
				Log.d(TAG, "QueryLeagueItemsTask.onPostExecute() queried=" + items.size());
			}
			int size;
			if (items == null || items.size() == 0) {
				size = 0;
			} else {
				size = items.size();
				mLeagueQuery = items;
			}
			setupLeague(QUERY, size);
			cancel(true);
			Log.d(TAG, "QueryLeagueItemsTask.onPostExecute() execute FetchLeagueItemsTask()");
			new FetchLeagueItemsTask().execute();
		}
	}
	private class LeagueListAdapter extends ArrayAdapter<LeagueItem> {
		public LeagueListAdapter(ArrayList<LeagueItem> items, int choice) {
			super(getActivity(), 0, items);
			Log.i(TAG, "LeagueListAdapter Constructor ("+choice+")");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.league_list_row, parent, false);
			}
			LeagueItem item = getItem(position);
			TextView leagueTextView = (TextView)convertView.findViewById(R.id.row_league_name_textView);
			Log.v(TAG, "LeagueListAdapter getView(): "+item.getOrgName());
			leagueTextView.setText(item.getOrgName());
			return convertView;
		}
	}
}