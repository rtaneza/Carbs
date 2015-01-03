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

import org.droidparts.widget.ClearableEditText;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;

public abstract class BaseFoodDbListFragment extends BaseListFragment 
    implements LoaderManager.LoaderCallbacks<Cursor> {
    
    protected SimpleCursorAdapter mCursorAdapter;

    protected abstract String getQueryString(String searchText);
    protected abstract FoodItem createFoodItemFromCursor(SQLiteCursor cursor);
    protected abstract String getFoodNameColumnName();
    public abstract String getQuantityPerUnitColumnName();
    public abstract String getUnitTextColumnName();
    public abstract String getCarbsColumnName();
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        //Log.i("Carbs", this.getClass().getSimpleName() + " onActivityCreated");
        
        ClearableEditText searchEditText = (ClearableEditText) getActivity().findViewById(R.id.search_text);
        addSearchTextListener(searchEditText);
        
        initListAdapter();
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {        
        ClearableEditText searchEditText = (ClearableEditText) getActivity().findViewById(R.id.search_text);
        String searchText = searchEditText.getText().toString();
        String queryString = getQueryString(searchText);
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
    
    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
        SQLiteCursor cursor = (SQLiteCursor)l.getItemAtPosition(position);
        FoodItem foodItem = createFoodItemFromCursor(cursor);
        mMainActivityNotifier.startActivityToAddFoodToMeal(foodItem);
    }
    
    public void refreshList() {
        initListAdapter();
    }
    
    private void initListAdapter() {
        String[] from = { getFoodNameColumnName(), FoodDbAdapter.COLUMN_TABLE_NAME, getCarbsColumnName() };
        int[] to = new int[] { R.id.food_item_name, R.id.food_item_name_extra, R.id.food_item_carbs };
        
        // Create an empty adapter we will use to display the loaded data.
        // Use custom adapter so that we have the same background color for all lists.
        mCursorAdapter = new FoodItemCursorAdapter(getActivity(), R.layout.food_item, null, from, to, 0);
        
        // We set the view binder for the adapter to our own FoodItemViewBinder.
        mCursorAdapter.setViewBinder(new FoodItemViewBinder(getQuantityPerUnitColumnName(), getUnitTextColumnName()));
        
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
