package com.gmail.taneza.ronald.carbs.main;

import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItemInfo;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;

public class FoodItemViewBinder implements ViewBinder {

    private String mTableName;
    private String mFoodNameColumnName;
    private String mQuantityPerUnitColumnName;
    private String mUnitTextColumnName;
    
    public FoodItemViewBinder(String tableName, String foodNameColumnName, String quantityPerUnitColumnName, String unitTextColumnName) {
        mTableName = tableName;
        mFoodNameColumnName = foodNameColumnName;
        mQuantityPerUnitColumnName = quantityPerUnitColumnName;
        mUnitTextColumnName = unitTextColumnName;
    }
    
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex(mFoodNameColumnName)) {
            TextView textView = (TextView)view;            
            String displayName = FoodDbAdapter.getDisplayName(cursor, mTableName, mFoodNameColumnName);
            textView.setText(displayName);            
            return true;
        }
        
        else if (columnIndex == cursor.getColumnIndex(FoodDbAdapter.COLUMN_TABLE_NAME)) {
            TextView textView = (TextView)view;
            
            String foodType = "";
            String tableName = cursor.getString(columnIndex);
            if (tableName.equals(FoodDbAdapter.MYFOODS_TABLE_NAME)) {
                foodType = String.format("%s ", FoodItemInfo.MY_FOOD_TEXT);
            }
            
            int quantityPerUnit = cursor.getInt(cursor.getColumnIndexOrThrow(mQuantityPerUnitColumnName));
            String unitText = cursor.getString(cursor.getColumnIndexOrThrow(mUnitTextColumnName));
            
            textView.setText(String.format("%s(%d %s)", foodType, quantityPerUnit, unitText));
            
            return true;
        }
        
        // For others, we simply return false so that the default binding is used.
        return false;
    }

}