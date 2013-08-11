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

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class FoodItemBaseArrayAdapter extends ArrayAdapter<FoodItem> {
	protected final Context mContext;
	private final ArrayList<FoodItem> mOriginalValues;
	private ArrayList<FoodItem> mFilteredValues;
	private Language mLanguage;
	private Filter mFilter;
	  
	public FoodItemBaseArrayAdapter(Context context, int layoutResourceId, ArrayList<FoodItem> values, Language language) {
	    super(context, layoutResourceId);
	    mContext = context;
	    mOriginalValues = values;
	    mLanguage = language;
	    
	    setValues(values);
	}
	
	public void setLanguage(Language language) {
		mLanguage = language;
	}
	
	public void setValues(ArrayList<FoodItem> values) {
	    mFilteredValues = new ArrayList<FoodItem>(values);
	    clear();
	    addAll(mFilteredValues);
	}
	
    protected String getFoodName(FoodItem foodItem) {
    	if (mLanguage == Language.ENGLISH) {
    		return foodItem.mEnglishName;
    	} else {
    		return foodItem.mDutchName;
    	}
    }
    
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new FoodItemFilter(this, mOriginalValues);
        }
        return mFilter;
    }
}
