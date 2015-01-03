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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.CarbsApp;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;
import com.gmail.taneza.ronald.carbs.common.FoodItemInfo;
import com.gmail.taneza.ronald.carbs.main.HelpActivity;
import com.gmail.taneza.ronald.carbs.main.HelpActivity.Mode;

public class MyFoodsActivity extends ActionBarActivity
    implements MyFoodsActivityNotifier {

    private final static int versionNumber = 1;
    private final static String myFoodsVersionHeader = String.format(Locale.US, "Carbs MyFoods version %d", versionNumber);
    
    public final static int MY_FOODS_RESULT_NO_CHANGES = RESULT_FIRST_USER;
    public final static int MY_FOODS_RESULT_ITEM_CHANGED = RESULT_FIRST_USER + 1;
    public final static int MY_FOODS_RESULT_ITEM_DELETED = RESULT_FIRST_USER + 2;
    
    private boolean mItemChanged;
    private boolean mItemDeleted;
    private FoodDbAdapter mFoodDbAdapter;
    private String mSharedText;
    private String[] mQuantityUnitsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemChanged = false;
        mItemDeleted = false;

        mFoodDbAdapter = ((CarbsApp)getApplication()).getFoodDbAdapter();
        
        mQuantityUnitsArray = getResources().getStringArray(R.array.quantity_unit_text_array);
        
        setContentView(R.layout.activity_my_foods);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // We process the intent in onResume() instead of onCreate() so that the activity view 
        // is already visible before we show an Alert dialog confirmation.
        
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            mSharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (mSharedText != null) {
                handleSharedText();
                setResult(RESULT_OK);
            }
        }
    }

    @Override
    public void finish() {
        int resultCode;
        
        if (mItemDeleted) {
            resultCode = MY_FOODS_RESULT_ITEM_DELETED;
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

            case R.id.menu_delete_my_food_items:
                fragment.deleteMyFoods();
                break;
                
            case R.id.menu_clear_my_foods:
                fragment.clearMyFoods();
                break;

            case R.id.menu_import_my_foods:
                importMyFoodsFromClipboard();
                break;
                
            case R.id.menu_export_my_foods:
                exportMyFoods();
                break;
                
            case R.id.menu_help:
                startHelpActivity();
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
    public void setItemDeleted() {
        mItemDeleted = true;
    }    

    public void exportMyFoods() {
        String myFoodsCsv = getMyFoodsCsv();
        
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, myFoodsCsv);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void startHelpActivity() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.HELP_ACTIVITY_MODE_MESSAGE, Mode.MyFoods.ordinal());
        startActivity(intent);
    }
    
    private String getMyFoodsCsv() {
        StringWriter sw = new StringWriter();
        
        sw.append(myFoodsVersionHeader);
        sw.append(String.format(Locale.US, "%n")); // platform-independent newline
        
        try {
            // EXCEL_PREFERENCE is:
            //   quote char:     "
            //   delimeter char: ,
            //   end of line:    \n
            // QuoteMode is the default NormalQuoteMode: 
            //   quotes are only applied if required to escape special characters (per RFC4180)
            ICsvListWriter csvWriter = new CsvListWriter(sw, CsvPreference.EXCEL_PREFERENCE);
                
            csvWriter.writeHeader(new String[] {
                FoodDbAdapter.MYFOODS_COLUMN_NAME,
                FoodDbAdapter.MYFOODS_COLUMN_QUANTITY_PER_UNIT,
                FoodDbAdapter.MYFOODS_COLUMN_UNIT_TEXT,
                FoodDbAdapter.MYFOODS_COLUMN_CARBS_GRAMS_PER_UNIT });

            ArrayList<FoodItemInfo> myFoodsList = mFoodDbAdapter.getAllMyFoods();
            for (FoodItemInfo info : myFoodsList) {
                csvWriter.write(new String[] {
                    info.getName(),
                    String.format(Locale.US, "%d", info.getQuantityPerUnit()),
                    info.getUnitText(),
                    String.format(Locale.US, "%.1f", info.getNumCarbsInGramsPerUnit()) });
            }
            
            csvWriter.close();
            
        } catch (IOException e) {
            // We don't write to a file, but just to an in-memory stringWriter,
            // so an IOException is very unlikely to occur.
        }
        
        return sw.toString();
    }

    @SuppressWarnings("deprecation")
    private void importMyFoodsFromClipboard() {
        mSharedText = null;
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()) {
                ClipDescription description = clipboard.getPrimaryClipDescription();
                if (description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ||
                    description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
                    android.content.ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    mSharedText = item.getText().toString();
                }
            }
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasText()) {
                mSharedText = clipboard.getText().toString();
            }
        }
        
        if (mSharedText != null) {
            handleSharedText();
        } else {
            showImportCompletionMessage(getText(R.string.my_foods_import_no_data_to_import).toString());
        }
    }
    
    private void handleSharedText() {
        String message = String.format(Locale.US, getResources().getString(R.string.my_foods_import_message), mSharedText);

        new AlertDialog.Builder(this)
        .setTitle(R.string.my_foods_import_title)
        .setMessage(message)
        .setPositiveButton(R.string.my_foods_import_do_import, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                addSharedTextToMyFoods();
            }
         })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
        .show();
    }
    
    private void addSharedTextToMyFoods() {
        int numItemsImported = 0;

        StringReader sr = new StringReader(mSharedText);
        try {
            ICsvListReader csvReader = new CsvListReader(sr, CsvPreference.EXCEL_PREFERENCE);
            
            String versionHeaders[] = csvReader.getHeader(true);
            if (versionHeaders.length != 1 || !versionHeaders[0].trim().equals(myFoodsVersionHeader)) {
                showImportCompletionMessage(getText(R.string.my_foods_import_invalid_format).toString());
                csvReader.close();
                return;
            }
            
            // column headers
            csvReader.getHeader(false);
    
            List<String> myFoodInfoLine;
            while ((myFoodInfoLine = csvReader.read()) != null ) {
                FoodItemInfo foodItemInfo = getFoodItemInfoFromCsvLine(myFoodInfoLine);
                if (foodItemInfo != null) {
                    mFoodDbAdapter.addMyFoodItemInfo(foodItemInfo);
                    numItemsImported++;
                }
            }
            
            csvReader.close();
            
        } catch (IOException e) {
            // We don't read from a file, but just to an in-memory stringReader,
            // so an IOException is very unlikely to occur.
        }

        String message = String.format(Locale.US, getResources().getString(R.string.my_foods_import_finished), numItemsImported);
        showImportCompletionMessage(message);
        
        if (numItemsImported > 0) {
            MyFoodsEditableFragment fragment = (MyFoodsEditableFragment)getSupportFragmentManager().findFragmentById(R.id.my_foods_editable_fragment);
            fragment.refreshMyFoods();
            mItemChanged = true;
        }
    }
    
    private FoodItemInfo getFoodItemInfoFromCsvLine(List<String> myFoodInfoLine) {
        if (myFoodInfoLine.size() != 4) {
            return null;
        }

        NumberFormat format = NumberFormat.getInstance(Locale.US);
        
        try {
            String name = myFoodInfoLine.get(0);
            int quantityPerUnit = format.parse(myFoodInfoLine.get(1)).intValue();
            String unitText = myFoodInfoLine.get(2);
            float carbsGramsPerUnit = format.parse(myFoodInfoLine.get(3)).floatValue();
            
            if (name == null || quantityPerUnit < 0 || unitText == null || !isValidUnitText(unitText) || carbsGramsPerUnit < 0) {
                return null;
            }
        
            FoodItem foodItem = new FoodItem(FoodDbAdapter.MYFOODS_TABLE_NAME, 0, MyFoodDetailsActivity.NEW_FOOD_DEFAULT_QUANTITY_PER_UNIT);
            FoodItemInfo foodItemInfo = new FoodItemInfo(foodItem, 
                    getUniqueFoodName(name), quantityPerUnit, carbsGramsPerUnit, unitText);
            
            return foodItemInfo;
            
        } catch (ParseException e) {
            return null;
        }
    }
    
    private void showImportCompletionMessage(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT)
             .show();
    }
    
    private String getUniqueFoodName(String foodName) {
        int count = 1;
        String newName = foodName;
        while (mFoodDbAdapter.myFoodNameExists(newName, 0)) {
            newName = String.format(Locale.US, "%s (%d)", foodName, ++count);
        }
        return newName;
    }
    
    private boolean isValidUnitText(String unitText) {
        for (String unit : mQuantityUnitsArray) {
            if (unitText.equals(unit)) {
                return true;
            }
        }        
        return false;
    }

}
