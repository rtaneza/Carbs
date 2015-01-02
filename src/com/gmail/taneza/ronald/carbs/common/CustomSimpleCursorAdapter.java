package com.gmail.taneza.ronald.carbs.common;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {
    private SparseBooleanArray mSelection;
    private int mNumSelected;

    public CustomSimpleCursorAdapter(Context context, int layout, Cursor c,
            String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mSelection = new SparseBooleanArray();
    }

    public void clearSelection() {
        mSelection.clear();
        mNumSelected = 0;
        notifyDataSetChanged();
    }
    
    public SparseBooleanArray getSelection() {
        return mSelection;
    }
    
    public int getNumSelected() {
        return mNumSelected;
    }

    public void toggleSelection(int position) {
        if (!isPositionSelected(position)) {
            setNewSelection(position);
        } else {
            deleteSelection(position);
        }
    }

    private void setNewSelection(int position) {
        mSelection.put(position, true);
        mNumSelected++;
        notifyDataSetChanged();
    }

    private void deleteSelection(int position) {
        mSelection.delete(position);
        mNumSelected--;
        notifyDataSetChanged();
    }

    private boolean isPositionSelected(int position) {
        return mSelection.get(position);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        //TODO: make this theme-independent
        v.setBackgroundColor(mContext.getResources().getColor(android.R.color.background_light)); //default color        
        if (isPositionSelected(position)) {
            v.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red
        }
        
        return v;
    }
}
