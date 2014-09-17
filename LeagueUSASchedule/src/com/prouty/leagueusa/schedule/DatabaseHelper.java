package com.prouty.leagueusa.schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseHelper";
	private static final String DB_NAME = "leagueusa.sqlite";
	private static final int VERSION = 1;

	private static final String TABLE_LEAGUE = "league";
	private static final String COLUMN_LEAGUE_LEAGUE_ID = "league_id";
	private static final String COLUMN_LEAGUE_ORG_NAME = "org_name";
	private static final String COLUMN_LEAGUE_LEAGUE_URL = "league_url";

	private static final String TABLE_SEASON = "season";
	private static final String COLUMN_SEASON_LEAGUE_ID = "league_id";
	private static final String COLUMN_SEASON_LEAGUE_URL = "league_url";
	private static final String COLUMN_SEASON_SEASON_ID = "season_id";
	private static final String COLUMN_SEASON_SEASON_NAME = "season_name";

	private static final String TABLE_DIVISION = "division";
	private static final String COLUMN_DIVISION_LEAGUE_ID = "league_id";
	private static final String COLUMN_DIVISION_LEAGUE_URL = "league_url";
	private static final String COLUMN_DIVISION_SEASON_ID = "season_id";
	private static final String COLUMN_DIVISION_SEASON_NAME = "season_name";
	private static final String COLUMN_DIVISION_DIVISION_ID = "division_id";
	private static final String COLUMN_DIVISION_DIVISION_NAME = "division_name";

	private static final String TABLE_CONFERENCE = "conference";
	private static final String COLUMN_CONFERENCE_LEAGUE_ID = "league_id";
	private static final String COLUMN_CONFERENCE_LEAGUE_URL = "league_url";
	private static final String COLUMN_CONFERENCE_SEASON_ID = "season_id";
	private static final String COLUMN_CONFERENCE_SEASON_NAME = "season_name";
	private static final String COLUMN_CONFERENCE_DIVISION_ID = "division_id";
	private static final String COLUMN_CONFERENCE_DIVISION_NAME = "division_name";
	private static final String COLUMN_CONFERENCE_CONFERENCE_ID = "conference_id";
	private static final String COLUMN_CONFERENCE_CONFERENCE_NAME = "conference_name";
	private static final String COLUMN_CONFERENCE_CONFERENCE_COUNT = "conference_count";

	private static final String TABLE_TEAM = "team";
	private static final String COLUMN_TEAM_LEAGUE_ID = "league_id";
	private static final String COLUMN_TEAM_LEAGUE_URL = "league_url";
	private static final String COLUMN_TEAM_SEASON_ID = "season_id";
	private static final String COLUMN_TEAM_SEASON_NAME = "season_name";
	private static final String COLUMN_TEAM_DIVISION_ID = "division_id";
	private static final String COLUMN_TEAM_DIVISION_NAME = "division_name";
	private static final String COLUMN_TEAM_CONFERENCE_ID = "conference_id";
	private static final String COLUMN_TEAM_CONFERENCE_NAME = "conference_name";
	private static final String COLUMN_TEAM_CONFERENCE_COUNT = "conference_count";
	private static final String COLUMN_TEAM_TEAM_ID = "team_id";
	private static final String COLUMN_TEAM_TEAM_NAME = "team_name";
	private static final String COLUMN_TEAM_TEAM_URL = "team_url";

	private static final String TABLE_GAME = "game";
	private static final String COLUMN_GAME_LEAGUE_ID = "league_id";
	private static final String COLUMN_GAME_LEAGUE_URL = "league_url";
	private static final String COLUMN_GAME_SEASON_ID = "season_id";
	private static final String COLUMN_GAME_SEASON_NAME = "season_name";
	private static final String COLUMN_GAME_DIVISION_ID = "division_id";
	private static final String COLUMN_GAME_DIVISION_NAME = "division_name";
	private static final String COLUMN_GAME_CONFERENCE_ID = "conference_id";
	private static final String COLUMN_GAME_CONFERENCE_NAME = "conference_name";
	private static final String COLUMN_GAME_CONFERENCE_COUNT = "conference_count";
	private static final String COLUMN_GAME_TEAM_ID = "team_id";
	private static final String COLUMN_GAME_TEAM_NAME = "team_name";
	private static final String COLUMN_GAME_GAME_ID = "game_id"; 
	private static final String COLUMN_GAME_GAME_SORT_ID = "game_sort_id" ;
	private static final String COLUMN_GAME_GAME_DATE_TIME = "game_date_time";
	private static final String COLUMN_GAME_GAME_HOME_TEAM = "game_home_team";
	private static final String COLUMN_GAME_GAME_AWAY_TEAM = "game_away_team";
	private static final String COLUMN_GAME_GAME_LOCATION = "game_location";
	private static final String COLUMN_GAME_GAME_START_TBD = "game_start_tbd";
	private static final String COLUMN_GAME_GAME_HOME_SCORE = "game_home_score";
	private static final String COLUMN_GAME_GAME_AWAY_SCORE = "game_away_score" ;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate()");
		db.execSQL("create table league (" +
				" league_id varchar(10),  org_name varchar(100), league_url varchar(100)," +
				" primary key (league_id, league_url))");
		db.execSQL("create table season (" +
				" league_id varchar(10), league_url varchar(100), season_id varchar(10), season_name varchar(100)," +
				" primary key (league_id, league_url, season_id))");
		db.execSQL("create table division (" +
				" league_id varchar(10), league_url varchar(100)," +
				" season_id varchar(10), season_name varchar(100),"+
				" division_id varchar(10), division_name varchar(100)," +
				" primary key (league_id, league_url, season_id, division_id))");
		db.execSQL("create table conference (" +
				" league_id varchar(10), league_url varchar(100)," +
				" season_id varchar(10), season_name varchar(100),"+
				" division_id varchar(10), division_name varchar(100)," +
				" conference_id varchar(10), conference_name varchar(100), conference_count varchar(10)," + 
				" primary key (league_id, league_url, season_id, division_id, conference_id))");
		db.execSQL("create table team (" +
				" league_id varchar(10), league_url varchar(100)," +
				" season_id varchar(10), season_name varchar(100),"+
				" division_id varchar(10), division_name varchar(100)," +
				" conference_id varchar(10), conference_name varchar(100), conference_count varchar(10)," + 
				" team_id varchar(10), team_name varchar(100), team_url varchar(300)," +
				" primary key (league_id, league_url, season_id, division_id, conference_id, team_id))");
		db.execSQL("create table game (" +
				" league_id varchar(10), league_url varchar(100)," +
				" season_id varchar(10), season_name varchar(100),"+
				" division_id varchar(10), division_name varchar(100)," +
				" conference_id varchar(10), conference_name varchar(100), conference_count varchar(10)," + 
				" team_id varchar(10), team_name varchar(100)," +
				" game_id varchar(10), game_sort_id varchar(10), game_date_time varchar(100)," +
				" game_home_team varchar(100), game_away_team varchar(100)," +
				" game_location varchar(100), game_start_tbd varchar(10)," +
				" game_home_score varchar(10), game_away_score varchar(10)," +
				" primary key (league_id, league_url, season_id, division_id, conference_id, team_id, game_id))");
		Log.d(TAG, "onCreate()ed");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// implement schema changes and data massage here when upgrading
	}

	public long deleteLeague() {
		Log.d(TAG, "deleteLeagues()");
		return getWritableDatabase().delete(TABLE_LEAGUE, null, null);
	}
	public long insertLeague(LeagueItem item) {
		Log.v(TAG, "insertLeague()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_LEAGUE_LEAGUE_ID, item.getLeagueId());
		cv.put(COLUMN_LEAGUE_ORG_NAME, item.getOrgName());
		cv.put(COLUMN_LEAGUE_LEAGUE_URL, item.getLeagueURL());
		return getWritableDatabase().insert(TABLE_LEAGUE, null, cv);
	}
	public LeagueCursor queryLeagues() {
		Log.d(TAG, "queryLeagues()");
		// equivalent to "select * from league order by lower(org_name) asc"
		// sorting by org_name as an alpha, ignore case is how Google docs does it
		Cursor wrapped = getReadableDatabase().query(TABLE_LEAGUE,
				null, null, null, null, null, "LOWER("+ COLUMN_LEAGUE_ORG_NAME + ")");
		return new LeagueCursor(wrapped);
	}

	public long deleteSeasonBySeasonItem(SeasonItem item) {
		Log.d(TAG, "deleteSeasonBySeasonItem()");
		return getWritableDatabase().delete(TABLE_SEASON,
				COLUMN_SEASON_LEAGUE_ID + " = ? AND " +
						COLUMN_SEASON_LEAGUE_URL + " = ?",		// Where column
						new String[] {String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL())}); // values
	}
	public long insertSeason(SeasonItem item) {
		Log.v(TAG, "insertSeason()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SEASON_LEAGUE_ID, item.getLeagueId());
		cv.put(COLUMN_SEASON_LEAGUE_URL, item.getLeagueURL());
		cv.put(COLUMN_SEASON_SEASON_ID, item.getSeasonId());
		cv.put(COLUMN_SEASON_SEASON_NAME, item.getSeasonName());
		return getWritableDatabase().insert(TABLE_SEASON, null, cv);
	}
	public SeasonCursor querySeasonsByLeagueItem(LeagueItem item) {
		Log.d(TAG, "querySeasonsByLeagueItem()");
		Cursor wrapped = getReadableDatabase().query(TABLE_SEASON,
				null, // all columns 
				COLUMN_SEASON_LEAGUE_ID + " = ? AND "+
				COLUMN_SEASON_LEAGUE_URL + " = ?",		// Where column
				new String[]{ String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL())}, // values
				null, // group by
				null, // having
				COLUMN_SEASON_SEASON_NAME + " asc", // order by
				null); // limit of rows
		return new SeasonCursor(wrapped);
	}

	public long deleteDivisionByDivisionItem(DivisionItem item) {
		Log.d(TAG, "deleteDivisionByDivisionItem()");
		return getWritableDatabase().delete(TABLE_DIVISION,
				COLUMN_DIVISION_LEAGUE_ID + " = ? AND " +
						COLUMN_DIVISION_LEAGUE_URL + " = ? AND " +
						COLUMN_DIVISION_SEASON_ID + " = ?",		// Where column
						new String[] {String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL()),
				String.valueOf(item.getSeasonId())}); // values
	}
	public long insertDivision(DivisionItem item) {
		Log.v(TAG, "insertDivision()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_DIVISION_LEAGUE_ID, item.getLeagueId());
		cv.put(COLUMN_DIVISION_LEAGUE_URL, item.getLeagueURL());
		cv.put(COLUMN_DIVISION_SEASON_ID, item.getSeasonId());
		cv.put(COLUMN_DIVISION_SEASON_NAME, item.getSeasonName());
		cv.put(COLUMN_DIVISION_DIVISION_ID, item.getDivisionId());
		cv.put(COLUMN_DIVISION_DIVISION_NAME, item.getDivisionName());
		return getWritableDatabase().insert(TABLE_DIVISION, null, cv);
	}
	public DivisionCursor queryDivisionsBySeasonItem(SeasonItem item) {
		Log.d(TAG, "queryDivisions()");
		Cursor wrapped = getReadableDatabase().query(TABLE_DIVISION,
				null, // all columns 
				COLUMN_DIVISION_LEAGUE_ID + " = ? AND "+
				COLUMN_DIVISION_LEAGUE_URL + " = ? AND "+
				COLUMN_DIVISION_SEASON_ID + " = ?",		// Where column
				new String[]{ String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL()),
				String.valueOf(item.getSeasonId())}, // values
				null, // group by
				null, // having
				COLUMN_DIVISION_DIVISION_NAME + " COLLATE NOCASE asc", // order by
				null); // limit of rows
		return new DivisionCursor(wrapped);
	}
	public long deleteConferenceByConferenceItem(ConferenceItem item) {
		Log.d(TAG, "deleteConferenceByConferenceItem()");
		return getWritableDatabase().delete(TABLE_CONFERENCE,
				COLUMN_CONFERENCE_LEAGUE_ID + " = ? AND " +
						COLUMN_CONFERENCE_LEAGUE_URL + " = ? AND " +
						COLUMN_CONFERENCE_SEASON_ID + " = ? AND " +
						COLUMN_CONFERENCE_DIVISION_ID + " = ?",		// Where column
						new String[] {String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL()),
				String.valueOf(item.getSeasonId()),
				String.valueOf(item.getDivisionId())}); // values
	}
	public long insertConference(ConferenceItem item) {
		Log.v(TAG, "insertConference()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CONFERENCE_LEAGUE_ID, item.getLeagueId());
		cv.put(COLUMN_CONFERENCE_LEAGUE_URL, item.getLeagueURL());
		cv.put(COLUMN_CONFERENCE_SEASON_ID, item.getSeasonId());
		cv.put(COLUMN_CONFERENCE_SEASON_NAME, item.getSeasonName());
		cv.put(COLUMN_CONFERENCE_DIVISION_ID, item.getDivisionId());
		cv.put(COLUMN_CONFERENCE_DIVISION_NAME, item.getDivisionName());
		cv.put(COLUMN_CONFERENCE_CONFERENCE_ID, item.getConferenceId());
		cv.put(COLUMN_CONFERENCE_CONFERENCE_NAME, item.getConferenceName());
		cv.put(COLUMN_CONFERENCE_CONFERENCE_COUNT, item.getConferenceCount());
		return getWritableDatabase().insert(TABLE_CONFERENCE, null, cv);
	}
	public ConferenceCursor queryConferencesByDivisionItem(DivisionItem item) {
		Log.d(TAG, "queryConference()");
		Cursor wrapped = getReadableDatabase().query(TABLE_CONFERENCE,
				null, // all columns 
				COLUMN_CONFERENCE_LEAGUE_ID + " = ? AND "+
				COLUMN_CONFERENCE_LEAGUE_URL + " = ? AND "+
				COLUMN_CONFERENCE_SEASON_ID + " = ? AND "+
				COLUMN_CONFERENCE_DIVISION_ID + " = ?",		// Where column
				new String[]{ String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL()),
				String.valueOf(item.getSeasonId()),
				String.valueOf(item.getDivisionId())}, // values
				null, // group by
				null, // having
				COLUMN_CONFERENCE_CONFERENCE_NAME + " COLLATE NOCASE asc", // order by
				null); // limit of rows
		return new ConferenceCursor(wrapped);
	}
	public long deleteTeamsByTeamItem(TeamItem item) {
		Log.d(TAG, "deleteTeamsByTeamItem()");
		return getWritableDatabase().delete(TABLE_TEAM,
				COLUMN_TEAM_LEAGUE_ID + " = ? AND " +
						COLUMN_TEAM_LEAGUE_URL + " = ? AND " +
						COLUMN_TEAM_SEASON_ID + " = ? AND " +
						COLUMN_TEAM_DIVISION_ID + " = ? AND "+
						COLUMN_TEAM_CONFERENCE_ID + " = ?",		// Where column
						new String[] {String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL()),
				String.valueOf(item.getSeasonId()),
				String.valueOf(item.getDivisionId()),
				String.valueOf(item.getConferenceId())}); // values
	}
	public long insertTeam(TeamItem item) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TEAM_LEAGUE_ID, item.getLeagueId());
		cv.put(COLUMN_TEAM_LEAGUE_URL, item.getLeagueURL());
		cv.put(COLUMN_TEAM_SEASON_ID, item.getSeasonId());
		cv.put(COLUMN_TEAM_SEASON_NAME, item.getSeasonName());
		cv.put(COLUMN_TEAM_DIVISION_ID, item.getDivisionId());
		cv.put(COLUMN_TEAM_DIVISION_NAME, item.getDivisionName());
		cv.put(COLUMN_TEAM_CONFERENCE_ID, item.getConferenceId());
		cv.put(COLUMN_TEAM_CONFERENCE_NAME, item.getConferenceName());
		cv.put(COLUMN_TEAM_CONFERENCE_COUNT, item.getConferenceCount());
		cv.put(COLUMN_TEAM_TEAM_ID, item.getTeamId());
		cv.put(COLUMN_TEAM_TEAM_NAME, item.getTeamName());
		cv.put(COLUMN_TEAM_TEAM_URL, item.getTeamURL());
		return getWritableDatabase().insert(TABLE_TEAM, null, cv);
	}
	public TeamCursor queryTeamsByConferenceItem(ConferenceItem item) {
		Log.d(TAG, "queryTeamsByConferenceItem()");
		// equivalent to "select * from league order by league_id asc"
		// sorting by user_id as an alpha... just copying JSON ordering
		Cursor wrapped = getReadableDatabase().query(TABLE_TEAM,
				null, // all columns 
				COLUMN_TEAM_LEAGUE_ID + " = ? AND "+
				COLUMN_TEAM_LEAGUE_URL + " = ? AND "+
				COLUMN_TEAM_SEASON_ID + " = ? AND "+
				COLUMN_TEAM_DIVISION_ID + " = ? AND "+
				COLUMN_TEAM_CONFERENCE_ID + " = ?",		// Where column
				new String[]{ String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL()),
				String.valueOf(item.getSeasonId()),
				String.valueOf(item.getDivisionId()),
				String.valueOf(item.getConferenceId())}, // values
				null, // group by
				null, // having
				COLUMN_TEAM_TEAM_NAME + " COLLATE NOCASE asc", // order by
				null); // limit of rows
		return new TeamCursor(wrapped);
	}
	public TeamCursor queryTeamByTeamItem(TeamItem item) {
		Log.d(TAG, "queryTeamByTeamItem()");
		// equivalent to "select * from league order by league_id asc"
		// sorting by user_id as an alpha... just copying JSON ordering
		Cursor wrapped = getReadableDatabase().query(TABLE_TEAM,
				null, // all columns 
				COLUMN_TEAM_LEAGUE_ID + " = ? AND "+
				COLUMN_TEAM_LEAGUE_URL + " = ? AND "+
				COLUMN_TEAM_SEASON_ID + " = ? AND "+
				COLUMN_TEAM_DIVISION_ID + " = ? AND "+
				COLUMN_TEAM_CONFERENCE_ID + " = ? AND "+
				COLUMN_TEAM_TEAM_ID + " = ?",		// Where column TEAM
				new String[]{ String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL()),
				String.valueOf(item.getSeasonId()),
				String.valueOf(item.getDivisionId()),
				String.valueOf(item.getConferenceId()),
				String.valueOf(item.getTeamId())}, // values
				null, // group by
				null, // having
				COLUMN_TEAM_TEAM_NAME + " COLLATE NOCASE asc", // order by
				null); // limit of rows
		return new TeamCursor(wrapped);
	}
	
	public long deleteGameByGameItem(GameItem item) {
		Log.d(TAG, "deleteGameByGameItem()");
		return getWritableDatabase().delete(TABLE_GAME,
				COLUMN_GAME_LEAGUE_ID + " = ? AND " +
						COLUMN_GAME_LEAGUE_URL + " = ? AND " +
						COLUMN_GAME_SEASON_ID + " = ? AND " +
						COLUMN_GAME_DIVISION_ID + " = ? AND "+
						COLUMN_GAME_CONFERENCE_ID + " = ? AND "+
						COLUMN_GAME_TEAM_ID + " = ?",		// Where column
						new String[] {String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL()),
				String.valueOf(item.getSeasonId()),
				String.valueOf(item.getDivisionId()),
				String.valueOf(item.getConferenceId()),
				String.valueOf(item.getTeamId())}); // values
	}
	public long insertGame(GameItem item, int sortID) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_GAME_LEAGUE_ID, item.getLeagueId());
		cv.put(COLUMN_GAME_LEAGUE_URL, item.getLeagueURL());
		cv.put(COLUMN_GAME_SEASON_ID, item.getSeasonId());
		cv.put(COLUMN_GAME_SEASON_NAME, item.getSeasonName());
		cv.put(COLUMN_GAME_DIVISION_ID, item.getDivisionId());
		cv.put(COLUMN_GAME_DIVISION_NAME, item.getDivisionName());
		cv.put(COLUMN_GAME_CONFERENCE_ID, item.getConferenceId());
		cv.put(COLUMN_GAME_CONFERENCE_NAME, item.getConferenceName());
		cv.put(COLUMN_GAME_CONFERENCE_COUNT, item.getConferenceCount());
		cv.put(COLUMN_GAME_TEAM_ID, item.getTeamId());
		cv.put(COLUMN_GAME_TEAM_NAME, item.getTeamName());
		cv.put(COLUMN_GAME_GAME_ID, item.getGameId());
		cv.put(COLUMN_GAME_GAME_SORT_ID, sortID);
		cv.put(COLUMN_GAME_GAME_DATE_TIME, item.getGameDateTime());
		cv.put(COLUMN_GAME_GAME_HOME_TEAM, item.getGameHomeTeam());
		cv.put(COLUMN_GAME_GAME_AWAY_TEAM, item.getGameAwayTeam());
		cv.put(COLUMN_GAME_GAME_LOCATION, item.getGameLocation());
		cv.put(COLUMN_GAME_GAME_START_TBD, item.getGameStartTBD());
		cv.put(COLUMN_GAME_GAME_HOME_SCORE, item.getGameHomeScore());
		cv.put(COLUMN_GAME_GAME_AWAY_SCORE, item.getGameAwayScore());

		return getWritableDatabase().insert(TABLE_GAME, null, cv);
	}
	public GameCursor queryGamesByTeamItem(TeamItem item) {
		Log.d(TAG, "queryGamesByTeamItem()");
		// equivalent to "select * from league order by league_id asc"
		// sorting by user_id as an alpha... just copying JSON ordering
		Cursor wrapped = getReadableDatabase().query(TABLE_GAME,
				null, // all columns 
				COLUMN_GAME_LEAGUE_ID + " = ? AND "+
				COLUMN_GAME_LEAGUE_URL + " = ? AND "+
				COLUMN_GAME_SEASON_ID + " = ? AND "+
				COLUMN_GAME_DIVISION_ID + " = ? AND "+
				COLUMN_GAME_CONFERENCE_ID + " = ? AND "+
				COLUMN_GAME_TEAM_ID + " = ?",		// Where column
				new String[]{ String.valueOf(item.getLeagueId()),
				String.valueOf(item.getLeagueURL()),
				String.valueOf(item.getSeasonId()),
				String.valueOf(item.getDivisionId()),
				String.valueOf(item.getConferenceId()),
				String.valueOf(item.getTeamId())}, // values
				null, // group by
				null, // having
				COLUMN_GAME_GAME_SORT_ID + " COLLATE NOCASE asc", // order by
				null); // limit of rows
		return new GameCursor(wrapped);
	}
	/**
	 * A convenience class to wrap a cursor that returns rows from the table.
	 * The {@link getUser()} method will give you a UserItem instance for the current row.
	 */
	public static class LeagueCursor extends CursorWrapper {
		public LeagueCursor(Cursor c) {
			super(c);
		}
		/**
		 * Returns a Run object configured for the current row, or null if the current row is invalid.
		 */
		public LeagueItem getLeagueItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			LeagueItem item = new LeagueItem();
			item.setLeagueId(getString(getColumnIndex(COLUMN_LEAGUE_LEAGUE_ID)));
			item.setOrgName(getString(getColumnIndex(COLUMN_LEAGUE_ORG_NAME)));
			item.setLeagueURL(getString(getColumnIndex(COLUMN_LEAGUE_LEAGUE_URL)));
			return item;
		}
	}
	public static class SeasonCursor extends CursorWrapper {
		public SeasonCursor(Cursor c) {
			super(c);
		}
		public SeasonItem getSeasonItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			SeasonItem item = new SeasonItem();
			item.setLeagueId(getString(getColumnIndex(COLUMN_SEASON_LEAGUE_ID)));
			item.setLeagueURL(getString(getColumnIndex(COLUMN_SEASON_LEAGUE_URL)));
			item.setSeasonId(getString(getColumnIndex(COLUMN_SEASON_SEASON_ID)));
			item.setSeasonName(getString(getColumnIndex(COLUMN_SEASON_SEASON_NAME)));
			return item;
		}
	}
	public static class DivisionCursor extends CursorWrapper {
		public DivisionCursor(Cursor c) {
			super(c);
		}
		public DivisionItem getDivisionItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			DivisionItem item = new DivisionItem();
			item.setLeagueId(getString(getColumnIndex(COLUMN_DIVISION_LEAGUE_ID)));
			item.setLeagueURL(getString(getColumnIndex(COLUMN_DIVISION_LEAGUE_URL)));
			item.setSeasonId(getString(getColumnIndex(COLUMN_DIVISION_SEASON_ID)));
			item.setSeasonName(getString(getColumnIndex(COLUMN_DIVISION_SEASON_NAME)));
			item.setDivisionId(getString(getColumnIndex(COLUMN_DIVISION_DIVISION_ID)));
			item.setDivisionName(getString(getColumnIndex(COLUMN_DIVISION_DIVISION_NAME)));
			return item;
		}
	}
	public static class ConferenceCursor extends CursorWrapper {
		public ConferenceCursor(Cursor c) {
			super(c);
		}
		public ConferenceItem getConferenceItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			ConferenceItem item = new ConferenceItem();
			item.setLeagueId(getString(getColumnIndex(COLUMN_CONFERENCE_LEAGUE_ID)));
			item.setLeagueURL(getString(getColumnIndex(COLUMN_CONFERENCE_LEAGUE_URL)));
			item.setSeasonId(getString(getColumnIndex(COLUMN_CONFERENCE_SEASON_ID)));
			item.setSeasonName(getString(getColumnIndex(COLUMN_CONFERENCE_SEASON_NAME)));
			item.setDivisionId(getString(getColumnIndex(COLUMN_CONFERENCE_DIVISION_ID)));
			item.setDivisionName(getString(getColumnIndex(COLUMN_CONFERENCE_DIVISION_NAME)));
			item.setConferenceId(getString(getColumnIndex(COLUMN_CONFERENCE_CONFERENCE_ID)));
			item.setConferenceName(getString(getColumnIndex(COLUMN_CONFERENCE_CONFERENCE_NAME)));
			item.setConferenceCount(getString(getColumnIndex(COLUMN_CONFERENCE_CONFERENCE_COUNT)));
			return item;
		}
	}
	public static class TeamCursor extends CursorWrapper {
		public TeamCursor(Cursor c) {
			super(c);
		}
		public TeamItem getTeamItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			TeamItem item = new TeamItem();
			item.setLeagueId(getString(getColumnIndex(COLUMN_TEAM_LEAGUE_ID)));
			item.setLeagueURL(getString(getColumnIndex(COLUMN_TEAM_LEAGUE_URL)));
			item.setSeasonId(getString(getColumnIndex(COLUMN_TEAM_SEASON_ID)));
			item.setSeasonName(getString(getColumnIndex(COLUMN_TEAM_SEASON_NAME)));
			item.setDivisionId(getString(getColumnIndex(COLUMN_TEAM_DIVISION_ID)));
			item.setDivisionName(getString(getColumnIndex(COLUMN_TEAM_DIVISION_NAME)));
			item.setConferenceId(getString(getColumnIndex(COLUMN_TEAM_CONFERENCE_ID)));
			item.setConferenceName(getString(getColumnIndex(COLUMN_TEAM_CONFERENCE_NAME)));
			item.setConferenceCount(getString(getColumnIndex(COLUMN_TEAM_CONFERENCE_COUNT)));
			item.setTeamId(getString(getColumnIndex(COLUMN_TEAM_TEAM_ID)));
			item.setTeamName(getString(getColumnIndex(COLUMN_TEAM_TEAM_NAME)));
			item.setTeamURL(getString(getColumnIndex(COLUMN_TEAM_TEAM_URL)));
			return item;
		}
	}
	public static class GameCursor extends CursorWrapper {
		public GameCursor(Cursor c) {
			super(c);
		}
		public GameItem getGameItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			GameItem item = new GameItem();
			item.setLeagueId(getString(getColumnIndex(COLUMN_GAME_LEAGUE_ID)));
			item.setLeagueURL(getString(getColumnIndex(COLUMN_GAME_LEAGUE_URL)));
			item.setSeasonId(getString(getColumnIndex(COLUMN_GAME_SEASON_ID)));
			item.setSeasonName(getString(getColumnIndex(COLUMN_GAME_SEASON_NAME)));
			item.setDivisionId(getString(getColumnIndex(COLUMN_GAME_DIVISION_ID)));
			item.setDivisionName(getString(getColumnIndex(COLUMN_GAME_DIVISION_NAME)));
			item.setConferenceId(getString(getColumnIndex(COLUMN_GAME_CONFERENCE_ID)));
			item.setConferenceName(getString(getColumnIndex(COLUMN_GAME_CONFERENCE_NAME)));
			item.setConferenceCount(getString(getColumnIndex(COLUMN_GAME_CONFERENCE_COUNT)));
			item.setTeamId(getString(getColumnIndex(COLUMN_GAME_TEAM_ID)));
			item.setTeamName(getString(getColumnIndex(COLUMN_GAME_TEAM_NAME)));
			item.setGameId(getString(getColumnIndex(COLUMN_GAME_GAME_ID)));
			//item.setGameSortId(getString(getColumnIndex(COLUMN_GAME_GAME_SORT_ID)));
			item.setGameDateTime(getString(getColumnIndex(COLUMN_GAME_GAME_DATE_TIME)));
			item.setGameHomeTeam(getString(getColumnIndex(COLUMN_GAME_GAME_HOME_TEAM)));
			item.setGameAwayTeam(getString(getColumnIndex(COLUMN_GAME_GAME_AWAY_TEAM)));
			item.setGameLocation(getString(getColumnIndex(COLUMN_GAME_GAME_LOCATION)));
			item.setGameStartTBD(getString(getColumnIndex(COLUMN_GAME_GAME_START_TBD)));
			item.setGameHomeScore(getString(getColumnIndex(COLUMN_GAME_GAME_HOME_SCORE)));
			item.setGameAwayScore(getString(getColumnIndex(COLUMN_GAME_GAME_AWAY_SCORE)));
			return item;
		}
	}
}