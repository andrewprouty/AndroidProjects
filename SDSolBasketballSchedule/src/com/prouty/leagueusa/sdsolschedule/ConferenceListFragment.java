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

public class ConferenceListFragment extends Fragment{
	private static final String TAG = "ConferenceListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private DivisionItem mDivisionItem;
	private ArrayList<ConferenceItem> mConferenceItems; // Query and then GET
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
    	if (choiceSize > 0) {
        	if (choice == GET) {
    			new InsertConferenceItemsTask().execute();
    		}
    		ConferenceListAdapter adapter = new ConferenceListAdapter(mConferenceItems);
			mListView.setAdapter(adapter);
    	}
	}
	private void returnConference(int position) {
		mConferenceItem = mConferenceItems.get(position);
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
    			mConferenceItems = items;
    		}
			setupConference(GET, size);
    		cancel(true);
		}
	}
	private class ConferenceListAdapter extends ArrayAdapter<ConferenceItem> {
		public ConferenceListAdapter(ArrayList<ConferenceItem> items) {
			super(getActivity(), 0, items);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.conference_list_row, parent, false);
			}

			ConferenceItem item = getItem(position);
			TextView conferenceTextView = (TextView)convertView.findViewById(R.id.row_conference_name_textView);
			Log.v(TAG, "adapter.getView() item.getConferenceName(): "+item.getConferenceName());
			conferenceTextView.setText(item.getConferenceName());
			return convertView;
		}
	}
    private class InsertConferenceItemsTask extends AsyncTask<Void,Void,Void> {
    	@Override
    	protected Void doInBackground(Void... nada) {
    		Log.d(TAG, "InsertConferenceItemsTask.doInBackground()");
    		try {
    			((ConferenceListActivity) getActivity()).insertConferenceItems(mConferenceItems);
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
    			new FetchConferenceItemsTask().execute(mDivisionItem);
    		} else {
    			size = items.size();
    			mConferenceItems = items;
           		setupConference(QUERY, size);
    		}
            cancel(true);
		}
	}
}