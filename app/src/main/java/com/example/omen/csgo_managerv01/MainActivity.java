package com.example.omen.csgo_managerv01;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    ListView match_list;
    DB db;
    Cursor cursor;
    SimpleCursorAdapter scAdapter;
    ArrayList<MapList> maps;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId() == R.id.lv_matchs) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            SQLiteCursor cursor = (SQLiteCursor) match_list.getItemAtPosition(info.position);

            menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(DB.GET_DATE_TEAM_1)) + " - " + cursor.getString(cursor.getColumnIndex(DB.GET_DATE_TEAM_2))
                    + " " + cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_1)) + " : " + cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_2))
                    + " " + cursor.getString(cursor.getColumnIndex(DB.GET_DATE_MAP_NAME)));

            String[] menuItems = getResources().getStringArray(R.array.context_menu);
            for(int i=0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0: // Edit
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                break;

            case 1: // Delete
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Cursor c = (SQLiteCursor) match_list.getItemAtPosition(info.position);
                db.del_game(c.getColumnIndex(DB.GET_DATE_GAME_ID));

                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                //db.del_game(match_list.);

                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        maps = new ArrayList<MapList>();
        MapList.initData(maps);

        // подключаемся к БД
        db = new DB(this);
        db.open();

        cursor = db.getDate();
        startManagingCursor(cursor);
        DB.logCursor(cursor);

        // формируем столбцы сопоставления
        //String[] from = new String[] { "match_id", "_id", "team_1", "team_2", "score_1", "score_2", "map_id", "name_map" };
        String[] from = new String[] { DB.GET_DATE_MATCH_ID, DB.GET_DATE_GAME_ID, DB.GET_DATE_TEAM_1, DB.GET_DATE_TEAM_2, DB.GET_DATE_SCORE_1, DB.GET_DATE_SCORE_2, DB.GET_DATE_MAP_ID, DB.GET_DATE_MAP_NAME};
        int[] to = new int[] { R.id.tv_id_match, R.id.tv_id_game, R.id.tv_name_team1, R.id.tv_name_team2, R.id.tv_score_team1, R.id.tv_score_team2, R.id.map_img, R.id.tv_name_map};

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.match_item, cursor, from, to);
        scAdapter.setViewBinder(new MyViewBinder());
        //scAdapter.notifyDataSetChanged();

        match_list = (ListView) findViewById(R.id.lv_matchs);
        registerForContextMenu(match_list);
        match_list.setAdapter(scAdapter);

        match_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), view.toString() + ", pos=" + position + ", id=" + id, Toast.LENGTH_SHORT).show();
                visibleTextView(view);
            }
        });
    }

    void visibleTextView(View v) {
        if(((TextView)v.findViewById(R.id.tv_id_game)).getVisibility()==View.VISIBLE) {
            ((TextView)v.findViewById(R.id.tv_id_game)).setVisibility(View.GONE);
            ((TextView)v.findViewById(R.id.tv_id_match)).setVisibility(View.GONE);
            ((TextView)v.findViewById(R.id.tv_name_map)).setVisibility(View.GONE);
            ((RelativeLayout) v.findViewById(R.id.customViewRect)).setVisibility(RelativeLayout.GONE);
        } else {
            ((TextView)v.findViewById(R.id.tv_id_game)).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.tv_id_match)).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.tv_name_map)).setVisibility(View.VISIBLE);
            ((RelativeLayout) v.findViewById(R.id.customViewRect)).setVisibility(RelativeLayout.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cursor.requery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_match:
                Intent intent = new Intent(getApplicationContext(), AddMatchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyViewBinder implements SimpleCursorAdapter.ViewBinder {
        int win = getResources().getColor(R.color.color_win);
        int lose = getResources().getColor(R.color.color_lose);
        int draw = getResources().getColor(R.color.color_draw);

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

            switch (view.getId()) {
                case R.id.tv_score_team1:
                    if(cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_1)) > cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_2))) {
                        ((TextView) view).setTextColor(win);
                    } else if(cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_1)) < cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_2))) {
                        ((TextView) view).setTextColor(lose);
                    } else {
                        ((TextView) view).setTextColor(draw);
                    }
                    break;

                case R.id.tv_score_team2:
                    if(cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_1)) > cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_2))) {
                        ((TextView) view).setTextColor(lose);
                    } else if(cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_1)) < cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_SCORE_2))) {
                        ((TextView) view).setTextColor(win);
                    } else {
                        ((TextView) view).setTextColor(draw);
                    }

                    break;

                case R.id.map_img:
                    int map = cursor.getInt(cursor.getColumnIndex(DB.GET_DATE_MAP_ID));
                    ((ImageView) view).setImageDrawable(getResources().getDrawable(maps.get(map - 1).getImgId()));
                    return true;
            }

            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // закрытие соединения с БД
        db.close();
    }
}
