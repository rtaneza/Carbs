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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItem implements Parcelable, Serializable {

	private static final long serialVersionUID = 6139044679990035502L;

	public static final String MY_FOOD_TEXT = "My Food";
	
	/* 
	 * Do not change, rename, or remove existing fields! 
	 * New fields may be added without needing to change serialVersionUID.
	 * More info on serialization:
	 * http://macchiato.com/columns/Durable4.html
	 * http://developer.android.com/reference/java/io/Serializable.html
	 */
	public int mProductCode;
	public String mEnglishName;
	public String mDutchName;
	public int mWeight; // weight input by the user
	public int mWeightPerUnit; // e.g. 100 g
	public float mNumCarbsInGramsPerUnit; // e.g. 30 g
	public String mUnitText; // e.g. g or ml
	public String mTableName; // Database table name
	
	public FoodItem(int productCode, String englishName, String dutchName, int weight, int weightInUnit, float numCarbsInGramsPerUnit, String unitText, String tableName) {
		mProductCode = productCode;
		mEnglishName = englishName;
		mDutchName = dutchName;
		mWeight = weight;
		mWeightPerUnit = weightInUnit;
		mNumCarbsInGramsPerUnit = numCarbsInGramsPerUnit;
		mUnitText = unitText;
		mTableName = tableName;
	}

	public float getNumCarbsInGrams() {
		return (mNumCarbsInGramsPerUnit * mWeight) / mWeightPerUnit;
	}
	
	private FoodItem(Parcel parcel) {
		mProductCode = parcel.readInt();
		mEnglishName = parcel.readString();
		mDutchName = parcel.readString();
		mWeight = parcel.readInt();
		mWeightPerUnit = parcel.readInt();
		mNumCarbsInGramsPerUnit = parcel.readFloat();
		mUnitText = parcel.readString();
		mTableName = parcel.readString();
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
		dest.writeInt(mWeight);
		dest.writeInt(mWeightPerUnit);
		dest.writeFloat(mNumCarbsInGramsPerUnit);
		dest.writeString(mUnitText);
		dest.writeString(mTableName);
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
		              .append(mWeight, rhs.mWeight)
		              .append(mWeightPerUnit, rhs.mWeightPerUnit)
		              .append(mNumCarbsInGramsPerUnit, rhs.mNumCarbsInGramsPerUnit)
		              .append(mUnitText, rhs.mUnitText)
		              .append(mTableName, rhs.mTableName)
		              .isEquals();
	}
	
	public int hashCode() {
	     // random odd number constants
	     return new HashCodeBuilder(12127, 9847)
	     	.append(mProductCode)
	     	.append(mEnglishName)
	     	.append(mDutchName)
	     	.append(mWeight)
	     	.append(mWeightPerUnit)
	     	.append(mNumCarbsInGramsPerUnit)
	     	.append(mUnitText)
	     	.append(mTableName)
	        .toHashCode();
	}
}
