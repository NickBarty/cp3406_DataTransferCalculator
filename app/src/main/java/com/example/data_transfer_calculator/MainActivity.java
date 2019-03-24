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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String DATASIZE_SPINNER = "datasize_spinner";
    public static final String TRANSFER_RATE_SPINNER = "transfer_rate_spinner";

    double datasize = 0;
    double transferRate = 0;
    //Set the spinners
    Spinner datasize_spinner;
    Spinner transferRate_spinner;

    //Get selected item from spinners
    String datasize_string;
    String transferRate_string;

    //Get entered inputs
    EditText datasize_int;
    EditText transferRate_int;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getLayoutComponents();
    }

    @Override
    protected void onResume() {
        applySettings();
        loadSpinners();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            saveSpinners();
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        }
    }

    public void onClick(View view) {
        hideKeyboard(MainActivity.this);
        calculateTime();
    }

    public void calculateTime() {
        getLayoutComponents();
        double datasize_statement = 0.0;
        double time = 0;

        try {
            if (datasize_string.equals("GB") || datasize_string.equals("Gigabytes")) {
                datasize = Double.parseDouble(datasize_int.getText().toString()) * 1000;
                datasize_statement = datasize / 1000;
            } else if (datasize_string.equals("MB") || datasize_string.equals("Megabytes")) {
                datasize = Double.parseDouble(datasize_int.getText().toString());
                datasize_statement = datasize;
            }


            if (transferRate_string.equals("MBps") || transferRate_string.equals("Megabytes p/s")) {
                transferRate = Double.parseDouble(transferRate_int.getText().toString());
                time = datasize / transferRate;
            } else if (transferRate_string.equals("Mbps") || transferRate_string.equals("Megabits p/s")) {
                transferRate = Double.parseDouble(transferRate_int.getText().toString());
                time = (datasize / transferRate) * 8;
            }
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(MainActivity.this, "Enter numbers into both fields", Toast.LENGTH_SHORT);
            toast.show();
        }

        double hours = time / 3600.0;
        double mins = (time % 3600) / 60.0;
        double seconds = time % 60.0;

        TextView transfer_statement = findViewById(R.id.transfer_statement);
        TextView time_statement = findViewById(R.id.time_statement);
        String data_statement;
        String calculate_statement;

        //If precision mode is on, display the statements accordingly
        if (getPrecisionPref()) {
            data_statement = String.format(Locale.ENGLISH,
                    "<html><font color=blue>%.2f</font> %s at <font color=blue>%.2f</font> %s will take</html>",
                    datasize_statement, datasize_string, transferRate, transferRate_string);
            transfer_statement.setText(Html.fromHtml(data_statement,0));
        } else {
            data_statement = String.format(Locale.ENGLISH,
                    "<html><font color=blue>%.0f</font> %s at <font color=blue>%.0f</font> %s will take</html>",
                    datasize_statement, datasize_string, transferRate, transferRate_string);
            transfer_statement.setText(Html.fromHtml(data_statement,0));
        }

        calculate_statement = String.format(Locale.ENGLISH,
                "<html><b>%.0f</b> Hours <b>%.0f</b> Minutes <b>%.0f</b> Seconds",
                hours, mins, seconds);
        time_statement.setText(Html.fromHtml(calculate_statement,0));
    }

    public void applySettings() {
        if (getPrecisionPref()) {
            //Set keyboard to decimals
            datasize_int.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            transferRate_int.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            //Set keyboard to numbers
            datasize_int.setInputType(InputType.TYPE_CLASS_NUMBER);
            transferRate_int.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if (getShorthandPref()) {
            ArrayAdapter<String> datasizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.datasizes_short));
            ArrayAdapter<String> transferRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.transfer_rates_short));
            datasize_spinner.setAdapter(datasizeAdapter);
            transferRate_spinner.setAdapter(transferRateAdapter);
        } else {
            ArrayAdapter<String> datasizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.datasizes_long));
            ArrayAdapter<String> transferRateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.transfer_rates_long));
            datasize_spinner.setAdapter(datasizeAdapter);
            transferRate_spinner.setAdapter(transferRateAdapter);
        }
    }

    public void saveSpinners() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putInt(DATASIZE_SPINNER, datasize_spinner.getSelectedItemPosition());
        prefEditor.putInt(TRANSFER_RATE_SPINNER, transferRate_spinner.getSelectedItemPosition());
        prefEditor.apply();
    }

    public void loadSpinners() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, 0);
        int datasize_value = sharedPreferences.getInt(DATASIZE_SPINNER, 0);
        int transfer_rate_value = sharedPreferences.getInt(TRANSFER_RATE_SPINNER, 0);

        datasize_spinner.setSelection(datasize_value);
        transferRate_spinner.setSelection(transfer_rate_value);
    }

    public void getLayoutComponents() {
        //Get the spinners
        datasize_spinner = findViewById(R.id.spinner_datasize);
        transferRate_spinner = findViewById(R.id.spinner_transfer_rates);

        //Get selected item from spinners
        datasize_string = datasize_spinner.getSelectedItem().toString();
        transferRate_string = transferRate_spinner.getSelectedItem().toString();

        //Get entered inputs
        datasize_int = findViewById(R.id.datasize_input);
        transferRate_int = findViewById(R.id.transfer_rate_input);

        //Set enter key listener for datasize editText field
        datasize_int.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    calculateTime();
                    return true;
                }
                return false;
            }
        });

        //Set enter key listener for transfer rate editText field
        transferRate_int.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    calculateTime();
                    return true;
                }
                return false;
            }
        });
    }

    public boolean getPrecisionPref() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", 0);
        return sharedPreferences.getBoolean("precision_mode", false);
    }

    public boolean getShorthandPref() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", 0);
        return sharedPreferences.getBoolean("shorthand_notation", false);
    }
}