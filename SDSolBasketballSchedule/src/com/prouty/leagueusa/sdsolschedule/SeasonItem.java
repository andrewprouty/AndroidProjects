package com.prouty.leagueusa.sdsolschedule;

public class SeasonItem {
    private String mLeagueId;
    private String mLeagueURL;
    private String mSeasonId;
    private String mSeasonName;

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mLeagueId == null) ? 0 : mLeagueId.hashCode());
		result = prime * result
				+ ((mLeagueURL == null) ? 0 : mLeagueURL.hashCode());
		result = prime * result
				+ ((mSeasonId == null) ? 0 : mSeasonId.hashCode());
		result = prime * result
				+ ((mSeasonName == null) ? 0 : mSeasonName.hashCode());
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
		SeasonItem other = (SeasonItem) obj;
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
		if (mSeasonId == null) {
			if (other.mSeasonId != null)
				return false;
		} else if (!mSeasonId.equals(other.mSeasonId))
			return false;
		if (mSeasonName == null) {
			if (other.mSeasonName != null)
				return false;
		} else if (!mSeasonName.equals(other.mSeasonName))
			return false;
		return true;
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

    public String getSeasonId() {
        return mSeasonId;
    }
    public void setSeasonId(String seasonId) {
        mSeasonId = seasonId;
    }

    public String getSeasonName() {
        return mSeasonName;
    }
    public void setSeasonName(String seasonName) {
        mSeasonName = seasonName;
    }

}
