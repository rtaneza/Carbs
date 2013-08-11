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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyFoodActivity extends ActionBarActivity {

	public enum Mode {
		NewFood,
		EditFood
	}

	public final static int MY_FOOD_RESULT_OK = RESULT_OK;
	public final static int MY_FOOD_RESULT_CANCELED = RESULT_CANCELED;
	public final static int MY_FOOD_RESULT_REMOVE = RESULT_FIRST_USER;
	
	public final static String FOOD_ITEM_MESSAGE = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_MESSAGE";
	public final static String FOOD_ITEM_RESULT = "com.gmail.taneza.ronald.carbs.FOOD_ITEM_RESULT";
	public final static String ACTIVITY_MODE_MESSAGE = "com.gmail.taneza.ronald.carbs.ACTIVITY_MODE_MESSAGE";
	
	private FoodItem mFoodItem;
	private EditText mWeightEditText;
	private TextView mNumCarbsTextView;
	private Mode mMode;
	private int mRemoveItemConfirmationStringId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_food);
		setupActionBar();

		// Get the message from the intent
		Intent intent = getIntent();

		mFoodItem = intent.getParcelableExtra(FOOD_ITEM_MESSAGE);
		Mode mode = Mode.values()[intent.getIntExtra(ACTIVITY_MODE_MESSAGE, Mode.NewFood.ordinal())];

		TextView foodNameTextView = (TextView) findViewById(R.id.my_food_name);
		foodNameTextView.setText(mFoodItem.getName());
		// Request focus and show soft keyboard automatically
		foodNameTextView.requestFocus();
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE); 
		
//		mWeightEditText = (EditText) findViewById(R.id.my_food_weight_edit);
//		mWeightEditText.setText(Integer.toString(mFoodItem.mWeight));
//
//		TextView weightUnitTextView = (TextView) findViewById(R.id.my_food_weight_unit);
//		weightUnitTextView.setText(mFoodItem.mUnitText);
//        
//		mNumCarbsTextView = (TextView) findViewById(R.id.my_food_carbs_text);
//		updateCarbsText();
//		
//		mMode = mode;
//		if (mode == Mode.EditFoodInMeal) {			
//			setTitle(R.string.title_activity_my_food_edit);
//			Button okButton = (Button) findViewById(R.id.my_food_ok_button);
//			okButton.setText(R.string.save_food_details);
//			mRemoveItemConfirmationStringId = R.string.remove_item_from_meal_confirmation;
//		} else if (mode == Mode.RecentFood) {
//			mRemoveItemConfirmationStringId = R.string.remove_item_from_recent_foods_confirmation;
//		}
//		
//		addWeightTextListener();
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.menu_my_food, menu);

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
	
//	private void addWeightTextListener() {
//		mWeightEditText.addTextChangedListener(new TextWatcher() {
//			public void afterTextChanged(Editable s) {
//				// Abstract Method of TextWatcher Interface.
//			}
//
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// Abstract Method of TextWatcher Interface.
//			}
//
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				Integer weight = 0;
//				try {
//					weight = Integer.parseInt(mWeightEditText.getText().toString());
//				}
//				catch (NumberFormatException e) {
//					// ignore invalid weight string
//				}
//
//				mFoodItem.mWeight = weight;
//				updateCarbsText();
//			}
//		});
//	}
//
//	public void removeItem() {
//		new AlertDialog.Builder(this)
//	    .setMessage(mRemoveItemConfirmationStringId)
//	    .setPositiveButton(R.string.remove_item_do_remove, new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) { 
//	            // continue with remove
//	    		Intent data = getIntent();
//	    		data.putExtra(FOOD_ITEM_RESULT, (Parcelable)mFoodItem);
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
		data.putExtra(FOOD_ITEM_RESULT, (Parcelable)mFoodItem);
	    setResult(MY_FOOD_RESULT_OK, data);
		finish();
	}
	
//	private void updateCarbsText() {
//		mNumCarbsTextView.setText(String.format("%.1f", mFoodItem.getNumCarbsInGrams()));
//	}
}
