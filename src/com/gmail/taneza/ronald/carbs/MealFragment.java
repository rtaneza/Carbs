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
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

public class MealFragment extends ListFragment {

	public final static int DEFAULT_WEIGHT_IN_GRAMS = 100;

	public final static String LANGUAGE_MESSAGE = "com.gmail.taneza.ronald.carbs.LANGUAGE_MESSAGE";
	public final static String FOOD_ITEM_MESSAGE = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_MESSAGE";
	public final static String FOOD_ITEM_RESULT = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_RESULT";
	
	public final static String STATE_LANGUAGE_KEY = "LANGUAGE";
	public final static String STATE_TOTAL_CARBS_KEY = "STATE_TOTAL_CARBS_KEY";
	
	private View mRootView;
	private TextView mTotalCarbsTextView;
	
	private Language mLanguage;
	private float mTotalCarbsInGrams;

	// This constructor is called from MainActivity.
	public MealFragment(Language language) {
		mLanguage = language;
		mTotalCarbsInGrams = 0;
	}
	
	// This constructor is called from the Fragment base class during
	// an orientation change. It needs an empty constructor.
	// We then restore the state from the savedInstanceState bundle in onCreate().
	public MealFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
    	Log.i("MealFragment", String.format("onCreate: savedInstanceState = %s", 
    			savedInstanceState != null ? savedInstanceState.toString() : "null"));
    	
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
            // Restore last state
			// This is called after an orientation change.
			mLanguage = Language.values()[savedInstanceState.getInt(STATE_LANGUAGE_KEY)];
			mTotalCarbsInGrams = savedInstanceState.getFloat(STATE_TOTAL_CARBS_KEY);
        }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		Log.i("MealFragment", "onCreateView");
		
		mRootView = inflater.inflate(R.layout.fragment_meal, container, false);

// 		mTotalCarbsTextView = (TextView) mRootView.findViewById(R.id.meal_total_carbs_text);
// 		updateTotalCarbs();
// 		
// 		Button clearButton = (Button)mRootView.findViewById(R.id.meal_clear_total_carbs_button);
 		//clearButton.setOnClickListener(this);
         
        initListAdapter();
         
        return mRootView;
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putInt(STATE_LANGUAGE_KEY, mLanguage.ordinal());
        outState.putFloat(STATE_TOTAL_CARBS_KEY, mTotalCarbsInGrams);
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
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == Activity.RESULT_OK) {
    		FoodItem foodItem = data.getParcelableExtra(FOOD_ITEM_RESULT);
    		mTotalCarbsInGrams += foodItem.getNumCarbsInGrams();
    		updateTotalCarbs();
        }
    }
    
    public void setLanguage(Language language) {
    	mLanguage = language;
    	initListAdapter();
    }
    
	private void initListAdapter() {
	    String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
	        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
	        "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
	        "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
	        "Android", "iPhone", "WindowsMobile" };

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
	        R.layout.meal_item, R.id.meal_item_name, list);
	    setListAdapter(adapter);
        
        restartLoader();
	}
	
	private void restartLoader() {
		//getLoaderManager().restartLoader(0, null, this);
	}
	
	private void updateTotalCarbs() {
		mTotalCarbsTextView.setText(String.format("%.2f", mTotalCarbsInGrams));
	}

//	@Override
//	public void onClick(View view) {
//        switch (view.getId()) {
//        	case R.id.food_list_clear_total_carbs_button:
//        		mTotalCarbsInGrams = 0;
//        		updateTotalCarbs();
//        		break;
//        }
//	}
	
}
