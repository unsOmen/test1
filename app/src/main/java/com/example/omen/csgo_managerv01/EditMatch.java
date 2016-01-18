package com.example.omen.csgo_managerv01;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditMatch extends FragmentActivity {

    DB db;
    Cursor cursor;
    Fragment frg;
    static long game_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_match);

        Intent intent = this.getIntent();
        game_id = intent.getLongExtra(MainActivity.INTENT_GAME_ID, 0);

        if(game_id==0)
            finish();

        db = new DB(this);
        db.open();
        cursor = db.getGame(game_id);

        if(cursor != null && cursor.moveToFirst()) {
            ((TextView) findViewById(R.id.edit_tv_team1)).setText(cursor.getString(cursor.getColumnIndex(DB.GET_DATE_TEAM_1)));
            ((TextView) findViewById(R.id.edit_tv_team2)).setText(cursor.getString(cursor.getColumnIndex(DB.GET_DATE_TEAM_2)));

            frg = getSupportFragmentManager().findFragmentById(R.id.edit_fragment);
            ((EditText) frg.getView().findViewById(R.id.fragment_score1)).setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_1))));
            ((EditText) frg.getView().findViewById(R.id.fragment_score2)).setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_2))));
            ((Spinner) frg.getView().findViewById(R.id.map_list)).setSelection(cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_MAP_ID))-1);
        } else {
            cursor.close();
        }

        ((Button) findViewById(R.id.btnEdit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db!=null && frg!=null) {
                    int score_1 = Integer.parseInt(((EditText) frg.getView().findViewById(R.id.fragment_score1)).getText().toString());
                    int score_2 = Integer.parseInt(((EditText) frg.getView().findViewById(R.id.fragment_score2)).getText().toString());
                    MapList map = (MapList)((Spinner) frg.getView().findViewById(R.id.map_list)).getSelectedItem();
                    db.update_game(game_id, score_1, score_2, map);
                    //cursor.requery();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
