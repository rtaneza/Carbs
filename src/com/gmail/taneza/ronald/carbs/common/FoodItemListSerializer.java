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

package com.gmail.taneza.ronald.carbs.common;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

/* 
 * The default java serializer / deserializer does not handle 
 * class renames, package renames, or class field renames,
 * so I implemented my own list-to-string serializer and deserializer.
 */
public class FoodItemListSerializer {
    
    private static final int versionNumber = 1;
    
    private static final String delimeter = ";";
    private static final int headerLength = 2; // versionNumber + numFoodItems
    
    public static String getStringFromList(ArrayList<FoodItem> foodItemList) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format(Locale.US, "%d", versionNumber));
        sb.append(delimeter);
        sb.append(String.format(Locale.US, "%d", foodItemList.size()));
        
        for (FoodItem foodItem : foodItemList) {
            verifyTableNameIsValidOrThrow(foodItem.getTableName());
            sb.append(delimeter);
            sb.append(foodItem.getTableName()); 
            sb.append(delimeter);
            sb.append(String.format(Locale.US, "%d", foodItem.getId())); 
            sb.append(delimeter);
            sb.append(String.format(Locale.US, "%d", foodItem.getQuantity())); 
            sb.append(delimeter);
            sb.append(String.format(Locale.US, "%d", foodItem.getTimeAddedMsec())); 
        }
        
        return sb.toString();
    }
    
    public static ArrayList<FoodItem> getListFromString(String foodItemListString) {
        ArrayList<FoodItem> foodItemList = new ArrayList<FoodItem>();
        
    if (foodItemListString == null) {
            return foodItemList;
        }
        
        String[] itemArray = foodItemListString.split(delimeter);
        if (itemArray.length < headerLength) {
            return foodItemList;
        }
        
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        format.setParseIntegerOnly(true);
        
        try {
            int index = 0;
            
            int version = format.parse(itemArray[index++]).intValue();
            if (version != versionNumber) {
                return foodItemList;
            }
            
            int numFoodItems = format.parse(itemArray[index++]).intValue();
            if (numFoodItems <= 0 || (itemArray.length - headerLength) < numFoodItems) {
                return foodItemList;
            }
            
            for (int i = 0; i < numFoodItems; i++) {
                String tableName = null;
                int id = -1;
                int quantity = -1;
                long timeAddedMsec = -1;
                
                tableName = itemArray[index++];
                id = format.parse(itemArray[index++]).intValue();
                quantity = format.parse(itemArray[index++]).intValue();
                timeAddedMsec = format.parse(itemArray[index++]).longValue();

                if (tableName == null || id <= 0 || quantity < 0 || timeAddedMsec < 0) {
                    return foodItemList;
                }
                
                FoodItem foodItem = new FoodItem(tableName, id, quantity, timeAddedMsec);
                foodItemList.add(foodItem);                
            }
            
        } catch (ParseException e) {
            // ignore, just return what we have parsed so far
            //Log.i("Carbs", e.toString());
        }
        
        return foodItemList;
    }
    
    public static void verifyTableNameIsValidOrThrow(String tableName) {
        if (tableName.contains(FoodItemListSerializer.delimeter)) {
            throw new IllegalArgumentException(
                    String.format("FoodItem tableName '%s' should not contain delimeter '%s'",
                    tableName, FoodItemListSerializer.delimeter));
        }
    }
}
