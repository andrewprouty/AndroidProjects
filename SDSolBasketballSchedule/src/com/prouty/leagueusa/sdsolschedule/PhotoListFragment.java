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

public class PhotoListFragment extends Fragment{
	private static final String TAG = "PhotoListFragment";
	private static final int GET = 0;
	private static final int QUERY = 1;
	private ArrayList<PhotoItem> mPhotoItems;
	private UserItem mUserItem;
	private PhotoItem mPhotoItem;
    
	View view;
	TextView mUserTextView;
	TextView mPhotoTextView;
	ListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true); // survive across Activity re-create (i.e. orientation)
		mUserItem=((PhotoListActivity) getActivity()).getUserItem();
		new FetchPhotoItemsTask().execute(mUserItem);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{       
		view = inflater.inflate(R.layout.fragment_photo_list, container,false);
		mUserTextView = (TextView)view.findViewById(R.id.photo_list_user_name);
		mPhotoTextView = (TextView)view.findViewById(R.id.photo_list_photo_name);
		mListView = (ListView)view.findViewById(R.id.photo_list_view);

		mUserTextView.setText(mUserItem.getUserName());

		mListView.setOnItemClickListener(new OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView textViewItem = ((TextView) view.findViewById(R.id.row_photo_name_textView));
				String listItemText = textViewItem.getText().toString();
				Log.d(TAG, "onCreatView()setOnItemClickListener.onItemClick() Photo ["+position+"]= "+listItemText);
				returnSelection(position);
			}
		});
		return view;
	}

	private void setupAdapter(int choice) {
		if (getActivity() == null || mListView == null) {
			return;
		}
    	Log.d(TAG, "setupAdapter("+choice+"): "+mUserItem.getUserId()+"-"+mUserItem.getUserName()+";");

    	if (choice == GET) {
    		if (mPhotoItems != null && mPhotoItems.size()>0) {
    			new InsertPhotoItemsTask().execute(); //save fetched to DB
    		}
    		else { // got none. If in DB - populate from there
    			new QueryPhotoItemsTask().execute(mUserItem);
    		}
		}
    	
    	if (mPhotoItems != null) {
			PhotoListAdapter adapter = new PhotoListAdapter(mPhotoItems);
			mListView.setAdapter(adapter);
			((PhotoListActivity) getActivity()).setPhotoItems(mPhotoItems);
		}
		else {
			mListView.setAdapter(null);
		}
	}

	private void returnSelection(int position) {
		mPhotoItem = mPhotoItems.get(position);
		Log.i(TAG, "returnSelection()=["+position+"] "+mPhotoItem.getPhotoId()+": "+mPhotoItem.getPhotoName());
		mPhotoTextView.setText(mPhotoItem.getPhotoName());
		((PhotoListActivity) getActivity()).launchPhotoDisplayActivity(mPhotoItem, position);
	}
	private class FetchPhotoItemsTask extends AsyncTask<UserItem,Void,ArrayList<PhotoItem>> {
		@Override
		protected ArrayList<PhotoItem> doInBackground(UserItem... params) {
        	Log.d(TAG, "FetchPhotoTask doInBackground()");
    		ArrayList<PhotoItem> items = null;
    		try { // pass context for app dir to cache file
        		items = new PhotoListBismarck().fetchItems(mUserItem, getActivity().getApplicationContext());
    		} catch (Exception e) {
    			Log.e(TAG, "doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<PhotoItem> photoItems) {
			mPhotoItems = photoItems;
			setupAdapter(GET);
            cancel(true); // done !
        	Log.d(TAG, "FetchPhotoTask onPostExecute()");
		}
	}
	private class PhotoListAdapter extends ArrayAdapter<PhotoItem> {
		public PhotoListAdapter(ArrayList<PhotoItem> photoItems) {
			super(getActivity(), 0, photoItems);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.photo_list_row, parent, false);
			}

			PhotoItem item = getItem(position);
			TextView photoTextView = (TextView)convertView.findViewById(R.id.row_photo_name_textView);
			Log.v(TAG, "adapter.getView() item.getUserName(): "+item.getUserName());
			photoTextView.setText(item.getPhotoName());

			return convertView;
		}
	}
    private class InsertPhotoItemsTask extends AsyncTask<Void,Void,Void> {
    	//<x,y,z> params: 1-doInBackground(x); 2-onProgressUpdate(y); 3-onPostExecute(z) 
    	@Override
    	protected Void doInBackground(Void... nada) {
    		Log.d(TAG, "InsertPhotoItemsTask.doInBackground()");
    		try {
    			((PhotoListActivity) getActivity()).insertPhotoItems(mPhotoItems, mUserItem);
    		} catch (Exception e) {
    			Log.e(TAG, "InsertPhotoItemsTask.doInBackground() Exception.", e);
    		}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void nada) {
    		Log.d(TAG, "InsertPhotoItemsTask.onPostExecute()");
    		cancel(true); // done !
    	}
    }
	private class QueryPhotoItemsTask extends AsyncTask<UserItem,Void,ArrayList<PhotoItem>> {
		@Override
		protected ArrayList<PhotoItem> doInBackground(UserItem... params) {
        	Log.d(TAG, "QueryPhotoItemsTask.doInBackground()");
    		ArrayList<PhotoItem> items = null;
    		try {
    			items = ((PhotoListActivity) getActivity()).queryPhotoItemsforUserId(mUserItem);
    		} catch (Exception e) {
    			Log.e(TAG, "QueryPhotoItemsTask.doInBackground() Exception.", e);
    		}
        	return items;
		}
		@Override
		protected void onPostExecute(ArrayList<PhotoItem> photoItems) {
			mPhotoItems = photoItems;
			setupAdapter(QUERY);
            cancel(true); // done !
        	Log.d(TAG, "QueryPhotoItemsTask.onPostExecute()");
		}
	}
}