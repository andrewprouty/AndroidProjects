package com.prouty.leagueusa.sdsolschedule;

public class FavoriteItem {
	private String mFavoriteName;
	private String mFavoriteURL;
    private String mLeagueId;
    private String mSeasonId;
    private String mDivisionId;
    private String mConferenceId;
    private String mTeamId;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mConferenceId == null) ? 0 : mConferenceId.hashCode());
		result = prime * result
				+ ((mDivisionId == null) ? 0 : mDivisionId.hashCode());
		result = prime * result
				+ ((mFavoriteName == null) ? 0 : mFavoriteName.hashCode());
		result = prime * result
				+ ((mFavoriteURL == null) ? 0 : mFavoriteURL.hashCode());
		result = prime * result
				+ ((mLeagueId == null) ? 0 : mLeagueId.hashCode());
		result = prime * result
				+ ((mSeasonId == null) ? 0 : mSeasonId.hashCode());
		result = prime * result + ((mTeamId == null) ? 0 : mTeamId.hashCode());
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
		FavoriteItem other = (FavoriteItem) obj;
		if (mConferenceId == null) {
			if (other.mConferenceId != null)
				return false;
		} else if (!mConferenceId.equals(other.mConferenceId))
			return false;
		if (mDivisionId == null) {
			if (other.mDivisionId != null)
				return false;
		} else if (!mDivisionId.equals(other.mDivisionId))
			return false;
		if (mFavoriteName == null) {
			if (other.mFavoriteName != null)
				return false;
		} else if (!mFavoriteName.equals(other.mFavoriteName))
			return false;
		if (mFavoriteURL == null) {
			if (other.mFavoriteURL != null)
				return false;
		} else if (!mFavoriteURL.equals(other.mFavoriteURL))
			return false;
		if (mLeagueId == null) {
			if (other.mLeagueId != null)
				return false;
		} else if (!mLeagueId.equals(other.mLeagueId))
			return false;
		if (mSeasonId == null) {
			if (other.mSeasonId != null)
				return false;
		} else if (!mSeasonId.equals(other.mSeasonId))
			return false;
		if (mTeamId == null) {
			if (other.mTeamId != null)
				return false;
		} else if (!mTeamId.equals(other.mTeamId))
			return false;
		return true;
	}
	public String getFavoriteName() {
		return mFavoriteName;
	}
	public void setFavoriteName(String favoriteName) {
		mFavoriteName = favoriteName;
	}
	public String getFavoriteURL() {
		return mFavoriteURL;
	}
	public void setFavoriteURL(String favoriteURL) {
		mFavoriteURL = favoriteURL;
	}
	public String getLeagueId() {
		return mLeagueId;
	}
	public void setLeagueId(String leagueId) {
		mLeagueId = leagueId;
	}
	public String getSeasonId() {
		return mSeasonId;
	}
	public void setSeasonId(String seasonId) {
		mSeasonId = seasonId;
	}
	public String getDivisionId() {
		return mDivisionId;
	}
	public void setDivisionId(String divisionId) {
		mDivisionId = divisionId;
	}
	public String getConferenceId() {
		return mConferenceId;
	}
	public void setConferenceId(String conferenceId) {
		mConferenceId = conferenceId;
	}
	public String getTeamId() {
		return mTeamId;
	}
	public void setTeamId(String teamId) {
		mTeamId = teamId;
	}
}