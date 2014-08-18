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

package com.gmail.taneza.ronald.carbs.myfoods;

import java.util.ArrayList;

import org.droidparts.widget.ClearableEditText;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.CarbsApp;
import com.gmail.taneza.ronald.carbs.common.CustomSimpleCursorAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;
import com.gmail.taneza.ronald.carbs.common.FoodItemInfo;
import com.gmail.taneza.ronald.carbs.common.FoodItemViewBinder;

public class MyFoodsEditableFragment extends ListFragment 
    implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private ClearableEditText mSearchEditText;
	
	private MyFoodsActivityNotifier mMyFoodsActivityNotifier;
	private CustomSimpleCursorAdapter mCursorAdapter;
	private FoodDbAdapter mFoodDbAdapter;

	private ActionMode mActionMode;
	private OnItemClickListener mOnItemClickListenerDefault;
	private OnItemClickListener mOnItemClickListenerActionMode;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);		
	    try {
	    	mMyFoodsActivityNotifier = (MyFoodsActivityNotifier) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement MyFoodsActivityNotifier");
	    }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_foods, container, false);
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mFoodDbAdapter = ((CarbsApp)getActivity().getApplication()).getFoodDbAdapter();
		
		mSearchEditText = (ClearableEditText) getActivity().findViewById(R.id.my_foods_search_text);
        addSearchTextListener(mSearchEditText);
        
        initListAdapter();

		mOnItemClickListenerDefault = getListView().getOnItemClickListener();
		
		mOnItemClickListenerActionMode = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onItemClickInActionMode(parent, view, position, id);
			}
		};
			
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					mCursorAdapter.clearSelection();
		    		
					mActionMode = getActivity().startActionMode(new ActionBarCallBack());
					onItemClickInActionMode(parent, view, position, id);
					
					getListView().setOnItemClickListener(mOnItemClickListenerActionMode);
					return true;
				}
			});
	}
	
	private void onItemClickInActionMode(AdapterView<?> parent, View view, int position, long id) {
		mCursorAdapter.toggleSelection(position);
		
        String selectedText = getResources().getString(R.string.selected);
        mActionMode.setTitle(String.format("%d %s", mCursorAdapter.getNumSelected(), selectedText));
	}

	@Override
	public void onResume() {
		super.onResume();
		
		// If the Carbs app is already open with the MyFoods activity visible, 
		// then the user opens another app and shares / imports MyFoods data to the Carbs app,
		// Android seems to open another instance of the Carbs MyFoods activity.
		// After the data is successfully imported, and the user goes back 
		// to the Carbs app that was already running before the import,
		// then the newly-imported MyFoods data is not shown.
		// So we refresh the the food list every time this activity is resumed.
		refreshMyFoods();
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
		FoodItem foodItem = new FoodItem(FoodDbAdapter.MYFOODS_TABLE_NAME, 0, MyFoodDetailsActivity.NEW_FOOD_DEFAULT_WEIGHT_PER_UNIT);
    	startMyFoodDetailsActivity(foodItem, MyFoodDetailsActivity.Mode.NewFood);
	}

	public void clearMyFoods() {
		new AlertDialog.Builder(getActivity())
		.setTitle(R.string.clear_my_foods_question)
	    .setMessage(R.string.clear_my_foods_confirmation)
	    .setPositiveButton(R.string.clear_my_foods_do_clear, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	    		mFoodDbAdapter.removeAllMyFoods();
	    		restartLoader();
	    		mMyFoodsActivityNotifier.setItemRemoved();
	        }
	     })
	    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .show();
	}
	
	public void refreshMyFoods() {
		restartLoader();
	}

    @Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
    	FoodItem foodItem = getFoodItemAtPosition(l, position);
    	startMyFoodDetailsActivity(foodItem, MyFoodDetailsActivity.Mode.EditFood);
    }

    private FoodItem getFoodItemAtPosition(ListView l, int position) {
    	SQLiteCursor cursor = (SQLiteCursor)l.getItemAtPosition(position);

    	return new FoodItem(
				cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_TABLE_NAME)),
				cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_ID)),
				cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_WEIGHT_PER_UNIT)));
    }
    
    private void startMyFoodDetailsActivity(FoodItem foodItem, MyFoodDetailsActivity.Mode mode) {
    	Intent intent = new Intent(getActivity(), MyFoodDetailsActivity.class);
    	intent.putExtra(MyFoodDetailsActivity.MY_FOOD_ITEM_MESSAGE, (Parcelable)foodItem);
    	intent.putExtra(MyFoodDetailsActivity.MY_FOOD_ACTIVITY_MODE_MESSAGE, mode.ordinal());
    	
    	startActivityForResult(intent, mode.ordinal());
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (resultCode) {
    		case MyFoodDetailsActivity.MY_FOOD_RESULT_OK:
    			FoodItemInfo foodItemInfo = data.getParcelableExtra(MyFoodDetailsActivity.MY_FOOD_ITEM_INFO_RESULT);
	    		if (requestCode == MyFoodDetailsActivity.Mode.NewFood.ordinal()) {
	    			mFoodDbAdapter.addMyFoodItemInfo(foodItemInfo);
	    		} else {
	    			mFoodDbAdapter.updateMyFoodItemInfo(foodItemInfo);
	    		}
	    		restartLoader();
	        	mMyFoodsActivityNotifier.setItemChanged();
	        	break;
	        	
    		case MyFoodDetailsActivity.MY_FOOD_RESULT_REMOVE:
    			FoodItem foodItem = data.getParcelableExtra(MyFoodDetailsActivity.MY_FOOD_ITEM_RESULT);
    			mFoodDbAdapter.removeMyFoodItem(foodItem);
	    		restartLoader();
	        	mMyFoodsActivityNotifier.setItemRemoved();
    			break;
    	}
	}
    
	private void initListAdapter() {
		String[] from = { FoodDbAdapter.MYFOODS_COLUMN_NAME, FoodDbAdapter.COLUMN_TABLE_NAME, FoodDbAdapter.MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT };
        int[] to = new int[] { R.id.food_item_name, R.id.food_item_name_extra, R.id.food_item_carbs };
        
        // Create an empty adapter we will use to display the loaded data.
        mCursorAdapter = new CustomSimpleCursorAdapter(getActivity(), R.layout.food_item, null, from, to, 0);
        
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
	
    private class ActionBarCallBack implements ActionMode.Callback {
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	        case R.id.menu_food_list_remove:
	        	new AlertDialog.Builder(getActivity())
	    	    .setMessage(R.string.remove_selected_items)
	    	    .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
	    	        public void onClick(DialogInterface dialog, int which) { 
	    	            // continue with remove
	    	        	removeFromList(mCursorAdapter.getSelection());
	                    mActionMode.finish();
	
	    	        	Toast.makeText(getActivity().getApplicationContext(),
	    	        			getText(R.string.items_removed),
	    	        			Toast.LENGTH_SHORT)
	    	        		 .show();
	    	        }
	    	     })
	    	    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	    	        public void onClick(DialogInterface dialog, int which) { 
	    	            // do nothing
	    	        }
	    	     })
	    	    .show();            	
	            return true;
	            
	        default:
	            return false;
	        }
	    }
	    
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	    	setRemoveItemsMode(true);
	    	
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.menu_food_list_action_bar, menu);
	        return true;
	    }
	
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    	mCursorAdapter.clearSelection();
			getListView().setOnItemClickListener(mOnItemClickListenerDefault);
	
			setRemoveItemsMode(false);
	    }
	
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false;
	    }
    }
    
    private void setRemoveItemsMode(boolean enable) {
    	mSearchEditText.setEnabled(!enable);
    }

	private void removeFromList(SparseBooleanArray selection) {
		ArrayList<FoodItem> itemsToRemove = new ArrayList<FoodItem>();
		for (int i = 0; i < selection.size(); i++) {
			itemsToRemove.add(getFoodItemAtPosition(getListView(), selection.keyAt(i)));
		}
		
		mFoodDbAdapter.removeMyFoodItems(itemsToRemove);
		restartLoader();
		mMyFoodsActivityNotifier.setItemRemoved();
	}
}
