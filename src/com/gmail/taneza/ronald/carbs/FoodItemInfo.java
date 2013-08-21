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

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItemInfo implements Parcelable {

	public static final String MY_FOOD_TEXT = "My Food";
	
	private FoodItem mFoodItem;
	private String mName;
	private int mWeightPerUnit; // e.g. 100 g
	private float mNumCarbsInGramsPerUnit; // e.g. 30 g
	private String mUnitText; // e.g. g or ml
	
	public FoodItemInfo(FoodItem foodItem, String name, int weightPerUnit, float numCarbsInGramsPerUnit, String unitText) {
		mFoodItem = foodItem;
		mName = name;
		mWeightPerUnit = weightPerUnit;
		mNumCarbsInGramsPerUnit = numCarbsInGramsPerUnit;
		mUnitText = unitText;
	}
	
	public FoodItem getFoodItem() {
		return mFoodItem;
	}

	public String getName() {
		return mName;
	}

	public int getWeightPerUnit() {
		return mWeightPerUnit;
	}

	public float getNumCarbsInGramsPerUnit() {
		return mNumCarbsInGramsPerUnit;
	}

	public String getUnitText() {
		return mUnitText;
	}
	
	public float getNumCarbsInGrams() {
		return (getNumCarbsInGramsPerUnit() * mFoodItem.getWeight()) / getWeightPerUnit();
	}

	public String getTableName() {
		return mFoodItem.getTableName();
	}
	
	public int getWeight() {
		return mFoodItem.getWeight();
	}
	
	public void setWeight(int weight) {
		mFoodItem.setWeight(weight);
	}

	private FoodItemInfo(Parcel parcel) {
		mFoodItem = parcel.readParcelable(FoodItem.class.getClassLoader());
		mName = parcel.readString();
		mWeightPerUnit = parcel.readInt();
		mNumCarbsInGramsPerUnit = parcel.readFloat();
		mUnitText = parcel.readString();
    }
	
	public static final Creator<FoodItemInfo> CREATOR = new Creator<FoodItemInfo>() {

        @Override
        public FoodItemInfo createFromParcel(Parcel parcel) {
            return new FoodItemInfo(parcel);
        }

        @Override
        public FoodItemInfo[] newArray(int size) {
            return new FoodItemInfo[size];
        }
    };
    
	@Override 
	public int describeContents() { 
		return 0; 
	}
	
	@Override 
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(mFoodItem, flags);
		dest.writeString(mName);
		dest.writeInt(mWeightPerUnit);
		dest.writeFloat(mNumCarbsInGramsPerUnit);
		dest.writeString(mUnitText);
	}
	
}
