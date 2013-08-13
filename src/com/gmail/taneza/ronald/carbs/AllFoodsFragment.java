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

import android.database.sqlite.SQLiteCursor;

public class AllFoodsFragment extends BaseFoodListFragment { 

	public AllFoodsFragment() {
		mWeightPerUnitColumnName = FoodDbAdapter.NEVO_COLUMN_WEIGHT_PER_UNIT;
		mCarbsColumnName = FoodDbAdapter.NEVO_COLUMN_CARBS_GRAMS_PER_UNIT;
		mUnitTextColumnName = FoodDbAdapter.NEVO_COLUMN_UNIT_TEXT;
	}
	
	@Override
	protected String getQueryString(String searchText) {
		return mMainActivityNotifier.getFoodDbAdapter().getQueryStringAllFoods(searchText);
	}

	@Override
	protected FoodItem createFoodItemFromCursor(SQLiteCursor cursor) {
		return new FoodItem(
				cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.COLUMN_TABLE_NAME)),
				cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.NEVO_COLUMN_PRODUCT_CODE)),
				cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.NEVO_COLUMN_WEIGHT_PER_UNIT)));
	}

	@Override
	protected String getFoodNameColumnName() {
		return mMainActivityNotifier.getFoodDbAdapter().getFoodNameColumnName();
	}
}
