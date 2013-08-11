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

	private static final long serialVersionUID = 6139044679990035503L;

	public static final String MY_FOOD_TEXT = "My Food";
	
	/* 
	 * Do not change, rename, or remove existing fields! 
	 * New fields may be added without needing to change serialVersionUID.
	 * More info on serialization:
	 * http://macchiato.com/columns/Durable4.html
	 * http://developer.android.com/reference/java/io/Serializable.html
	 */
//	public int mProductCode;
//	public String mEnglishName;
//	public String mDutchName;
//	public int mWeight; // weight input by the user
//	public int mWeightPerUnit; // e.g. 100 g
//	public float mNumCarbsInGramsPerUnit; // e.g. 30 g
//	public String mUnitText; // e.g. g or ml
//	public String mTableName; // Database table name
	
	private String mTableName;
	private int mId;
	private int mWeight;
	private FoodDbAdapter mFoodDbAdapter;
	private FoodDbItem mFoodDbItem;
	
	// TODO: 
	// Rename FoodItem to FoodItemStruct
	// Rename FoodDbItem to FoodItem -> this is what will be used in lists
	// new FoodItem(foodItemStruct)
	
	public FoodItem(FoodDbAdapter foodDbAdapter, String tableName, int id, int weight) {
		mFoodDbAdapter = foodDbAdapter;
		mTableName = tableName;
		mId = id;
		mWeight = weight;
		
		mFoodDbItem = foodDbAdapter.getFoodDbItem(tableName, id);
	}

	public String getTableName() {
		return mTableName;
	}
	
	public int getWeight() {
		return mWeight;
	}
	
	public void setWeight(int weight) {
		mWeight = weight;
	}
	
	public String getName() {
		return mFoodDbItem.getName();
	}

	public int getWeightPerUnit() {
		return mFoodDbItem.getWeightPerUnit();
	}

	public float getNumCarbsInGramsPerUnit() {
		return mFoodDbItem.getNumCarbsInGramsPerUnit();
	}

	public String getUnitText() {
		return mFoodDbItem.getUnitText();
	}
	
	public float getNumCarbsInGrams() {
		return (getNumCarbsInGramsPerUnit() * mWeight) / getWeightPerUnit();
	}
	
	private FoodItem(Parcel parcel) {
		mTableName = parcel.readString();
		mId = parcel.readInt();
		mWeight = parcel.readInt();
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
		dest.writeString(mTableName);
		dest.writeInt(mId);
		dest.writeInt(mWeight);
	}
	
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
		  return false;
		}
		FoodItem rhs = (FoodItem) obj;
		return new EqualsBuilder()
        	.append(mTableName, rhs.mTableName)
        	.append(mId, rhs.mId)
            .append(mWeight, rhs.mWeight)
            .isEquals();
	}
	
	public int hashCode() {
	     // random odd number constants
	     return new HashCodeBuilder(12127, 9847)
	     	.append(mTableName)
	     	.append(mId)
	     	.append(mWeight)
	        .toHashCode();
	}
}
