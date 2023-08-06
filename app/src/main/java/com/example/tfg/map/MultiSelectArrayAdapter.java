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

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(items.get(position));
        return convertView;
    }

    public boolean[] getCheckedItemPositions() {
        return itemChecked;
    }

    public void toggleChecked(int position) {
        itemChecked[position] = !itemChecked[position];
        notifyDataSetChanged();
    }

    public String[] getItemsArray() {
        return items.toArray(new String[0]);
    }
}