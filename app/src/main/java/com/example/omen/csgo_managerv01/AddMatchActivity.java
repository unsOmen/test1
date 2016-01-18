package com.example.omen.csgo_managerv01;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by OmeN on 29.11.2015.
 */
public class AddMatchActivity extends FragmentActivity {

    private static final String LOG_TAG = "myLog";

    private static final int DIALOG_SEL_TEAM1 = 1;
    private static final int DIALOG_SEL_TEAM2 = 2;
    private static final int DIALOG_DATE = 3;
    private static final int DIALOG_ADD_TEAM = 4;

    private static final String FRAGMENT_NAME = "match_";

    private FragmentManager manager;
    private FragmentTransaction transaction;

    TextView click_tv;
    EditText add_team_name;
    int count_match=0;

    TextView dateMatch;
    SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd.MM.yyyy");
    long currentTime = System.currentTimeMillis();
    Date date = new Date(currentTime);

    DB db;
    Cursor c;

    private int id_team1;
    private int id_team2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_match);

        db = new DB(this);
        db.open();
        c = db.getTeams();
        db.logCursor(c);
        startManagingCursor(c);

        manager = getSupportFragmentManager();

        dateMatch = (TextView) findViewById(R.id.date_match);
        dateMatch.setText(simpleDateFormat.format(date));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sel_tv_team1_name:
                click_tv = (TextView) findViewById(v.getId());
                showDialog(DIALOG_SEL_TEAM1);
                break;
            case R.id.sel_tv_team2_name:
                click_tv = (TextView) findViewById(v.getId());
                showDialog(DIALOG_SEL_TEAM2);
                break;
            case R.id.btnAddMatch:
                if(id_team1 == id_team2 || count_match == 0)
                    break;
                c.moveToPosition(id_team1-1);
                Log.d(LOG_TAG, "Team1=" + id_team1 + " / " + c.getString(c.getColumnIndexOrThrow(DB.TEAM_COLUMN_NAME)));
                long id_match = db.insert_match(id_team1, id_team2, dateMatch.getText().toString());

                c.moveToPosition(id_team2-1);
                Log.d(LOG_TAG, "Team2=" + id_team2 + " / " + c.getString(c.getColumnIndexOrThrow(DB.TEAM_COLUMN_NAME)));

                for(int i=0; i<count_match; i++) {
                    Fragment frg = manager.findFragmentByTag(FRAGMENT_NAME + i);
                    MapList map = (MapList)((Spinner) frg.getView().findViewById(R.id.map_list)).getSelectedItem();

                    int score_team1 = Integer.parseInt(((EditText) frg.getView().findViewById(R.id.fragment_score1)).getText().toString());
                    int score_team2 = Integer.parseInt(((EditText) frg.getView().findViewById(R.id.fragment_score2)).getText().toString());

                    Log.d(LOG_TAG, "Score: " + score_team1 + " / " +
                            score_team2 + " - " + map.getName() + "/" + map.getId() + "//" + dateMatch.getText().toString());
                    db.insert_game(id_match, score_team1, score_team2, map);
                }
                finish();
                break;
            case R.id.btnAddGame:
                transaction = manager.beginTransaction();
                match_fragment frg = new match_fragment();
                transaction.add(R.id.container, frg, FRAGMENT_NAME + count_match);
                transaction.commit();

                count_match++;
                break;

            case R.id.btnDelGame:
                if(count_match<=0)
                    break;
                transaction = manager.beginTransaction();
                transaction.remove(manager.findFragmentByTag(FRAGMENT_NAME + (count_match-1)));
                transaction.commit();
                count_match--;
                break;

            case R.id.date_match:
                showDialog(DIALOG_DATE);
                break;

            case R.id.btnAddTeam:
                showDialog(DIALOG_ADD_TEAM);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_SEL_TEAM1:
                Log.d(LOG_TAG, "create count=" + c.getCount());
                builder.setTitle(R.string.sel_team1);
                builder.setCursor(c, myClickListener, DB.TEAM_COLUMN_NAME);
                break;

            case DIALOG_SEL_TEAM2:
                builder.setTitle(R.string.sel_team2);
                builder.setCursor(c, myClickListener, DB.TEAM_COLUMN_NAME);
                break;

            case DIALOG_DATE:
                DatePickerDialog dpd = new DatePickerDialog(this, myCallBack, date.getYear(), date.getMonth(), date.getDay());
                return dpd;

            case DIALOG_ADD_TEAM:
                builder.setTitle("ADD TEAM");
                View v = View.inflate(getApplicationContext(), R.layout.add_team, null);
                add_team_name = (EditText) v.findViewById(R.id.add_team_etTeamName);
                builder.setView(v);
                builder.setPositiveButton(R.string.add, addTeamClickListener);
                builder.setNegativeButton(R.string.cancel, addTeamClickListener);
        }
        return builder.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        c.requery();
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateMatch.setText(""+ dayOfMonth + "." + (monthOfYear+1) + "." +year);
        }
    };

    DialogInterface.OnClickListener addTeamClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    db.insert_team(add_team_name.getText().toString());
                    add_team_name.setText("");
                    break;

                case Dialog.BUTTON_NEGATIVE:
                    add_team_name.setText("");
                    break;
            }
        }
    };

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            c.moveToPosition(which);
            click_tv.setText(c.getString(c.getColumnIndexOrThrow(DB.TEAM_COLUMN_NAME)));
            switch (click_tv.getId()) {
                case R.id.sel_tv_team1_name:
                    id_team1 = which+1;
                    break;
                case R.id.sel_tv_team2_name:
                    id_team2 = which+1;
                    break;
                default:
                    break;
            }
            click_tv = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
