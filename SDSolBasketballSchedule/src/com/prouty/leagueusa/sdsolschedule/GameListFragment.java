package com.prouty.leagueusa.sdsolschedule;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GameListFragment extends Fragment{
	private static final String TAG = "GameListFragment";
	private static final int GET = 0;
	private TeamItem mTeamItem;
	private ArrayList<GameItem> mGameItems;
	
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
		mTeamItem=((GameListActivity) getActivity()).getTeamItem();
		new FetchGameItemsTask().execute(mTeamItem);

		if (mTeamItem.getConferenceCount().equals("one")) {
			view = inflater.inflate(R.layout.fragment_game_list_no_conference, container,false);
		}
		else {
			view = inflater.inflate(R.layout.fragment_game_list_show_conference, container,false);
			mConferenceTextView = (TextView)view.findViewById(R.id.game_list_conference_name); 
			mConferenceTextView.setText(mTeamItem.getConferenceName());
		}
		
		mSeasonTextView = (TextView)view.findViewById(R.id.game_list_season_name);
		mDivisionTextView = (TextView)view.findViewById(R.id.game_list_division_name); 
		mTeamTextView = (TextView)view.findViewById(R.id.game_list_team_name);
		mListView = (ListView)view.findViewById(R.id.game_list_view);

		mSeasonTextView.setText(mTeamItem.getSeasonName());
		mDivisionTextView.setText(mTeamItem.getDivisionName());
		mTeamTextView.setText(mTeamItem.getTeamName());
		return view;
	}

	private void setupGame(int choice) {
		if (getActivity() == null || mListView == null) {
			return;
		}
    	Log.d(TAG, "setupGame("+choice+") team: "+mTeamItem.getTeamId()+"-"+mTeamItem.getTeamName());
    	if (choice == GET) {
    		if (mGameItems != null && mGameItems.size()>0) {
    			Log.w(TAG, "setupGame() replace with insert/save to DB"); //TODO Game insert DB
    			//new InsertGameItemsTask().execute(); //save fetched to DB
    		}
    		else { // got none. If in DB - populate from there
    			Log.e(TAG, "setupGame() replace with query from DB"); //TODO Game query DB
    			//new QueryGameItemsTask().execute(mSeasonItem);
    		}
		}
    	if (mGameItems != null) {
    		GameListAdapter adapter = new GameListAdapter(mGameItems);
			mListView.setAdapter(adapter);
		}
		else {
			mListView.setAdapter(null);
		}
	}
	private class FetchGameItemsTask extends AsyncTask<TeamItem,Void,ArrayList<GameItem>> {
		@Override
		protected ArrayList<GameItem> doInBackground(TeamItem... params) {
        	Log.d(TAG, "FetchTeamTask doInBackground()");
    		ArrayList<GameItem> items = null;
    		try { // pass context for app dir to cache file
        		items = new GameListLeagueUSA().fetchItems(mTeamItem, getActivity().getApplicationContext());
    		} catch (Exception e) {
    			Log.e(TAG, "doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<GameItem> items) {
			mGameItems = items;
			setupGame(GET);
            cancel(true); // done !
        	Log.d(TAG, "FetchTeamItemsTask onPostExecute()");
		}
	}
	private class GameListAdapter extends ArrayAdapter<GameItem> {
		public GameListAdapter(ArrayList<GameItem> items) {
			super(getActivity(), 0, items);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.game_list_row, parent, false);
			}

			GameItem item = getItem(position);
			Log.d(TAG, "GameListAdapter ["+position+"]");

			TextView dateTimeTextView = (TextView)convertView.findViewById(R.id.row_game_dateTime_textView);
			Log.v(TAG, "adapter GameDateTime(): "+item.getGameDateTime());
			dateTimeTextView.setText(item.getGameDateTime());
			dateTimeTextView.setTypeface(null, Typeface.BOLD);

			TextView homeTeamTextView = (TextView)convertView.findViewById(R.id.row_game_home_team_textView);
			Log.v(TAG, "adapter Home: "+item.getGameHomeTeam());
			homeTeamTextView.setText(item.getGameHomeTeam());

			TextView awayTeamTextView = (TextView)convertView.findViewById(R.id.row_game_away_team_textView);
			Log.v(TAG, "adapter Away: "+item.getGameAwayTeam());
			awayTeamTextView.setText(item.getGameAwayTeam());

			TextView homeScoreTextView = (TextView)convertView.findViewById(R.id.row_game_home_score_textView);
			Log.v(TAG, "adapter GameHomeScore(): "+item.getGameHomeScore());
			homeScoreTextView.setText(item.getGameHomeScore());

			TextView awayScoreTextView = (TextView)convertView.findViewById(R.id.row_game_away_score_textView);
			Log.v(TAG, "adapter GameAwayScore(): "+item.getGameAwayScore());
			awayScoreTextView.setText(item.getGameAwayScore());

			TextView locationTextView = (TextView)convertView.findViewById(R.id.row_game_location_textView);
			Log.v(TAG, "adapter GameLocation(): "+item.getGameLocation());
			locationTextView.setText(item.getGameLocation());
			return convertView;
		}
	}
}