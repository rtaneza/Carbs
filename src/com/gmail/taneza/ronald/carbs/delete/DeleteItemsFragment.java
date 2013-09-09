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

package com.gmail.taneza.ronald.carbs.delete;

import java.util.ArrayList;

import org.droidparts.widget.ClearableEditText;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.CarbsApp;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;
import com.gmail.taneza.ronald.carbs.common.FoodItemArrayAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItemBaseArrayAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItemInfo;
import com.gmail.taneza.ronald.carbs.common.FoodItemViewBinder;
import com.gmail.taneza.ronald.carbs.main.BaseListFragment;

public class DeleteItemsFragment extends ListFragment {

	private FoodDbAdapter mFoodDbAdapter;
    private ArrayList<FoodItem> mFoodItemsList;
	private DeleteItemsArrayAdapter mArrayAdapter;

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mFoodDbAdapter = ((CarbsApp)getActivity().getApplication()).getFoodDbAdapter();
		
		mFoodItemsList = new ArrayList<FoodItem>();
		mFoodItemsList.add(new FoodItem(FoodDbAdapter.NEVO_TABLE_NAME, 1, 100));
		mFoodItemsList.add(new FoodItem(FoodDbAdapter.NEVO_TABLE_NAME, 2, 234));
		
		mArrayAdapter = new DeleteItemsArrayAdapter(getActivity(), mFoodDbAdapter, R.layout.fragment_delete_items, mFoodItemsList);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		setListAdapter(mArrayAdapter);
	}
	
    @Override 
    public void onListItemClick(ListView listView, View view, int position, long id) {
    	FoodItem foodItem = (FoodItem) listView.getItemAtPosition(position);
    	foodItem.toggleChecked();
    	
    	CheckBox checkBox = (CheckBox) view.findViewById(R.id.delete_item_checkbox);
    	checkBox.setChecked(foodItem.getChecked());
    }
}
