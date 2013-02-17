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

import android.os.Bundle;
import android.app.ListActivity;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class FoodListActivity extends ListActivity {

	private FoodDbAdapter mDbHelper;
	private EditText searchEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_list);
        mDbHelper = new FoodDbAdapter(this);
        mDbHelper.open();
        fillFoodList();
        addSearchTextListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void fillFoodList() {
		// TODO: use another thread
        // Get all food info from the database and create the item list
		Cursor c = mDbHelper.getAllFood();
        updateFoodList(c);
    }
	
	private void updateFoodList(Cursor c) {
		// TODO: use non-deprecated API's
        startManagingCursor(c);

        String[] from = new String[] { FoodDbAdapter.KEY_NAME, FoodDbAdapter.KEY_CARBS };
        int[] to = new int[] { R.id.name, R.id.carbs_amount};
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter foodList =
            new SimpleCursorAdapter(this, R.layout.food_row, c, from, to);
        setListAdapter(foodList);
	}
	
	private void addSearchTextListener() {
		searchEditText = (EditText) findViewById(R.id.search_text);
		searchEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// Abstract Method of TextWatcher Interface.
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Abstract Method of TextWatcher Interface.
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Cursor c = mDbHelper.getFoodWithName(searchEditText.getText().toString().trim());
				updateFoodList(c);
			}
		});
	}
}
