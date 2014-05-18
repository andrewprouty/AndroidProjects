package com.prouty.leagueusa.schedule;

public class ConferenceItem {
    private String mLeagueId;
    private String mLeagueURL;
    private String mSeasonId;
    private String mSeasonName;
    private String mDivisionId;
    private String mDivisionName;

    private String mConferenceId;
    private String mConferenceName;
    private String mConferenceCount;

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((mConferenceCount == null) ? 0 : mConferenceCount.hashCode());
		result = prime * result
				+ ((mConferenceId == null) ? 0 : mConferenceId.hashCode());
		result = prime * result
				+ ((mConferenceName == null) ? 0 : mConferenceName.hashCode());
		result = prime * result
				+ ((mDivisionId == null) ? 0 : mDivisionId.hashCode());
		result = prime * result
				+ ((mDivisionName == null) ? 0 : mDivisionName.hashCode());
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
		ConferenceItem other = (ConferenceItem) obj;
		if (mConferenceCount == null) {
			if (other.mConferenceCount != null)
				return false;
		} else if (!mConferenceCount.equals(other.mConferenceCount))
			return false;
		if (mConferenceId == null) {
			if (other.mConferenceId != null)
				return false;
		} else if (!mConferenceId.equals(other.mConferenceId))
			return false;
		if (mConferenceName == null) {
			if (other.mConferenceName != null)
				return false;
		} else if (!mConferenceName.equals(other.mConferenceName))
			return false;
		if (mDivisionId == null) {
			if (other.mDivisionId != null)
				return false;
		} else if (!mDivisionId.equals(other.mDivisionId))
			return false;
		if (mDivisionName == null) {
			if (other.mDivisionName != null)
				return false;
		} else if (!mDivisionName.equals(other.mDivisionName))
			return false;
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
    public String getDivisionId() {
        return mDivisionId;
    }
    public void setDivisionId(String divisionId) {
        mDivisionId = divisionId;
    }
    public String getDivisionName() {
        return mDivisionName;
    }
    public void setDivisionName(String divisionName) {
        mDivisionName = divisionName;
    }
    
    public String getConferenceId() {
        return mConferenceId;
    }
    public void setConferenceId(String conferenceId) {
        mConferenceId = conferenceId;
    }
    public String getConferenceName() {
        return mConferenceName;
    }
    public void setConferenceName(String conferenceName) {
        mConferenceName = conferenceName;
    }
    public String getConferenceCount() {
        return mConferenceCount;
    }
    public void setConferenceCount(String conferenceCount) {
        mConferenceCount = conferenceCount;
    }
}
