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

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class FoodDbAdapter extends SQLiteAssetHelper {
	public static final String COLUMN_TABLE_NAME = "TableName";

	public static final String NEVO_TABLE_NAME = "Food";
	public static final String NEVO_COLUMN_DUTCH_NAME = "Product_omschrijving";
	public static final String NEVO_COLUMN_ENGLISH_NAME = "EnglishName";
	public static final String NEVO_COLUMN_WEIGHT_PER_UNIT = "Hoeveelheid";
	public static final String NEVO_COLUMN_UNIT_TEXT = "Meeteenheid";
	public static final String NEVO_COLUMN_CARBS_GRAMS_PER_UNIT = "_05001";
	public static final String NEVO_COLUMN_PRODUCT_CODE = "Productcode";

	public static final String MYFOODS_TABLE_NAME = "MyFoods";
	public static final String MYFOODS_COLUMN_NAME = "Name";
	public static final String MYFOODS_COLUMN_WEIGHT_PER_UNIT = "WeightPerUnit";
	public static final String MYFOODS_COLUMN_UNIT_TEXT = "UnitText";
	public static final String MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT = "CarbsGramsPerUnit";
	public static final String MYFOODS_COLUMN_ID = "Id";
	
    private static final String DATABASE_NAME = "NevoFoodListWithEnglishNamesAndMyFoods";
    private static final int DATABASE_VERSION = 1;
    
    private Language mLanguage;
    
    public FoodDbAdapter(Context context, Language language) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        deleteDbIfItAlreadyExists(context);
    	mLanguage = language;
    }
    
	public void open() {
    	getReadableDatabase();
    }
	
    public String getQueryStringAllFoods(String foodName) {
		SQLiteQueryBuilder foodQb = new SQLiteQueryBuilder();

		// Cursor requires an "_id" column
		// todo: find out what "0" really means
		String [] foodSqlSelect = {"0 _id", COLUMN_TABLE_NAME, NEVO_COLUMN_DUTCH_NAME, NEVO_COLUMN_ENGLISH_NAME, 
				NEVO_COLUMN_WEIGHT_PER_UNIT, NEVO_COLUMN_UNIT_TEXT, 
				NEVO_COLUMN_CARBS_GRAMS_PER_UNIT, NEVO_COLUMN_PRODUCT_CODE}; 
		String foodWhereClause = getLanguageString() + " like '%" + foodName + "%'";
		
		foodQb.setTables(NEVO_TABLE_NAME);
		String foodSubQuery = foodQb.buildUnionSubQuery(COLUMN_TABLE_NAME, foodSqlSelect, null, foodSqlSelect.length, 
				NEVO_TABLE_NAME, foodWhereClause, null, null);
		
		SQLiteQueryBuilder myFoodsQb = new SQLiteQueryBuilder();
		// Both select statements must have the same number of columns, so we repeat the "Name" column here
		String [] myFoodsSqlSelect = {"0 _id", COLUMN_TABLE_NAME, MYFOODS_COLUMN_NAME, MYFOODS_COLUMN_NAME, 
				MYFOODS_COLUMN_WEIGHT_PER_UNIT, MYFOODS_COLUMN_UNIT_TEXT, 
				MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT, MYFOODS_COLUMN_ID}; 
		String myFoodsWhereClause = MYFOODS_COLUMN_NAME + " like '%" + foodName + "%'";
		
		myFoodsQb.setTables(MYFOODS_TABLE_NAME);
		String myFoodsSubQuery = myFoodsQb.buildUnionSubQuery(COLUMN_TABLE_NAME, myFoodsSqlSelect, null, myFoodsSqlSelect.length, 
				MYFOODS_TABLE_NAME, myFoodsWhereClause, null, null);

		SQLiteQueryBuilder unionQb = new SQLiteQueryBuilder();
		String queryString = unionQb.buildUnionQuery(new String[] { foodSubQuery, myFoodsSubQuery }, getLanguageString(), null);
		
		//Log.i("Carbs", "Query string: " + queryString);
		return queryString;
    }

    public String getQueryStringMyFoods(String foodName) {
		SQLiteQueryBuilder myFoodsQb = new SQLiteQueryBuilder();
		String [] myFoodsSqlSelect = {"0 _id", String.format("\"%s\" AS %s", MYFOODS_TABLE_NAME, COLUMN_TABLE_NAME),
				MYFOODS_COLUMN_NAME, MYFOODS_COLUMN_WEIGHT_PER_UNIT, MYFOODS_COLUMN_UNIT_TEXT, 
				MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT, MYFOODS_COLUMN_ID}; 
		String myFoodsWhereClause = MYFOODS_COLUMN_NAME + " like '%" + foodName + "%'";
		
		myFoodsQb.setTables(MYFOODS_TABLE_NAME);
		String queryString = myFoodsQb.buildQuery(myFoodsSqlSelect, myFoodsWhereClause, null, null, MYFOODS_COLUMN_NAME, null);
		
		//Log.i("Carbs", "MyFoods query string: " + queryString);
		return queryString;
    }
    
    public void setLanguage(Language language) {
    	mLanguage = language;
    }
    
    public String getFoodNameColumnName() {
    	return getLanguageString();
    }
    
    private void deleteDbIfItAlreadyExists(Context context) {
		File db = context.getDatabasePath(DATABASE_NAME);
		if (db.exists()) {
			Log.i("Carbs", String.format("Deleted db %s", db.toString()));
			db.delete();			
		}
	}
    
    private String getLanguageString()
    {
    	switch (mLanguage) {
    		case ENGLISH:
    			return NEVO_COLUMN_ENGLISH_NAME;
    		case DUTCH:
    			return NEVO_COLUMN_DUTCH_NAME;
    		default:
    			return null;
    	}
    }
}
