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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FoodItemArrayAdapter extends FoodItemBaseArrayAdapter {
	public FoodItemArrayAdapter(Context context, ArrayList<FoodItem> values, Language language) {
	    super(context, R.layout.fragment_meal, values, language);
	}
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) mContext
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.meal_item, parent, false);
	    
	    //TODO: why does this happen sometimes?
	    if (position >= mFilteredValues.size()) {
	    	return rowView;
	    }
	    
	    FoodItem foodItem = mFilteredValues.get(position);
	    TextView nameTextView = (TextView) rowView.findViewById(R.id.meal_item_name);
	    nameTextView.setText(getFoodName(foodItem));

    	String foodType = "";
    	if (foodItem.mTableName.equals(FoodDbAdapter.MYFOODS_TABLE_NAME)) {
    		foodType = String.format("%s ", FoodItem.MY_FOOD_TEXT);
    	}
    	
	    TextView nameExtraTextView = (TextView) rowView.findViewById(R.id.meal_item_name_extra);
	    String foodTypeAndWeight = String.format("%s(%d %s)", foodType, foodItem.mWeight, foodItem.mUnitText);
	    nameExtraTextView.setText(foodTypeAndWeight);
	    
	    TextView carbsTextView = (TextView) rowView.findViewById(R.id.meal_item_carbs);
	    carbsTextView.setText(String.format("%.1f", foodItem.getNumCarbsInGrams()));

	    return rowView;
	}
}
