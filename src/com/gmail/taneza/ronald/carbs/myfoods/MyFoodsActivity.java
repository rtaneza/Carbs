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
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.gmail.taneza.ronald.carbs.R;

public class MyFoodsActivity extends ActionBarActivity
	implements MyFoodsActivityNotifier {

	public final static int MY_FOODS_RESULT_NO_CHANGES = RESULT_FIRST_USER;
	public final static int MY_FOODS_RESULT_ITEM_CHANGED = RESULT_FIRST_USER + 1;
	public final static int MY_FOODS_RESULT_ITEM_REMOVED = RESULT_FIRST_USER + 2;
	
	private boolean mItemChanged;
	private boolean mItemRemoved;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemChanged = false;
        mItemRemoved = false;
		
        setContentView(R.layout.activity_my_foods);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

	@Override
	public void finish() {
		int resultCode;
		
		if (mItemRemoved) {
			resultCode = MY_FOODS_RESULT_ITEM_REMOVED;
		} else if (mItemChanged) {
			resultCode = MY_FOODS_RESULT_ITEM_CHANGED;
		} else {
			resultCode = MY_FOODS_RESULT_NO_CHANGES;
		}
		
	    setResult(resultCode);
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
		MyFoodsEditableFragment fragment = (MyFoodsEditableFragment)getSupportFragmentManager().findFragmentById(R.id.my_foods_editable_fragment);
		
		switch (item.getItemId()) {
			case R.id.menu_add_my_food:
				fragment.addNewFood();
				break;
				
			case R.id.menu_clear_my_foods:
				fragment.clearMyFoods();
				break;
				
			case R.id.menu_export_my_foods:
				fragment.exportMyFoods();
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

	@Override
	public void setItemRemoved() {
		mItemRemoved = true;
	}	
}
