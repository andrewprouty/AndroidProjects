package com.prouty.leagueusa.sdsolschedule;

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
	private static final String COLUMN_LEAGUE_LEAGUE_NAME = "league_name";

	private static final String TABLE_SEASON = "season";
	private static final String COLUMN_SEASON_LEAGUE_ID = "league_id";
	private static final String COLUMN_SEASON_SEASON_ID = "season_id";
	private static final String COLUMN_SEASON_SEASON_NAME = "season_name";

	private static final String TABLE_DIVISION = "division";
	private static final String COLUMN_DIVISION_LEAGUE_ID = "league_id";
	private static final String COLUMN_DIVISION_SEASON_ID = "season_id";
	private static final String COLUMN_DIVISION_DIVISION_ID = "division_id";
	private static final String COLUMN_DIVISION_DIVISION_NAME = "division_name";

	private static final String TABLE_CONFERENCE = "conference";
	private static final String COLUMN_CONFERENCE_CONFERENCE_ID = "conference_id";
	private static final String COLUMN_CONFERENCE_CONFERENCE_NAME = "conference_name";

	private static final String TABLE_TEAM = "team";
	private static final String COLUMN_TEAM_TEAM_ID = "team_id";
	private static final String COLUMN_TEAM_TEAM_NAME = "team_name";

	private static final String TABLE_USER = "user";
	private static final String COLUMN_USER_USER_ID = "user_id";
	private static final String COLUMN_USER_USER_NAME = "user_name";

	private static final String TABLE_PHOTO = "photo";
	private static final String COLUMN_PHOTO_PHOTO_ID = "photo_id";
	private static final String COLUMN_PHOTO_PHOTO_NAME = "photo_name";
	private static final String COLUMN_PHOTO_USER_ID = "user_id";
	private static final String COLUMN_PHOTO_USER_NAME = "user_name";

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate()");
		db.execSQL("create table league (" +
				" league_id varchar(10) primary key, league_name varchar(100))");
		
		db.execSQL("create table season (" +
				" league_id varchar(10), season_id varchar(10), season_name varchar(100)," +
				" primary key (league_id, season_id))");

		db.execSQL("create table division (" +
				" league_id varchar(10), season_id varchar(10),"+
				" division_id varchar(10), division_name varchar(100)," +
				" primary key (league_id, season_id, division_id))");

		db.execSQL("create table conference (" +
				" conference_id varchar(10) primary key, conference_name varchar(100))");
		db.execSQL("create table team (" +
				" team_id varchar(10) primary key, team_name varchar(100))");

		db.execSQL("create table user (" +
				" user_id varchar(10) primary key, user_name varchar(100))");
		db.execSQL("create table photo (" +
				" photo_id integer not null, photo_name varchar(100),"+
				" user_id varchar(10) references user(user_id), user_name varchar(100)," +
				" primary key (photo_id, user_id))");
		Log.d(TAG, "onCreate()ed");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// implement schema changes and data massage here when upgrading
	}

	public long deleteLeagues() {
		Log.d(TAG, "deleteLeagues()");
		return getWritableDatabase().delete(TABLE_LEAGUE, null, null);
	}
	public long insertLeague(LeagueItem item) {
		Log.d(TAG, "insertLeague()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_LEAGUE_LEAGUE_ID, item.getLeagueId());
		cv.put(COLUMN_LEAGUE_LEAGUE_NAME, item.getLeagueName());
		return getWritableDatabase().insert(TABLE_LEAGUE, null, cv);
	}
	public LeagueCursor queryLeagues() {
		Log.d(TAG, "queryLeagues()");
		// equivalent to "select * from league order by league_id asc"
		// sorting by user_id as an alpha... just copying JSON ordering
		Cursor wrapped = getReadableDatabase().query(TABLE_LEAGUE,
				null, null, null, null, null, COLUMN_LEAGUE_LEAGUE_ID + " asc");
		return new LeagueCursor(wrapped);
	}

	public long deleteSeason() {
		Log.d(TAG, "deleteSeason()");
		return getWritableDatabase().delete(TABLE_SEASON, null, null);
	}
	public long insertSeason(SeasonItem item) {
		Log.d(TAG, "insertSeason()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SEASON_LEAGUE_ID, item.getLeagueId());
		cv.put(COLUMN_SEASON_SEASON_ID, item.getSeasonId());
		cv.put(COLUMN_SEASON_SEASON_NAME, item.getSeasonName());
		return getWritableDatabase().insert(TABLE_SEASON, null, cv);
	}
	public SeasonCursor querySeasonsbyLeagueId(String id) {
		Log.d(TAG, "querySeasons()");
		Cursor wrapped = getReadableDatabase().query(TABLE_SEASON,
				null, // all columns 
				COLUMN_SEASON_LEAGUE_ID + " = ?", // THAT User Id
				new String[]{ String.valueOf(id) }, // with this value
				null, // group by
				null, // having
				COLUMN_SEASON_SEASON_NAME + " asc", // order by
				null); // limit of rows
		return new SeasonCursor(wrapped);
	}

	public long deleteDivision() {
		Log.d(TAG, "deleteDivision()");
		return getWritableDatabase().delete(TABLE_DIVISION, null, null);
	}
	public long insertDivision(DivisionItem item) {
		Log.d(TAG, "insertDivision()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_DIVISION_DIVISION_ID, item.getDivisionId());
		cv.put(COLUMN_DIVISION_DIVISION_NAME, item.getDivisionName());
		return getWritableDatabase().insert(TABLE_DIVISION, null, cv);
	}
	public DivisionCursor queryDivisionsbyIds(String leagueId, String seasonId) {
		Log.d(TAG, "queryDivisions()");
		Cursor wrapped = getReadableDatabase().query(TABLE_SEASON,
				null, // all columns 
				COLUMN_SEASON_LEAGUE_ID + " = ? AND "+	// Where column
				COLUMN_SEASON_SEASON_ID + " = ?",		// Where column
				new String[]{ String.valueOf(leagueId), String.valueOf(leagueId)}, // values
				null, // group by
				null, // having
				COLUMN_SEASON_SEASON_NAME + " asc", // order by
				null); // limit of rows
		return new DivisionCursor(wrapped);
	}

	public long deleteConference() {
		Log.d(TAG, "deleteConference()");
		return getWritableDatabase().delete(TABLE_CONFERENCE, null, null);
	}
	public long insertConference(ConferenceItem item) {
		Log.d(TAG, "insertConference()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CONFERENCE_CONFERENCE_ID, item.getConferenceId());
		cv.put(COLUMN_CONFERENCE_CONFERENCE_NAME, item.getConferenceName());
		return getWritableDatabase().insert(TABLE_CONFERENCE, null, cv);
	}
	public ConferenceCursor queryConferences() {
		Log.d(TAG, "queryConferences()");
		// equivalent to "select * from league order by league_id asc"
		// sorting by user_id as an alpha... just copying JSON ordering
		Cursor wrapped = getReadableDatabase().query(TABLE_CONFERENCE,
				null, null, null, null, null, COLUMN_CONFERENCE_CONFERENCE_ID + " asc");
		return new ConferenceCursor(wrapped);
	}

	public long deleteTeam() {
		Log.d(TAG, "deleteTeam()");
		return getWritableDatabase().delete(TABLE_TEAM, null, null);
	}
	public long insertTeam(TeamItem item) {
		Log.d(TAG, "insertTeam()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TEAM_TEAM_ID, item.getTeamId());
		cv.put(COLUMN_TEAM_TEAM_NAME, item.getTeamName());
		return getWritableDatabase().insert(TABLE_TEAM, null, cv);
	}
	public TeamCursor queryTeams() {
		Log.d(TAG, "queryTeams()");
		// equivalent to "select * from league order by league_id asc"
		// sorting by user_id as an alpha... just copying JSON ordering
		Cursor wrapped = getReadableDatabase().query(TABLE_TEAM,
				null, null, null, null, null, COLUMN_TEAM_TEAM_ID + " asc");
		return new TeamCursor(wrapped);
	}

	public long deleteUsers() {
		Log.d(TAG, "deleteUsers()");
		return getWritableDatabase().delete(TABLE_USER, null, null);
	}
	public long insertUser(UserItem item) {
		Log.d(TAG, "insertUser()");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_USER_USER_ID, item.getUserId());
		cv.put(COLUMN_USER_USER_NAME, item.getUserName());
		return getWritableDatabase().insert(TABLE_USER, null, cv);
	}
	public UserCursor queryUsers() {
		Log.d(TAG, "queryUsers()");
		// equivalent to "select * from user order by user_id asc"
		// sorting by user_id as an alpha... just copying JSON ordering
		Cursor wrapped = getReadableDatabase().query(TABLE_USER,
				null, null, null, null, null, COLUMN_USER_USER_ID + " asc");
		return new UserCursor(wrapped);
	}
	
	public long insertPhoto(PhotoItem item) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PHOTO_PHOTO_ID, item.getPhotoId());
		cv.put(COLUMN_PHOTO_PHOTO_NAME, item.getPhotoName());
		cv.put(COLUMN_PHOTO_USER_ID, item.getUserId());
		cv.put(COLUMN_PHOTO_USER_NAME, item.getUserName());
		return getWritableDatabase().insert(TABLE_PHOTO, null, cv);
	}
	public long deletePhotosforUserId(String user) {
		Log.d(TAG, "deletePhotosforUserId() "+user);
		return getWritableDatabase().delete(TABLE_PHOTO,
				COLUMN_PHOTO_USER_ID + " = ?", // THAT User Id
				new String[]{ String.valueOf(user) }); // with this value
	}
	public PhotoCursor queryPhotosForUserId(String id) {
		// this JSON is sorting by NUMERIC photo_id asc
		Cursor wrapped = getReadableDatabase().query(TABLE_PHOTO, 
				null, // all columns 
				COLUMN_PHOTO_USER_ID + " = ?", // THAT User Id
				new String[]{ String.valueOf(id) }, // with this value
				null, // group by
				null, // having
				COLUMN_PHOTO_PHOTO_ID + " asc", // order by
				null); // limit of rows
		return new PhotoCursor(wrapped);
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
			item.setLeagueName(getString(getColumnIndex(COLUMN_LEAGUE_LEAGUE_NAME)));
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
			item.setSeasonId(getString(getColumnIndex(COLUMN_DIVISION_SEASON_ID)));
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
			item.setConferenceId(getString(getColumnIndex(COLUMN_CONFERENCE_CONFERENCE_ID)));
			item.setConferenceName(getString(getColumnIndex(COLUMN_CONFERENCE_CONFERENCE_NAME)));
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
			item.setTeamId(getString(getColumnIndex(COLUMN_TEAM_TEAM_ID)));
			item.setTeamName(getString(getColumnIndex(COLUMN_TEAM_TEAM_NAME)));
			return item;
		}
	}

	public static class UserCursor extends CursorWrapper {
		public UserCursor(Cursor c) {
			super(c);
		}
		/**
		 * Returns a Run object configured for the current row, or null if the current row is invalid.
		 */
		public UserItem getUserItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			UserItem item = new UserItem();
			item.setUserId(getString(getColumnIndex(COLUMN_USER_USER_ID)));
			item.setUserName(getString(getColumnIndex(COLUMN_USER_USER_NAME)));
			return item;
		}
	}

	public static class PhotoCursor extends CursorWrapper {
		public PhotoCursor(Cursor c) {
			super(c);
		}
		public PhotoItem getPhotoItem() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			PhotoItem item = new PhotoItem();
			item.setPhotoId(getString(getColumnIndex(COLUMN_PHOTO_PHOTO_ID)));
			item.setPhotoName(getString(getColumnIndex(COLUMN_PHOTO_PHOTO_NAME)));
			item.setUserId(getString(getColumnIndex(COLUMN_PHOTO_USER_ID)));
			item.setUserName(getString(getColumnIndex(COLUMN_PHOTO_USER_NAME)));
			return item;
		}
	}
}