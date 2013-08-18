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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MyFoodDetailsActivity extends ActionBarActivity {

	public static final String NEW_FOOD_DEFAULT_NAME = "My food"; 
	public static final String NEW_FOOD_DEFAULT_UNIT_TEXT = "g"; 
	public static final int NEW_FOOD_DEFAULT_WEIGHT = 100;
	public static final int NEW_FOOD_DEFAULT_CARBS = 0;
	
	public enum Mode {
		NewFood,
		EditFood
	}

	public final static int MY_FOOD_RESULT_OK = RESULT_OK;
	public final static int MY_FOOD_RESULT_CANCELED = RESULT_CANCELED;
	public final static int MY_FOOD_RESULT_REMOVE = RESULT_FIRST_USER;
	
	public final static String MY_FOOD_ITEM_MESSAGE = "com.gmail.taneza.ronald.carbs.MY_FOOD_ITEM_MESSAGE";
	public final static String MY_FOOD_ITEM_RESULT = "com.gmail.taneza.ronald.carbs.MY_FOOD_ITEM_RESULT";
	public final static String MY_FOOD_ACTIVITY_MODE_MESSAGE = "com.gmail.taneza.ronald.carbs.MY_FOOD_ACTIVITY_MODE_MESSAGE";
	
	private FoodItemInfo mFoodItemInfo;
	private EditText mWeightEditText;
	private TextView mNumCarbsTextView;
	private Mode mMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_food_details);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the message from the intent
		Intent intent = getIntent();

		FoodItem foodItem = intent.getParcelableExtra(MY_FOOD_ITEM_MESSAGE);
		Mode mode = Mode.values()[intent.getIntExtra(MY_FOOD_ACTIVITY_MODE_MESSAGE, Mode.NewFood.ordinal())];

		mMode = mode;
		if (mode == Mode.NewFood) {
			mFoodItemInfo = new FoodItemInfo(foodItem, NEW_FOOD_DEFAULT_NAME, foodItem.getWeight(), NEW_FOOD_DEFAULT_CARBS, NEW_FOOD_DEFAULT_UNIT_TEXT);
		} else {			
			FoodDbAdapter foodDbAdapter = ((CarbsApp)getApplication()).getFoodDbAdapter();
			mFoodItemInfo = foodDbAdapter.getFoodItemInfo(foodItem);

			setTitle(R.string.title_activity_my_food_edit);
			Button okButton = (Button) findViewById(R.id.my_food_ok_button);
			okButton.setText(R.string.save_food_details);
		}
		
		TextView foodNameTextView = (TextView) findViewById(R.id.my_food_name);
		foodNameTextView.setText(mFoodItemInfo.getName());
		// Request focus and show soft keyboard automatically
		foodNameTextView.requestFocus();
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE); 
		
		mWeightEditText = (EditText) findViewById(R.id.my_food_weight_edit);
		mWeightEditText.setText(Integer.toString(mFoodItemInfo.getWeightPerUnit()));

		Spinner weightUnitTextSpinner = (Spinner) findViewById(R.id.my_food_weight_unit_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.weight_unit_text_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		// Apply the adapter to the spinner
		weightUnitTextSpinner.setAdapter(adapter);
		
		weightUnitTextSpinner.setSelection(adapter.getPosition(mFoodItemInfo.getUnitText()));
		
		mNumCarbsTextView = (TextView) findViewById(R.id.my_food_carbs);
		mNumCarbsTextView.setText(String.format("%.1f", mFoodItemInfo.getNumCarbsInGrams()));
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
			//removeItem();
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
//
//	public void removeItem() {
//		new AlertDialog.Builder(this)
//	    .setMessage(mRemoveItemConfirmationStringId)
//	    .setPositiveButton(R.string.remove_item_do_remove, new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) { 
//	            // continue with remove
//	    		Intent data = getIntent();
//	    		data.putExtra(MY_FOOD_ITEM_RESULT, (Parcelable)mFoodItem);
//	    	    setResult(MY_FOOD_RESULT_REMOVE, data);
//	    		finish();
//	        }
//	     })
//	    .setNegativeButton(R.string.remove_item_cancel, new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) { 
//	            // do nothing
//	        }
//	     })
//	    .show();
//	}
//
	public void cancel(View v) {
	    setResult(MY_FOOD_RESULT_CANCELED);
		finish();
	}
	
	public void addToMyFoods(View v) {
		Intent data = getIntent();
		data.putExtra(MY_FOOD_ITEM_RESULT, (Parcelable)mFoodItemInfo.getFoodItem());
	    setResult(MY_FOOD_RESULT_OK, data);
		finish();
	}
}
