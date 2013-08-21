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

import android.app.Activity;
import android.support.v4.app.ListFragment;

public class BaseListFragment extends ListFragment {

	protected MainActivityNotifier mMainActivityNotifier;
	protected FoodDbAdapter mFoodDbAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mFoodDbAdapter = ((CarbsApp)getActivity().getApplication()).getFoodDbAdapter();
		
	    try {
	    	mMainActivityNotifier = (MainActivityNotifier) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement MainActivityNotifier");
	    }
	}
}
