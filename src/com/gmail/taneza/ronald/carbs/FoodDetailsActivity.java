package com.gmail.taneza.ronald.carbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class FoodDetailsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.food_details);
		// Show the Up button in the action bar.
		setupActionBar();

		// Get the message from the intent
		Intent intent = getIntent();
		
		TextView dutchNameTextView = (TextView) findViewById(R.id.food_details_name);
		dutchNameTextView.setText(intent.getStringExtra(FoodListActivity.DUTCH_NAME_MESSAGE));
		
		TextView numCarbsTextView = (TextView) findViewById(R.id.food_details_num_carbs);
		numCarbsTextView.setText(Float.toString(intent.getFloatExtra(FoodListActivity.NUM_CARBS, 0)));
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

}
