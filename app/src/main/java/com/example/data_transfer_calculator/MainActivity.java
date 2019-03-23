package com.example.data_transfer_calculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int datasize = 0;
    int transferRate = 0;
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
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        applySettings();
        super.onResume();
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

    public void calculateTime(View view) {
        getLayoutComponents();
        String datasize_string_statement = "0";
        int time = 0;

        try {
            if (datasize_string.equals("GB")) {
                datasize = Integer.parseInt(datasize_int.getText().toString()) * 1000;
                datasize_string_statement = String.valueOf(datasize / 1000);
            } else if (datasize_string.equals("MB")) {
                datasize = Integer.parseInt(datasize_int.getText().toString());
                datasize_string_statement = String.valueOf(datasize);
            }


            if (transferRate_string.equals("MBps")) {
                transferRate = Integer.parseInt(transferRate_int.getText().toString());
                time = datasize / transferRate;
            } else if (transferRate_string.equals("Mbps")) {
                transferRate = Integer.parseInt(transferRate_int.getText().toString());
                time = (datasize / transferRate) * 8;
            }
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(MainActivity.this, "Enter numbers into both fields", Toast.LENGTH_SHORT);
            toast.show();
        }

        int hours = time / 3600;
        int mins = (time % 3600) / 60;
        int seconds = time % 60;

        TextView transfer_statement = findViewById(R.id.transfer_statement);
        StringBuilder transfer_statement_text = new StringBuilder("Transferring " +
                datasize_string_statement + datasize_string + " at " + transferRate +
                transferRate_string + " will take");
        transfer_statement.setText(transfer_statement_text);


        TextView time_statement = findViewById(R.id.time_statement);
        StringBuilder time_statement_text = new StringBuilder(hours + " Hours "
                + mins + " Minutes " + seconds + " Seconds");
        time_statement.setText(time_statement_text);
    }

    public boolean getPrecisionPref() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", 0);
        return sharedPreferences.getBoolean("precision_mode", false);
    }

    public boolean getShorthandPref() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", 0);
        return sharedPreferences.getBoolean("shorthand_notation", true);
    }
}