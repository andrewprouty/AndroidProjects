package com.prouty.leagueusa.sdsolschedule;

public class LeagueItem {
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LeagueItem other = (LeagueItem) obj;
		if (mLeagueId == null) {
			if (other.mLeagueId != null)
				return false;
		} else if (!mLeagueId.equals(other.mLeagueId))
			return false;
		if (mLeagueURL == null) {
			if (other.mLeagueURL != null)
				return false;
		} else if (!mLeagueURL.equals(other.mLeagueURL))
			return false;
		if (mOrgName == null) {
			if (other.mOrgName != null)
				return false;
		} else if (!mOrgName.equals(other.mOrgName))
			return false;
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
