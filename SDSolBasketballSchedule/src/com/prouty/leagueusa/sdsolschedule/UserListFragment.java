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

public class UserListFragment extends Fragment{
	private static final String TAG = "UserListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private ArrayList<UserItem> mUserItems;
	private UserItem mUserItem;
	FetchUserItemsTask mFetchUserItemsTask = new FetchUserItemsTask();
	
	View view;
	TextView mUserTextView;
	ListView mListView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // survive across Activity re-create (i.e. orientation)
        mFetchUserItemsTask.execute();
    }
	
    @Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{       
		Log.d(TAG, "onCreateView()");
		view = inflater.inflate(R.layout.fragment_user_list, container,false);
        mUserTextView = (TextView)view.findViewById(R.id.user_list_textView);
		mListView = (ListView)view.findViewById(R.id.user_list_view);
		mListView.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textViewItem = ((TextView) view.findViewById(R.id.row_user_name_textView));
				String listItemText = textViewItem.getText().toString();
				Log.d(TAG, "().onItemClick() User ["+position+"]= "+listItemText);
				returnSelection(position);
			}
		});
		return view;
	}
	
	private void setupAdapter(int choice) {
    	if (getActivity() == null || mListView == null) {
    		return;
    	}
    	Log.d(TAG, "setupAdapter("+choice+")");

    	if (choice == GET) {
    		if (mUserItems != null && mUserItems.size()>0) {
    			// Async to save the fetched list to DB
    			new InsertUserItemsTask().execute(); // save fetched to DB
    		}
    		else {
    			// none. If in DB can populate from there
    			new QueryUserItemsTask().execute();
    		}
    	}
		if (mUserItems != null) {
			UserListAdapter adapter = new UserListAdapter(mUserItems);
			mListView.setAdapter(adapter);
		}
		else {
			mListView.setAdapter(null);
		}
    }

    private void returnSelection(int position) {
		mFetchUserItemsTask.cancel(true);
    	mUserItem = mUserItems.get(position);
    	Log.i(TAG, "returnSelection()=["+position+"] "+mUserItem.getUserId()+": "+mUserItem.getUserName());
		mUserTextView.setText(mUserItem.getUserName());
		((MainUserListActivity) getActivity()).launchPhotoListActivity(mUserItem);
    }
    private class FetchUserItemsTask extends AsyncTask<Void,Void,ArrayList<UserItem>> {
        @Override
        protected ArrayList<UserItem> doInBackground(Void... params) {
        	Log.d(TAG, "FetchUserItemsTask.doInBackground()");
    		ArrayList<UserItem> items = null;
    		try {
    			// pass context for app dir to cache file
        		items = new UserListBismarck().fetchItems(getActivity().getApplicationContext());
    		} catch (Exception e) {
    			Log.e(TAG, "FetchUserItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
        }
        @Override
        protected void onPostExecute(ArrayList<UserItem> userItems) {
        	try {
        		mUserItems = userItems;
        		Log.d(TAG, "FetchUserItemsTask.onPostExecute()");
        		setupAdapter(GET); // show listing
        		cancel(true); // done !
        	} catch (Exception e) {
        		Log.e(TAG, "FetchUserItemsTask.doInBackground() Exception.", e);
        	}
        }
    }
    private class UserListAdapter extends ArrayAdapter<UserItem> {
        public UserListAdapter(ArrayList<UserItem> userItems) {
            super(getActivity(), 0, userItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.user_list_row, parent, false);
            }
            
            UserItem item = getItem(position);
            TextView userTextView = (TextView)convertView.findViewById(R.id.row_user_name_textView);
			Log.v(TAG, "adapter.getView() item.getUserName(): "+item.getUserName());
            userTextView.setText(item.getUserName());
            
            return convertView;
        }
    }
    private class InsertUserItemsTask extends AsyncTask<Void,Void,Void> {
    	//<x,y,z> params: 1-doInBackground(x); 2-onProgressUpdate(y); 3-onPostExecute(z) 
    	@Override
    	protected Void doInBackground(Void... nada) {
    		Log.d(TAG, "InsertUserItemsTask.doInBackground()");
    		try {
        		 ((MainUserListActivity) getActivity()).insertUserItems(mUserItems);
    		} catch (Exception e) {
    			Log.e(TAG, "InsertUserItemsTask.doInBackground() Exception.", e);
    		}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void nada) {
    		Log.d(TAG, "InsertUserItemsTask.onPostExecute()");
    		cancel(true); // done !
    	}
    }
	private class QueryUserItemsTask extends AsyncTask<Void,Void,ArrayList<UserItem>> {
		@Override
		protected ArrayList<UserItem> doInBackground(Void... nada) {
        	Log.d(TAG, "QueryUserItemsTask.doInBackground()");
    		ArrayList<UserItem> items = null;
    		try {
    			items = ((MainUserListActivity) getActivity()).queryUserItems();
    		} catch (Exception e) {
    			Log.e(TAG, "QueryUserItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<UserItem> userItems) {
			mUserItems = userItems;
			setupAdapter(QUERY);
            cancel(true); // done !
        	Log.d(TAG, "QueryUserItemsTask.onPostExecute()");
		}
	}
}