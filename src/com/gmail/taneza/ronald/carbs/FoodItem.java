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

	private static final long serialVersionUID = 6139044679990035504L;

	/* 
	 * Do not change, rename, or remove existing fields! 
	 * New fields may be added without needing to change serialVersionUID.
	 * More info on serialization:
	 * http://macchiato.com/columns/Durable4.html
	 * http://developer.android.com/reference/java/io/Serializable.html
	 */
	private String mTableName;
	private int mId;
	private int mWeight;
	
	public FoodItem(String tableName, int id, int weight) {
		mTableName = tableName;
		mId = id;
		mWeight = weight;
	}

	public String getTableName() {
		return mTableName;
	}
	
	public int getId() {
		return mId;
	}
	
	public int getWeight() {
		return mWeight;
	}
	
	public void setWeight(int weight) {
		mWeight = weight;
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
