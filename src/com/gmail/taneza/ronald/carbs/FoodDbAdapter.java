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

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class FoodDbAdapter extends SQLiteAssetHelper {
	
	public enum Language {
	   ENGLISH,
	   DUTCH
	}
	
	public static final String TABLE_NAME = "Food";
	public static final String KEY_DUTCH_NAME = "Product_omschrijving";
	public static final String KEY_ENGLISH_NAME = "EnglishName";
	public static final String KEY_CARBS = "_05001";
	
    private static final String DATABASE_NAME = "NevoFoodListWithEnglishNames";
    private static final int DATABASE_VERSION = 1;
    
    private Language language;
    
    public FoodDbAdapter(Context context, Language language) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	deleteDbIfItAlreadyExists(context);
    	setLanguage(language);
    }
    
	public void open() {
    	getReadableDatabase();
    }
	
    public String getFoodNameQueryString(String foodName) {
    	String whereClause = getLanguageString() + " like '%" + foodName + "%'";
		return getQueryString(whereClause.toString());
	}
    
    public String getQueryString(String whereClause) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		// Cursor requires an "_id" column
		// todo: find out what "0" really means
		String [] sqlSelect = {"0 _id", getLanguageString(), KEY_CARBS}; 
		String sqlTables = TABLE_NAME;

		qb.setTables(sqlTables);
		return qb.buildQuery(sqlSelect, whereClause, null,
				null, getLanguageString(), null);
    }
    
    public void setLanguage(Language language) {
    	this.language = language;
    }
    
    public Language getLanguage() {
    	return language;
    }
    
    public String[] getColumnStringArray() {
    	return new String[] { getLanguageString(), FoodDbAdapter.KEY_CARBS };
    }
    
    private void deleteDbIfItAlreadyExists(Context context) {
		String dbName = context.getFilesDir().getPath() + context.getPackageName() + "/databases/" + DATABASE_NAME;
		File f = new File(dbName);
		if (f.exists()) {
			f.delete();			
		}
	}
    
    private String getLanguageString()
    {
    	switch (language) {
    		case ENGLISH:
    			return KEY_ENGLISH_NAME;
    		case DUTCH:
    			return KEY_DUTCH_NAME;
    		default:
    			return null;
    	}
    }
}
