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
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;

public class MyFoodsEditableFragment extends ListFragment 
    implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private SimpleCursorAdapter mCursorAdapter;
	private FoodDbAdapter mFoodDbAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_foods, container, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mFoodDbAdapter = ((CarbsApp)getActivity().getApplication()).getFoodDbAdapter();
		
		ClearableEditText searchEditText = (ClearableEditText) getActivity().findViewById(R.id.my_foods_search_text);
        addSearchTextListener(searchEditText);
        
        initListAdapter();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {		
		ClearableEditText searchEditText = (ClearableEditText) getActivity().findViewById(R.id.my_foods_search_text);
		String searchText = searchEditText.getText().toString();
		String queryString = mFoodDbAdapter.getQueryStringMyFoods(searchText);
		return new SQLiteCursorLoader(getActivity(), mFoodDbAdapter, queryString, null);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mCursorAdapter.swapCursor(null);		
	}
	
	public void addNewFood() {
		FoodItem foodItem = new FoodItem(FoodDbAdapter.MYFOODS_TABLE_NAME, 0, MyFoodDetailsActivity.NEW_FOOD_DEFAULT_WEIGHT);
    	startMyFoodDetailsActivity(foodItem, MyFoodDetailsActivity.Mode.NewFood);
	}
	
    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
    	SQLiteCursor cursor = (SQLiteCursor)l.getItemAtPosition(position);

    	FoodItem foodItem = new FoodItem(
				cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_TABLE_NAME)),
				cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_ID)),
				cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_WEIGHT_PER_UNIT)));
    	
    	startMyFoodDetailsActivity(foodItem, MyFoodDetailsActivity.Mode.EditFood);
    }
    
    private void startMyFoodDetailsActivity(FoodItem foodItem, MyFoodDetailsActivity.Mode mode) {
    	Intent intent = new Intent(getActivity(), MyFoodDetailsActivity.class);
    	intent.putExtra(MyFoodDetailsActivity.MY_FOOD_ITEM_MESSAGE, (Parcelable)foodItem);
    	intent.putExtra(MyFoodDetailsActivity.MY_FOOD_ACTIVITY_MODE_MESSAGE, mode.ordinal());
    	
    	startActivityForResult(intent, 0);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == MyFoodDetailsActivity.MY_FOOD_RESULT_OK) {
    		FoodItem foodItem = data.getParcelableExtra(MyFoodDetailsActivity.MY_FOOD_ITEM_RESULT);
    		//mMainActivityNotifier.addFoodItemToMeal(foodItem);
        }
	}
    
	private void initListAdapter() {
		String[] from = { FoodDbAdapter.MYFOODS_COLUMN_NAME, FoodDbAdapter.COLUMN_TABLE_NAME, FoodDbAdapter.MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT };
        int[] to = new int[] { R.id.food_item_name, R.id.food_item_name_extra, R.id.food_item_carbs };
        
        // Create an empty adapter we will use to display the loaded data.
        mCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.food_item, null, from, to, 0);
        
        // We set the view binder for the adapter to our own FoodItemViewBinder.
        mCursorAdapter.setViewBinder(new FoodItemViewBinder(
        		FoodDbAdapter.MYFOODS_COLUMN_WEIGHT_PER_UNIT, FoodDbAdapter.MYFOODS_COLUMN_UNIT_TEXT));
        
        setListAdapter(mCursorAdapter);
        
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
}
