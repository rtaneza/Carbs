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

import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;

public class FoodsFragment extends BaseFoodDbListFragment implements OnItemSelectedListener  { 

	public static final int SEARCH_ALL_FOODS = 0;
	public static final int SEARCH_BUILTIN_FOODS = 1;
	public static final int SEARCH_MY_FOODS = 2;
	
	private int mSearchOption = SEARCH_ALL_FOODS;
	private FoodSearchAdapter mFoodSearchAdapter;
	private FoodSearchAdapter mAllFoodsSearchAdapter;
	private FoodSearchAdapter mBuiltinFoodsSearchAdapter;
	private FoodSearchAdapter mMyFoodsSearchAdapter;
	
	public FoodsFragment() {
		mAllFoodsSearchAdapter = new AllFoodsSearchAdapter();
		mBuiltinFoodsSearchAdapter = new BuiltinFoodsSearchAdapter();
		mMyFoodsSearchAdapter = new MyFoodsSearchAdapter();
		updateFoodSearchAdapter();
	}
	
	private void updateFoodSearchAdapter() {
		switch (mSearchOption) {
			case SEARCH_ALL_FOODS:
				mFoodSearchAdapter = mAllFoodsSearchAdapter;
				break;
				
			case SEARCH_BUILTIN_FOODS:
				mFoodSearchAdapter = mBuiltinFoodsSearchAdapter;
				break;
				
			case SEARCH_MY_FOODS:
				mFoodSearchAdapter = mMyFoodsSearchAdapter;
				break;

			default:
        		throw new IllegalArgumentException("Invalid mSearchOption");
		}
	}

	@Override
	protected String getFoodNameColumnName() {
		return mFoodSearchAdapter.getFoodNameColumnName();
	}

	@Override
	public String getWeightPerUnitColumnName() {
		return mFoodSearchAdapter.getWeightPerUnitColumnName();
	}

	@Override
	public String getUnitTextColumnName() {
		return mFoodSearchAdapter.getUnitTextColumnName();
	}

	@Override
	public String getCarbsColumnName() {
		return mFoodSearchAdapter.getCarbsColumnName();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {

		Spinner searchOptionSpinner = (Spinner) getActivity().findViewById(R.id.search_option_spinner);
		searchOptionSpinner.setOnItemSelectedListener(this);
		mSearchOption = searchOptionSpinner.getSelectedItemPosition();

		return inflater.inflate(R.layout.fragment_all_foods, container, false);
	}
	
	@Override
	protected String getQueryString(String searchText) {
		return mFoodSearchAdapter.getQueryString(searchText);
	}

	@Override
	protected FoodItem createFoodItemFromCursor(SQLiteCursor cursor) {
		return mFoodSearchAdapter.createFoodItemFromCursor(cursor);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		mSearchOption = pos;
		updateFoodSearchAdapter();
		refreshList();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	private class AllFoodsSearchAdapter implements FoodSearchAdapter {
		@Override
		public String getFoodNameColumnName() {
			return mFoodDbAdapter.getFoodNameColumnName();
		}

		@Override
		public String getWeightPerUnitColumnName() {
			return FoodDbAdapter.NEVO_COLUMN_WEIGHT_PER_UNIT;
		}

		@Override
		public String getUnitTextColumnName() {
			return FoodDbAdapter.NEVO_COLUMN_UNIT_TEXT;
		}

		@Override
		public String getCarbsColumnName() {
			return FoodDbAdapter.NEVO_COLUMN_CARBS_GRAMS_PER_UNIT;
		}

		@Override
		public String getQueryString(String searchText) {
			return mFoodDbAdapter.getQueryStringAllFoods(searchText);
		}

		@Override
		public FoodItem createFoodItemFromCursor(SQLiteCursor cursor) {
			return new FoodItem(
					cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_TABLE_NAME)),
					cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.NEVO_COLUMN_PRODUCT_CODE)),
					cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.NEVO_COLUMN_WEIGHT_PER_UNIT)));
		}
	}

	private class BuiltinFoodsSearchAdapter implements FoodSearchAdapter {
		@Override
		public String getFoodNameColumnName() {
			return mFoodDbAdapter.getFoodNameColumnName();
		}

		@Override
		public String getWeightPerUnitColumnName() {
			return FoodDbAdapter.NEVO_COLUMN_WEIGHT_PER_UNIT;
		}

		@Override
		public String getUnitTextColumnName() {
			return FoodDbAdapter.NEVO_COLUMN_UNIT_TEXT;
		}

		@Override
		public String getCarbsColumnName() {
			return FoodDbAdapter.NEVO_COLUMN_CARBS_GRAMS_PER_UNIT;
		}

		@Override
		public String getQueryString(String searchText) {
			return mFoodDbAdapter.getQueryStringBuiltinFoods(searchText);
		}

		@Override
		public FoodItem createFoodItemFromCursor(SQLiteCursor cursor) {
			return new FoodItem(
					cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_TABLE_NAME)),
					cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.NEVO_COLUMN_PRODUCT_CODE)),
					cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.NEVO_COLUMN_WEIGHT_PER_UNIT)));
		}
	}

	private class MyFoodsSearchAdapter implements FoodSearchAdapter {
		@Override
		public String getFoodNameColumnName() {
			return FoodDbAdapter.MYFOODS_COLUMN_NAME;
		}

		@Override
		public String getWeightPerUnitColumnName() {
			return FoodDbAdapter.MYFOODS_COLUMN_WEIGHT_PER_UNIT;
		}

		@Override
		public String getUnitTextColumnName() {
			return FoodDbAdapter.MYFOODS_COLUMN_UNIT_TEXT;
		}

		@Override
		public String getCarbsColumnName() {
			return FoodDbAdapter.MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT;
		}

		@Override
		public String getQueryString(String searchText) {
			return mFoodDbAdapter.getQueryStringMyFoods(searchText);
		}

		@Override
		public FoodItem createFoodItemFromCursor(SQLiteCursor cursor) {
			return new FoodItem(
					cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_TABLE_NAME)),
					cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_ID)),
					cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_WEIGHT_PER_UNIT)));
		}
	}
	
}
