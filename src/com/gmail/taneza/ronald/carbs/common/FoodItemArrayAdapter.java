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
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.taneza.ronald.carbs.R;

public class FoodItemArrayAdapter extends FoodItemBaseArrayAdapter {
    
    public FoodItemArrayAdapter(Context context, FoodDbAdapter foodDbAdapter, ArrayList<FoodItem> values) {
        super(context, foodDbAdapter, R.layout.fragment_meal, values);
    }
    
    @Override
    public View getRowView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.meal_item, parent, false);
        
        final FoodItem foodItem = getItem(position);
        final FoodItemInfo foodItemInfo = mFoodDbAdapter.getFoodItemInfo(foodItem);
        
        TextView nameTextView = (TextView) rowView.findViewById(R.id.meal_item_name);
        nameTextView.setText(foodItemInfo.getName());

        String foodType = "";
        if (foodItemInfo.getTableName().equals(FoodDbAdapter.MYFOODS_TABLE_NAME)) {
            foodType = String.format(Locale.getDefault(), "%s ", FoodItemInfo.MY_FOOD_TEXT);
        }
        
        TextView nameExtraTextView = (TextView) rowView.findViewById(R.id.meal_item_name_extra);
        String foodTypeAndWeight = String.format(Locale.getDefault(), "%s(%d %s)", 
                foodType, foodItemInfo.getWeight(), foodItemInfo.getUnitText());
        nameExtraTextView.setText(foodTypeAndWeight);
        
        TextView carbsTextView = (TextView) rowView.findViewById(R.id.meal_item_carbs);
        carbsTextView.setText(String.format(Locale.getDefault(), "%.1f", 
                foodItemInfo.getNumCarbsInGrams()));

        return rowView;
    }
}
