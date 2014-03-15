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

import android.util.SparseBooleanArray;

import com.gmail.taneza.ronald.carbs.common.FoodItem;
import com.gmail.taneza.ronald.carbs.common.Language;

public interface MainActivityNotifier {
	public Language getLanguage();
	
	public void startActivityToAddFoodToMeal(FoodItem foodItem);
	public void startActivityToAddRecentFoodToMeal(FoodItem foodItem);
	public void startActivityToEditFoodInMeal(FoodItem foodItem, int foodItemIndex);
	
	public ArrayList<FoodItem> getFoodItemsList();
	public ArrayList<FoodItem> getRecentFoodsList();

	public void setRemoveRecentFoodItemsMode(boolean enable);
	public void removeFromRecentFoodItemsList(SparseBooleanArray itemsToRemove);
	
	public void setRemoveFoodItemsMode(boolean enable);
	public void removeFromFoodItemsList(SparseBooleanArray itemsToRemove);
}
