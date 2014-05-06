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
public class DivisionListFragment extends Fragment{
	private static final String TAG = "DivisionListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private ArrayList<DivisionItem> mDivisionQueried;
	private ArrayList<DivisionItem> mDivisionFetched;
	private ArrayList<DivisionItem> mDivisionDisplay;
	private ArrayList<ConferenceItem> mConferenceItems;
	private SeasonItem mSeasonItem;
	private DivisionItem mDivisionItem;
	private ConferenceItem mConferenceItem;

	View view;
	TextView mSeasonTextView;
	TextView mDivisionTextView;
	ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
		setRetainInstance(true); // survive across Activity re-create (i.e. orientation)
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{       
		Log.d(TAG, "onCreateView()");
		mSeasonItem=((DivisionListActivity) getActivity()).getSeasonItem();
		new QueryDivisionItemsTask().execute(mSeasonItem); // TODO Query & Fetch duplicates UI refresh
		new FetchDivisionItemsTask().execute(mSeasonItem);

		view = inflater.inflate(R.layout.fragment_division_list, container,false);
		mSeasonTextView = (TextView)view.findViewById(R.id.division_list_season_name);
		mDivisionTextView = (TextView)view.findViewById(R.id.division_list_division_name);
		mListView = (ListView)view.findViewById(R.id.division_list_view);

		mSeasonTextView.setText(mSeasonItem.getSeasonName());

		mListView.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textViewItem = ((TextView) view.findViewById(R.id.row_division_name_textView));
				String listItemText = textViewItem.getText().toString();
				Log.d(TAG, "onCreatView()setOnItemClickListener.onItemClick() Division ["+position+"]= "+listItemText);
				selectDivision(position);
			}
		});
		return view;
	}
	private void setupDivision(int choice, int choiceSize) {
		if (getActivity() == null || mListView == null) {
			return;
		}
		Log.d(TAG, "setupDivision("+choice+") choiceSize="+choiceSize+" season="+mSeasonItem.getSeasonId()+"-"+mSeasonItem.getSeasonName());
		if (mDivisionDisplay == null || mDivisionDisplay.size() == 0) {
			if (choiceSize > 0) {
				if (choice == GET) {						//No results yet, but I have some
					mDivisionDisplay = mDivisionFetched;
					new InsertDivisionItemsTask().execute();	// Most likely Query was fast but empty
				}
				else {
					mDivisionDisplay = mDivisionQueried;
				}
				DivisionListAdapter adapter = new DivisionListAdapter(mDivisionDisplay);
				mListView.setAdapter(adapter);
			} //[else] 1st with no results, or 2nd and nobody had results
		}
		else {//else: 1st had results.  I am 2nd 
			if (choiceSize > 0) {							// Both had results
				if (!mDivisionFetched.equals(mDivisionQueried)) {
					Log.d(TAG, "setupDivision("+choice+") Fetched != Queried. Sizes only: "
							+ mDivisionFetched.size() + " " + mDivisionQueried.size());
					if (choice == GET) {
						new InsertDivisionItemsTask().execute();
						Toast.makeText(getActivity().getApplicationContext(), R.string.try_again_for_update, Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Log.d(TAG, "setupDivision("+choice+") Fetched=Queried");
				}
			}
		}
	}
	private void selectDivision(int position) {
		mDivisionItem = mDivisionDisplay.get(position);
		mDivisionTextView.setText(mDivisionItem.getDivisionName());
		Log.i(TAG, "selectDivision()=["+position+"] "
				+ " league ID="    + mDivisionItem.getLeagueId()
				+ ", url="         + mDivisionItem.getLeagueURL()
				+ " season ID="    + mDivisionItem.getSeasonId()
				+ ", name="        + mDivisionItem.getSeasonName() 
				+ " division ID="  + mDivisionItem.getDivisionId()
				+ ", name="        + mDivisionItem.getDivisionName());
		new QueryConferenceItemsTask().execute(mDivisionItem); //TODO duplicates NEXT screen.  Move Fetch to post query? 
		new FetchConferenceItemsTask().execute(mDivisionItem);
	}
	private void returnConference(int choice, int choiceSize) {
		if (getActivity() == null || mListView == null) {
			return;
		}
		Log.d(TAG, "returnConference("+choice+") choiceSize="+choiceSize+" division="+mDivisionItem.getDivisionId()+"-"+mDivisionItem.getDivisionName());

		if (choiceSize > 0) {
			if (choice == GET) {
				new InsertConferenceItemsTask().execute(); 
			}
		}
		if (mConferenceItems != null) {
			int size = mConferenceItems.size();
			if (size == 0) {
				Toast.makeText(getActivity().getApplicationContext(), R.string.no_information_available, Toast.LENGTH_SHORT).show();
			}
			else {
				mConferenceItem = mConferenceItems.get(0);
				Log.d(TAG, "returnConference() about to log");
				Log.v(TAG, "returnConference():"
						+ " league ID="    + mConferenceItem.getLeagueId()
						+ ", url="         + mConferenceItem.getLeagueURL()
						+ " season ID="    + mConferenceItem.getSeasonId()
						+ ", name="        + mConferenceItem.getSeasonName() 
						+ " division ID="  + mConferenceItem.getDivisionId()
						+ ", name="        + mConferenceItem.getDivisionName()
						+ " conferenceId=" + mConferenceItem.getConferenceId()
						+ ", name="        + mConferenceItem.getConferenceName()
						+ ", count="       + mConferenceItem.getConferenceCount());
				if(size == 1) {
					((DivisionListActivity) getActivity()).launchTeamListActivity(mConferenceItem);
				}
				else { // multiple conferences, the user must choose
					((DivisionListActivity) getActivity()).launchConferenceListActivity(mConferenceItem);
				}
			}
		}
	}
	private class FetchDivisionItemsTask extends AsyncTask<SeasonItem,Void,ArrayList<DivisionItem>> {
		@Override
		protected ArrayList<DivisionItem> doInBackground(SeasonItem... params) {
			Log.d(TAG, "FetchDivisionTask doInBackground()");
			ArrayList<DivisionItem> items = null;
			try { // pass context for app dir to cache file
				items = new DivisionListLeagueUSA().fetchItems(mSeasonItem, getActivity().getApplicationContext());
			} catch (Exception e) {
				Log.e(TAG, "doInBackground() Exception.", e);
			}
			return items;
		}
		@Override
		protected void onPostExecute(ArrayList<DivisionItem> items) {
			Log.d(TAG, "FetchDivisionItemsTask onPostExecute()");
			int size;
			if (items == null || items.size() == 0) {
				size = 0;
			} else {
				size = items.size();
				mDivisionFetched = items;
			}
			setupDivision(GET, size);
			cancel(true);
		}
	}
	private class DivisionListAdapter extends ArrayAdapter<DivisionItem> {
		public DivisionListAdapter(ArrayList<DivisionItem> items) {
			super(getActivity(), 0, items);
			Log.d(TAG, "DivisionListAdapter Constructor");
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.division_list_row, parent, false);
			}
			DivisionItem item = getItem(position);
			TextView divisionTextView = (TextView)convertView.findViewById(R.id.row_division_name_textView);
			Log.v(TAG, "adapter.getView() item.getDivisionName(): "+item.getDivisionName());
			divisionTextView.setText(item.getDivisionName());
			return convertView;
		}
	}
	private class InsertDivisionItemsTask extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... nada) {
			Log.d(TAG, "InsertDivisionItemsTask.doInBackground()");
			try {
				((DivisionListActivity) getActivity()).insertDivisionItems(mDivisionFetched);
			} catch (Exception e) {
				Log.e(TAG, "InsertDivisionItemsTask.doInBackground() Exception.", e);
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void nada) {
			Log.d(TAG, "InsertDivisionItemsTask.onPostExecute()");
			cancel(true);
		}
	}
	private class QueryDivisionItemsTask extends AsyncTask<SeasonItem,Void,ArrayList<DivisionItem>> {
		@Override
		protected ArrayList<DivisionItem> doInBackground(SeasonItem... nada) {
			Log.d(TAG, "QueryDivisionItemsTask.doInBackground()");
			ArrayList<DivisionItem> items = null;
			try {
				items = ((DivisionListActivity) getActivity()).queryDivisionsBySeasonItem(mSeasonItem);
			} catch (Exception e) {
				Log.e(TAG, "QueryDivisionItemsTask.doInBackground() Exception.", e);
			}
			return items;
		}
		@Override
		protected void onPostExecute(ArrayList<DivisionItem> items) {
			Log.d(TAG, "QueryDivisionItemsTask.onPostExecute() queried=" + items.size());
			int size;
			if (items == null || items.size() == 0) {
				size = 0;
			} else {
				size = items.size();
				mDivisionQueried = items;
			}
			setupDivision(QUERY, size);
			cancel(true);
		}
	}
	private class FetchConferenceItemsTask extends AsyncTask<DivisionItem,Void,ArrayList<ConferenceItem>> {
		@Override
		protected ArrayList<ConferenceItem> doInBackground(DivisionItem... params) {
			Log.d(TAG, "FetchConferenceTask doInBackground()");
			ArrayList<ConferenceItem> items = null;
			try { // pass context for app dir to cache file
				items = new ConferenceListLeagueUSA().fetchItems(mDivisionItem, getActivity().getApplicationContext());
			} catch (Exception e) {
				Log.e(TAG, "doInBackground() Exception.", e);
			}
			return items;
		}
		@Override
		protected void onPostExecute(ArrayList<ConferenceItem> items) {
			Log.d(TAG, "FetchConferenceItemsTask.onPostExecute() fetched=" + items.size());
			int size;
			if (items == null || items.size() == 0) {
				size = 0;
			} else {
				size = items.size();
				mConferenceItems = items;
			}
			returnConference(GET, size);
			cancel(true);
		}
	}
	private class InsertConferenceItemsTask extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... nada) {
			Log.d(TAG, "InsertConferenceItemsTask.doInBackground()");
			try {
				((DivisionListActivity) getActivity()).insertConferenceItems(mConferenceItems);
			} catch (Exception e) {
				Log.e(TAG, "InsertConferenceItemsTask.doInBackground() Exception.", e);
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void nada) {
			Log.d(TAG, "InsertConferenceItemsTask.onPostExecute()");
			cancel(true);
		}
	}
	private class QueryConferenceItemsTask extends AsyncTask<DivisionItem,Void,ArrayList<ConferenceItem>> {
		@Override
		protected ArrayList<ConferenceItem> doInBackground(DivisionItem... nada) {
			Log.d(TAG, "QueryConferenceItemsTask.doInBackground()");
			ArrayList<ConferenceItem> items = null;
			try {
				items = ((DivisionListActivity) getActivity()).queryConferenceByDivisionItem(mDivisionItem);
			} catch (Exception e) {
				Log.e(TAG, "QueryConferenceItemsTask.doInBackground() Exception.", e);
			}
			return items;
		}
		@Override
		protected void onPostExecute(ArrayList<ConferenceItem> items) {
			Log.d(TAG, "QueryConferenceItemsTask.onPostExecute() queried=" + items.size());
			int size;
			if (items == null || items.size() == 0) {
				size = 0;
			} else {
				size = items.size();
				mConferenceItems = items;
			}
			returnConference(QUERY, size);
			cancel(true);
		}
	}
}