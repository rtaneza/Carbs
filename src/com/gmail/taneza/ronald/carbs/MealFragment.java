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
	
	private View mRootView;
	private TextView mTotalCarbsTextView;
	
	private float mTotalCarbsInGrams;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		
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
}
