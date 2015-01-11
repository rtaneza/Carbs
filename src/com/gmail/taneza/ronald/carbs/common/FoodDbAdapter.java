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

package com.gmail.taneza.ronald.carbs.common;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class FoodDbAdapter extends SQLiteAssetHelper {
    public static final String COLUMN_TABLE_NAME = "TableName";

    public static final String NEVO_TABLE_NAME = "Nevo";
    public static final String NEVO_COLUMN_DUTCH_NAME = "Product_omschrijving";
    public static final String NEVO_COLUMN_ENGLISH_NAME = "EnglishName";
    public static final String NEVO_COLUMN_QUANTITY_PER_UNIT = "Hoeveelheid";
    public static final String NEVO_COLUMN_UNIT_TEXT = "Meeteenheid";
    public static final String NEVO_COLUMN_CARBS_GRAMS_PER_UNIT = "_05001";
    public static final String NEVO_COLUMN_PRODUCT_CODE = "Productcode";
    public static final String NEVO_COLUMN_MANUFACTURER_NAME = "Fabrikantnaam";

    public static final String MYFOODS_TABLE_NAME = "MyFoods";
    public static final String MYFOODS_COLUMN_NAME = "Name";
    public static final String MYFOODS_COLUMN_QUANTITY_PER_UNIT = "QuantityPerUnit";
    public static final String MYFOODS_COLUMN_UNIT_TEXT = "UnitText";
    public static final String MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT = "CarbsGramsPerUnit";
    public static final String MYFOODS_COLUMN_ID = "Id";
    
    private static final String DATABASE_NAME = "CarbsFoods";
    private static final int DATABASE_VERSION = 1;
    
    private Language mLanguage;
    private SQLiteDatabase mDatabase;
    
    private HashMap<FoodItem, FoodItemInfo> mFoodItemCache;
    
    public FoodDbAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Enable the following line only during development. It's intended to replace an existing db on the device, if there's a new db with the same version as the previous one.
        //deleteDbIfItAlreadyExists(context);
        
        mFoodItemCache = new HashMap<FoodItem, FoodItemInfo>();
    }
    
    public void open() {
        mDatabase = getWritableDatabase();
    }

    private String getNevoWhereClause(String foodName) {
        String pattern = "'%" + foodName + "%'";
        String whereClause = String.format(Locale.US, "%s like %s OR %s like %s",
                getFoodNameColumnName(), pattern, NEVO_COLUMN_MANUFACTURER_NAME, pattern);
        return whereClause;
    }
    
    private String getMyFoodsWhereClause(String foodName) {
        return MYFOODS_COLUMN_NAME + " like '%" + foodName + "%'";
    }
    
    @SuppressWarnings("deprecation")
    public String getQueryStringAllFoods(String foodName) {
        // Cursor requires an "_id" column. 
        // Our db doesn't have it, so this will return a value of 0 for the _id column.
        String [] nevoSelectColumns = {"0 _id", COLUMN_TABLE_NAME, NEVO_COLUMN_DUTCH_NAME, NEVO_COLUMN_ENGLISH_NAME, 
                NEVO_COLUMN_MANUFACTURER_NAME,
                NEVO_COLUMN_QUANTITY_PER_UNIT, NEVO_COLUMN_UNIT_TEXT, 
                NEVO_COLUMN_CARBS_GRAMS_PER_UNIT, NEVO_COLUMN_PRODUCT_CODE}; 
        String nevoWhereClause = getNevoWhereClause(foodName);
        
        SQLiteQueryBuilder foodQb = new SQLiteQueryBuilder();
        foodQb.setTables(NEVO_TABLE_NAME);
        
        // This method was deprecated in API level 11, but we still would like to support older API versions.
        String foodSubQuery = foodQb.buildUnionSubQuery(COLUMN_TABLE_NAME, nevoSelectColumns, null, nevoSelectColumns.length, 
                NEVO_TABLE_NAME, nevoWhereClause, null, null, null);

        // Both select statements must have the same number of columns, because they are used in a union query.
        // So we repeat the MyFoods "Name" column here.
        // There is no manufacturer name column in MyFoods, so always return an empty string for it.
        String [] myFoodsSelectColumns = {"0 _id", COLUMN_TABLE_NAME, MYFOODS_COLUMN_NAME, MYFOODS_COLUMN_NAME, 
                String.format("\"\" AS %s", NEVO_COLUMN_MANUFACTURER_NAME), 
                MYFOODS_COLUMN_QUANTITY_PER_UNIT, MYFOODS_COLUMN_UNIT_TEXT, 
                MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT, MYFOODS_COLUMN_ID}; 
        String myFoodsWhereClause = getMyFoodsWhereClause(foodName);

        SQLiteQueryBuilder myFoodsQb = new SQLiteQueryBuilder();
        myFoodsQb.setTables(MYFOODS_TABLE_NAME);
        
        // This method was deprecated in API level 11, but we still would like to support older API versions.
        String myFoodsSubQuery = myFoodsQb.buildUnionSubQuery(COLUMN_TABLE_NAME, myFoodsSelectColumns, null, myFoodsSelectColumns.length, 
                MYFOODS_TABLE_NAME, myFoodsWhereClause, null, null, null);

        SQLiteQueryBuilder unionQb = new SQLiteQueryBuilder();
        String queryString = unionQb.buildUnionQuery(new String[] { foodSubQuery, myFoodsSubQuery }, getFoodNameColumnName(), null);
        
        //Log.i("Carbs", "Query string: " + queryString);
        return queryString;
    }

    @SuppressWarnings("deprecation")
    public String getQueryStringBuiltinFoods(String foodName) {
        String [] selectColumns = {"0 _id", COLUMN_TABLE_NAME, NEVO_COLUMN_DUTCH_NAME, NEVO_COLUMN_ENGLISH_NAME, 
                NEVO_COLUMN_MANUFACTURER_NAME,
                NEVO_COLUMN_QUANTITY_PER_UNIT, NEVO_COLUMN_UNIT_TEXT, 
                NEVO_COLUMN_CARBS_GRAMS_PER_UNIT, NEVO_COLUMN_PRODUCT_CODE}; 
        String whereClause = getNevoWhereClause(foodName);

        SQLiteQueryBuilder foodQb = new SQLiteQueryBuilder();
        foodQb.setTables(NEVO_TABLE_NAME);
        String queryString = foodQb.buildQuery(selectColumns, whereClause, null, null, null, getFoodNameColumnName(), null);
        
        //Log.i("Carbs", "BuiltinFoods query string: " + queryString);
        return queryString;
    }

    @SuppressWarnings("deprecation")
    public String getQueryStringMyFoods(String foodName) {
        // All sql queries in this class map the table name to COLUMN_TABLE_NAME.
        String [] selectColumns = {"0 _id", String.format("\"%s\" AS %s", MYFOODS_TABLE_NAME, COLUMN_TABLE_NAME),
                String.format("\"\" AS %s", NEVO_COLUMN_MANUFACTURER_NAME), 
                MYFOODS_COLUMN_NAME, MYFOODS_COLUMN_QUANTITY_PER_UNIT, MYFOODS_COLUMN_UNIT_TEXT, 
                MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT, MYFOODS_COLUMN_ID}; 
        String whereClause = getMyFoodsWhereClause(foodName);

        SQLiteQueryBuilder myFoodsQb = new SQLiteQueryBuilder();
        myFoodsQb.setTables(MYFOODS_TABLE_NAME);
        // This method was deprecated in API level 11, but we still would like to support older API versions.
        String queryString = myFoodsQb.buildQuery(selectColumns, whereClause, null, null, null, MYFOODS_COLUMN_NAME, null);
        
        //Log.i("Carbs", "MyFoods query string: " + queryString);
        return queryString;
    }
    
    public ArrayList<FoodItemInfo> getAllMyFoods() {
        String [] columns = {"0 _id", 
                MYFOODS_COLUMN_NAME, MYFOODS_COLUMN_QUANTITY_PER_UNIT, MYFOODS_COLUMN_UNIT_TEXT, 
                MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT, MYFOODS_COLUMN_ID};
        Cursor cursor = mDatabase.query(MYFOODS_TABLE_NAME, columns, null, null, null, null, MYFOODS_COLUMN_NAME);
        
        ArrayList<FoodItemInfo> list = new ArrayList<FoodItemInfo>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FoodItem foodItem = new FoodItem(
                    MYFOODS_TABLE_NAME,
                    cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_QUANTITY_PER_UNIT)));
            FoodItemInfo foodItemInfo = new FoodItemInfo(foodItem, 
                    cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_QUANTITY_PER_UNIT)),
                    cursor.getFloat(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FoodDbAdapter.MYFOODS_COLUMN_UNIT_TEXT)));
             
            list.add(foodItemInfo);
            cursor.moveToNext();
        }
        
        return list;
    }
    
    public void setLanguage(Language language) {
        mLanguage = language;
        mFoodItemCache.clear();
    }
    
    public String getFoodNameColumnName() {
        switch (mLanguage) {
            case ENGLISH:
                return NEVO_COLUMN_ENGLISH_NAME;
            case DUTCH:
                return NEVO_COLUMN_DUTCH_NAME;
            default:
                return null;
        }
    }
    
    public static String getDisplayName(Cursor cursor, String tableName, String foodNameColumnName) {
        if (tableName.equals(NEVO_TABLE_NAME)) {
            // Displayed name = name + (manufacturer name)
            String name = cursor.getString(cursor.getColumnIndexOrThrow(foodNameColumnName));
            String manufacturerName = cursor.getString(cursor.getColumnIndexOrThrow(NEVO_COLUMN_MANUFACTURER_NAME));
            StringBuilder displayName = new StringBuilder();
            displayName.append(name);
            if (!manufacturerName.trim().isEmpty()) {
                displayName.append(String.format(Locale.US, " (%s)", manufacturerName));
            }
            return displayName.toString();
        
        } else if (tableName.equals(MYFOODS_TABLE_NAME)) {
            return cursor.getString(cursor.getColumnIndexOrThrow(MYFOODS_COLUMN_NAME));
        
        } else {
            throw new InvalidParameterException(String.format(Locale.US, "Invalid tableName: %s", tableName));
        }
    }
    
    public FoodItemInfo getFoodItemInfo(FoodItem foodItem) {
        FoodItemInfo foodItemInfo;
        
        if (mFoodItemCache.containsKey(foodItem)) {
            foodItemInfo = mFoodItemCache.get(foodItem);
            //Log.i("Carbs", String.format("Get from cache [%d] %s - %d - %s", mFoodItemHashMap.size(), foodItem.getTableName(), foodItem.getId(), foodItemInfo.getName()));
            return foodItemInfo;
        }
        
        String tableName = foodItem.getTableName();
        
        if (tableName.equals(NEVO_TABLE_NAME)) {
            String foodNameColumnName = getFoodNameColumnName();
            String[] columns = { foodNameColumnName, NEVO_COLUMN_MANUFACTURER_NAME, NEVO_COLUMN_QUANTITY_PER_UNIT, 
                    NEVO_COLUMN_CARBS_GRAMS_PER_UNIT, NEVO_COLUMN_UNIT_TEXT };
            // All fields in the Nevo table are string by default, even some contain numeric data, 
            // so the Id has to be placed inside quotes.
            String selection = String.format(Locale.US, "%s = '%d'", NEVO_COLUMN_PRODUCT_CODE, foodItem.getId());
            Cursor cursor = mDatabase.query(tableName, columns, selection, null, null, null, foodNameColumnName);
            if (cursor.moveToFirst()) {
                String displayName = getDisplayName(cursor, tableName, foodNameColumnName);
                foodItemInfo = new FoodItemInfo(
                        foodItem,
                        displayName,
                        cursor.getInt(cursor.getColumnIndexOrThrow(NEVO_COLUMN_QUANTITY_PER_UNIT)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(NEVO_COLUMN_CARBS_GRAMS_PER_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NEVO_COLUMN_UNIT_TEXT)));
            } else {
                foodItemInfo = null;
            }
            
        } else if (tableName.equals(MYFOODS_TABLE_NAME)) {
            String[] columns = { MYFOODS_COLUMN_NAME, MYFOODS_COLUMN_QUANTITY_PER_UNIT, 
                    MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT, MYFOODS_COLUMN_UNIT_TEXT };
            String selection = String.format(Locale.US, "%s = %d", MYFOODS_COLUMN_ID, foodItem.getId());
            Cursor cursor = mDatabase.query(tableName, columns, selection, null, null, null, MYFOODS_COLUMN_NAME);
            if (cursor.moveToFirst()) {
                foodItemInfo = new FoodItemInfo(
                        foodItem,
                        cursor.getString(cursor.getColumnIndexOrThrow(MYFOODS_COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MYFOODS_COLUMN_QUANTITY_PER_UNIT)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MYFOODS_COLUMN_UNIT_TEXT)));
            } else {
                foodItemInfo = null;
            }
            
        } else {
            throw new InvalidParameterException(String.format(Locale.US, "Invalid tableName: %s", tableName));
        }

        if (foodItemInfo != null) {
            mFoodItemCache.put(foodItem, foodItemInfo);
            //Log.i("Carbs", String.format("Add to cache [%d] %s - %d - %s",  mFoodItemHashMap.size(), foodItem.getTableName(), foodItem.getId(), foodItemInfo.getName()));
        }
        
        return foodItemInfo;
    }

    public void addMyFoodItemInfo(FoodItemInfo foodItemInfo) {
        ContentValues values = new ContentValues();
        values.put(MYFOODS_COLUMN_NAME, foodItemInfo.getName());
        values.put(MYFOODS_COLUMN_QUANTITY_PER_UNIT, foodItemInfo.getQuantityPerUnit());
        values.put(MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT, foodItemInfo.getNumCarbsInGramsPerUnit());
        values.put(MYFOODS_COLUMN_UNIT_TEXT, foodItemInfo.getUnitText());
        
        mDatabase.insertOrThrow(MYFOODS_TABLE_NAME, null, values);
    }
    
    public void updateMyFoodItemInfo(FoodItemInfo foodItemInfo) {
        ContentValues values = new ContentValues();
        values.put(MYFOODS_COLUMN_ID, foodItemInfo.getFoodItem().getId());
        values.put(MYFOODS_COLUMN_NAME, foodItemInfo.getName());
        values.put(MYFOODS_COLUMN_QUANTITY_PER_UNIT, foodItemInfo.getQuantityPerUnit());
        values.put(MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT, foodItemInfo.getNumCarbsInGramsPerUnit());
        values.put(MYFOODS_COLUMN_UNIT_TEXT, foodItemInfo.getUnitText());
        
        mDatabase.replaceOrThrow(MYFOODS_TABLE_NAME, null, values);

        //Log.i("Carbs", String.format("Delete %d: %s", foodItemInfo.getFoodItem().getId(), foodItemInfo.getName()));
        deleteMyFoodItemFromCache(foodItemInfo.getFoodItem());
    }
    
    public void deleteMyFoodItem(FoodItem foodItem) {
        String whereClause = String.format("%s = %d", MYFOODS_COLUMN_ID, foodItem.getId());
        mDatabase.delete(MYFOODS_TABLE_NAME, whereClause, null);
        deleteMyFoodItemFromCache(foodItem);
    }

    public void deleteMyFoodItems(ArrayList<FoodItem> itemsToDelete) {
        
        if (itemsToDelete.size() <= 0) {
            return;
        }
        
        StringBuilder whereClause = new StringBuilder();
        
        for (int i = 0; i < itemsToDelete.size(); i++) {
            FoodItem foodItem = itemsToDelete.get(i);
            
            if (whereClause.length() > 0) {
                whereClause.append(" OR ");
            }
            whereClause.append(String.format("%s = %d", MYFOODS_COLUMN_ID, foodItem.getId()));
            
            deleteMyFoodItemFromCache(foodItem);
        }

        mDatabase.delete(MYFOODS_TABLE_NAME, whereClause.toString(), null);
    }
    
    public void deleteAllMyFoods() {
        mDatabase.delete(MYFOODS_TABLE_NAME, null, null);
        deleteAllMyFoodsFromCache();
    }

    // Returns true if MyFood table already contains item with 'foodName' and ID not equal to 'exceptId'
    public boolean myFoodNameExists(String foodName, int exceptId) {
        String[] columns = { MYFOODS_COLUMN_NAME, MYFOODS_COLUMN_ID };
        String selection = MYFOODS_COLUMN_NAME + " like '" + foodName + "'";
        
        Cursor cursor = mDatabase.query(MYFOODS_TABLE_NAME, columns, selection, null, null, null, MYFOODS_COLUMN_NAME);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MYFOODS_COLUMN_ID));
            if (id != exceptId) {
                return true;
            }
            cursor.moveToNext();
        }
        return false;
    }

    private void deleteMyFoodItemFromCache(FoodItem foodItem) {
        // Delete all FoodItem's that have the same TableName and ID, regardless of the quantity.
        Iterator<Entry<FoodItem, FoodItemInfo>> it = mFoodItemCache.entrySet().iterator();
        while (it.hasNext()) {
            FoodItem item = it.next().getKey();
            if ((item.getTableName().equals(foodItem.getTableName())) &&
                (item.getId() == foodItem.getId())) {
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
    }

    private void deleteAllMyFoodsFromCache() {
        Iterator<Entry<FoodItem, FoodItemInfo>> it = mFoodItemCache.entrySet().iterator();
        while (it.hasNext()) {
            FoodItem foodItem = it.next().getKey();
            if (foodItem.getTableName().equals(MYFOODS_TABLE_NAME)) {
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
    }

    private void deleteDbIfItAlreadyExists(Context context) {
        File db = context.getDatabasePath(DATABASE_NAME);
        if (db.exists()) {
            Log.i("Carbs", String.format("Deleted db %s", db.toString()));
            db.delete();            
        }
    }
}
