package com.gmail.taneza.ronald.carbs.main;

import android.database.sqlite.SQLiteCursor;
import com.gmail.taneza.ronald.carbs.common.FoodItem;

public interface FoodSearchAdapter {

	public String getFoodNameColumnName();
	public String getWeightPerUnitColumnName();
	public String getUnitTextColumnName();
	public String getCarbsColumnName();

	public String getQueryString(String searchText);
	public FoodItem createFoodItemFromCursor(SQLiteCursor cursor);
}
