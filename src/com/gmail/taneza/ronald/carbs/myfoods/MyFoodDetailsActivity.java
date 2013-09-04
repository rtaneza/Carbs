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

package com.gmail.taneza.ronald.carbs.myfoods;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.CarbsApp;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;
import com.gmail.taneza.ronald.carbs.common.FoodItemInfo;

public class MyFoodDetailsActivity extends ActionBarActivity {

	public static final String NEW_FOOD_DEFAULT_NAME = "My food"; 
	public static final String NEW_FOOD_DEFAULT_UNIT_TEXT = "g"; 
	public static final int NEW_FOOD_DEFAULT_WEIGHT_PER_UNIT = 100;
	public static final int NEW_FOOD_DEFAULT_CARBS = 0;
	
	public enum Mode {
		NewFood,
		EditFood
	}

	public final static int MY_FOOD_RESULT_OK = RESULT_OK;
	public final static int MY_FOOD_RESULT_CANCELED = RESULT_CANCELED;
	public final static int MY_FOOD_RESULT_REMOVE = RESULT_FIRST_USER;
	
	public final static String MY_FOOD_ITEM_MESSAGE = "com.gmail.taneza.ronald.carbs.MY_FOOD_ITEM_MESSAGE";
	public final static String MY_FOOD_ITEM_INFO_RESULT = "com.gmail.taneza.ronald.carbs.MY_FOOD_ITEM_INFO_RESULT";
	public final static String MY_FOOD_ITEM_RESULT = "com.gmail.taneza.ronald.carbs.MY_FOOD_ITEM_RESULT";
	public final static String MY_FOOD_ACTIVITY_MODE_MESSAGE = "com.gmail.taneza.ronald.carbs.MY_FOOD_ACTIVITY_MODE_MESSAGE";
	
	private FoodItem mFoodItem;
	private TextView mFoodNameTextView;
	private EditText mWeightEditText;
	private TextView mNumCarbsTextView;
	private Spinner mWeightUnitTextSpinner;
	private Mode mMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_food_details);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the message from the intent
		Intent intent = getIntent();

		mFoodItem = intent.getParcelableExtra(MY_FOOD_ITEM_MESSAGE);
		FoodItemInfo foodItemInfo;
		
		Mode mode = Mode.values()[intent.getIntExtra(MY_FOOD_ACTIVITY_MODE_MESSAGE, Mode.NewFood.ordinal())];
		mMode = mode;
		if (mode == Mode.NewFood) {
			foodItemInfo = new FoodItemInfo(mFoodItem, NEW_FOOD_DEFAULT_NAME, NEW_FOOD_DEFAULT_WEIGHT_PER_UNIT, NEW_FOOD_DEFAULT_CARBS, NEW_FOOD_DEFAULT_UNIT_TEXT);
		} else {			
			FoodDbAdapter foodDbAdapter = ((CarbsApp)getApplication()).getFoodDbAdapter();
			foodItemInfo = foodDbAdapter.getFoodItemInfo(mFoodItem);

			setTitle(R.string.title_activity_my_food_edit);
			Button okButton = (Button) findViewById(R.id.my_food_ok_button);
			okButton.setText(R.string.save_food_details);
		}
		
		mFoodNameTextView = (TextView) findViewById(R.id.my_food_name);
		mFoodNameTextView.setText(foodItemInfo.getName());
		// Request focus and show soft keyboard automatically
		mFoodNameTextView.requestFocus();
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE); 
		
		mWeightEditText = (EditText) findViewById(R.id.my_food_weight_edit);
		mWeightEditText.setText(Integer.toString(foodItemInfo.getWeightPerUnit()));

		mWeightUnitTextSpinner = (Spinner) findViewById(R.id.my_food_weight_unit_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
		        R.array.weight_unit_text_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mWeightUnitTextSpinner.setAdapter(arrayAdapter);
		mWeightUnitTextSpinner.setSelection(arrayAdapter.getPosition(foodItemInfo.getUnitText()));
		
		mNumCarbsTextView = (TextView) findViewById(R.id.my_food_carbs);
		
		// Display decimal place only when non-zero, so it's easier to edit
		String numCarbsString;
		float numCarbs = foodItemInfo.getNumCarbsInGrams();
		if (numCarbs == (int)numCarbs) {
			numCarbsString = String.format("%.0f", numCarbs);
		} else {
			numCarbsString = String.format("%.1f", numCarbs);
		}
		mNumCarbsTextView.setText(numCarbsString);
	}
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.menu_my_food_details, menu);

    	if (mMode == Mode.NewFood) {
    		MenuItem menuItem = menu.findItem(R.id.menu_my_food_remove);
    		menuItem.setVisible(false);
    	}
    	
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_my_food_remove:
			removeItem();
			return true;
			
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void removeItem() {
		new AlertDialog.Builder(this)
	    .setMessage(R.string.remove_item_from_my_foods)
	    .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with remove
	    		Intent data = getIntent();
	    		data.putExtra(MY_FOOD_ITEM_RESULT, (Parcelable)mFoodItem);
	    	    setResult(MY_FOOD_RESULT_REMOVE, data);
	    		finish();
	        }
	     })
	    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .show();
	}

	public void cancel(View v) {
	    setResult(MY_FOOD_RESULT_CANCELED);
		finish();
	}
	
	public void addOrUpdate(View v) {
		FoodItemInfo foodItemInfo = new FoodItemInfo(mFoodItem, 
				mFoodNameTextView.getText().toString(),
				Integer.parseInt(mWeightEditText.getText().toString()),
				Float.parseFloat(mNumCarbsTextView.getText().toString()),
				mWeightUnitTextSpinner.getSelectedItem().toString());
		
		Intent data = getIntent();
		data.putExtra(MY_FOOD_ITEM_INFO_RESULT, (Parcelable)foodItemInfo);
	    setResult(MY_FOOD_RESULT_OK, data);
		finish();
	}
}
