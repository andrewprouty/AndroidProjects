package com.prouty.leagueusa.schedule;

import java.util.ArrayList;

import com.prouty.leagueusa.sdsolschedule.R;

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

public class ConferenceListFragment extends Fragment{
	private static final String TAG = "ConferenceListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private DivisionItem mDivisionItem;
	private ArrayList<ConferenceItem> mConferenceQuery;
	private ArrayList<ConferenceItem> mConferenceFetch;
	private ArrayList<ConferenceItem> mConferenceDisplay;
	private ConferenceItem mConferenceItem;

	View view;
	TextView mSeasonTextView;
	TextView mDivisionTextView;
	TextView mConferenceTextView;
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
		mDivisionItem=((ConferenceListActivity) getActivity()).getDivisionItem();
		new QueryConferenceItemsTask().execute(mDivisionItem);

		view = inflater.inflate(R.layout.fragment_conference_list, container,false);
		mSeasonTextView = (TextView)view.findViewById(R.id.conference_list_season_name);
		mDivisionTextView = (TextView)view.findViewById(R.id.conference_list_division_name);
		mConferenceTextView = (TextView)view.findViewById(R.id.conference_list_conference_name);
		mListView = (ListView)view.findViewById(R.id.conference_list_view);

		mSeasonTextView.setText(mDivisionItem.getSeasonName());
		mDivisionTextView.setText(mDivisionItem.getDivisionName());

		mListView.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textViewItem = ((TextView) view.findViewById(R.id.row_conference_name_textView));
				String listItemText = textViewItem.getText().toString();
				Log.d(TAG, "onCreatView()setOnItemClickListener.onItemClick() Conference ["+position+"]= "+listItemText);
				returnConference(position);
			}
		});
		return view;
	}

	private void setupConference(int choice, int choiceSize) {
		if (getActivity() == null || mListView == null) {
			return;
		}
		Log.d(TAG, "setupConference("+choice+") division: "+mDivisionItem.getDivisionId()+"-"+mDivisionItem.getDivisionName());
		if (mConferenceDisplay == null || mConferenceDisplay.size() == 0) {
			if (choiceSize > 0) {
				if (choice == GET) {
					Log.d(TAG, "setupConference("+choice+") (2nd/GET) has the only results so insert them");
					mConferenceDisplay = mConferenceFetch;
					new InsertConferenceItemsTask().execute();
				}
				else {
					mConferenceDisplay = mConferenceQuery;
				}
				ConferenceListAdapter adapter = new ConferenceListAdapter(mConferenceDisplay, choice);
				mListView.setAdapter(adapter);
			}
			else {
				if (choice == QUERY) {
					Log.d(TAG, "setupConference("+choice+") (1st/QUERY) has no results");
				}
				else {
					Log.w(TAG, "setupConference("+choice+") (2nd/GET) also has no results");
					String msg = getActivity().getApplicationContext().getResources().getString(R.string.name_conference);
					msg = getActivity().getApplicationContext().getResources().getString(R.string.no_information_available, msg);
					Toast.makeText(getActivity().getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
				}
			}			
		}
		else { 
			if (choiceSize == 0) {
				Log.w(TAG, "setupConference("+choice+") (1st/QUERY) had results (2nd/GET) had none. Offline?");
			}
			else{
				if (!mConferenceFetch.equals(mConferenceQuery)) {
					Log.w(TAG, "setupConference("+choice+") Fetched != Queried. Sizes info only: "
							+ mConferenceFetch.size() + " " + mConferenceQuery.size());
					if (choice == GET) {
						Log.d(TAG, "setupConference("+choice+") (2nd/GET) difference so inserting");
						new InsertConferenceItemsTask().execute();
						Toast.makeText(getActivity().getApplicationContext(), R.string.try_again_for_update, Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Log.d(TAG, "setupConference("+choice+") Fetched=Queried");
				}
			}
		}
	}
	private void returnConference(int position) {
		mConferenceItem = mConferenceDisplay.get(position);
		mConferenceTextView.setText(mConferenceItem.getConferenceName());
		Log.d(TAG, "returnConference():"
				+ " league ID="    + mConferenceItem.getLeagueId()
				+ ", url="         + mConferenceItem.getLeagueURL()
				+ " season ID="    + mConferenceItem.getSeasonId()
				+ ", name="        + mConferenceItem.getSeasonName() 
				+ " division ID="  + mConferenceItem.getDivisionId()
				+ ", name="        + mConferenceItem.getDivisionName()
				+ " conferenceId=" + mConferenceItem.getConferenceId()
				+ ", name="        + mConferenceItem.getConferenceName()
				+ ", count="       + mConferenceItem.getConferenceCount());
		((ConferenceListActivity) getActivity()).launchTeamListActivity(mConferenceItem);
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
				mConferenceFetch = items;
			}
			setupConference(GET, size);
			cancel(true);
		}
	}
	private class ConferenceListAdapter extends ArrayAdapter<ConferenceItem> {
		public ConferenceListAdapter(ArrayList<ConferenceItem> items, int choice) {
			super(getActivity(), 0, items);
			Log.i(TAG, "ConferenceListAdapter Constructor ("+choice+")");
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.conference_list_row, parent, false);
			}

			ConferenceItem item = getItem(position);
			TextView conferenceTextView = (TextView)convertView.findViewById(R.id.row_conference_name_textView);
			Log.v(TAG, "ConferenceListAdapter getView() ["+position+"]: "+item.getConferenceName());
			conferenceTextView.setText(item.getConferenceName());
			return convertView;
		}
	}
	private class InsertConferenceItemsTask extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... nada) {
			Log.d(TAG, "InsertConferenceItemsTask.doInBackground()");
			try {
				((ConferenceListActivity) getActivity()).insertConferenceItems(mConferenceFetch);
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
				items = ((ConferenceListActivity) getActivity()).queryConferencesByDivisionItem(mDivisionItem);
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
				mConferenceQuery = items;
			}
			setupConference(QUERY, size);
			cancel(true);
			new FetchConferenceItemsTask().execute(mDivisionItem);
		}
	}
}