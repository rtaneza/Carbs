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

package com.gmail.taneza.ronald.carbs.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.gmail.taneza.ronald.carbs.R;

public class HelpActivity extends ActionBarActivity {

    public enum Mode {
        Main,
        MyFoods
    }
    
    public final static String HELP_ACTIVITY_MODE_MESSAGE = "com.gmail.taneza.ronald.carbs.HELP_ACTIVITY_MODE_MESSAGE";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_help);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Get the message from the intent
        Intent intent = getIntent();
        Mode mode = Mode.values()[intent.getIntExtra(HELP_ACTIVITY_MODE_MESSAGE, Mode.Main.ordinal())];
        
        int helpStringId = -1;
        switch (mode) {
            case Main:
                helpStringId = R.string.main_help_text;
                break;

            case MyFoods:
                helpStringId = R.string.my_foods_help_text;
                break;
                
            default:
                throw new IllegalArgumentException("Invalid mode");
        }
        
        TextView helpTextView = (TextView)findViewById(R.id.help_text_view);
        helpTextView.setText(helpStringId);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // We don't use NavUtils.navigateUpFromSameTask() because Help may be started from either Main or MyFoods.
                // The "finish()" call simulates a "back" button press.
                // The HelpActivity cannot be started from other apps, so this should be fine.
                finish();
                return true;
            }
        return super.onOptionsItemSelected(item);
    }
}
