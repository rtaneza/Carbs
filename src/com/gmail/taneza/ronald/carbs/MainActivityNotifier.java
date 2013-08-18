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

public interface MainActivityNotifier {
	public Language getLanguage();
	
	public void addFoodItemToMeal(FoodItem foodItem);
	public void replaceFoodItemInMeal(int index, FoodItem foodItem);
	// Meal list is not filterable, and can contain duplicate foodItems, so we need the index to remove the correct foodItem
	public void removeFoodItemFromMeal(int index);
	// Recent list is filterable, so a foodItem's list index may change, so we pass the foodItem object to remove it.
	// Also, the recent list does not contain duplicate foodItems, so a foodItem object is unique in the list.
	public void removeFoodItemFromRecentFoods(FoodItem foodItem);
	
	public ArrayList<FoodItem> getFoodItemsList();
	public ArrayList<FoodItem> getRecentFoodsList();
}
