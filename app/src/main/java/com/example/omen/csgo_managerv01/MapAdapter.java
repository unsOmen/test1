package com.example.omen.csgo_managerv01;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by OmeN on 10.12.2015.
 */
public class MapAdapter extends ArrayAdapter {

    private Context cnt;
    private ArrayList<MapList> maps;

    public MapAdapter(Context context, int textViewResourceId, ArrayList<MapList> maps) {
        super(context, textViewResourceId);
        this.cnt = context;
        this.maps = maps;
    }

    @Override
    public int getCount() {
        return maps.size();
    }

    @Override
    public Object getItem(int position) {
        return maps.get(position).getMap();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(cnt);

        label.setGravity(Gravity.CENTER);
        label.setTypeface(label.getTypeface(), Typeface.BOLD);
        label.setTextColor(Color.WHITE);
        label.setBackgroundResource(maps.get(position).getImgId());
        label.setText(maps.get(position).getName());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(cnt);

        label.setGravity(Gravity.CENTER);
        label.setPadding(5,5,5,5);
        label.setTextSize(20);
        label.setText(maps.get(position).getName());

        return label;
    }
}
