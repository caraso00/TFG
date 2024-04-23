package com.example.tfg.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

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