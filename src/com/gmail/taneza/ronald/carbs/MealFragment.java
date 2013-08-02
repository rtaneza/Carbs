/*
 * Copyright (C) 2013 Ronald Ta�eza
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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MealFragment extends ListFragment {

	public final static String LANGUAGE_MESSAGE = "com.gmail.taneza.ronald.carbs.LANGUAGE_MESSAGE";
	public final static String FOOD_ITEM_MESSAGE = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_MESSAGE";
	public final static String FOOD_ITEM_RESULT = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_RESULT";

	private MainActivityNotifier mMainActivityNotifier;
	private View mRootView;
	//TODO: change to ArrayAdapter<FoodItem>
	private ArrayAdapter<String> mArrayAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
	    try {
	    	mMainActivityNotifier = (MainActivityNotifier) activity;
	    } catch(ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement MainActivityNotifier");
	    }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		
		mRootView = inflater.inflate(R.layout.fragment_meal, container, false);

		ArrayList<FoodItem> foodItemList = mMainActivityNotifier.getFoodItemList();
		ArrayList<String> foodItemNames = new ArrayList<String>();
    	for (FoodItem foodItem: foodItemList) {
    		foodItemNames.add(getFoodName(foodItem));
    	}
    	
		mArrayAdapter = new ArrayAdapter<String>(getActivity(),
		        R.layout.meal_item, R.id.meal_item_name, foodItemNames);
		setListAdapter(mArrayAdapter);
         
        return mRootView;
	}
	
    private String getFoodName(FoodItem foodItem) {
    	if (mMainActivityNotifier.getLanguage() == Language.ENGLISH) {
    		return foodItem.mEnglishName;
    	} else {
    		return foodItem.mDutchName;
    	}
    }
	
	public void addFood(FoodItem foodItem) {
		mArrayAdapter.add(getFoodName(foodItem));
	}
	
	public void clearMeal() {
		mArrayAdapter.clear();
	}
	
	public void setLanguage(Language language) {
		mArrayAdapter.clear();
		
		ArrayList<FoodItem> foodItemList = mMainActivityNotifier.getFoodItemList();
    	for (FoodItem foodItem: foodItemList) {
    		mArrayAdapter.add(getFoodName(foodItem));
    	}
    }
	
    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
//    	SQLiteCursor cursor = (SQLiteCursor)l.getItemAtPosition(position);
//
//    	FoodItem foodItem = new FoodItem(
//    			cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_ENGLISH_NAME)),
//    			cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_DUTCH_NAME)),
//    			DEFAULT_WEIGHT_IN_GRAMS,
//    			cursor.getFloat(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_CARBS)),
//    			cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_PRODUCT_CODE)));
//    	
//    	Intent intent = new Intent(getActivity(), FoodDetailsActivity.class);
//    	intent.putExtra(LANGUAGE_MESSAGE, mLanguage);
//    	intent.putExtra(FOOD_ITEM_MESSAGE, foodItem);
//
//    	startActivityForResult(intent, 0);
    }
}
