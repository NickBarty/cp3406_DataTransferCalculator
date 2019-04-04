package com.example.data_transfer_calculator;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    //Set shared preferences that is used to save the state of objects
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String DECIMAL_MODE = "decimal_mode";
    public static final String SHORTHAND_NOTATION = "shorthand_notation";
    public static final String DATASIZE_SPINNER = "datasize_spinner";
    public static final String TRANSFER_RATE_SPINNER = "transfer_rate_spinner";

    //Initialise calculation variables
    double datasize_in_MB = 0;
    double transferRate = 0;
    double display_datasize_in_MB = 0.0;
    double total_seconds = 0;

    //Initialise the spinners
    Spinner datasize_spinner;
    Spinner transferRate_spinner;

    //Initialise strings to store spinner selections
    String datasize_notation;
    String transferRate_notation;

    //Initialise edit text fields to get user inputs
    EditText datasize_text_input;
    EditText transferRate_text_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set screen orientation to only be portrait & content to be displayed on startup
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        //Create toolbar object and set it
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set/get the layout variables & set the key listeners for the app
        TimeConverter.getLayoutVariables(this);
        AppUtilities.setKeyListeners(this);
    }

    @Override
    protected void onResume() {
        //Calls methods to apply current settings and load the spinners when MainActivity is resumed
        AppUtilities.applySettings(this);
        AppUtilities.loadSpinners(this);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        //Start settings activity when settings is selected
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        //Calls appropriate class methods when calculate time button is pressed
        AppUtilities.hideKeyboard(MainActivity.this);
        TimeConverter.displayTime(this);
    }

}