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

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

public abstract class BaseFoodListFragment extends BaseListFragment 
    implements LoaderManager.LoaderCallbacks<Cursor> {

	protected String mWeightPerUnitColumnName;
	protected String mCarbsColumnName;
	protected String mUnitTextColumnName;
	
	protected FoodDbAdapter mFoodDbAdapter;
	protected SimpleCursorAdapter mAdapter;
	protected ClearableEditText mSearchEditText;

	protected abstract String getQueryString(String searchText);
	protected abstract FoodItem createFoodItemFromCursor(SQLiteCursor cursor);
	protected abstract String getFoodNameColumnName();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
    	mFoodDbAdapter = mMainActivityNotifier.getFoodDbAdapter();
    	
		return inflater.inflate(R.layout.fragment_all_foods, container, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mSearchEditText = (ClearableEditText) getActivity().findViewById(R.id.search_text);
        addSearchTextListener(mSearchEditText);
        
        initListAdapter();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String searchText = mSearchEditText.getText().toString();
		String queryString = getQueryString(searchText);		
		return new SQLiteCursorLoader(getActivity(), mFoodDbAdapter, queryString, null);
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

    	FoodItem foodItem = createFoodItemFromCursor(cursor);
    	
    	Intent intent = new Intent(getActivity(), FoodDetailsActivity.class);
    	intent.putExtra(FoodDetailsActivity.LANGUAGE_MESSAGE, mMainActivityNotifier.getLanguage());
    	intent.putExtra(FoodDetailsActivity.FOOD_ITEM_MESSAGE, (Parcelable)foodItem);
    	intent.putExtra(FoodDetailsActivity.ACTIVITY_MODE_MESSAGE, FoodDetailsActivity.Mode.NewFood.ordinal());
    	
    	startActivityForResult(intent, 0);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == FoodDetailsActivity.FOOD_DETAILS_RESULT_OK) {
    		FoodItem foodItem = data.getParcelableExtra(FoodDetailsActivity.FOOD_ITEM_RESULT);
    		mMainActivityNotifier.addFoodItemToMeal(foodItem);
        }
    }
    
    public void setLanguage(Language language) {
    	mFoodDbAdapter.setLanguage(language);
    	initListAdapter();
    }
    
	private void initListAdapter() {
		String[] from = { getFoodNameColumnName(), FoodDbAdapter.COLUMN_TABLE_NAME, mCarbsColumnName };
        int[] to = new int[] { R.id.food_item_name, R.id.food_item_name_extra, R.id.food_item_carbs };
        
        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(),
        		R.layout.food_item, null, from, to, 0);
        
        // We set the view binder for the adapter to our own FoodItemViewBinder.
        mAdapter.setViewBinder(new FoodItemViewBinder());
        
        setListAdapter(mAdapter);
        
        restartLoader();
	}
	
	private void restartLoader() {
		getLoaderManager().restartLoader(0, null, this);
	}
	
	private void addSearchTextListener(ClearableEditText searchEditText) {
		
		searchEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				restartLoader();
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
	
	private class FoodItemViewBinder implements ViewBinder {

	    @Override
	    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
	        if (columnIndex == cursor.getColumnIndex(FoodDbAdapter.COLUMN_TABLE_NAME)) {
	        	TextView textView = (TextView)view;
	        	
	        	String foodType = "";
	        	String tableName = cursor.getString(columnIndex);
	        	if (tableName.equals(FoodDbAdapter.MYFOODS_TABLE_NAME)) {
	        		foodType = String.format("%s ", FoodItem.MY_FOOD_TEXT);
	        	}
	        	
	        	int weightPerUnit = cursor.getInt(cursor.getColumnIndexOrThrow(mWeightPerUnitColumnName));
	        	String unitText = cursor.getString(cursor.getColumnIndexOrThrow(mUnitTextColumnName));
	        	
	        	textView.setText(String.format("%s(%d %s)", foodType, weightPerUnit, unitText));
	        	
	        	return true;
	        }
	        
	        // For others, we simply return false so that the default binding
	        // happens.
	        return false;
	    }
	 
	}
}
