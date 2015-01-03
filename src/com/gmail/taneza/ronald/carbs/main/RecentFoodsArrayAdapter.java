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

package com.gmail.taneza.ronald.carbs.main;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;
import com.gmail.taneza.ronald.carbs.common.FoodItemInfo;

public class RecentFoodsArrayAdapter extends FoodItemBaseArrayAdapter {
    public RecentFoodsArrayAdapter(Context context, FoodDbAdapter foodDbAdapter, ArrayList<FoodItem> values) {
        super(context, foodDbAdapter, R.layout.fragment_recent_foods, values);
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
        
        Date dateAdded = foodItemInfo.getDateAdded();
        // This returns a dateFormat that depends on the locale.
        // For example: Sep 8, 2013 6:53 PM
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        String dateAndTimeAdded = dateFormat.format(dateAdded);
        
        TextView nameExtraTextView = (TextView) rowView.findViewById(R.id.meal_item_name_extra);
        String foodTypeAndWeight = String.format(Locale.getDefault(), "%s:  %s(%d %s)", dateAndTimeAdded, foodType, foodItemInfo.getWeight(), foodItemInfo.getUnitText());
        nameExtraTextView.setText(foodTypeAndWeight);
        
        TextView carbsTextView = (TextView) rowView.findViewById(R.id.meal_item_carbs);
        carbsTextView.setText(String.format(Locale.getDefault(), "%.1f", foodItemInfo.getNumCarbsInGrams()));

        return rowView;
    }
}
