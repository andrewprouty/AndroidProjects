package com.prouty.leagueusa.sdsolschedule;

public class GameItem {
    private String mLeagueId;
    private String mLeagueURL;
    private String mSeasonId;
    private String mSeasonName;
    private String mDivisionId;
    private String mDivisionName;
    private String mConferenceId;
    private String mConferenceName;
    private String mConferenceCount;
    private String mTeamId;
	private String mTeamName;

    private String mGameId;
    private String mGameDateTime;
    private String mGameHomeTeam;
    private String mGameAwayTeam;
    private String mGameLocation;
    private String mGameStartTBD;
    private String mGameHomeScore;
    private String mGameAwayScore;

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
				+ ((mGameAwayScore == null) ? 0 : mGameAwayScore.hashCode());
		result = prime * result
				+ ((mGameAwayTeam == null) ? 0 : mGameAwayTeam.hashCode());
		result = prime * result
				+ ((mGameStartTBD == null) ? 0 : mGameStartTBD.hashCode());
		result = prime * result
				+ ((mGameDateTime == null) ? 0 : mGameDateTime.hashCode());
		result = prime * result
				+ ((mGameHomeScore == null) ? 0 : mGameHomeScore.hashCode());
		result = prime * result
				+ ((mGameHomeTeam == null) ? 0 : mGameHomeTeam.hashCode());
		result = prime * result + ((mGameId == null) ? 0 : mGameId.hashCode());
		result = prime * result
				+ ((mGameLocation == null) ? 0 : mGameLocation.hashCode());
		result = prime * result
				+ ((mLeagueId == null) ? 0 : mLeagueId.hashCode());
		result = prime * result
				+ ((mLeagueURL == null) ? 0 : mLeagueURL.hashCode());
		result = prime * result
				+ ((mSeasonId == null) ? 0 : mSeasonId.hashCode());
		result = prime * result
				+ ((mSeasonName == null) ? 0 : mSeasonName.hashCode());
		result = prime * result + ((mTeamId == null) ? 0 : mTeamId.hashCode());
		result = prime * result
				+ ((mTeamName == null) ? 0 : mTeamName.hashCode());
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
		GameItem other = (GameItem) obj;
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
		if (mGameAwayScore == null) {
			if (other.mGameAwayScore != null)
				return false;
		} else if (!mGameAwayScore.equals(other.mGameAwayScore))
			return false;
		if (mGameAwayTeam == null) {
			if (other.mGameAwayTeam != null)
				return false;
		} else if (!mGameAwayTeam.equals(other.mGameAwayTeam))
			return false;
		if (mGameStartTBD == null) {
			if (other.mGameStartTBD != null)
				return false;
		} else if (!mGameStartTBD.equals(other.mGameStartTBD))
			return false;
		if (mGameDateTime == null) {
			if (other.mGameDateTime != null)
				return false;
		} else if (!mGameDateTime.equals(other.mGameDateTime))
			return false;
		if (mGameHomeScore == null) {
			if (other.mGameHomeScore != null)
				return false;
		} else if (!mGameHomeScore.equals(other.mGameHomeScore))
			return false;
		if (mGameHomeTeam == null) {
			if (other.mGameHomeTeam != null)
				return false;
		} else if (!mGameHomeTeam.equals(other.mGameHomeTeam))
			return false;
		if (mGameId == null) {
			if (other.mGameId != null)
				return false;
		} else if (!mGameId.equals(other.mGameId))
			return false;
		if (mGameLocation == null) {
			if (other.mGameLocation != null)
				return false;
		} else if (!mGameLocation.equals(other.mGameLocation))
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
		if (mTeamId == null) {
			if (other.mTeamId != null)
				return false;
		} else if (!mTeamId.equals(other.mTeamId))
			return false;
		if (mTeamName == null) {
			if (other.mTeamName != null)
				return false;
		} else if (!mTeamName.equals(other.mTeamName))
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
        mConferenceId= conferenceId;
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

    public String getTeamId() {
        return mTeamId;
    }
    public void setTeamId(String teamId) {
        mTeamId = teamId;
    }
    public String getTeamName() {
        return mTeamName;
    }
    public void setTeamName(String teamName) {
        mTeamName = teamName;
    }
    //game
	public String getGameId() {
        return mGameId;
    }
    public void setGameId(String gameId) {
        mGameId = gameId;
    }
    public String getGameDateTime() {
        return mGameDateTime;
    }
    public void setGameDateTime(String gameDateTime) {
        mGameDateTime = gameDateTime;
    }
    public String getGameHomeTeam() {
        return mGameHomeTeam;
    }
    public void setGameHomeTeam(String gameHomeTeam) {
        mGameHomeTeam = gameHomeTeam;
    }
    public String getGameAwayTeam() {
        return mGameAwayTeam;
    }
    public void setGameAwayTeam(String gameAwayTeam) {
        mGameAwayTeam = gameAwayTeam;
    }
    public String getGameLocation() {
        return mGameLocation;
    }
    public void setGameLocation(String gameLocation) {
        mGameLocation = gameLocation;
    }
    public String getGameStartTBD() {
        return mGameStartTBD;
    }
    public void setGameStartTBD(String gameStartTBD) {
        mGameStartTBD = gameStartTBD;
    }
    public String getGameHomeScore() {
        return mGameHomeScore;
    }
    public void setGameHomeScore(String gameHomeScore) {
        mGameHomeScore = gameHomeScore;
    }
    public String getGameAwayScore() {
        return mGameAwayScore;
    }
    public void setGameAwayScore(String gameAwayScore) {
        mGameAwayScore = gameAwayScore;
    }
}