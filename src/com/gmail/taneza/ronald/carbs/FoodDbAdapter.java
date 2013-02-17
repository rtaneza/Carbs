package com.gmail.taneza.ronald.carbs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class FoodDbAdapter extends SQLiteAssetHelper {
	
	public static final String KEY_NAME = "Product_omschrijving";
	public static final String KEY_CARBS = "_05001";

    private static final String DATABASE_NAME = "NevoFood";
    //todo: move back to version 1 and find way to delete other development versions
    private static final int DATABASE_VERSION = 3;
    
    public FoodDbAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }

    public Cursor getAllFood() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		// todo: find out what "0" really means
		String [] sqlSelect = {"0 _id", KEY_NAME, KEY_CARBS}; 
		String sqlTables = "Nutrients";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null,
				null, null, null);

		c.moveToFirst();
		return c;
	}
}
