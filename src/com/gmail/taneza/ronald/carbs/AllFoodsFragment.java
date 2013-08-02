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

import org.droidparts.widget.ClearableEditText;

import android.app.Activity;
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
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

// TODOS
// create separate activity for menu list
// create recent food list
// save last used weight per food

public class AllFoodsFragment extends ListFragment 
    implements LoaderManager.LoaderCallbacks<Cursor> {

	public final static int DEFAULT_WEIGHT_IN_GRAMS = 100;

	public final static String LANGUAGE_MESSAGE = "com.gmail.taneza.ronald.carbs.LANGUAGE_MESSAGE";
	public final static String FOOD_ITEM_MESSAGE = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_MESSAGE";
	public final static String FOOD_ITEM_RESULT = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_RESULT";
	
	private MainActivityNotifier mMainActivityNotifier;
	private FoodDbAdapter mDbAdapter;
    private SimpleCursorAdapter mAdapter;
	private ClearableEditText mSearchEditText;

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
		Log.i("AllFoodsFragment", "onCreateView");

    	mDbAdapter = new FoodDbAdapter(getActivity(), mMainActivityNotifier.getLanguage());
    	mDbAdapter.open();
    	
		View rootView = inflater.inflate(R.layout.fragment_all_foods, container, false);

        addSearchTextListener(rootView);

        initListAdapter();
         
        return rootView;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String searchText = mSearchEditText.getText().toString();
		String queryString = mDbAdapter.getFoodNameQueryString(searchText);		
		return new SQLiteCursorLoader(getActivity(), mDbAdapter, queryString, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);		
	}
	
    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
    	SQLiteCursor cursor = (SQLiteCursor)l.getItemAtPosition(position);

    	FoodItem foodItem = new FoodItem(
    			cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_ENGLISH_NAME)),
    			cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_DUTCH_NAME)),
    			DEFAULT_WEIGHT_IN_GRAMS,
    			cursor.getFloat(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_CARBS)),
    			cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_PRODUCT_CODE)));
    	
    	Intent intent = new Intent(getActivity(), FoodDetailsActivity.class);
    	intent.putExtra(LANGUAGE_MESSAGE, mMainActivityNotifier.getLanguage());
    	intent.putExtra(FOOD_ITEM_MESSAGE, foodItem);

    	startActivityForResult(intent, 0);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == Activity.RESULT_OK) {
    		FoodItem foodItem = data.getParcelableExtra(FOOD_ITEM_RESULT);
    		mMainActivityNotifier.addFoodItemToMeal(foodItem);
        }
    }
    
    public void setLanguage(Language language) {
    	mDbAdapter.setLanguage(language);
    	initListAdapter();
    }
    
	private void initListAdapter() {
        String[] from = mDbAdapter.getColumnStringArray();
        int[] to = new int[] { R.id.food_item_name, R.id.food_item_carbs};
        
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(),
        		R.layout.food_item, null, from, to, 0);
        setListAdapter(mAdapter);
        
        restartLoader();
	}
	
	private void restartLoader() {
		getLoaderManager().restartLoader(0, null, this);
	}
	
	private void addSearchTextListener(View rootView) {
		mSearchEditText = (ClearableEditText) rootView.findViewById(R.id.search_text);
		mSearchEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				restartLoader();
			}
		});
	}
}
