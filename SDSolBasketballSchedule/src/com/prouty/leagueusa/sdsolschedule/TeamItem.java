package com.prouty.leagueusa.sdsolschedule;

public class TeamItem {
    private String mLeagueId;
    private String mLeagueURL;
    private String mSeasonId;
    private String mSeasonName;
    private String mDivisionId;
    private String mDivisionName;
    private String mConferenceId;

    private String mTeamId;
    private String mTeamName;

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
    public void setDivisionName(String divisionName) {
        mDivisionName = divisionName;
    }
    public String getDivisionName() {
        return mDivisionName;
    }
    public String getConferenceId() {
        return mConferenceId;
    }
    public void setConferenceId(String conferenceId) {
        mConferenceId= conferenceId;
    }
    //team
    public String getTeamName() {
        return mTeamName;
    }
    public void setTeamName(String teamName) {
        mTeamName = teamName;
    }
    
    public String getTeamId() {
        return mTeamId;
    }
    public void setTeamId(String teamId) {
        mTeamId = teamId;
    }
}