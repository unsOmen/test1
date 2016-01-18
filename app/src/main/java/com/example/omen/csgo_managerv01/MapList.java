package com.example.omen.csgo_managerv01;

import java.util.ArrayList;

/**
 * Created by OmeN on 10.12.2015.
 */
public enum MapList {
    DE_DUST2(1, "Dust2", R.drawable.dust2),
    DE_TRAIN(2, "Train", R.drawable.train),
    DE_MIRAGE(3, "Mirage", R.drawable.mirage),
    DE_INFERNO(4, "Inferno", R.drawable.inferno),
    DE_CACHE(5, "Cache", R.drawable.cache),
    DE_OVERPASS(6, "Overpass", R.drawable.overpass),
    DE_COBBLESTONE(7, "Cobblestone", R.drawable.cobblestone);

    private final int _id;
    private String name;
    private int img;

    private MapList(int _id, String name, int img) {
        this._id = _id;
        this.name = name;
        this.img = img;
    }

    public int getImgId() {
        return img;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Object getMap() {
        return this;
    }

    public static void initData(ArrayList<MapList> list) {
        list.add(MapList.DE_DUST2);
        list.add(MapList.DE_TRAIN);
        list.add(MapList.DE_MIRAGE);
        list.add(MapList.DE_INFERNO);
        list.add(MapList.DE_CACHE);
        list.add(MapList.DE_OVERPASS);
        list.add(MapList.DE_COBBLESTONE);
    }
}
