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
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener,
        ViewPager.OnPageChangeListener,
        MainActivityNotifier {

	public final static String PREF_LANGUAGE = "PREF_LANGUAGE";
	public final static String PREF_RECENT_FOODS_LIST = "PREF_RECENT_FOODS_LIST";
	public final static String PREF_FOOD_ITEMS_LIST = "PREF_FOOD_ITEMS_LIST";

	public final static int RECENT_FOODS_LIST_MAX_SIZE = 30;
	
	public final static int ALL_FOODS_TAB_INDEX = 0;
	public final static int MY_FOODS_TAB_INDEX = 1;
	public final static int RECENT_FOODS_TAB_INDEX = 2;
	public final static int MEAL_TAB_INDEX = 3;
	
	private Language mLanguage;
	private FoodDbAdapter mFoodDbAdapter;
    private ArrayList<FoodItem> mFoodItemsList;
    private ArrayList<FoodItem> mRecentFoodsList;
	
	private TextView mTotalCarbsTextView;
    private ViewPager mViewPager;
    private MainPagerAdapter mPagerAdapter;
    private Menu mOptionsMenu;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
 		// Restore preferences
 		SharedPreferences prefs = getPreferences(0);
 		mLanguage = Language.values()[prefs.getInt(PREF_LANGUAGE, Language.DUTCH.ordinal())];
 		
 		mFoodItemsList = new ArrayList<FoodItem>();
 		mRecentFoodsList = new ArrayList<FoodItem>();
 		
// 		try {
// 			mFoodItemsList = (ArrayList<FoodItem>) ObjectSerializer.deserialize(
// 					prefs.getString(PREF_FOOD_ITEMS_LIST, ObjectSerializer.serialize(new ArrayList<FoodItem>())));
// 			mRecentFoodsList = (ArrayList<FoodItem>) ObjectSerializer.deserialize(
// 					prefs.getString(PREF_RECENT_FOODS_LIST, ObjectSerializer.serialize(new ArrayList<FoodItem>())));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

		mFoodDbAdapter = ((CarbsApp)getApplication()).getDatabase();
		mFoodDbAdapter.setLanguage(mLanguage);

        setContentView(R.layout.activity_main);

 		mTotalCarbsTextView = (TextView)findViewById(R.id.meal_total_carbs_text);
 		
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
                .setText(R.string.title_my_foods)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.title_recent_foods)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab()
                .setText(R.string.title_meal)
                .setTabListener(this));
        
        mViewPager.setCurrentItem(ALL_FOODS_TAB_INDEX);

        actionBar.setHomeButtonEnabled(false);
        
 		updateRecentFoodsAndMealData();
    }
	
	@Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences prefs = getPreferences(0);
      SharedPreferences.Editor editor = prefs.edit();
      editor.putInt(PREF_LANGUAGE, mLanguage.ordinal());
//      try {
//    	  editor.putString(PREF_FOOD_ITEMS_LIST, ObjectSerializer.serialize(mFoodItemsList));
//    	  editor.putString(PREF_RECENT_FOODS_LIST, ObjectSerializer.serialize(mRecentFoodsList));
//      } catch (IOException e) {
//    	  e.printStackTrace();
//      }

      // Commit the edits!
      editor.commit();
    }
	
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());

 		View searchText = findViewById(R.id.search_text);
        if (tab.getPosition() == MEAL_TAB_INDEX) {
        	searchText.setVisibility(View.GONE);
        } else {
        	searchText.setVisibility(View.VISIBLE);
        }
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
                case MY_FOODS_TAB_INDEX:
                    return new MyFoodsFragment();
                case RECENT_FOODS_TAB_INDEX:
                    return new RecentFoodsFragment();
                case MEAL_TAB_INDEX:
                	return new MealFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        mOptionsMenu = menu;
		getMenuInflater().inflate(R.menu.menu_main, menu);

		setLanguageTextInOptionsMenu(mLanguage);
		
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.menu_language_option_english:
	        	setLanguage(Language.ENGLISH);
	        	break;
	        case R.id.menu_language_option_dutch:
	        	setLanguage(Language.DUTCH);
	        	break;
	        case R.id.menu_clear_recent_foods:
	        	clearRecentFoods();
	        	break;
	        case R.id.menu_clear_meal:
        		clearMeal();
	        	break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
        return true;
    }

	private void setLanguage(Language language) {
	    if (language != mLanguage) {
	    	mLanguage = language;

	    	setLanguageTextInOptionsMenu(language);
			
	    	AllFoodsFragment allFoodsFragment = (AllFoodsFragment)getFragment(ALL_FOODS_TAB_INDEX);
	    	if (allFoodsFragment != null)
	    	{
	    		allFoodsFragment.setLanguage(language);
	    	}

	    	MyFoodsFragment myFoodsFragment = (MyFoodsFragment)getFragment(MY_FOODS_TAB_INDEX);
	    	if (myFoodsFragment != null)
	    	{
	    		myFoodsFragment.setLanguage(language);
	    	}
	    	
	    	RecentFoodsFragment recentFoodsFragment = (RecentFoodsFragment)getFragment(RECENT_FOODS_TAB_INDEX);
	    	if (recentFoodsFragment != null)
	    	{
	    		recentFoodsFragment.setLanguage(language);
	    	}
	    	
	    	MealFragment mealFragment = (MealFragment)getFragment(MEAL_TAB_INDEX);
	    	if (mealFragment != null)
	    	{
	    		mealFragment.setLanguage(language);
	    	}
	    }
	}
	
	private void setLanguageTextInOptionsMenu(Language language) {
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
	
	private void updateTotalCarbsText() {
		float totalCarbsInGrams = 0;
		
		for (FoodItem item : mFoodItemsList) {
			final FoodItemInfo info = mFoodDbAdapter.getFoodItemInfo(item);
			totalCarbsInGrams += info.getNumCarbsInGrams();
		}
		
		mTotalCarbsTextView.setText(String.format("%.1f g", totalCarbsInGrams));
	}
	
	private void updateMealTabText() {
		final ActionBar actionBar = getSupportActionBar();
        Tab mealTab = actionBar.getTabAt(MEAL_TAB_INDEX);
        String origTitle = getResources().getString(R.string.title_activity_meal);
        
        int numFoodItems = mFoodItemsList.size();
        if (numFoodItems > 0) {
        	mealTab.setText(String.format("%s (%d)", origTitle, numFoodItems));
        } else {
        	mealTab.setText(origTitle);
        }
	}
	
	private void updateRecentFoodsAndMealData() {
		updateTotalCarbsText();
    	updateMealTabText();
    	
    	RecentFoodsFragment recentFoodsFragment = (RecentFoodsFragment)getFragment(RECENT_FOODS_TAB_INDEX);
		// During an orientation change, the fragment is still null
    	if (recentFoodsFragment != null) {
    		recentFoodsFragment.setFoodItemList(mRecentFoodsList);
    	}
    	
    	MealFragment mealFragment = (MealFragment)getFragment(MEAL_TAB_INDEX);
		// During an orientation change, the fragment is still null
    	if (mealFragment != null) {
    		mealFragment.setFoodItemList(mFoodItemsList);
    	}
	}

	private void clearRecentFoods() {
		mRecentFoodsList.clear();
		updateRecentFoodsAndMealData();
	}
	
	private void clearMeal() {
		mFoodItemsList.clear();
		updateRecentFoodsAndMealData();
	}
	
	private void addFoodItemtoRecentFoodsList(FoodItem foodItem) {
		// remove any duplicate from the list
		for (int i = 0; i < mRecentFoodsList.size(); i++) {
			FoodItem item = mRecentFoodsList.get(i);
			if (item.equals(foodItem)) {
				mRecentFoodsList.remove(i);
				break;
			}
		}
		
		// insert into the start of the mRecentFoodsList if not yet present
		if (mRecentFoodsList.size() >= RECENT_FOODS_LIST_MAX_SIZE) {
			mRecentFoodsList.remove(RECENT_FOODS_LIST_MAX_SIZE - 1);
		}
		
    	mRecentFoodsList.add(0, foodItem);
	}
	
	@Override
	public void addFoodItemToMeal(FoodItem foodItem) {
		mFoodItemsList.add(foodItem);
		
		addFoodItemtoRecentFoodsList(foodItem);
		updateRecentFoodsAndMealData();
	}

	@Override
	public void replaceFoodItemInMeal(int index, FoodItem foodItem) {
		mFoodItemsList.remove(index);
		mFoodItemsList.add(index, foodItem);

		addFoodItemtoRecentFoodsList(foodItem);
		updateRecentFoodsAndMealData();
	}

	@Override
	public void removeFoodItemFromMeal(int index) {
		mFoodItemsList.remove(index);
		updateRecentFoodsAndMealData();
	}

	@Override
	public void removeFoodItemFromRecentFoods(FoodItem foodItem) {
		mRecentFoodsList.remove(foodItem);
		updateRecentFoodsAndMealData();
	}
	
	@Override
	public Language getLanguage() {
		return mLanguage;
	}
	
	@Override
	public FoodDbAdapter getFoodDbAdapter() {
		return mFoodDbAdapter;
	}
	
	private Fragment getFragment(int index) {
		String fragmentTag = getFragmentTag(index);
    	return getSupportFragmentManager().findFragmentByTag(fragmentTag);
	}

	@Override
	public ArrayList<FoodItem> getFoodItemsList() {
		return mFoodItemsList;
	}

	@Override
	public ArrayList<FoodItem> getRecentFoodsList() {
		return mRecentFoodsList;
	}
}
