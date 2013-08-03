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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class RecentFoodsFragment extends BaseListFragment {

	private View mRootView;
	private FoodItemArrayAdapter mFoodItemArrayAdapter;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_recent_foods, container, false);
	    
		ArrayList<FoodItem> recentFoodsList = mMainActivityNotifier.getRecentFoodsList();
    	mFoodItemArrayAdapter = new FoodItemArrayAdapter(getActivity(), recentFoodsList, mMainActivityNotifier.getLanguage());
		setListAdapter(mFoodItemArrayAdapter);
		
		return mRootView;
	}
	
	public void notifyFoodItemListChanged() {
		if (mFoodItemArrayAdapter != null) {
			// During an orientation change, the MainActivity onCreate() calls notifyFoodItemListChanged()
			// _before_ onCreateView(), so mFoodItemArrayAdapter is still null
			mFoodItemArrayAdapter.notifyDataSetChanged();
		}
	}
	
	public void setLanguage(Language language) {
		if (mFoodItemArrayAdapter != null) {
			mFoodItemArrayAdapter.setLanguage(language);
			notifyFoodItemListChanged();
		}
    }

    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
    	FoodItem foodItem = (FoodItem)l.getItemAtPosition(position);
    	
    	Intent intent = new Intent(getActivity(), FoodDetailsActivity.class);
    	intent.putExtra(FoodDetailsActivity.LANGUAGE_MESSAGE, mMainActivityNotifier.getLanguage());
    	intent.putExtra(FoodDetailsActivity.FOOD_ITEM_MESSAGE, (Parcelable)foodItem);
    	intent.putExtra(FoodDetailsActivity.ACTIVITY_MODE_MESSAGE, FoodDetailsActivity.Mode.RecentFood.ordinal());

    	startActivityForResult(intent, 0);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	FoodItem foodItem;
        switch (resultCode) {
        	case FoodDetailsActivity.FOOD_DETAILS_RESULT_OK:
        		foodItem = data.getParcelableExtra(FoodDetailsActivity.FOOD_ITEM_RESULT);
	    		mMainActivityNotifier.addFoodItemToMeal(foodItem);
	    		break;
	    		
        	case FoodDetailsActivity.FOOD_DETAILS_RESULT_REMOVE:
        		foodItem = data.getParcelableExtra(FoodDetailsActivity.FOOD_ITEM_RESULT);
	    		mMainActivityNotifier.removeFoodItemFromRecentFoods(foodItem);
	    		break;
        }
    }
}
