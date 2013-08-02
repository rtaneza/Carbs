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

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItem implements Parcelable, Serializable {

	private static final long serialVersionUID = 6139044679990035502L;
	
	public String mEnglishName;
	public String mDutchName;
	public int mWeightInGrams;
	public float mNumCarbsInGramsPer100Grams;
	public int mProductCode;
	
	public float getNumCarbsInGrams() {
		return (mNumCarbsInGramsPer100Grams * mWeightInGrams) / 100;
	}
	
	public FoodItem(String englishName, String dutchName, int weightInGrams, float numCarbsInGramsPer100Grams, int productCode) {
		mEnglishName = englishName;
		mDutchName = dutchName;
		mWeightInGrams = weightInGrams;
		mNumCarbsInGramsPer100Grams = numCarbsInGramsPer100Grams;
		mProductCode = productCode;
	}
	
	private FoodItem(Parcel parcel) {
		mEnglishName = parcel.readString();
		mDutchName = parcel.readString();
		mWeightInGrams = parcel.readInt();
		mNumCarbsInGramsPer100Grams = parcel.readFloat();
		mProductCode = parcel.readInt();
    }

	public static final Creator<FoodItem> CREATOR = new Creator<FoodItem>() {

        @Override
        public FoodItem createFromParcel(Parcel parcel) {
            return new FoodItem(parcel);
        }

        @Override
        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };
    
	@Override 
	public int describeContents() { 
		return 0; 
	}
	
	@Override 
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mEnglishName);
		dest.writeString(mDutchName);
		dest.writeInt(mWeightInGrams);
		dest.writeFloat(mNumCarbsInGramsPer100Grams);
		dest.writeInt(mProductCode);
	}
}
