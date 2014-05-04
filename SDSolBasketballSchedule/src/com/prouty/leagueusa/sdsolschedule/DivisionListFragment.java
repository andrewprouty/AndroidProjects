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
	private ArrayList<DivisionItem> mDivisionItems;
	private ArrayList<ConferenceItem> mConferenceItems;
	private SeasonItem mSeasonItem;
	private DivisionItem mDivisionItem;
	private ConferenceItem mConferenceItem;
	
	FetchDivisionItemsTask mFetchDivisionItemsTask = new FetchDivisionItemsTask();

	View view;
	TextView mSeasonTextView;
	TextView mDivisionTextView;
	ListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	Log.d(TAG, "onCreate()");

		setRetainInstance(true); // survive across Activity re-create (i.e. orientation)
		mSeasonItem=((DivisionListActivity) getActivity()).getSeasonItem();
		mFetchDivisionItemsTask.execute(mSeasonItem);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{       
    	Log.d(TAG, "onCreateView()");
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

	private void setupDivision(int choice) {
		if (getActivity() == null || mListView == null) {
			return;
		}
    	Log.d(TAG, "setupDivision("+choice+") season: "+mSeasonItem.getSeasonId()+"-"+mSeasonItem.getSeasonName());
    	if (choice == GET) {
    		if (mDivisionItems != null && mDivisionItems.size()>0) {
    			Log.w(TAG, "setupDivision() replace with insert/save to DB"); //TODO
    			//new InsertDivisionItemsTask().execute(); //save fetched to DB
    		}
    		else { // got none. If in DB - populate from there
    			Log.e(TAG, "setupDivision() replace with query from DB"); //TODO
    			//new QueryDivisionItemsTask().execute(mSeasonItem);
    		}
		}
    	if (mDivisionItems != null) {
    		DivisionListAdapter adapter = new DivisionListAdapter(mDivisionItems);
			mListView.setAdapter(adapter);
		}
		else {
			mListView.setAdapter(null);
		}
	}
	private void selectDivision(int position) {
		mFetchDivisionItemsTask.cancel(true);
    	mDivisionItem = mDivisionItems.get(position);
		mDivisionTextView.setText(mDivisionItem.getDivisionName());
		Log.i(TAG, "selectDivision()=["+position+"] "
				+ mDivisionItem.getLeagueId() + " ("
				+ mDivisionItem.getLeagueURL() + "); "
				+ mDivisionItem.getSeasonId() + "-"
				+ mDivisionItem.getSeasonName() + "; " 
				+ mDivisionItem.getDivisionId() + "-"
				+ mDivisionItem.getDivisionName());
		new FetchConferenceItemsTask().execute(mDivisionItem); //new here as may repeat

	}
	private void returnConference(int choice) {
		//TODO (Limitation) NO UI for Conference, SDSol uses 1:1 division:conference
		if (getActivity() == null || mListView == null) {
    		return;
    	}
		Log.d(TAG, "returnConference("+choice+")");

    	if (choice == GET) {
    		if (mConferenceItems != null && mConferenceItems.size()>0) {
    			// Async to save the fetched list to DB
    			Log.w(TAG, "returnConference() replace with insert/save to DB"); //TODO
    			//new InsertConferenceItemsTask().execute(); // save fetched to DB
    		}
    		else {	// none. If in DB can populate from there
    			Log.e(TAG, "returnConference() replace with query from DB"); //TODO
    			//new QueryConferenceItemsTask().execute();
    		}
    	}
		if (mConferenceItems != null) {
			int size = mConferenceItems.size();
			if (size > 1) {
				Log.e(TAG, "returnConference() 1 conference should have been returned, received. Will use [0] "+ size);
			}
		}
		else {
			Log.e(TAG, "returnConference() 1 conference should have been returned, received zero. Required- no good guess");
			int msgId = R.string.fatal_multiple_conference_per_division;
			Toast.makeText(getActivity().getApplicationContext(), msgId, Toast.LENGTH_SHORT).show();
			return;
		}
		mConferenceItem = mConferenceItems.get(0);
		Log.d(TAG, "returnConference().");
		Log.d(TAG, "returnConference()"
				+ mConferenceItem.getLeagueId() + " ("
				+ mConferenceItem.getLeagueURL() + "); "
				+ mConferenceItem.getSeasonId() + "-"
				+ mConferenceItem.getSeasonName() + "; " 
				+ mConferenceItem.getDivisionId() + "-"
				+ mConferenceItem.getDivisionName() + "; "
				+ mConferenceItem.getConferenceId() + "-"
				+ mConferenceItem.getConferenceName());
		((DivisionListActivity) getActivity()).launchTeamListActivity(mConferenceItem);
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
		protected void onPostExecute(ArrayList<DivisionItem> divisionItems) {
			mDivisionItems = divisionItems;
			setupDivision(GET);
            cancel(true); // done !
        	Log.d(TAG, "FetchDivisionItemsTask onPostExecute()");
		}
	}
	private class DivisionListAdapter extends ArrayAdapter<DivisionItem> {
		public DivisionListAdapter(ArrayList<DivisionItem> items) {
			super(getActivity(), 0, items);
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
		protected void onPostExecute(ArrayList<ConferenceItem> conferenceItems) {
			mConferenceItems = conferenceItems;
			returnConference(GET);
            cancel(true); // done !
        	Log.d(TAG, "FetchConferenceItemsTask onPostExecute()");
		}
	}
}