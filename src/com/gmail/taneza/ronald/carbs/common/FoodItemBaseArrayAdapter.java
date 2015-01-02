/*
 * Copyright (C) 2013 Ronald Tañeza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.taneza.ronald.carbs.common;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public abstract class FoodItemBaseArrayAdapter extends ArrayAdapter<FoodItem> {
    protected final LayoutInflater mInflater;
    protected final FoodDbAdapter mFoodDbAdapter;
    private final ArrayList<FoodItem> mOriginalValues;
    private ArrayList<FoodItem> mFilteredValues;
    private Filter mFilter;
    private Context mContext;
    private SparseBooleanArray mSelection;
    private int mNumSelected = 0;
      
    public FoodItemBaseArrayAdapter(Context context, FoodDbAdapter foodDbAdapter, int layoutResourceId, ArrayList<FoodItem> values) {
        super(context, layoutResourceId);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFoodDbAdapter = foodDbAdapter;
        mOriginalValues = values;
        mContext = context;
        mSelection = new SparseBooleanArray();
        
        setValues(values);
    }
    
    public void setValues(ArrayList<FoodItem> values) {
        mFilteredValues = new ArrayList<FoodItem>(values);
        clear();
        // addAll() is only available starting in API level 11, but we want to support older API versions
        for (FoodItem item : mFilteredValues) {
            add(item);
        }
        
        clearSelection();
    }
    
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new FoodItemFilter(this, mFoodDbAdapter, mOriginalValues);
        }
        return mFilter;
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
        View v = getRowView(position, convertView, parent);

        //TODO: make this theme-independent
        v.setBackgroundColor(mContext.getResources().getColor(android.R.color.background_light)); //default color        
        if (isPositionSelected(position)) {
            v.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red
        }
        
        return v;
    }
    
    abstract View getRowView(int position, View convertView, ViewGroup parent);
}
