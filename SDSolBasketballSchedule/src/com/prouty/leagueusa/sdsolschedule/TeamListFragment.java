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

public class TeamListFragment extends Fragment{
	private static final String TAG = "TeamListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private ConferenceItem mConferenceItem;
	private ArrayList<TeamItem> mTeamItems;
	private TeamItem mTeamItem;
	
	View view;
	TextView mSeasonTextView;
	TextView mDivisionTextView;
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
		new FetchTeamItemsTask().execute(mConferenceItem);
		
		view = inflater.inflate(R.layout.fragment_team_list, container,false);
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
				Log.d(TAG, "onCreatView()setOnItemClickListener.onItemClick() Team ["+position+"]= "+listItemText);
				returnTeam(position);
			}
		});
		return view;
	}

	private void setupTeam(int choice) {
		if (getActivity() == null || mListView == null) {
			return;
		}
    	Log.d(TAG, "setupTeam("+choice+") season: "+mConferenceItem.getConferenceId()+"-"+mConferenceItem.getConferenceName());
    	if (choice == GET) {
    		if (mTeamItems != null && mTeamItems.size()>0) {
    			Log.w(TAG, "setupTeam() replace with insert/save to DB"); //TODO insert DB
    			//new InsertTeamItemsTask().execute(); //save fetched to DB
    		}
    		else { // got none. If in DB - populate from there
    			Log.e(TAG, "setupTeam() replace with query from DB"); //TODO query DB
    			//new QueryTeamItemsTask().execute(mSeasonItem);
    		}
		}
    	if (mTeamItems != null) {
    		TeamListAdapter adapter = new TeamListAdapter(mTeamItems);
			mListView.setAdapter(adapter);
		}
		else {
			mListView.setAdapter(null);
		}
	}
	private void returnTeam(int position) {
    	mTeamItem = mTeamItems.get(position);
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
				+ ", name="         + mTeamItem.getTeamName());

		//Toast.makeText(getActivity().getApplicationContext(),mTeamItem.getTeamName(), Toast.LENGTH_SHORT).show();
		((TeamListActivity) getActivity()).launchGameListActivity(mTeamItem);
	}
	private class FetchTeamItemsTask extends AsyncTask<ConferenceItem,Void,ArrayList<TeamItem>> {
		@Override
		protected ArrayList<TeamItem> doInBackground(ConferenceItem... params) {
        	Log.d(TAG, "FetchTeamTask doInBackground()");
    		ArrayList<TeamItem> items = null;
    		try { // pass context for app dir to cache file
        		items = new TeamListLeagueUSA().fetchItems(mConferenceItem, getActivity().getApplicationContext());
    		} catch (Exception e) {
    			Log.e(TAG, "doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<TeamItem> teamItems) {
			mTeamItems = teamItems;
			setupTeam(GET);
            cancel(true); // done !
        	Log.d(TAG, "FetchTeamItemsTask onPostExecute()");
		}
	}
	private class TeamListAdapter extends ArrayAdapter<TeamItem> {
		public TeamListAdapter(ArrayList<TeamItem> items) {
			super(getActivity(), 0, items);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.team_list_row, parent, false);
			}

			TeamItem item = getItem(position);
			TextView teamTextView = (TextView)convertView.findViewById(R.id.row_team_name_textView);
			Log.v(TAG, "adapter.getView() item.getTeamName(): "+item.getTeamName());
			teamTextView.setText(item.getTeamName());
			return convertView;
		}
	}
}