/*
 * Copyright (C) 2013 Ronald Ta�eza
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItem implements Parcelable, Serializable {

	private static final long serialVersionUID = 6139044679990035502L;

	public int mProductCode;
	public String mEnglishName;
	public String mDutchName;
	public int mWeightInGrams;
	public float mNumCarbsInGramsPer100Grams;
	
	public float getNumCarbsInGrams() {
		return (mNumCarbsInGramsPer100Grams * mWeightInGrams) / 100;
	}
	
	public FoodItem(int productCode, String englishName, String dutchName, int weightInGrams, float numCarbsInGramsPer100Grams) {
		mProductCode = productCode;
		mEnglishName = englishName;
		mDutchName = dutchName;
		mWeightInGrams = weightInGrams;
		mNumCarbsInGramsPer100Grams = numCarbsInGramsPer100Grams;
	}
	
	private FoodItem(Parcel parcel) {
		mProductCode = parcel.readInt();
		mEnglishName = parcel.readString();
		mDutchName = parcel.readString();
		mWeightInGrams = parcel.readInt();
		mNumCarbsInGramsPer100Grams = parcel.readFloat();
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
		dest.writeInt(mProductCode);
		dest.writeString(mEnglishName);
		dest.writeString(mDutchName);
		dest.writeInt(mWeightInGrams);
		dest.writeFloat(mNumCarbsInGramsPer100Grams);
	}
	
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
		  return false;
		}
		FoodItem rhs = (FoodItem) obj;
		return new EqualsBuilder()
		              .append(mProductCode, rhs.mProductCode)
		              .append(mEnglishName, rhs.mEnglishName)
		              .append(mDutchName, rhs.mDutchName)
		              .append(mWeightInGrams, rhs.mWeightInGrams)
		              .append(mNumCarbsInGramsPer100Grams, rhs.mNumCarbsInGramsPer100Grams)
		              .isEquals();
	}
	
	public int hashCode() {
	     // random odd number constants
	     return new HashCodeBuilder(12127, 9847)
	     	.append(mProductCode)
	     	.append(mEnglishName)
	     	.append(mDutchName)
	     	.append(mWeightInGrams)
	     	.append(mNumCarbsInGramsPer100Grams)
	        .toHashCode();
	}
}
