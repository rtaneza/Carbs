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

package com.gmail.taneza.ronald.carbs.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.droidparts.widget.ClearableEditText;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.CarbsApp;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;
import com.gmail.taneza.ronald.carbs.common.FoodItemInfo;
import com.gmail.taneza.ronald.carbs.common.FoodItemListSerializer;
import com.gmail.taneza.ronald.carbs.common.Language;
import com.gmail.taneza.ronald.carbs.myfoods.MyFoodsActivity;

public class MainActivity extends ActionBarActivity implements
        ActionBar.TabListener,
        ViewPager.OnPageChangeListener,
        MainActivityNotifier {

	public final static String PREF_LANGUAGE = "PREF_LANGUAGE";
	public final static String PREF_RECENT_FOODS_LIST = "PREF_RECENT_FOODS_LIST";
	public final static String PREF_FOOD_ITEMS_LIST = "PREF_FOOD_ITEMS_LIST";

	public final static int RECENT_FOODS_LIST_MAX_SIZE = 50;
	
	public final static int ALL_FOODS_TAB_INDEX = 0;
	public final static int MY_FOODS_TAB_INDEX = 1;
	public final static int RECENT_FOODS_TAB_INDEX = 2;
	public final static int MEAL_TAB_INDEX = 3;

	public final static int REQUEST_CODE_ADD_FOOD_TO_MEAL = 0;
	public final static int REQUEST_CODE_EDIT_FOOD_IN_MEAL = 1;
	public final static int REQUEST_CODE_SHOW_MY_FOODS = 2;
	
	private Language mLanguage;
	private FoodDbAdapter mFoodDbAdapter;
    private ArrayList<FoodItem> mFoodItemsList;
    private ArrayList<FoodItem> mRecentFoodsList;
	
	private TextView mTotalCarbsTextView;
    private ViewPager mViewPager;
    private MainPagerAdapter mPagerAdapter;
    private Menu mOptionsMenu;
    private ClearableEditText mSearchEditText;
    private int mEditFoodItemIndex;
    
    private Intent mCalculatorIntent;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		//Log.i("Carbs", this.getClass().getSimpleName() + " onCreate");
		
        initializeCalculatorIntent();
        
 		// Restore preferences
 		SharedPreferences prefs = getPreferences(0);
 		mLanguage = Language.values()[prefs.getInt(PREF_LANGUAGE, Language.DUTCH.ordinal())];
 		
 		mFoodItemsList = FoodItemListSerializer.getListFromString(prefs.getString(PREF_FOOD_ITEMS_LIST, ""));
 		mRecentFoodsList = FoodItemListSerializer.getListFromString(prefs.getString(PREF_RECENT_FOODS_LIST, ""));
 		
		mFoodDbAdapter = ((CarbsApp)getApplication()).getFoodDbAdapter();
		mFoodDbAdapter.setLanguage(mLanguage);

		pruneRecentAndFoodLists();
		
        setContentView(R.layout.activity_main);

 		mTotalCarbsTextView = (TextView)findViewById(R.id.meal_total_carbs_text);
		mSearchEditText = (ClearableEditText)findViewById(R.id.search_text);
 		
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
        
        refreshAllTabsAndMealTotal();
 		
 		mEditFoodItemIndex = -1;
    }
	
	@Override
    protected void onStop() {
		pruneRecentAndFoodLists();
		
		// We need an Editor object to make preference changes.
		SharedPreferences prefs = getPreferences(0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(PREF_LANGUAGE, mLanguage.ordinal());		
		editor.putString(PREF_FOOD_ITEMS_LIST, FoodItemListSerializer.getStringFromList(mFoodItemsList));
		editor.putString(PREF_RECENT_FOODS_LIST, FoodItemListSerializer.getStringFromList(mRecentFoodsList));

		// Commit the edits!
		editor.commit();

		super.onStop();
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
                case MY_FOODS_TAB_INDEX:
                    return new MyFoodsMainFragment();
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
		
		if (mCalculatorIntent == null) {
			MenuItem openCalculatorMenuItem = mOptionsMenu.findItem(R.id.menu_open_calculator);
			openCalculatorMenuItem.setVisible(false);
		}

		setLanguageTextInOptionsMenu(mLanguage);
		
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.menu_my_foods:
	    		startMyFoodsActivity();
	    		break;
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
	        case R.id.menu_copy_meal_total:
	        	copyMealTotalToClipboard();
	        	break;
	        case R.id.menu_open_calculator:
	        	if (mCalculatorIntent != null) {
	        		// The default calculator does not accept any intent data,
	        		// so for now: copy to clipboard, open calculator, then
	        		// the user should manually paste the value to the calculator.
	        		copyMealTotalToClipboard();
	        		startActivity(mCalculatorIntent);
	        	}
	        	break;
	        case R.id.menu_main_about:
	        	showAboutDialog();
	        	break;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
        return true;
    }
    
	private void startMyFoodsActivity() {
    	Intent intent = new Intent(this, MyFoodsActivity.class);
    	startActivityForResult(intent, REQUEST_CODE_SHOW_MY_FOODS);
    }
    
	public void startActivityToAddFoodToMeal(FoodItem foodItem) {
    	startFoodDetailsActivityForResult(foodItem, FoodDetailsActivity.Mode.NewFood, REQUEST_CODE_ADD_FOOD_TO_MEAL);
	}

	public void startActivityToAddRecentFoodToMeal(FoodItem foodItem) {
    	startFoodDetailsActivityForResult(foodItem, FoodDetailsActivity.Mode.RecentFood, REQUEST_CODE_ADD_FOOD_TO_MEAL);
	}
	
	public void startActivityToEditFoodInMeal(FoodItem foodItem, int foodItemIndex) {
    	mEditFoodItemIndex = foodItemIndex;
    	startFoodDetailsActivityForResult(foodItem, FoodDetailsActivity.Mode.EditFoodInMeal, REQUEST_CODE_EDIT_FOOD_IN_MEAL);
	}
	
	private void startFoodDetailsActivityForResult(FoodItem foodItem, FoodDetailsActivity.Mode mode, int requestCode) {
    	Intent intent = new Intent(this, FoodDetailsActivity.class);
    	intent.putExtra(FoodDetailsActivity.FOOD_ITEM_MESSAGE, (Parcelable)foodItem);
    	intent.putExtra(FoodDetailsActivity.ACTIVITY_MODE_MESSAGE, mode.ordinal());

    	startActivityForResult(intent, requestCode);
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		FoodItem foodItem;
		
    	switch (requestCode) {
	    	case REQUEST_CODE_ADD_FOOD_TO_MEAL:
	            switch (resultCode) {
	            	case FoodDetailsActivity.FOOD_DETAILS_RESULT_OK:
	            		foodItem = data.getParcelableExtra(FoodDetailsActivity.FOOD_ITEM_RESULT);
	    	    		addFoodItemToMeal(foodItem);
	    	    		clearSearchText();
	    	    		break;
	    	    		
	            	case FoodDetailsActivity.FOOD_DETAILS_RESULT_REMOVE:
	            		foodItem = data.getParcelableExtra(FoodDetailsActivity.FOOD_ITEM_RESULT);
	    	    		removeFoodItemFromRecentFoods(foodItem);
	    	    		break;
	            }
	            break;
	            
	    	case REQUEST_CODE_EDIT_FOOD_IN_MEAL:
	    		// The food item list in the meal is not filterable, and can contain duplicate foodItems, 
	    		// so we need the index to remove the correct foodItem.
	            switch (resultCode) {
	            	case FoodDetailsActivity.FOOD_DETAILS_RESULT_OK:
	            		foodItem = data.getParcelableExtra(FoodDetailsActivity.FOOD_ITEM_RESULT);
	    	    		replaceFoodItemInMeal(mEditFoodItemIndex, foodItem);
	    	    		clearSearchText();
	    	    		break;
	    	    		
	            	case FoodDetailsActivity.FOOD_DETAILS_RESULT_REMOVE:
	            		foodItem = data.getParcelableExtra(FoodDetailsActivity.FOOD_ITEM_RESULT);
	    	    		removeFoodItemFromMeal(mEditFoodItemIndex);
	    	    		break;
	            }
	            mEditFoodItemIndex = -1;
	    		break;
	    		
	    	case REQUEST_CODE_SHOW_MY_FOODS:
	            switch (resultCode) {
	            	case MyFoodsActivity.MY_FOODS_RESULT_ITEM_CHANGED:
	            		refreshAllTabsAndMealTotal();
	            		break;

	            	case MyFoodsActivity.MY_FOODS_RESULT_ITEM_REMOVED:
	            		pruneRecentAndFoodLists();
	            		refreshAllTabsAndMealTotal();
	            		break;
	            }
	    		break;
    	}
    }

	@Override
	public Language getLanguage() {
		return mLanguage;
	}
	
	@Override
	public ArrayList<FoodItem> getFoodItemsList() {
		return mFoodItemsList;
	}

	@Override
	public ArrayList<FoodItem> getRecentFoodsList() {
		return mRecentFoodsList;
	}
	
	private void setLanguage(Language language) {
	    if (language != mLanguage) {
	    	mLanguage = language;

	    	setLanguageTextInOptionsMenu(language);

	    	mFoodDbAdapter.setLanguage(language);
	    	
	    	refreshAllTabs();
	    }
	}
	
	private void setLanguageTextInOptionsMenu(Language language) {
		int languageId = (language == Language.ENGLISH) ?
				R.string.language_english : R.string.language_dutch;
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
        String origTitle = getResources().getString(R.string.title_meal);
        
        int numFoodItems = mFoodItemsList.size();
        if (numFoodItems > 0) {
        	mealTab.setText(String.format("%s (%d)", origTitle, numFoodItems));
        } else {
        	mealTab.setText(origTitle);
        }
	}
	

	private void refreshAllTabs() {
		refreshAllFoodsAndMyFoodsTabs();
		refreshRecentFoodsAndMealTabs();	
	}

	private void refreshAllTabsAndMealTotal() {
		refreshAllFoodsAndMyFoodsTabs();
		updateRecentFoodsAndMealData();	
	}
	
	private void refreshAllFoodsAndMyFoodsTabs() {
		// During an orientation change, the fragment may still be null
    	AllFoodsFragment allFoodsFragment = (AllFoodsFragment)getFragment(ALL_FOODS_TAB_INDEX);
    	if (allFoodsFragment != null) {
    		allFoodsFragment.refreshList();
    	}
    	
    	MyFoodsMainFragment myFoodsFragment = (MyFoodsMainFragment)getFragment(MY_FOODS_TAB_INDEX);
    	if (myFoodsFragment != null) {
    		myFoodsFragment.refreshList();
    	}
	}

	private void refreshRecentFoodsAndMealTabs() {
		// During an orientation change, the fragment is still null
    	RecentFoodsFragment recentFoodsFragment = (RecentFoodsFragment)getFragment(RECENT_FOODS_TAB_INDEX);
    	if (recentFoodsFragment != null) {
    		recentFoodsFragment.setFoodItemList(mRecentFoodsList);
    	}
    	
    	MealFragment mealFragment = (MealFragment)getFragment(MEAL_TAB_INDEX);
    	if (mealFragment != null) {
    		mealFragment.setFoodItemList(mFoodItemsList);
    	}
	}

	private void updateRecentFoodsAndMealData() {
		updateTotalCarbsText();
    	updateMealTabText();
    	refreshRecentFoodsAndMealTabs();
	}

	private void clearRecentFoods() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.clear_recent_foods_question)
	    .setMessage(R.string.clear_recent_foods_confirmation)
	    .setPositiveButton(R.string.clear_recent_foods_do_clear, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with the clear
	    		mRecentFoodsList.clear();
	    		updateRecentFoodsAndMealData();
	        }
	     })
	    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .show();
	}
	
	private void clearMeal() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.clear_meal_question)
	    .setMessage(R.string.clear_meal_confirmation)
	    .setPositiveButton(R.string.clear_meal_do_clear, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with the clear
	    		mFoodItemsList.clear();
	    		updateRecentFoodsAndMealData();
	        }
	     })
	    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	    .show();
	}
	
	private void addFoodItemtoRecentFoodsList(FoodItem foodItem) {
		// remove any duplicate from the list
		for (int i = 0; i < mRecentFoodsList.size(); i++) {
			FoodItem item = mRecentFoodsList.get(i);
			if (item.equalsExceptTimeAdded(foodItem)) {
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
	
	public void addFoodItemToMeal(FoodItem foodItem) {
		mFoodItemsList.add(foodItem);
		
		addFoodItemtoRecentFoodsList(foodItem);
		updateRecentFoodsAndMealData();
	}

	public void replaceFoodItemInMeal(int index, FoodItem foodItem) {
		mFoodItemsList.remove(index);
		mFoodItemsList.add(index, foodItem);

		addFoodItemtoRecentFoodsList(foodItem);
		updateRecentFoodsAndMealData();
	}

	public void removeFoodItemFromMeal(int index) {
		mFoodItemsList.remove(index);
		updateRecentFoodsAndMealData();
	}

	public void removeFoodItemFromRecentFoods(FoodItem foodItem) {
		mRecentFoodsList.remove(foodItem);
		updateRecentFoodsAndMealData();
	}
	
	private Fragment getFragment(int index) {
		String fragmentTag = getFragmentTag(index);
    	return getSupportFragmentManager().findFragmentByTag(fragmentTag);
	}

	private void pruneRecentAndFoodLists() {
		for (int i = 0; i < mRecentFoodsList.size();) {
			FoodItem foodItem = mRecentFoodsList.get(i);
			if (mFoodDbAdapter.getFoodItemInfo(foodItem) == null) {
				mRecentFoodsList.remove(i);
			} else {
				i++;
			}
		}

		for (int i = 0; i < mFoodItemsList.size();) {
			FoodItem foodItem = mFoodItemsList.get(i);
			if (mFoodDbAdapter.getFoodItemInfo(foodItem) == null) {
				mFoodItemsList.remove(i);
			} else {
				i++;
			}
		}
	}
	
	private void initializeCalculatorIntent() {
	    PackageManager packageManager = getPackageManager();
		List<PackageInfo> packages = packageManager.getInstalledPackages(0);  
		for (PackageInfo pi : packages) {
			if (pi.packageName.toString().toLowerCase(Locale.getDefault()).contains("calcul")){
			    mCalculatorIntent = packageManager.getLaunchIntentForPackage(pi.packageName);
			    return;
			 }
		}
	}

    @SuppressWarnings("deprecation")
    private void copyMealTotalToClipboard() {
    	String mealTotalValue = mTotalCarbsTextView.getText().toString().replace("g", "").trim();

    	if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
    	    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    	    android.content.ClipData clip = android.content.ClipData.newPlainText("Carbs meal total", mealTotalValue);
            clipboard.setPrimaryClip(clip);
    	} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    	    clipboard.setText(mealTotalValue);
    	}
    	
    	Toast.makeText(getApplicationContext(),
    			getText(R.string.meal_total_copied_to_clipboard),
    			Toast.LENGTH_SHORT)
    		 .show();
	}
    
    private void clearSearchText() {
    	mSearchEditText.setText("");
    }
    
    private void showAboutDialog() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(String.format(Locale.getDefault(), "%s%n", getText(R.string.app_name)));
    	sb.append(String.format(Locale.getDefault(), "Version: %s%n%n", getVersion()));
    	sb.append(String.format(Locale.getDefault(), "Contact: ronald.taneza@gmail.com"));

		new AlertDialog.Builder(this)
		.setTitle(R.string.about)
	    .setMessage(sb.toString())
	    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
		})
	    .show();
    }
    
    private String getVersion() {
    	String version = "Unknown";
    	try {
    		version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
    	} catch (NameNotFoundException e) {
    	}
    	return version;
    }
}
