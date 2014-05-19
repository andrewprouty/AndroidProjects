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

public class TeamListFragment extends Fragment{
	private static final String TAG = "TeamListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private ConferenceItem mConferenceItem;
	private ArrayList<TeamItem> mTeamQuery;
	private ArrayList<TeamItem> mTeamFetch;
	private ArrayList<TeamItem> mTeamDisplay;
	private TeamItem mTeamItem;
	
	View view;
	TextView mSeasonTextView;
	TextView mDivisionTextView;
	TextView mConferenceTextView;
	TextView mTeamTextView;
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
		mConferenceItem=((TeamListActivity) getActivity()).getConferenceItem();
		if (mTeamDisplay != null) {
			mTeamDisplay.clear(); // reset in case of orientation switch
		}
		new QueryTeamItemsTask().execute(mConferenceItem);
		
		if (mConferenceItem.getConferenceCount().equals("one")) {
			view = inflater.inflate(R.layout.fragment_team_list_no_conference, container,false);
		}
		else {
			view = inflater.inflate(R.layout.fragment_team_list_show_conference, container,false);
			mConferenceTextView = (TextView)view.findViewById(R.id.team_list_conference_name); 
			mConferenceTextView.setText(mConferenceItem.getConferenceName());
		}
		mSeasonTextView = (TextView)view.findViewById(R.id.team_list_season_name);
		mDivisionTextView = (TextView)view.findViewById(R.id.team_list_division_name); 
		mTeamTextView = (TextView)view.findViewById(R.id.team_list_team_name);
		mListView = (ListView)view.findViewById(R.id.team_list_view);

		mSeasonTextView.setText(mConferenceItem.getSeasonName());
		mDivisionTextView.setText(mConferenceItem.getDivisionName());

		mListView.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textViewItem = ((TextView) view.findViewById(R.id.row_team_name_textView));
				String listItemText = textViewItem.getText().toString();
				Log.d(TAG, "onCreateView()setOnItemClickListener.onItemClick() Team ["+position+"]= "+listItemText);
				returnTeam(position);
			}
		});
		return view;
	}
	private void setupTeam(int choice, int choiceSize) {
		if (getActivity() == null || mListView == null) {
			return;
		}
    	Log.d(TAG, "setupTeam("+choice+") season: "+mConferenceItem.getConferenceId()+"-"+mConferenceItem.getConferenceName());
		if (mTeamDisplay == null || mTeamDisplay.size() == 0) {
			if (choiceSize > 0) {
				if (choice == GET) {						// No results yet, but I have some
					Log.d(TAG, "setupTeam("+choice+") (2nd/GET) has the only results so insert them");
					mTeamDisplay = mTeamFetch;
					new InsertTeamItemsTask().execute();
				}
				else {
					mTeamDisplay = mTeamQuery;
				}
				TeamListAdapter adapter = new TeamListAdapter(mTeamDisplay, choice);
				mListView.setAdapter(adapter);
			}
			else {
				if (choice == QUERY) {
					Log.d(TAG, "setupTeam("+choice+") (1st/QUERY) has no results");
				}
				else {
					Log.w(TAG, "setupTeam("+choice+") (2nd/GET) also has no results");
					String msg = getActivity().getApplicationContext().getResources().getString(R.string.name_team);
					msg = getActivity().getApplicationContext().getResources().getString(R.string.no_information_available, msg);
					Toast.makeText(getActivity().getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
				}
			}
		}
		else { 
			if (choiceSize == 0) {
				Log.w(TAG, "setupTeam("+choice+") (1st/QUERY) had results (2nd/GET) had none. Offline?");
			}
			else {
				if (!mTeamFetch.equals(mTeamQuery)) {
					Log.w(TAG, "setupTeam("+choice+") Fetched != Queried. Sizes info only: "
							+ mTeamFetch.size() + " " + mTeamQuery.size());
					if (choice == GET) {
						new InsertTeamItemsTask().execute();
						Toast.makeText(getActivity().getApplicationContext(), R.string.try_again_for_update, Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Log.d(TAG, "setupTeam("+choice+") Fetched=Queried");
				}
			}
		}
	}
	private void returnTeam(int position) {
		
    	mTeamItem = mTeamDisplay.get(position);
		mTeamTextView.setText(mTeamItem.getTeamName());
		Log.i(TAG, "returnTeam()=["+position+"]"
				+ " league ID="     + mTeamItem.getLeagueId()
				+ ", url="          + mTeamItem.getLeagueURL()
				+ " season ID="     + mTeamItem.getSeasonId()
				+ ", name="         + mTeamItem.getSeasonName() 
				+ " division ID="   + mTeamItem.getDivisionId()
				+ ", name="         + mTeamItem.getDivisionName()
				+ " conferenceId="  + mTeamItem.getConferenceId()
				+ ", name="         + mTeamItem.getConferenceName()
				+ ", count="        + mTeamItem.getConferenceCount()
				+ " team ID="       + mTeamItem.getTeamId()
				+ ", name="         + mTeamItem.getTeamName()
				+ ", url="          + mTeamItem.getTeamURL());

		((TeamListActivity) getActivity()).launchGameListActivity(mTeamItem);
	}
	private class FetchTeamItemsTask extends AsyncTask<ConferenceItem,Void,ArrayList<TeamItem>> {
		@Override
		protected ArrayList<TeamItem> doInBackground(ConferenceItem... params) {
        	Log.d(TAG, "FetchTeamItemsTask doInBackground()");
    		ArrayList<TeamItem> items = null;
    		try { // pass context for app dir to cache file
        		items = new TeamListLeagueUSA().fetchItems(mConferenceItem, getActivity().getApplicationContext());
    		} catch (Exception e) {
    			Log.e(TAG, "doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<TeamItem> items) {
        	try {
        		Log.d(TAG, "FetchTeamItemsTask.onPostExecute() fetched=" + items.size());
        		int size;
        		if (items == null || items.size() == 0) {
        			size = 0;
        		} else {
        			size = items.size();
            		mTeamFetch = items;
        		}
           		setupTeam(GET, size);
        		cancel(true);
        	} catch (Exception e) {
        		Log.e(TAG, "FetchTeamItemsTask.doInBackground() Exception.", e);
        	}
		}
	}
	private class TeamListAdapter extends ArrayAdapter<TeamItem> {
		public TeamListAdapter(ArrayList<TeamItem> items, int choice) {
			super(getActivity(), 0, items);
			Log.i(TAG, "TeamListAdapter Constructor ("+choice+")");
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.team_list_row, parent, false);
			}
			TeamItem item = getItem(position);
			TextView teamTextView = (TextView)convertView.findViewById(R.id.row_team_name_textView);
			Log.v(TAG, "TeamListAdapter getView() ["+position+"]: "+item.getTeamName());
			teamTextView.setText(item.getTeamName());
			return convertView;
		}
	}
    private class InsertTeamItemsTask extends AsyncTask<Void,Void,Void> {
    	@Override
    	protected Void doInBackground(Void... nada) {
    		Log.d(TAG, "InsertTeamItemsTask.doInBackground()");
    		try {
    			((TeamListActivity) getActivity()).insertTeamItems(mTeamFetch);
    		} catch (Exception e) {
    			Log.e(TAG, "InsertTeamItemsTask.doInBackground() Exception.", e);
    		}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void nada) {
    		Log.d(TAG, "InsertTeamItemsTask.onPostExecute()");
    		cancel(true);
    	}
    }
	private class QueryTeamItemsTask extends AsyncTask<ConferenceItem,Void,ArrayList<TeamItem>> {
		@Override
		protected ArrayList<TeamItem> doInBackground(ConferenceItem... nada) {
        	Log.d(TAG, "QueryTeamItemsTask.doInBackground()");
    		ArrayList<TeamItem> items = null;
    		try {
    			items = ((TeamListActivity) getActivity()).queryTeamByConferenceItem(mConferenceItem);
    		} catch (Exception e) {
    			Log.e(TAG, "QueryTeamItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<TeamItem> items) {
        	Log.d(TAG, "QueryTeamItemsTask.onPostExecute() queried=" + items.size());
    		int size;
    		if (items == null || items.size() == 0) {
    			size = 0;
    		} else {
    			size = items.size();
        		mTeamQuery = items;
    		}
       		setupTeam(QUERY, size);
            cancel(true);
    		new FetchTeamItemsTask().execute(mConferenceItem);
		}
	}
}