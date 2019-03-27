package com.example.data_transfer_calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Objects;

class AppUtilities {
    private static void saveSpinners(MainActivity mainActivity) {
        //Saves the state of the spinners when called

        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(MainActivity.SHARED_PREFS, 0);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putInt(MainActivity.DATASIZE_SPINNER, mainActivity.datasize_spinner.getSelectedItemPosition());
        prefEditor.putInt(MainActivity.TRANSFER_RATE_SPINNER, mainActivity.transferRate_spinner.getSelectedItemPosition());
        prefEditor.apply();
    }

    static void loadSpinners(MainActivity mainActivity) {
        //Loads the state of the spinners when called

        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(MainActivity.SHARED_PREFS, 0);
        int datasize_value = sharedPreferences.getInt(MainActivity.DATASIZE_SPINNER, 0);
        int transfer_rate_value = sharedPreferences.getInt(MainActivity.TRANSFER_RATE_SPINNER, 0);

        mainActivity.datasize_spinner.setSelection(datasize_value);
        mainActivity.transferRate_spinner.setSelection(transfer_rate_value);
    }

    static void hideKeyboard(Activity activity) {
        //Hides the keyboard if its on screen when called
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        }
    }

    static void applySettings(MainActivity mainActivity) {
        //Finds the selected settings and applies the appropriate constraints to the app

        //Set spinner lists
        ArrayAdapter<String> datasizeAdapter_short = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item, mainActivity.getResources().getStringArray(R.array.datasizes_short));
        ArrayAdapter<String> transferRateAdapter_short = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item, mainActivity.getResources().getStringArray(R.array.transfer_rates_short));
        ArrayAdapter<String> datasizeAdapter_long = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item, mainActivity.getResources().getStringArray(R.array.datasizes_long));
        ArrayAdapter<String> transferRateAdapter_long = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_dropdown_item, mainActivity.getResources().getStringArray(R.array.transfer_rates_long));

        if (getPrecisionPref(mainActivity)) {
            //Set keyboard to decimals
            mainActivity.datasize_text_input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            mainActivity.transferRate_text_input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            //Set keyboard to numbers
            mainActivity.datasize_text_input.setInputType(InputType.TYPE_CLASS_NUMBER);
            mainActivity.transferRate_text_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if (getShorthandPref(mainActivity)) {
            //Set spinners to shorthand notations
            mainActivity.datasize_spinner.setAdapter(datasizeAdapter_short);
            mainActivity.transferRate_spinner.setAdapter(transferRateAdapter_short);
        } else {
            //Set spinners to full length names
            mainActivity.datasize_spinner.setAdapter(datasizeAdapter_long);
            mainActivity.transferRate_spinner.setAdapter(transferRateAdapter_long);
        }
    }

    static void setKeyListeners(final MainActivity mainActivity) {
        //Set enter key listener for datasize_in_MB editText field
        mainActivity.datasize_text_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    TimeConverter.displayTime(mainActivity);
                    return true;
                }
                return false;
            }
        });

        //Set enter key listener for transfer rate editText field
        mainActivity.transferRate_text_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    TimeConverter.displayTime(mainActivity);
                    return true;
                }
                return false;
            }
        });

        //Tell the app to save the selection of the spinners when either spinner is opened
        mainActivity.datasize_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveSpinners(mainActivity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                saveSpinners(mainActivity);
            }
        });

        mainActivity.transferRate_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveSpinners(mainActivity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                saveSpinners(mainActivity);
            }
        });
    }

    static boolean getPrecisionPref(MainActivity mainActivity) {
        //Gets the precision setting preference
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("sharedPrefs", 0);
        return sharedPreferences.getBoolean("decimal_mode", false);
    }

    private static boolean getShorthandPref(MainActivity mainActivity) {
        //Gets the shorthand notation preference
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("sharedPrefs", 0);
        return sharedPreferences.getBoolean("shorthand_notation", false);
    }
}
