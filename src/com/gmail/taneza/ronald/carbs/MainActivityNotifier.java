package com.gmail.taneza.ronald.carbs;

import java.util.ArrayList;

public interface MainActivityNotifier {
	public Language getLanguage();
	public void addFoodItemToMeal(FoodItem foodItem);
	public ArrayList<FoodItem> getFoodItemList();
}
