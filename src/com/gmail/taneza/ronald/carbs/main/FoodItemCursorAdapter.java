package com.gmail.taneza.ronald.carbs.main;

import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.CarbsApp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

public class FoodItemCursorAdapter extends SimpleCursorAdapter {
    private SparseBooleanArray mSelection;
    private int mNumSelected;

    public FoodItemCursorAdapter(Context context, int layout, Cursor c,
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

        if (isPositionSelected(position)) {
            v.setBackgroundResource(R.color.list_background_selected);
        } else {
            v.setBackgroundResource(R.color.list_background_default);
        }
        
        return v;
    }
}
