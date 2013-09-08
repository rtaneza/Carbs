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

import java.util.ArrayList;

import org.droidparts.widget.ClearableEditText;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;
import com.gmail.taneza.ronald.carbs.common.FoodItemBaseArrayAdapter;

public abstract class BaseFoodListFragment extends BaseListFragment {

	protected ClearableEditText mSearchEditText;
	protected FoodItemBaseArrayAdapter mFoodItemArrayAdapter;

	protected abstract ArrayList<FoodItem> getFoodList();
	protected abstract void startActivityToAddOrEditFood(FoodItem foodItem, int foodItemIndex);
	protected abstract FoodItemBaseArrayAdapter createFoodItemArrayAdapter(Context context, FoodDbAdapter foodDbAdapter, ArrayList<FoodItem> values);
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mSearchEditText = (ClearableEditText) getActivity().findViewById(R.id.search_text);
        addSearchTextListener(mSearchEditText);

		ArrayList<FoodItem> recentFoodsList = getFoodList();
		mFoodItemArrayAdapter = createFoodItemArrayAdapter(getActivity(), mFoodDbAdapter, recentFoodsList);
		setListAdapter(mFoodItemArrayAdapter);
	}
	
	@Override
	public void onStart() {
		// This is called when the fragment is visible to the user.
		super.onStart();
		filterListBasedOnSearchText();
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
    	startActivityToAddOrEditFood(foodItem, position);
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
