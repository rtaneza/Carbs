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

package com.gmail.taneza.ronald.carbs;

import java.util.ArrayList;

import android.widget.Filter;

public class FoodItemFilter extends Filter {

	FoodItemBaseArrayAdapter mAdapter;
	
	public FoodItemFilter(FoodItemBaseArrayAdapter adapter) {
		mAdapter = adapter;
    }
	 
	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults result = new FilterResults();
        String searchText = constraint.toString().toLowerCase();
        
        if (searchText.trim().length() == 0) {
            result.values = mAdapter.mValues;
            result.count = mAdapter.mValues.size();
            
        } else {
        	final ArrayList<FoodItem> originalList = new ArrayList<FoodItem>(mAdapter.mValues);
            final ArrayList<FoodItem> filteredList = new ArrayList<FoodItem>();
            
            int count = originalList.size();
            for (int i = 0; i < count; i++) {
                final FoodItem foodItem = originalList.get(i);
                if (mAdapter.getFoodName(foodItem).toLowerCase().contains(searchText)) {
                	filteredList.add(foodItem);
                }
            }
            
            result.values = filteredList;
            result.count = filteredList.size();
        }
        
        return result;
	}

	@SuppressWarnings("unchecked")
	@Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
    	mAdapter.setFilteredValues((ArrayList<FoodItem>)results.values);
    }
}
