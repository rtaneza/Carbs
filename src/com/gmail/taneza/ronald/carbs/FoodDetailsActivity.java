package com.gmail.taneza.ronald.carbs;

import org.droidparts.widget.ClearableEditText;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class FoodDetailsActivity extends ActionBarActivity {

	private FoodItem mFoodItem;
	private EditText mWeightEditText;
	private TextView mNumCarbsTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.food_details);
		// Show the Up button in the action bar.
		setupActionBar();

		// Get the message from the intent
		Intent intent = getIntent();
		mFoodItem = intent.getParcelableExtra(FoodListActivity.FOOD_ITEM_MESSAGE);
		
		TextView dutchNameTextView = (TextView) findViewById(R.id.food_details_name);
		dutchNameTextView.setText(mFoodItem.mEnglishName);
		
		mWeightEditText = (EditText) findViewById(R.id.food_details_weight_edit);
		mWeightEditText.setText(Integer.toString(mFoodItem.mWeightInGrams));

		mNumCarbsTextView = (TextView) findViewById(R.id.food_details_carbs_text);
		mNumCarbsTextView.setText(String.format("%.2f", mFoodItem.getNumCarbsInGrams()));
		
		addWeightTextListener();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.food_details_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
					// ignore invalid weight
				}

				mFoodItem.mWeightInGrams = weight;
				mNumCarbsTextView.setText(String.format("%.2f", mFoodItem.getNumCarbsInGrams()));
			}
		});
	}
	
	public void AddToMeal(View v) {
		Intent data = getIntent();
		data.putExtra(FoodListActivity.FOOD_ITEM_RESULT, mFoodItem);
		if (getParent() == null) {
		    setResult(RESULT_OK, data);
		} else {
		    getParent().setResult(RESULT_OK, data);
		}
		finish();
	}
}
