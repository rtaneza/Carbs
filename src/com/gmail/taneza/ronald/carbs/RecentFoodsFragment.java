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

import org.droidparts.widget.ClearableEditText;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RecentFoodsFragment extends BaseListFragment {

	private View mRootView;
	protected ClearableEditText mSearchEditText;
	private FoodItemArrayAdapter mFoodItemArrayAdapter;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_recent_foods, container, false);
		return mRootView;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mSearchEditText = (ClearableEditText) getActivity().findViewById(R.id.search_text);
        addSearchTextListener(mSearchEditText);

		ArrayList<FoodItem> recentFoodsList = mMainActivityNotifier.getRecentFoodsList();
    	mFoodItemArrayAdapter = new FoodItemArrayAdapter(getActivity(), recentFoodsList, mMainActivityNotifier.getLanguage());
		setListAdapter(mFoodItemArrayAdapter);
	}
	
	@Override
	public void onStart() {
		// This is called when the fragment is visible to the user.
		super.onStart();		
		filterListBasedOnSearchText();
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

    	// use the requestCode arg for the list position
    	startActivityForResult(intent, position);
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
        		int index = requestCode;
	    		mMainActivityNotifier.removeFoodItemFromRecentFoods(index);
	    		break;
        }
    }

	private void addSearchTextListener(ClearableEditText searchEditText) {
		
		searchEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterListBasedOnSearchText();
			}
		});
		
		// This listener is called when the Enter key is pressed
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// Ignore the Enter key because we already do the processing every time the text changes
				return true;
			}
		});
	}
	
    private void filterListBasedOnSearchText() {
		mFoodItemArrayAdapter.getFilter().filter(mSearchEditText.getText().toString());
    }
}
