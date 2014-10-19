package com.prouty.leagueusa.schedule;

import android.util.Log;

public class LeagueItem {
	private static final String TAG = "LeagueItem";
    private String mLeagueId;
    private String mOrgName;
    private String mLeagueURL;

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mLeagueId == null) ? 0 : mLeagueId.hashCode());
		result = prime * result
				+ ((mLeagueURL == null) ? 0 : mLeagueURL.hashCode());
		result = prime * result
				+ ((mOrgName == null) ? 0 : mOrgName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object queryObj) {
		if (this == queryObj) {
			Log.v(TAG, "equals()=true identical objects");
			return true;
		}
		if (queryObj == null) {
			Log.w(TAG, "equals()=false passed (queryObj) object is null");
			return false;
		}
		if (getClass() != queryObj.getClass()) {
			Log.w(TAG, "equals()=false objects of different class");
			return false;
		}
		LeagueItem queryItem= (LeagueItem) queryObj;
		if (mLeagueId == null) {
			if (queryItem.mLeagueId != null) {
				Log.w(TAG, "equals()=false mLeagueId=null, queryItem.mLeagueId !=null");
				return false;
			}
		} else if (!mLeagueId.equals(queryItem.mLeagueId)) {
			Log.w(TAG, "equals()=false mLeagueId != null ("+mLeagueId+
					"), different from queryItem.mLeagueId ("+queryItem.mLeagueId+")");
			return false;
		}
		
		if (mLeagueURL == null) {
			if (queryItem.mLeagueURL != null) {
				Log.w(TAG, "equals()=false mLeagueURL=null, queryItem.mLeagueURL !=null");
				return false;
			}
		} else if (!mLeagueURL.equals(queryItem.mLeagueURL)) {
			Log.w(TAG, "equals()=false mLeagueURL !=null ("+mLeagueURL+
					"), different from queryItem.mLeagueURL ("+queryItem.mLeagueURL+")");
			return false;
		}
		
		if (mOrgName == null) {
			if (queryItem.mOrgName != null) {
				Log.w(TAG, "equals()=false mOrgName=null, queryItem.mOrgName !=null");
				return false;
			}
		} else if (!mOrgName.equals(queryItem.mOrgName)) {
			Log.w(TAG, "equals()=false mOrgName !=null ("+mOrgName+
					"), different from queryItem.mOrgName ("+queryItem.mOrgName+")");
			return false;
		}
		return true;
	}
	public String getOrgName() {
        return mOrgName;
    }
    public void setOrgName(String orgName) {
        mOrgName = orgName;
    }
    
    public String getLeagueId() {
        return mLeagueId;
    }
    public void setLeagueId(String leagueId) {
        mLeagueId = leagueId;
    }

    public String getLeagueURL() {
        return mLeagueURL;
    }
    public void setLeagueURL(String leagueURL) {
        mLeagueURL = leagueURL;
    }
}
