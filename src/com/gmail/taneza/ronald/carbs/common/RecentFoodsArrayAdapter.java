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

import android.content.Context;

import com.gmail.taneza.ronald.carbs.R;

// TODO: Currently not used. Add date info and use this from RecentFoodsFragment.

public class RecentFoodsArrayAdapter extends FoodItemBaseArrayAdapter {
	public RecentFoodsArrayAdapter(Context context, FoodDbAdapter foodDbAdapter, ArrayList<FoodItem> values) {
	    super(context, foodDbAdapter, R.layout.fragment_recent_foods, values);
	}
    
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//	    LayoutInflater inflater = (LayoutInflater) mContext
//	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	    View rowView = inflater.inflate(R.layout.meal_item, parent, false);
//	    
//	    FoodItem foodItem = getItem(position);
//	    TextView nameTextView = (TextView) rowView.findViewById(R.id.meal_item_name);
//	    String foodNameAndWeight = String.format("%s (%d g)", foodItem.getName(), foodItem.getWeight());
//	    nameTextView.setText(foodNameAndWeight);
//
//	    TextView carbsTextView = (TextView) rowView.findViewById(R.id.meal_item_carbs);
//	    carbsTextView.setText(String.format("%.1f", foodItem.getNumCarbsInGrams()));
//
//	    return rowView;
//	}
}
