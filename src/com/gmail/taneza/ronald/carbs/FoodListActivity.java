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
import android.content.Loader;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.app.LoaderManager;
//todo
//import android.support.v7.app.ActionBar;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.gmail.taneza.ronald.carbs.FoodDbAdapter.Language;

import org.droidparts.widget.ClearableEditText;

// TODOS
// save the language setting
// show current language setting as icon on action bar (NL / EN)
// rename activity_main.xml to main_actions.xml

public class FoodListActivity extends ListActivity 
    implements LoaderManager.LoaderCallbacks<Cursor> {

	private FoodDbAdapter mDbHelper;
    private SimpleCursorAdapter mAdapter;
	private ClearableEditText mSearchEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_list);
		
        mDbHelper = new FoodDbAdapter(this, FoodDbAdapter.Language.DUTCH);
        mDbHelper.open();
        
        addSearchTextListener();

        initListAdapter();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Language newLanguage;
		
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.menu_language_english:
	        	newLanguage = Language.ENGLISH;
	        	break;
	        case R.id.menu_language_dutch:
	        	newLanguage = Language.DUTCH;
	        	break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    if (mDbHelper.getLanguage() != newLanguage) {
        	mDbHelper.setLanguage(newLanguage);
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
				// Abstract Method of TextWatcher Interface.
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Abstract Method of TextWatcher Interface.
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				restartLoader();
			}
		});
	}
}
