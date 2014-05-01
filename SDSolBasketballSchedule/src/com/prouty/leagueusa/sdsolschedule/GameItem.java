package com.prouty.leagueusa.sdsolschedule;

public class GameItem {
    private String mGameId;
    private String mGameDate;
    private String mGameTime;
    private String mGameOpponent;
    private String mGameLocation;
    private String mGameScore;

    public String getGameId() {
        return mGameId;
    }
    public void setGameId(String gameId) {
        mGameId = gameId;
    }

    public String getGameDate() {
        return mGameDate;
    }
    public void setGameDate(String gameDate) {
        mGameDate = gameDate;
    }

    public String getGameTime() {
        return mGameTime;
    }
    public void setGameTime(String gameTime) {
        mGameTime = gameTime;
    }

    public String getGameOpponent() {
        return mGameOpponent;
    }
    public void setGameOpponent(String gameOpponent) {
        mGameOpponent = gameOpponent;
    }

    public String getGameLocation() {
        return mGameLocation;
    }
    public void setGameLocation(String gameLocation) {
        mGameOpponent = gameLocation;
    }

    public String getGameScore() {
        return mGameScore;
    }
    public void setGameScore(String gameScore) {
        mGameScore = gameScore;
    }
}