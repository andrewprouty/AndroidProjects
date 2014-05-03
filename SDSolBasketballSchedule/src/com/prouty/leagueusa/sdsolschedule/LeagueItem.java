package com.prouty.leagueusa.sdsolschedule;

public class LeagueItem {
    private String mLeagueId;
    private String mOrgName;
    private String mLeagueURL;

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
