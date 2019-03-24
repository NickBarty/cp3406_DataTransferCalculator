package com.example.data_transfer_calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;

public class Settings extends AppCompatActivity {
    Switch precision_mode;
    Switch shorthand_notation;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRECISION_MODE = "precision_mode";
    public static final String SHORTHAND_NOTATION = "shorthand_notation";

    boolean precision;
    boolean shorthand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        precision_mode = findViewById(R.id.switch_precisionMode);
        shorthand_notation = findViewById(R.id.switch_shorthandNotation);

        loadData();
        updateViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //Add back button to toolbar
        onBackPressed();
        return true;
    }

    public void onClickSaveData(View view) {
        //Saves the preferences whenever a switch is clicked
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PRECISION_MODE, precision_mode.isChecked());
        editor.putBoolean(SHORTHAND_NOTATION, shorthand_notation.isChecked());
        editor.apply();
    }

    public void loadData() {
        //Loads data when activity is created
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, 0);
        precision = sharedPreferences.getBoolean(PRECISION_MODE, false);
        shorthand = sharedPreferences.getBoolean(SHORTHAND_NOTATION, false);
    }

    public void updateViews() {
        //Updates switches state
        precision_mode.setChecked(precision);
        shorthand_notation.setChecked(shorthand);
    }
}
