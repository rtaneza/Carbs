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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

public class FoodDetailsActivity extends ActionBarActivity {

	private FoodItem mFoodItem;
	private EditText mWeightEditText;
	private TextView mNumCarbsTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_food_details);
		setupActionBar();

		// Get the message from the intent
		Intent intent = getIntent();

    	Language language = (Language) intent.getSerializableExtra(AllFoodsFragment.LANGUAGE_MESSAGE);
		mFoodItem = intent.getParcelableExtra(AllFoodsFragment.FOOD_ITEM_MESSAGE);
		
		TextView dutchNameTextView = (TextView) findViewById(R.id.food_details_name);
		if (language == Language.ENGLISH) {
			dutchNameTextView.setText(mFoodItem.mEnglishName);
		} else {
			dutchNameTextView.setText(mFoodItem.mDutchName);
		}
		
		mWeightEditText = (EditText) findViewById(R.id.food_details_weight_edit);
		mWeightEditText.setText(Integer.toString(mFoodItem.mWeightInGrams));
		// Request focus and show soft keyboard automatically
		mWeightEditText.requestFocus();
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE); 

		mNumCarbsTextView = (TextView) findViewById(R.id.food_details_carbs_text);
		updateCarbsText();
		
		addWeightTextListener();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_food_details, menu);
		return true;
	}

	private void addWeightTextListener() {
		mWeightEditText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// Abstract Method of TextWatcher Interface.
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Abstract Method of TextWatcher Interface.
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Integer weight = 0;
				try {
					weight = Integer.parseInt(mWeightEditText.getText().toString());
				}
				catch (NumberFormatException e) {
					// ignore invalid weight string
				}

				mFoodItem.mWeightInGrams = weight;
				updateCarbsText();
			}
		});
	}

	public void cancel(View v) {
	    setResult(RESULT_CANCELED);
		finish();
	}
	
	public void addToMeal(View v) {
		Intent data = getIntent();
		data.putExtra(AllFoodsFragment.FOOD_ITEM_RESULT, mFoodItem);
	    setResult(RESULT_OK, data);
		finish();
	}
	
	private void updateCarbsText() {
		mNumCarbsTextView.setText(String.format("%.2f", mFoodItem.getNumCarbsInGrams()));
	}
}
