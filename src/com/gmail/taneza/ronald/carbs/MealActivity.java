package com.gmail.taneza.ronald.carbs;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MealActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meal_activitiy);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meal_actions, menu);
		return true;
	}

}