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

public class SeasonListFragment extends Fragment{
	private static final String TAG = "SeasonListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private LeagueItem mLeagueItem;
	private ArrayList<SeasonItem> mSeasonQuery;
	private ArrayList<SeasonItem> mSeasonFetch;
	private ArrayList<SeasonItem> mSeasonDisplay;
	private SeasonItem mSeasonItem;
	SeasonListAdapter adapter;

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
		mLeagueItem=((MainActivity) getActivity()).getLeagueItem();
		if (mSeasonDisplay != null) {
			mSeasonDisplay.clear(); // reset in case of orientation switch
		}
		new QuerySeasonItemsTask().execute();

		view = inflater.inflate(R.layout.fragment_season_list, container,false);
		mSeasonTextView = (TextView)view.findViewById(R.id.season_list_season_name);
		mListView = (ListView)view.findViewById(R.id.season_list_view);
		
		if (mSeasonItem != null) {
			mSeasonTextView.setText(mSeasonItem.getSeasonName());
		}
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
	private void setupSeason(int choice, int choiceSize) {
		if (getActivity() == null || mListView == null) {
			return;
		}
		Log.d(TAG, "setupSeason("+choice+") choiceSize="+choiceSize);
		if (mSeasonDisplay == null || mSeasonDisplay.size() == 0) {
			if (choiceSize > 0) {
				if (choice == GET) {
					Log.d(TAG, "setupSeason("+choice+") (2nd/GET) has the only results so insert them");
					mSeasonDisplay = mSeasonFetch;
					new InsertSeasonItemsTask().execute();
				}
				else {
					mSeasonDisplay = mSeasonQuery;
				}
				adapter = new SeasonListAdapter(mSeasonDisplay, choice);
				mListView.setAdapter(adapter);
			}
			else {
				if (choice == QUERY) {
					Log.d(TAG, "setupSeason("+choice+") (1st/QUERY) has no results");
				}
				else {
					Log.w(TAG, "setupSeason("+choice+") (2nd/GET) also has no results");
					String msg = getActivity().getApplicationContext().getResources().getString(R.string.name_season);
					msg = getActivity().getApplicationContext().getResources().getString(R.string.no_information_available, msg);
					Toast.makeText(getActivity().getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
				}
			}
		}
		else {
			if (choiceSize == 0) {
				Log.w(TAG, "setupSeason("+choice+") (1st/QUERY) had results (2nd/GET) had none. Offline?");
			}
			else {
				if (!mSeasonFetch.equals(mSeasonQuery)) {
					Log.w(TAG, "setupSeason("+choice+") Fetched != Queried. Sizes only: "
							+ mSeasonFetch.size() + " " + mSeasonQuery.size());
					if (choice == GET) {
						Log.d(TAG, "setupSeason("+choice+") (2nd/GET) difference so inserting");
						mSeasonDisplay.clear();
						mSeasonDisplay.addAll(mSeasonFetch);
						adapter.notifyDataSetChanged();
						Toast.makeText(getActivity().getApplicationContext(), R.string.new_information_available, Toast.LENGTH_SHORT).show();
						new InsertSeasonItemsTask().execute();
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
			new FetchSeasonItemsTask().execute();
		}
	}
}