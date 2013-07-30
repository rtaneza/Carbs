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

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.app.LoaderManager;
//todo
//import android.support.v7.app.ActionBar;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.gmail.taneza.ronald.carbs.FoodDbAdapter.Language;

import org.droidparts.widget.ClearableEditText;

// TODOS
// create separate activity for menu list
// create recent food list
// save last used weight per food

public class FoodListActivity extends ListActivity 
    implements LoaderManager.LoaderCallbacks<Cursor> {

	public final static int DEFAULT_WEIGHT_IN_GRAMS = 100;

	public final static String PREF_LANGUAGE = "PREF_LANGUAGE";
	
	public final static String FOOD_ITEM_MESSAGE = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_MESSAGE";
	public final static String FOOD_ITEM_RESULT = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_RESULT";
	
	private FoodDbAdapter mDbHelper;
    private SimpleCursorAdapter mAdapter;
	private ClearableEditText mSearchEditText;
	private float mTotalCarbsInGrams = 0;
	private TextView mTotalCarbsTextView;
	private Menu mMenu;
	private Language mLanguage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_list);

		mTotalCarbsTextView = (TextView) findViewById(R.id.food_list_total_carbs_text);
		UpdateTotalCarbs();

        mDbHelper = new FoodDbAdapter(this);
        mDbHelper.open();
        
		// Restore preferences
		SharedPreferences settings = getPreferences(0);
		mLanguage = Language.values()[settings.getInt(PREF_LANGUAGE, Language.DUTCH.ordinal())];
        setLanguage();
        
        addSearchTextListener();

        initListAdapter();
	}
	
	@Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getPreferences(0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putInt(PREF_LANGUAGE, mLanguage.ordinal());

      // Commit the edits!
      editor.commit();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		mMenu = menu;
		getMenuInflater().inflate(R.menu.main_actions, menu);
		setLanguage();
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.menu_language_option_english:
    			mLanguage = Language.ENGLISH;
	        	break;
	        case R.id.menu_language_option_dutch:
    			mLanguage = Language.DUTCH;
	        	break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }

	    if (mDbHelper.getLanguage() != mLanguage) {
	    	setLanguage();
	    	initListAdapter();
	    }
        return true;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String queryString = mDbHelper.getFoodNameQueryString(mSearchEditText.getText().toString().trim());		
		return new SQLiteCursorLoader(this, mDbHelper, queryString, null);
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
    			cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_DUTCH_NAME)),
    			cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_ENGLISH_NAME)),
    			DEFAULT_WEIGHT_IN_GRAMS,
    			cursor.getFloat(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_CARBS)),
    			cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_PRODUCT_CODE)));
    	
    	Intent intent = new Intent(this, FoodDetailsActivity.class);
    	intent.putExtra(FOOD_ITEM_MESSAGE, foodItem);

    	startActivityForResult(intent, 0);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
    		FoodItem foodItem = data.getParcelableExtra(FoodListActivity.FOOD_ITEM_RESULT);
    		mTotalCarbsInGrams += foodItem.getNumCarbsInGrams();
    		UpdateTotalCarbs();
        }
    }
    
    public void ClearTotalCarbs(View v) {
    	mTotalCarbsInGrams = 0;
		UpdateTotalCarbs();
    }

	private void initListAdapter() {
        String[] from = mDbHelper.getColumnStringArray();
        int[] to = new int[] { R.id.name, R.id.carbs_amount};
        
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(this,
        		R.layout.food_row, null, from, to, 0);
        setListAdapter(mAdapter);
        
        restartLoader();
	}
	
	private void restartLoader() {
		getLoaderManager().restartLoader(0, null, this);
	}
	
	private void addSearchTextListener() {
		mSearchEditText = (ClearableEditText) findViewById(R.id.search_text);
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
	
	private void setLanguage() {
    	mDbHelper.setLanguage(mLanguage);

		if (mMenu != null) {
			int languageId = (mLanguage == Language.ENGLISH) ?
					R.string.menu_language_english : R.string.menu_language_dutch;
			MenuItem languageMenuItem = mMenu.findItem(R.id.menu_language);
	    	languageMenuItem.setTitle(languageId);
		}
	}
	
	private void UpdateTotalCarbs() {
		mTotalCarbsTextView.setText(String.format("%.2f", mTotalCarbsInGrams));
	}
}
