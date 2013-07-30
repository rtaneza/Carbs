package com.gmail.taneza.ronald.carbs;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodItem implements Parcelable {

	public String EnglishName;
	public String DutchName;
	public int WeightInGrams;
	public float NumCarbsInGrams;
	public int ProductCode;
	
	public FoodItem(String englishName, String dutchName, int weightInGrams, float numCarbsInGrams, int productCode) {
		EnglishName = englishName;
		DutchName = dutchName;
		WeightInGrams = weightInGrams;
		NumCarbsInGrams = numCarbsInGrams;
		ProductCode = productCode;
	}
	
	private FoodItem(Parcel parcel) {
		EnglishName = parcel.readString();
		DutchName = parcel.readString();
		WeightInGrams = parcel.readInt();
		NumCarbsInGrams = parcel.readFloat();
		ProductCode = parcel.readInt();
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
		dest.writeString(EnglishName);
		dest.writeString(DutchName);
		dest.writeInt(WeightInGrams);
		dest.writeFloat(NumCarbsInGrams);
		dest.writeInt(ProductCode);
	}
}
