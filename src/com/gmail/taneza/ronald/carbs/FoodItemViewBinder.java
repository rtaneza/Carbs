package com.gmail.taneza.ronald.carbs;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class FoodItemViewBinder implements ViewBinder {

	protected String mWeightPerUnitColumnName;
	protected String mUnitTextColumnName;
	
	public FoodItemViewBinder(String weightPerUnitColumnName, String unitTextColumnName) {
		mWeightPerUnitColumnName = weightPerUnitColumnName;
		mUnitTextColumnName = unitTextColumnName;
	}
	
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex(FoodDbAdapter.COLUMN_TABLE_NAME)) {
        	TextView textView = (TextView)view;
        	
        	String foodType = "";
        	String tableName = cursor.getString(columnIndex);
        	if (tableName.equals(FoodDbAdapter.MYFOODS_TABLE_NAME)) {
        		foodType = String.format("%s ", FoodItemInfo.MY_FOOD_TEXT);
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