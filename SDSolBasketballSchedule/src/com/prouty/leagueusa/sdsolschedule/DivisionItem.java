package com.prouty.leagueusa.sdsolschedule;

public class DivisionItem {
    private String mLeagueId;
    private String mSeasonId;
    private String mDivisionId;
    private String mDivisionName;

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
    public String getDivisionName() {
        return mDivisionName;
    }
    public void setDivisionName(String divisionName) {
        mDivisionName = divisionName;
    }
}
