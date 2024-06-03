package com.example.tfg.map;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiSelectArrayAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final ArrayList<String> items;
    private final boolean[] itemChecked;

    public MultiSelectArrayAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.items = objects;
        itemChecked = new boolean[items.size()];
    }
}