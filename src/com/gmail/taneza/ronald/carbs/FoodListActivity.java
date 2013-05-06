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

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Loader;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.app.LoaderManager;

public class FoodListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	private FoodDbAdapter mDbHelper;
    private SimpleCursorAdapter mAdapter;
	private EditText mSearchEditText;

	private static final int LOADER_ID_ALL_FOOD = 0;
	private static final int LOADER_ID_SEARCH_TEXT = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_list);
		
        mDbHelper = new FoodDbAdapter(this);
        mDbHelper.open();
        
        addSearchTextListener();

        String[] from = new String[] { FoodDbAdapter.KEY_DUTCH_NAME, FoodDbAdapter.KEY_CARBS };
        int[] to = new int[] { R.id.name, R.id.carbs_amount};
        
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(this,
        		R.layout.food_row, null, from, to, 0);
        setListAdapter(mAdapter);

        initLoader(LOADER_ID_ALL_FOOD);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void initLoader(int id) {
		getLoaderManager().restartLoader(id, null, this);
	}
	
	private void addSearchTextListener() {
		mSearchEditText = (EditText) findViewById(R.id.search_text);
		mSearchEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// Abstract Method of TextWatcher Interface.
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Abstract Method of TextWatcher Interface.
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				initLoader(LOADER_ID_SEARCH_TEXT);
			}
		});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String queryString = null;
		
		switch (id) {
		case LOADER_ID_ALL_FOOD:
			queryString = mDbHelper.getQueryStringAllFood();
			break;
		case LOADER_ID_SEARCH_TEXT:
			queryString = mDbHelper.getQueryStringFoodWithName(mSearchEditText.getText().toString().trim());
			break;
		}
		
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
}
