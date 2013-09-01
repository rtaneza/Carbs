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

import android.widget.Filter;

public class FoodItemFilter extends Filter {

	private FoodItemBaseArrayAdapter mAdapter;
	private FoodDbAdapter mFoodDbAdapter;
	private ArrayList<FoodItem> mOriginalValues;
	
	public FoodItemFilter(FoodItemBaseArrayAdapter adapter, FoodDbAdapter foodDbAdapter, ArrayList<FoodItem> originalValues) {
		mAdapter = adapter;
		mFoodDbAdapter = foodDbAdapter;
		mOriginalValues = originalValues;
    }
	 
	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults results = new FilterResults();
        String searchText = constraint.toString().toLowerCase();
        
        if (searchText.trim().length() == 0) {
            results.values = mOriginalValues;
            results.count = mOriginalValues.size();
            
        } else {
        	final ArrayList<FoodItem> originalList = new ArrayList<FoodItem>(mOriginalValues);
            final ArrayList<FoodItem> filteredList = new ArrayList<FoodItem>();
            
            int count = originalList.size();
            for (int i = 0; i < count; i++) {
                final FoodItem foodItem = originalList.get(i);
                final FoodItemInfo foodItemInfo = mFoodDbAdapter.getFoodItemInfo(foodItem);
                if (foodItemInfo.getName().toLowerCase().contains(searchText)) {
                	filteredList.add(foodItem);
                }
            }
            
            results.values = filteredList;
            results.count = filteredList.size();
        }
        
        return results;
	}

	@SuppressWarnings("unchecked")
	@Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
    	mAdapter.setValues((ArrayList<FoodItem>)results.values);
    }
}
