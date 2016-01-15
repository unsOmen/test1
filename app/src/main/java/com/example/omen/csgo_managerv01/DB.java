package com.example.omen.csgo_managerv01;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by OmeN on 28.11.2015.
 */
public class DB {

    private static final String LOG_TAG = "myLog";

    private static final String DB_NAME = "csgo_manager4";
    private static final int DB_VERSION = 1;

    // таблица команд
    private static final String TEAM_TABLE = "teams";
    public static final String TEAM_COLUMN_ID = "_id";
    public static final String TEAM_COLUMN_NAME = "name";
    private static final String TEAM_TABLE_CREATE = "create table "
            + TEAM_TABLE + "("
            + TEAM_COLUMN_ID + " integer primary key autoincrement, "
            + TEAM_COLUMN_NAME + " text not null unique"
            + ");";

    // таблица игр
    private static final String GAME_TABLE = "games";
    public static final String GAME_COLUMN_ID = "_id";
    public static final String GAME_COLUMN_MATCH_ID = "match_id";
    public static final String GAME_COLUMN_SCORE_TEAM1 = "score1";
    public static final String GAME_COLUMN_SCORE_TEAM2 = "score2";
    public static final String GAME_COLUMN_MAP_ID = "id_map";
    public static final String GAME_COLUMN_MAP_NAME = "map_name";
    private static final String GAME_TABLE_CREATE = "create table "
            + GAME_TABLE + "("
            + GAME_COLUMN_ID + " integer primary key autoincrement, "
            + GAME_COLUMN_MATCH_ID + " integer not null, "
            + GAME_COLUMN_SCORE_TEAM1 + " integer, "
            + GAME_COLUMN_SCORE_TEAM2 + " integer, "
            + GAME_COLUMN_MAP_ID + " integer not null, "
            + GAME_COLUMN_MAP_NAME + " text not null"
            + ");";

    // таблица матчей
    private static final String MATCH_TABLE = "matchs";
    public static final String MATCH_COLUMN_ID = "_id";
    public static final String MATCH_COLUMN_TEAM1_ID = "team1_id";
    public static final String MATCH_COLUMN_TEAM2_ID = "team2_id";
    public static final String MATCH_COLUMN_DATE = "date_match";
    private static final String MATCH_TABLE_CREATE = "create table "
            + MATCH_TABLE + "("
            + MATCH_COLUMN_ID + " integer primary key autoincrement, "
            + MATCH_COLUMN_TEAM1_ID + " integer, "
            + MATCH_COLUMN_TEAM2_ID + " integer, "
            + MATCH_COLUMN_DATE + " text"
            + ");";

    // имена колонок для вывода данных
    public static final String GET_DATE_MATCH_ID = "match_id";
    public static final String GET_DATE_GAME_ID = "_id";
    public static final String GET_DATE_TEAM_1 = "team_1";
    public static final String GET_DATE_TEAM_2 = "team_2";
    public static final String GET_DATE_SCORE_1 = "score_1";
    public static final String GET_DATE_SCORE_2 = "score_2";
    public static final String GET_DATE_MAP_ID = "map_id";
    public static final String GET_DATE_MAP_NAME = "name_map";

    private Context m_Context;
    private DBHelper mDBHelper;
    private SQLiteDatabase m_DB;

    public DB(Context context) {
        this.m_Context = context;
    }

    public Cursor getDate() {
        Cursor c;
        String sqlQuery = "select t1." + MATCH_COLUMN_ID + " as " + GET_DATE_MATCH_ID + ", t2." + GAME_COLUMN_ID + " as " + GET_DATE_GAME_ID + ", "
                //+ "(select t3." + TEAM_COLUMN_NAME +" from " + TEAM_TABLE +" as t3 where t1." + MATCH_COLUMN_TEAM1_ID + " = t3." + TEAM_COLUMN_ID +") as team_1, "
                + "t3." + TEAM_COLUMN_NAME + " as " + GET_DATE_TEAM_1 + ", "
                //+ "(select t3." + TEAM_COLUMN_NAME +" from " + TEAM_TABLE +" as t3 where t1." + MATCH_COLUMN_TEAM2_ID + " = t3." + TEAM_COLUMN_ID +") as team_2, "
                + "t4." + TEAM_COLUMN_NAME + " as " + GET_DATE_TEAM_2 + ", "
                + "t2.score1 as " + GET_DATE_SCORE_1 + ", "
                + "t2.score2 as " + GET_DATE_SCORE_2 + ", "
                + "t2." + GAME_COLUMN_MAP_ID + " as " + GET_DATE_MAP_ID + ", "
                + "t2." + GAME_COLUMN_MAP_NAME + " as " + GET_DATE_MAP_NAME + " "
                + "from " + MATCH_TABLE + " as t1 "
                + "inner join " + GAME_TABLE +" as t2 on t1." + MATCH_COLUMN_ID + " = t2." + GAME_COLUMN_MATCH_ID + " "
                + "join " + TEAM_TABLE + " as t3 on t1." + MATCH_COLUMN_TEAM1_ID + " = t3." + TEAM_COLUMN_ID + " "
                + "join " + TEAM_TABLE + " as t4 on t1." + MATCH_COLUMN_TEAM2_ID + " = t4." + TEAM_COLUMN_ID;

        //mDB.query(PHONE_TABLE, null, PHONE_COLUMN_COMPANY + " = "
        //+ companyID, null, null, null, null);

        //String table = "matchs as t1 inner join games as t2 on t1._id = t2.match_id";
        //String columns[] = { "t1._id as match_id", "t1.team1_id as team_1", "t1.team2_id as team_2", "t2.score1 as score_1", "t2.score2 as score_2"};

        //c = m_DB.query(table, columns, null, null, null, null, null);

        c = m_DB.rawQuery(sqlQuery, new String[] {});
        //logCursor(c);
        return c;
    }

    public Cursor getTeams() {
        return m_DB.query(TEAM_TABLE, null ,null ,null, null, null, null);
    }

    public static void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);
                } while (c.moveToNext());
            }
        } else
            Log.d(LOG_TAG, "Cursor is null");
    }

    // открываем подключание
    public void open() {
        mDBHelper = new DBHelper(m_Context, DB_NAME, null, DB_VERSION);
        m_DB = mDBHelper.getWritableDatabase();
    }

    public void insert_team(String team_name) {
        m_DB.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(TEAM_COLUMN_NAME, team_name);
        m_DB.insert(TEAM_TABLE, null, cv);
        m_DB.setTransactionSuccessful();
        m_DB.endTransaction();
    }

    public long insert_match(int id_team1, int id_team2, String date) {
        m_DB.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(MATCH_COLUMN_TEAM1_ID, id_team1);
        cv.put(MATCH_COLUMN_TEAM2_ID, id_team2);
        cv.put(MATCH_COLUMN_DATE, date);
        long rowID = m_DB.insert(MATCH_TABLE, null, cv);
        m_DB.setTransactionSuccessful();
        m_DB.endTransaction();

        return rowID;
    }

    public void insert_game(long rowID, int score_team1, int score_team2, MapList map) {
        m_DB.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(GAME_COLUMN_MATCH_ID, rowID);
        cv.put(GAME_COLUMN_SCORE_TEAM1, score_team1);
        cv.put(GAME_COLUMN_SCORE_TEAM2, score_team2);
        cv.put(GAME_COLUMN_MAP_ID, map.getId());
        cv.put(GAME_COLUMN_MAP_NAME, map.getName());
        m_DB.insert(GAME_TABLE, null, cv);
        m_DB.setTransactionSuccessful();
        m_DB.endTransaction();
    }

    public void del_game(long id) {
        m_DB.beginTransaction();
        int delCount = m_DB.delete(GAME_TABLE, GAME_COLUMN_ID + " = " + id, null);
        Toast.makeText(m_Context, "Delete count = " + delCount, Toast.LENGTH_SHORT).show();
        m_DB.setTransactionSuccessful();
        m_DB.endTransaction();
    }

    // закрываем подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //ContentValues cv = new ContentValues();

            // создаем таблицы
            db.execSQL(TEAM_TABLE_CREATE);
            db.execSQL(GAME_TABLE_CREATE);
            db.execSQL(MATCH_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
