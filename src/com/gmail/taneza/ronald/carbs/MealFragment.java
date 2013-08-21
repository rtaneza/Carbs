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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MealFragment extends BaseListFragment {

	public final static String LANGUAGE_MESSAGE = "com.gmail.taneza.ronald.carbs.LANGUAGE_MESSAGE";
	public final static String FOOD_ITEM_MESSAGE = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_MESSAGE";
	public final static String FOOD_ITEM_RESULT = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_RESULT";

	private FoodItemArrayAdapter mFoodItemArrayAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_meal, container, false);
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    
		ArrayList<FoodItem> foodItemList = mMainActivityNotifier.getFoodItemsList();
		mFoodItemArrayAdapter = new FoodItemArrayAdapter(getActivity(), mFoodDbAdapter, foodItemList);
		setListAdapter(mFoodItemArrayAdapter);
	}
	
	public void setFoodItemList(ArrayList<FoodItem> foodItemList) {
		if (mFoodItemArrayAdapter != null) {
			// During an orientation change, the MainActivity onCreate() calls setFoodItemList()
			// _before_ onCreateView(), so mFoodItemArrayAdapter is still null
			mFoodItemArrayAdapter.setValues(foodItemList);
		}
	}
		
	public void refreshList() {
		if (mFoodItemArrayAdapter != null) {
			mFoodItemArrayAdapter.notifyDataSetChanged();
		}
    }

    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
    	FoodItem foodItem = (FoodItem)l.getItemAtPosition(position);
    	mMainActivityNotifier.startActivityToEditFoodInMeal(foodItem, position);
    }
}
