package com.gmail.taneza.ronald.carbs.main;

import android.database.sqlite.SQLiteCursor;
import com.gmail.taneza.ronald.carbs.common.FoodItem;
import com.gmail.taneza.ronald.carbs.common.SelectionClause;

public interface FoodSearchAdapter {

    public String getFoodNameColumnName();
    public String getQuantityPerUnitColumnName();
    public String getUnitTextColumnName();
    public String getCarbsColumnName();

    public SelectionClause getQueryClause(String searchText);
    public FoodItem createFoodItemFromCursor(SQLiteCursor cursor);
}
