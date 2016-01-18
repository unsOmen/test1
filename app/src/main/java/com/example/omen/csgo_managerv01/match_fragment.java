package com.example.omen.csgo_managerv01;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class match_fragment extends Fragment {

    MapAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_match, container, false);

        ArrayList<MapList> list = new ArrayList<MapList>();
        MapList.initData(list);

        adapter = new MapAdapter(getContext(), android.R.layout.simple_spinner_item, list);

        Spinner spinner = (Spinner) v.findViewById(R.id.map_list);
        spinner.setAdapter(adapter);

        return v;
    }
}
