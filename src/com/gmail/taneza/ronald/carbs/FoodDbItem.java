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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FoodDbItem {

	private String mTableName;
	private int mId;
	
	public FoodDbItem(String tableName, int id) {
		mTableName = tableName;
		mId = id;
	}

	public FoodDbItem(FoodItem foodItem) {
		this(foodItem.getTableName(), foodItem.getId());
	}
	
	public String getTableName() {
		return mTableName;
	}
	
	public int getId() {
		return mId;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
		  return false;
		}
		FoodDbItem rhs = (FoodDbItem) obj;
		return new EqualsBuilder()
        	.append(mTableName, rhs.mTableName)
        	.append(mId, rhs.mId)
            .isEquals();
	}
	
	public int hashCode() {
	     // random odd number constants
	     return new HashCodeBuilder(72345, 1563)
	     	.append(mTableName)
	     	.append(mId)
	        .toHashCode();
	}
}
