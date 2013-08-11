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

public class FoodDbItem {

	private String mName;
	private int mWeightPerUnit; // e.g. 100 g
	private float mNumCarbsInGramsPerUnit; // e.g. 30 g
	private String mUnitText; // e.g. g or ml
	
	public FoodDbItem(String name, int weightPerUnit, float numCarbsInGramsPerUnit, String unitText) {
		mName = name;
		mWeightPerUnit = weightPerUnit;
		mNumCarbsInGramsPerUnit = numCarbsInGramsPerUnit;
		mUnitText = unitText;
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
}
