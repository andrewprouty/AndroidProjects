package com.prouty.leagueusa.schedule;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GameListFragment extends Fragment{
	private static final String TAG = "GameListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private TeamItem mTeamItem;
	private ArrayList<GameItem> mGameFetch;
	private ArrayList<GameItem> mGameQuery;
	private ArrayList<GameItem> mGameDisplay;
	GameListAdapter adapter;
	
	private Menu mMenu; //To add the important button 
	private ArrayList<FavoriteItem> mFavoriteItems;
	private FavoriteItem mFavoriteItem;
	private TeamItem mFavoriteTeam;
	private boolean mFavorite;

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
		setHasOptionsMenu(true);
		if (mGameDisplay != null) {
			mGameDisplay.clear(); // reset in case of orientation switch
		}
		new QueryGameItemsTask().execute(mTeamItem);

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

	//mMenu referenced used to access the starred icon 
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "onCreateOptionsMenu()");
		inflater.inflate(R.menu.activity_important_actions, menu);
	    mMenu=menu;
	    super.onCreateOptionsMenu(menu,inflater);
	}
	//Gets called every time the user presses the menu button, use for dynamic menus
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
		FavoriteListUtil util = new FavoriteListUtil();
		FavoriteItem favItem = new FavoriteItem();
		mFavorite=false;
		mFavoriteItems=util.getFavoriteList(getActivity().getApplicationContext());
		Log.d(TAG, "onPrepareOptionsMenu() favorite count:"+mFavoriteItems.size());
		menu.removeGroup(1);
		for (int i=0; i<mFavoriteItems.size(); i++) {
			favItem=mFavoriteItems.get(i);
			Log.d(TAG, "onPrepareOptionsMenu() ["+i+"] "+"fav="+favItem.getFavoriteName()+","+favItem.getFavoriteURL());
	        menu.add(1,i,i, favItem.getFavoriteName());
	        if (favItem.getLeagueId().equals(mTeamItem.getLeagueId()) &&
	    	    favItem.getSeasonId().equals(mTeamItem.getSeasonId()) &&
	    	    favItem.getDivisionId().equals(mTeamItem.getDivisionId()) &&
   	        	favItem.getConferenceId().equals(mTeamItem.getConferenceId()) &&
   	        	favItem.getTeamId().equals(mTeamItem.getTeamId())) {
	        	mFavorite=true;
				Log.d(TAG, "onPrepareOptionsMenu() Found the favorite");
	        }
		}
		MenuItem starred = mMenu.findItem(R.id.action_choose_important);
		if (mFavorite) {
			starred.setIcon(R.drawable.ic_action_important);
		}
		else {
			starred.setIcon(R.drawable.ic_action_not_important);
			Log.v(TAG, "onPrepareOptionsMenu() This team was not in the favorite list");
		}
        super.onPrepareOptionsMenu(menu);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
	    switch (menu.getItemId()) {
	        case R.id.action_refresh:
	    		Log.d(TAG, "onOptionsItemSelected() calling refresh");
	            return true;
	        case R.id.action_choose_important:
	    		Log.d(TAG, "onOptionsItemSelected() favorite set/unset "+
	    				"team ID="+mTeamItem.getTeamId() +
	    				" name="+mTeamItem.getTeamName() +
	    				" url="+mTeamItem.getTeamURL());
    			FavoriteListUtil util = new FavoriteListUtil();
	    		if (mFavorite) {
		    		Log.d(TAG, "onOptionsItemSelected() removing Team ID="+mTeamItem.getTeamId() +
		    				" Name="+mTeamItem.getTeamName());
	    			util.removeFavoriteItem(getActivity().getApplicationContext(), mTeamItem.getTeamURL());
	    		}
	    		else {
		    		Log.d(TAG, "onOptionsItemSelected() adding Team ID="+mTeamItem.getTeamId() +
		    				" Name="+mTeamItem.getTeamName());
		    		mFavoriteItem = util.addFavoriteItem(getActivity().getApplicationContext(), mTeamItem);
	    		}
    			getActivity().supportInvalidateOptionsMenu(); //triggers onPrepareOptions which resets menu
	            return true;
	        default:
	    		Log.d(TAG, "onOptionsItemSelected() chose menu ID: "+menu.getItemId());
	    		if (mFavoriteItems != null && mFavoriteItems.size() > 0) {
	    			mFavoriteItem = mFavoriteItems.get(menu.getItemId());
		    		Log.d(TAG, "onOptionsItemSelected() chose FavItem Key= "
		    				+mFavoriteItem.getFavoriteURL()+" Value="+mFavoriteItem.getFavoriteName());
		    		util = new FavoriteListUtil();
		    		mFavoriteTeam=util.queryTeamByTeamURL(getActivity().getApplicationContext(),mFavoriteItem.getFavoriteURL());
		    		if (mFavoriteTeam != null ) {
			    		Log.d(TAG, "onOptionsItemSelected() this FavTeam: " + mFavoriteTeam.getTeamName());
		    			util.launchGameListActivity(getActivity().getApplicationContext(), mFavoriteTeam);
		    		}
		    		else {
						Toast.makeText(getActivity().getApplicationContext(), R.string.broken_must_navigate, Toast.LENGTH_SHORT).show();
		    		}
	    		}
	            return super.onOptionsItemSelected(menu);
	    }
	}

	private void setupGame(int choice, int choiceSize) {
		if (getActivity() == null || mListView == null) {
			return;
		}
    	Log.d(TAG, "setupGame("+choice+") team: "+mTeamItem.getTeamId()+"-"+mTeamItem.getTeamName());
		if (mGameDisplay == null || mGameDisplay.size() == 0) {
			if (choiceSize > 0) {
				if (choice == GET) {
					Log.d(TAG, "setupGame("+choice+") (2nd/GET) has the only results so insert them");
					mGameDisplay = mGameFetch;
					new InsertGameItemsTask().execute();
				}
				else {
					mGameDisplay = mGameQuery;
				}
				adapter = new GameListAdapter(mGameDisplay, choice);
				mListView.setAdapter(adapter);
			}
			else {
				if (choice == QUERY) {
					Log.d(TAG, "setupGame("+choice+") (1st/QUERY) has no results");
				}
				else {
					Log.w(TAG, "setupGame("+choice+") (2nd/GET) also has no results");
					String msg = getActivity().getApplicationContext().getResources().getString(R.string.name_game);
					msg = getActivity().getApplicationContext().getResources().getString(R.string.no_information_available, msg);
					Toast.makeText(getActivity().getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
				}
			}			
		}
		else { 
			if (choiceSize == 0) {
				Log.w(TAG, "setupGame("+choice+") (1st/QUERY) had results (2nd/GET) had none. Offline?");
			}
			else {
		    	if (!mGameFetch.equals(mGameQuery)) {
					Log.w(TAG, "setupGame("+choice+") Fetched != Queried. Sizes info only: "
							+ mGameFetch.size() + " " + mGameQuery.size());
					if (choice == GET) {
						Log.w(TAG, "setupGame("+choice+") Fetched!=Queried, GET to Insert");
						mGameDisplay.clear();
						mGameDisplay.addAll(mGameFetch);
						adapter.notifyDataSetChanged();
						Toast.makeText(getActivity().getApplicationContext(), R.string.new_information_available, Toast.LENGTH_SHORT).show();
						new InsertGameItemsTask().execute();
					}
		    	}
		    	else {
					Log.d(TAG, "setupGame("+choice+") Fetched=Queried");
				}
			}
		}
	}
	private class FetchGameItemsTask extends AsyncTask<TeamItem,Void,ArrayList<GameItem>> {
		@Override
		protected ArrayList<GameItem> doInBackground(TeamItem... params) {
        	Log.d(TAG, "FetchGameItemsTask doInBackground()");
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
        	try {
        		Log.d(TAG, "FetchGameItemsTask.onPostExecute() fetched=" + items.size());
        		int size;
        		if (items == null || items.size() == 0) {
        			size = 0;
        		} else {
        			size = items.size();
            		mGameFetch = items;
        		}
           		setupGame(GET, size);
        		cancel(true);
        	} catch (Exception e) {
        		Log.e(TAG, "FetchGameItemsTask.doInBackground() Exception.", e);
        	}
		}
	}
	private class GameListAdapter extends ArrayAdapter<GameItem> {
		public GameListAdapter(ArrayList<GameItem> items, int choice) {
			super(getActivity(), 0, items);
			Log.d(TAG, "GameListAdapter Constructor ("+choice+")");
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.game_list_row, parent, false);
			}

			GameItem item = getItem(position);
			Log.v(TAG, "GameListAdapter getView() ["+position+"]");

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
			TextView awayScoreTextView = (TextView)convertView.findViewById(R.id.row_game_away_score_textView);
			Log.v(TAG, "adapter GateStartTBD(): "+item.getGameStartTBD()+" len:"+item.getGameStartTBD().length());
			Log.v(TAG, "adapter GameHomeScore(): "+item.getGameHomeScore()+" len:"+item.getGameHomeScore().length());
			Log.v(TAG, "adapter GameAwayScore(): "+item.getGameAwayScore()+" len:"+item.getGameAwayScore().length());
			if(item.getGameHomeScore().length() == 0 || item.getGameAwayScore().length() == 0) {
				String starttbd = item.getGameStartTBD();
				if (starttbd.equals("1")) {
					Log.v(TAG, "adapter starttbd = Normal ="+starttbd);
					homeScoreTextView.setText(R.string.game_home_score_hint);
					awayScoreTextView.setText(R.string.game_away_score_hint);
				}
				else if (starttbd.equals("2")) {
					Log.v(TAG, "adapter starttbd = To Be Determined ="+starttbd);
					homeScoreTextView.setText(R.string.game_2tbd_1_of_2);
					awayScoreTextView.setText(R.string.game_2tbd_2_of_2);
				}
				else if (starttbd.equals("3")) {
					Log.v(TAG, "adapter starttbd = Rained Out ="+starttbd);
					homeScoreTextView.setText(R.string.game_3rain_1_of_2);
					awayScoreTextView.setText(R.string.game_3rain_2_of_2);
				}
				else if (starttbd.equals("4")) {
					Log.v(TAG, "adapter starttbd = Cancelled ="+starttbd);
					homeScoreTextView.setText(R.string.game_4cancel_1_of_2);
					awayScoreTextView.setText(R.string.game_4cancel_2_of_2);
				}
				else if (starttbd.equals("5")) {
					Log.v(TAG, "adapter starttbd = Make Up ="+starttbd);
					homeScoreTextView.setText(R.string.game_5makeup_1_of_2);
					awayScoreTextView.setText(R.string.game_5makeup_2_of_2);
				}
				else { //? ERROR... no other known codes
					Log.e(TAG, "adapter starttbd = ? ="+starttbd);
					homeScoreTextView.setText(R.string.game_home_score_hint);
					awayScoreTextView.setText(R.string.game_away_score_hint);
				}
			}
			else {
				homeScoreTextView.setText(item.getGameHomeScore());
				awayScoreTextView.setText(item.getGameAwayScore());
			}
			TextView locationTextView = (TextView)convertView.findViewById(R.id.row_game_location_textView);
			Log.v(TAG, "adapter GameLocation(): "+item.getGameLocation());
			locationTextView.setText(item.getGameLocation());
			return convertView;
		}
	}
    private class InsertGameItemsTask extends AsyncTask<Void,Void,Void> {
    	@Override
    	protected Void doInBackground(Void... nada) {
    		Log.d(TAG, "InsertGameItemsTask.doInBackground()");
    		try {
    			((GameListActivity) getActivity()).insertGameItems(mGameFetch);
    		} catch (Exception e) {
    			Log.e(TAG, "InsertGameItemsTask.doInBackground() Exception.", e);
    		}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void nada) {
    		Log.d(TAG, "InsertGameItemsTask.onPostExecute()");
    		cancel(true); // done !
    	}
    }
	private class QueryGameItemsTask extends AsyncTask<TeamItem,Void,ArrayList<GameItem>> {
		@Override
		protected ArrayList<GameItem> doInBackground(TeamItem... nada) {
        	Log.d(TAG, "QueryGameItemsTask.doInBackground()");
    		ArrayList<GameItem> items = null;
    		try {
    			items = ((GameListActivity) getActivity()).queryGamesByTeamItem(mTeamItem);
    		} catch (Exception e) {
    			Log.e(TAG, "QueryGameItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<GameItem> items) {
        	Log.d(TAG, "QueryGameItemsTask.onPostExecute() queried=" + items.size());
    		int size;
    		if (items == null || items.size() == 0) {
    			size = 0;
    		} else {
    			size = items.size();
        		mGameQuery = items;
    		}
       		setupGame(QUERY, size);
            cancel(true);
    		new FetchGameItemsTask().execute(mTeamItem);
		}
	}
}