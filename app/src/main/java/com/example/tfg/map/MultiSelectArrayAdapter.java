package com.example.tfg.map;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiSelectArrayAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> items;
    private boolean[] itemChecked;

    public MultiSelectArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.items = new ArrayList<>(Arrays.asList(objects));
        itemChecked = new boolean[items.size()];
    }
}