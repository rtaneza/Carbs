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

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItem implements Parcelable {
	
	private String mTableName;
	private int mId;
	private int mWeight;
	private long mTimeAddedMsec;
	private boolean mChecked;
	
	public FoodItem(String tableName, int id, int weight, long timeAddedMsec) {
		FoodItemListSerializer.verifyTableNameIsValidOrThrow(tableName);		
		mTableName = tableName;
		mId = id;
		mWeight = weight;
		mTimeAddedMsec = timeAddedMsec;
	}

	public FoodItem(String tableName, int id, int weight) {
		this(tableName, id, weight, (new Date()).getTime());
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
	
	public long getTimeAddedMsec() {
		return mTimeAddedMsec;
	}
	
	public boolean getChecked() {
		return mChecked;
	}

	public void toggleChecked() {
		mChecked = !mChecked;
	}
	
	private FoodItem(Parcel parcel) {
		mTableName = parcel.readString();
		mId = parcel.readInt();
		mWeight = parcel.readInt();
		mTimeAddedMsec = parcel.readLong();
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
		dest.writeLong(mTimeAddedMsec);
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
            .append(mTimeAddedMsec, rhs.mTimeAddedMsec)
            .isEquals();
	}

	public boolean equalsExceptTimeAdded(Object obj) {
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
	     	.append(mTimeAddedMsec)
	        .toHashCode();
	}
}
