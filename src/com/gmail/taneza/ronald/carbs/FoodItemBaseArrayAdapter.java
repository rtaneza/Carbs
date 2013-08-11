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
	protected final ArrayList<FoodItem> mValues;
	protected ArrayList<FoodItem> mFilteredValues;
	protected Language mLanguage;
	protected Filter mFilter;
	  
	public FoodItemBaseArrayAdapter(Context context, int layoutResourceId, ArrayList<FoodItem> values, Language language) {
	    super(context, layoutResourceId, values);
	    mContext = context;
	    mValues = values;
	    mFilteredValues = new ArrayList<FoodItem>(values);
	    mLanguage = language;
	}
	
	public void setLanguage(Language language) {
		mLanguage = language;
	}
	
	public void setFilteredValues(ArrayList<FoodItem> values) {
		mFilteredValues = values;
		notifyDataSetChanged();
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
            mFilter = new FoodItemFilter(this);
        }
        return mFilter;
    }
}
