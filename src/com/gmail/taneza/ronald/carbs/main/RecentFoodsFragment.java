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

import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.taneza.ronald.carbs.R;
import com.gmail.taneza.ronald.carbs.common.FoodDbAdapter;
import com.gmail.taneza.ronald.carbs.common.FoodItem;

public class RecentFoodsFragment extends BaseFoodListFragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent_foods, container, false);
    }

    @Override
    public ArrayList<FoodItem> getFoodList() {
        return mMainActivityNotifier.getRecentFoodsList();
    }

    @Override
    public void startActivityToAddOrEditFood(FoodItem foodItem, int foodItemIndex) {
        // foodItemIndex is not used
        mMainActivityNotifier.startActivityToAddRecentFoodToMeal(foodItem);
    }
    
    @Override
    public FoodItemBaseArrayAdapter createFoodItemArrayAdapter(Context context, FoodDbAdapter foodDbAdapter, ArrayList<FoodItem> values) {
        return new RecentFoodsArrayAdapter(context, foodDbAdapter, values);
    }

    @Override
    protected void setDeleteItemsMode(boolean enable) {
        mMainActivityNotifier.setDeleteRecentFoodItemsMode(enable);
    }

    @Override
    protected void deleteFromList(SparseBooleanArray itemsToDelete) {
        mMainActivityNotifier.deleteFromRecentFoodItemsList(itemsToDelete);
    }
}
