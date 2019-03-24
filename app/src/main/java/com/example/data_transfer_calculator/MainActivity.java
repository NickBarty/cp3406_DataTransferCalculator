package com.example.data_transfer_calculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //Set shared preferences that is used to save the state of objects
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String DATASIZE_SPINNER = "datasize_spinner";
    public static final String TRANSFER_RATE_SPINNER = "transfer_rate_spinner";

    //Initialise calculation variables
    double datasize_in_KB = 0;
    double transferRate = 0;
    double display_datasize_in_MB = 0.0;
    int total_seconds = 0;

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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getLayoutVariables();
        setKeyListeners();
    }

    @Override
    protected void onResume() {
        //Calls methods to apply current settings and load the spinners when MainActivity is resumed
        applySettings();
        loadSpinners();
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
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideKeyboard(Activity activity) {
        //Hides the keyboard if its on screen when called
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        }
    }

    public void onClick(View view) {
        //Calls appropriate methods when calculate time button is pressed
        hideKeyboard(MainActivity.this);
        displayTime();
    }

    public void displayTime() {
        //Updates the text views on screen based on user inputs

        //Call methods to acquire all relevant variables
        getLayoutVariables();
        calculateStatements();

        //Initialise used text views
        TextView transfer_statement = findViewById(R.id.transfer_statement);
        TextView time_statement = findViewById(R.id.time_statement);
        String data_statement;
        String calculate_statement;

        //If precision mode on, display data_statement accordingly
        if (getPrecisionPref()) {
            data_statement = String.format(Locale.ENGLISH,
                    "<html><font color=blue>%.2f</font> %s at <font color=blue>%.2f</font> %s will take</html>",
                    display_datasize_in_MB, datasize_notation, transferRate, transferRate_notation);
            transfer_statement.setText(Html.fromHtml(data_statement, 0));
        }

        //If precision mode off, display data_statement accordingly
        else {
            data_statement = String.format(Locale.ENGLISH,
                    "<html><font color=blue>%.0f</font> %s at <font color=blue>%.0f</font> %s will take</html>",
                    display_datasize_in_MB, datasize_notation, transferRate, transferRate_notation);
            transfer_statement.setText(Html.fromHtml(data_statement, 0));
        }

        //Display calculated time
        calculate_statement = String.format(Locale.ENGLISH,
                "<html><b>%d</b> Hours <b>%d</b> Minutes <b>%d</b> Seconds",
                calculateTime(total_seconds)[0], calculateTime(total_seconds)[1], calculateTime(total_seconds)[2]);
        time_statement.setText(Html.fromHtml(calculate_statement, 0));
    }

    public int[] calculateTime(int total_seconds) {
        //Converts derived total seconds from given inputs to hours/minutes/seconds
        int time_values[] = new int[3];

        //Calculate Hours
        time_values[0] = total_seconds / 3600;
        //Calculate Minutes
        time_values[1] = (total_seconds % 3600) / 60;
        //Calculate Seconds
        time_values[2] = total_seconds % 60;
        return time_values;
    }

    public void applySettings() {
        //Finds the selected settings and applies the appropriate constraints to the app

        //Set spinner lists
        ArrayAdapter<String> datasizeAdapter_short = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.datasizes_short));
        ArrayAdapter<String> transferRateAdapter_short = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.transfer_rates_short));
        ArrayAdapter<String> datasizeAdapter_long = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.datasizes_long));
        ArrayAdapter<String> transferRateAdapter_long = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.transfer_rates_long));


        if (getPrecisionPref()) {
            //Set keyboard to decimals
            datasize_text_input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            transferRate_text_input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            //Set keyboard to numbers
            datasize_text_input.setInputType(InputType.TYPE_CLASS_NUMBER);
            transferRate_text_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if (getShorthandPref()) {
            //Set spinners to shorthand notations
            datasize_spinner.setAdapter(datasizeAdapter_short);
            transferRate_spinner.setAdapter(transferRateAdapter_short);
        } else {
            //Set spinners to full length names
            datasize_spinner.setAdapter(datasizeAdapter_long);
            transferRate_spinner.setAdapter(transferRateAdapter_long);
        }
    }

    public void saveSpinners() {
        //Saves the state of the spinners when called

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putInt(DATASIZE_SPINNER, datasize_spinner.getSelectedItemPosition());
        prefEditor.putInt(TRANSFER_RATE_SPINNER, transferRate_spinner.getSelectedItemPosition());
        prefEditor.apply();
    }

    public void loadSpinners() {
        //Loads the state of the spinners when called

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, 0);
        int datasize_value = sharedPreferences.getInt(DATASIZE_SPINNER, 0);
        int transfer_rate_value = sharedPreferences.getInt(TRANSFER_RATE_SPINNER, 0);

        datasize_spinner.setSelection(datasize_value);
        transferRate_spinner.setSelection(transfer_rate_value);
    }

    public void calculateStatements() {
        //Gets the value of the inputs and performs calculations based on spinner state

        //Try to perform calculations
        try {
            //Datasize calculations
            switch (datasize_notation) {
                case "KB":
                case "Kilobytes":
                    datasize_in_KB = Double.parseDouble(datasize_text_input.getText().toString());
                    //Number that is displayed on screen
                    display_datasize_in_MB = datasize_in_KB;
                    break;

                case "MB":
                case "Megabytes":
                    datasize_in_KB = Double.parseDouble(datasize_text_input.getText().toString()) * 1000;
                    //Number that is displayed on screen
                    display_datasize_in_MB = datasize_in_KB / 1000;
                    break;

                case "GB":
                case "Gigabytes":
                    datasize_in_KB = Double.parseDouble(datasize_text_input.getText().toString()) * 1000000;
                    //Number that is displayed on screen
                    display_datasize_in_MB = datasize_in_KB / 1000000;
                    break;

                case "TB":
                case "Terabytes":
                    datasize_in_KB = Double.parseDouble(datasize_text_input.getText().toString()) * 1000000000;
                    //Number that is displayed on screen
                    display_datasize_in_MB = datasize_in_KB / 1000000000;
                    break;
            }

            //Transfer rate calculations
            transferRate = Double.parseDouble(transferRate_text_input.getText().toString());
            switch (transferRate_notation) {
                case "KBps":
                case "Kilobytes p/s":
                    total_seconds = (int) (datasize_in_KB / transferRate);
                    break;

                case "MBps":
                case "Megabytes p/s":
                    total_seconds = (int) (datasize_in_KB / transferRate) / 1000;
                    break;

                case "GBps":
                case "Gigabytes p/s":
                    total_seconds = (int) (datasize_in_KB / transferRate) / 1000000;
                    break;
            }

            //Catch non-number and divide by 0 errors and display toast accordingly
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(MainActivity.this, "Enter numbers into both fields", Toast.LENGTH_SHORT);
            toast.show();
        } catch (ArithmeticException e) {
            Toast toast = Toast.makeText(MainActivity.this, "Enter numbers other than 0", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void getLayoutVariables() {
        //Get the spinners
        datasize_spinner = findViewById(R.id.spinner_datasize);
        transferRate_spinner = findViewById(R.id.spinner_transfer_rates);

        //Get selected item from spinners
        datasize_notation = datasize_spinner.getSelectedItem().toString();
        transferRate_notation = transferRate_spinner.getSelectedItem().toString();

        //Get entered inputs
        datasize_text_input = findViewById(R.id.datasize_input);
        transferRate_text_input = findViewById(R.id.transfer_rate_input);
    }

    public void setKeyListeners() {
        //Set enter key listener for datasize_in_KB editText field
        datasize_text_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    displayTime();
                    return true;
                }
                return false;
            }
        });

        //Set enter key listener for transfer rate editText field
        transferRate_text_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    displayTime();
                    return true;
                }
                return false;
            }
        });

        //Tell the app to save the selection of the spinners when either spinner is opened
        datasize_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveSpinners();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                saveSpinners();
            }
        });

        transferRate_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveSpinners();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                saveSpinners();
            }
        });
    }

    public boolean getPrecisionPref() {
        //Gets the precision setting preference
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", 0);
        return sharedPreferences.getBoolean("precision_mode", false);
    }

    public boolean getShorthandPref() {
        //Gets the shorthand notation preference
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", 0);
        return sharedPreferences.getBoolean("shorthand_notation", false);
    }
}