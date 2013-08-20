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

import java.io.IOException;
import java.util.ArrayList;

import org.apache.pig.impl.util.ObjectSerializer;

import com.gmail.taneza.ronald.carbs.FoodDetailsActivity.Mode;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MyFoodsActivity extends ActionBarActivity
	implements MyFoodsActivityNotifier {

	public final static int MY_FOODS_RESULT_NO_CHANGES = RESULT_FIRST_USER;
	public final static int MY_FOODS_RESULT_ITEM_CHANGED = RESULT_FIRST_USER + 1;
	
	private boolean mItemChanged;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemChanged = false;
		
        setContentView(R.layout.activity_my_foods);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

	@Override
	public void finish() {
	    setResult(mItemChanged ? MY_FOODS_RESULT_ITEM_CHANGED : MY_FOODS_RESULT_NO_CHANGES);
		super.finish();
	}
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.menu_my_foods, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_add_my_food:
				MyFoodsEditableFragment fragment = (MyFoodsEditableFragment)getSupportFragmentManager().findFragmentById(R.id.my_foods_editable_fragment);
				fragment.addNewFood();
				break;
				
			case android.R.id.home:
				finish();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setItemChanged() {
		mItemChanged = true;
	}
}
