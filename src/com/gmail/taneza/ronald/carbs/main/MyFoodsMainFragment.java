/*
 * Copyright (C) 2013 Ronald Ta�eza
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

import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;

public class MyFoodsMainFragment extends BaseFoodDbListFragment { 

	public MyFoodsMainFragment() {
		mWeightPerUnitColumnName = FoodDbAdapter.MYFOODS_COLUMN_WEIGHT_PER_UNIT;
		mUnitTextColumnName = FoodDbAdapter.MYFOODS_COLUMN_UNIT_TEXT;
		mCarbsColumnName = FoodDbAdapter.MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my_foods, container, false);
	}
	
	@Override
	protected String getQueryString(String searchText) {
		return mFoodDbAdapter.getQueryStringMyFoods(searchText);
	}
	
	@Override
	protected FoodItem createFoodItemFromCursor(SQLiteCursor cursor) {
		return new FoodItem(
				cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_TABLE_NAME)),
				cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_ID)),
				cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_WEIGHT_PER_UNIT)));
	}

	@Override
	protected String getFoodNameColumnName() {
		return FoodDbAdapter.MYFOODS_COLUMN_NAME;
	}
}
