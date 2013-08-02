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

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener,
        ViewPager.OnPageChangeListener,
        OnClickListener,
        MainActivityNotifier {

	public final static String PREF_LANGUAGE = "PREF_LANGUAGE";
	
	public final static int ALL_FOODS_TAB_INDEX = 0;
	public final static int MEAL_TAB_INDEX = 1;

	public final static String STATE_TOTAL_CARBS_KEY = "STATE_TOTAL_CARBS_KEY";
	
	private Language mLanguage;
	private float mTotalCarbsInGrams;
    private ArrayList<FoodItem> mFoodItemList;
	
	private TextView mTotalCarbsTextView;
    private ViewPager mViewPager;
    private MainPagerAdapter mPagerAdapter;
    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			// This is called after an orientation change.
			mTotalCarbsInGrams = savedInstanceState.getFloat(STATE_TOTAL_CARBS_KEY);
        }

		mFoodItemList = new ArrayList<FoodItem>();
		
 		// Restore preferences
 		SharedPreferences settings = getPreferences(0);
 		mLanguage = Language.values()[settings.getInt(PREF_LANGUAGE, Language.DUTCH.ordinal())];
 		
        setContentView(R.layout.activity_main);

 		mTotalCarbsTextView = (TextView)findViewById(R.id.meal_total_carbs_text);
 		updateTotalCarbsText();
 		
 		Button clearButton = (Button)findViewById(R.id.meal_total_clear_button);
 		clearButton.setOnClickListener(this);
         
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(this);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.title_all_foods)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.title_meal)
                .setTabListener(this));
        
        mViewPager.setCurrentItem(ALL_FOODS_TAB_INDEX);
        
        actionBar.setHomeButtonEnabled(false);
    }
    
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(STATE_TOTAL_CARBS_KEY, mTotalCarbsInGrams);
    }
	
	@Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getPreferences(0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putInt(PREF_LANGUAGE, mLanguage.ordinal());

      // Commit the edits!
      editor.commit();
    }
	
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case ALL_FOODS_TAB_INDEX:
                    return new AllFoodsFragment();
                case MEAL_TAB_INDEX:
                	return new MealFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mOptionsMenu = menu;
		getMenuInflater().inflate(R.menu.menu_main, menu);
		setLanguageInOptionsMenu(mLanguage);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Language newLanguage;
    	
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.menu_language_option_english:
	        	newLanguage = Language.ENGLISH;
	        	break;
	        case R.id.menu_language_option_dutch:
	        	newLanguage = Language.DUTCH;
	        	break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }

	    if (newLanguage != mLanguage) {
			mLanguage = newLanguage;
	    	setLanguage(mLanguage);
	    }
	    
        return true;
    }

	private void setLanguage(Language language) {
		setLanguageInOptionsMenu(language);
		
    	String fragmentTag = getFragmentTag(ALL_FOODS_TAB_INDEX);
    	AllFoodsFragment fragment = (AllFoodsFragment)getSupportFragmentManager().findFragmentByTag(fragmentTag);
    	fragment.setLanguage(language);
	}
	
	private void setLanguageInOptionsMenu(Language language) {
		int languageId = (language == Language.ENGLISH) ?
				R.string.menu_language_english : R.string.menu_language_dutch;
		MenuItem languageMenuItem = mOptionsMenu.findItem(R.id.menu_language);
    	languageMenuItem.setTitle(languageId);
	}

	private static String getFragmentTag(int index)
	{
		// Reference: http://stackoverflow.com/questions/6976027/reusing-fragments-in-a-fragmentpageradapter
	    return "android:switcher:" + R.id.pager + ":" + index;
	}
	
	@Override
	public void onClick(View view) {
        switch (view.getId()) {
        	case R.id.meal_total_clear_button:
        		clearMeal();
        		break;
        }
	}

	private void updateTotalCarbsText() {
		mTotalCarbsTextView.setText(String.format("%.2f", mTotalCarbsInGrams));
	}
	
	private void clearMeal() {
		mTotalCarbsInGrams = 0;
		mFoodItemList.clear();
		
		updateTotalCarbsText();
		
		String fragmentTag = getFragmentTag(MEAL_TAB_INDEX);
    	MealFragment fragment = (MealFragment)getSupportFragmentManager().findFragmentByTag(fragmentTag);
    	fragment.clearMeal();
    	
    	setMealTabTitleSuffix("");
	}
	
	@Override
	public void addFoodItemToMeal(FoodItem foodItem) {
		mTotalCarbsInGrams += foodItem.getNumCarbsInGrams();
		mFoodItemList.add(foodItem);
		updateTotalCarbsText();
		
		String fragmentTag = getFragmentTag(MEAL_TAB_INDEX);
    	MealFragment fragment = (MealFragment)getSupportFragmentManager().findFragmentByTag(fragmentTag);
    	//TODO
    	fragment.addFood(foodItem);
    	
    	setMealTabTitleSuffix(String.format(" (%d)", mFoodItemList.size()));
	}

	@Override
	public Language getLanguage() {
		return mLanguage;
	}
	
	private void setMealTabTitleSuffix(String suffix) {
		final ActionBar actionBar = getSupportActionBar();
        Tab mealTab = actionBar.getTabAt(MEAL_TAB_INDEX);
        String origTitle = getResources().getString(R.string.title_activity_meal);
        if (!suffix.isEmpty()) {
        	mealTab.setText(String.format("%s%s", origTitle, suffix));
        } else {
        	mealTab.setText(origTitle);
        }
	}
}
