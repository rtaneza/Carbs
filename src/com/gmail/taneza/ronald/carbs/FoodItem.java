package com.gmail.taneza.ronald.carbs;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItem implements Parcelable {

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
    
	@Override public int describeContents() { 
		return 0; 
	}
	
	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mEnglishName);
		dest.writeString(mDutchName);
		dest.writeInt(mWeightInGrams);
		dest.writeFloat(mNumCarbsInGramsPer100Grams);
		dest.writeInt(mProductCode);
	}
}
