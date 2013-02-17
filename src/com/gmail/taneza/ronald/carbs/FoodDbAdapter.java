package com.gmail.taneza.ronald.carbs;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class FoodDbAdapter extends SQLiteAssetHelper {
	
	public static final String KEY_NAME = "Product_omschrijving";
	public static final String KEY_CARBS = "_05001";

    private static final String DATABASE_NAME = "NevoFood";
    private static final int DATABASE_VERSION = 1;
    
    private SQLiteDatabase db;
    
    public FoodDbAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	//deleteDbIfItAlreadyExists(context);
    }
    
    /** For development:
    private void deleteDbIfItAlreadyExists(Context context) {
		String dbName = "/data/data/" + context.getPackageName() + "/databases/" + DATABASE_NAME;
		File f = new File(dbName);
		if (f.exists()) {
			f.delete();			
		}
	}
    */
    
	public void open() {
    	db = getReadableDatabase();
    }

    public Cursor getAllFood() {
    	return queryDb(null);
    }    
    
    public Cursor getFoodWithName(String name) {
    	String whereClause = KEY_NAME + " like '%" + name + "%'";
		return queryDb(whereClause.toString());
	}
    
    private Cursor queryDb(String whereClause) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		// Cursor requires an "_id" column
		// todo: find out what "0" really means
		String [] sqlSelect = {"0 _id", KEY_NAME, KEY_CARBS}; 
		String sqlTables = "Nutrients";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, whereClause, null,
				null, null, null);

		if (c != null) {
			c.moveToFirst();
		}
		
		return c;
	}    
}
