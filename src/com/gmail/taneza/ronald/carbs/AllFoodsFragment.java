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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

// TODOS
// create separate activity for menu list
// create recent food list
// save last used weight per food

public class AllFoodsFragment extends ListFragment 
    implements LoaderManager.LoaderCallbacks<Cursor>, OnClickListener {

	public final static int DEFAULT_WEIGHT_IN_GRAMS = 100;

	public final static String LANGUAGE_MESSAGE = "com.gmail.taneza.ronald.carbs.LANGUAGE_MESSAGE";
	public final static String FOOD_ITEM_MESSAGE = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_MESSAGE";
	public final static String FOOD_ITEM_RESULT = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_RESULT";
	
	public final static String STATE_LANGUAGE_KEY = "LANGUAGE";
	public final static String STATE_TOTAL_CARBS_KEY = "STATE_TOTAL_CARBS_KEY";
	
	private FoodDbAdapter mDbAdapter;
    private SimpleCursorAdapter mAdapter;
	private ClearableEditText mSearchEditText;
	private TextView mTotalCarbsTextView;
	
	private Language mLanguage;
	private float mTotalCarbsInGrams;

	// This constructor is called from MainActivity.
	public AllFoodsFragment(Language language) {
		mLanguage = language;
		mTotalCarbsInGrams = 0;
	}
	
	// This constructor is called from the Fragment base class during
	// an orientation change. It needs an empty constructor.
	// We then restore the state from the savedInstanceState bundle in onCreate().
	public AllFoodsFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
    	Log.i("AllFoodsFragment", String.format("onCreate: savedInstanceState = %s", 
    			savedInstanceState != null ? savedInstanceState.toString() : "null"));
    	
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
            // Restore last state
			// This is called after an orientation change.
			mLanguage = Language.values()[savedInstanceState.getInt(STATE_LANGUAGE_KEY)];
			mTotalCarbsInGrams = savedInstanceState.getFloat(STATE_TOTAL_CARBS_KEY);
        }
		
    	mDbAdapter = new FoodDbAdapter(getActivity(), mLanguage);
    	mDbAdapter.open();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		Log.i("AllFoodsFragment", "onCreateView");
		
		View rootView = inflater.inflate(R.layout.fragment_all_foods, container, false);

 		mTotalCarbsTextView = (TextView) rootView.findViewById(R.id.food_list_total_carbs_text);
 		updateTotalCarbs();
 		
 		Button clearButton = (Button)rootView.findViewById(R.id.food_list_clear_total_carbs_button);
 		clearButton.setOnClickListener(this);
         
        addSearchTextListener(rootView);

        initListAdapter();
         
        return rootView;
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putInt(STATE_LANGUAGE_KEY, mLanguage.ordinal());
        outState.putFloat(STATE_TOTAL_CARBS_KEY, mTotalCarbsInGrams);
    }
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String queryString = mDbAdapter.getFoodNameQueryString(mSearchEditText.getText().toString().trim());		
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
    	intent.putExtra(LANGUAGE_MESSAGE, mLanguage);
    	intent.putExtra(FOOD_ITEM_MESSAGE, foodItem);

    	startActivityForResult(intent, 0);
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
    	mDbAdapter.setLanguage(mLanguage);
    	initListAdapter();
    }
    
	private void initListAdapter() {
        String[] from = mDbAdapter.getColumnStringArray();
        int[] to = new int[] { R.id.name, R.id.carbs_amount};
        
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(),
        		R.layout.food_row, null, from, to, 0);
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
	
	private void updateTotalCarbs() {
		mTotalCarbsTextView.setText(String.format("%.2f", mTotalCarbsInGrams));
	}

	@Override
	public void onClick(View view) {
        switch (view.getId()) {
        	case R.id.food_list_clear_total_carbs_button:
        		mTotalCarbsInGrams = 0;
        		updateTotalCarbs();
        		break;
        }
	}
}
