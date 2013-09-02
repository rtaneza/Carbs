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

package com.gmail.taneza.ronald.carbs.common;

import android.app.Application;

public class CarbsApp extends Application {
	private FoodDbAdapter mFoodDbAdapter;

    @Override
    public void onCreate() {
        super.onCreate(); 
        mFoodDbAdapter = new FoodDbAdapter(this);
		mFoodDbAdapter.open();

    }

    public FoodDbAdapter getFoodDbAdapter(){
        return mFoodDbAdapter;
    }

    @Override
    public void onTerminate() {
        mFoodDbAdapter.close();
        super.onTerminate();
    }   
}