package com.gmail.taneza.ronald.carbs;

import android.os.Bundle;
import android.app.ListActivity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {

	private FoodDbAdapter mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        mDbHelper = new FoodDbAdapter(this);
        fillData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.getAllFood();
        startManagingCursor(c);

        String[] from = new String[] { FoodDbAdapter.KEY_NAME, FoodDbAdapter.KEY_CARBS };
        int[] to = new int[] { R.id.name, R.id.carbs_amount};
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
            new SimpleCursorAdapter(this, R.layout.food_row, c, from, to);
        setListAdapter(notes);
    }
}
