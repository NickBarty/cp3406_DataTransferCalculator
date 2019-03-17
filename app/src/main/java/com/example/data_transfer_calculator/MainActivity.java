package com.example.data_transfer_calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int datasize = 0;
    int transferRate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void calculateTime(View view) {
        //Set the spinners
        Spinner datasize_spinner = findViewById(R.id.spinner_datasize);
        Spinner transferRate_spinner = findViewById(R.id.spinner_transfer_rates);

        //Get selected item from spinners
        String datasize_string = datasize_spinner.getSelectedItem().toString();
        String transferRate_string = transferRate_spinner.getSelectedItem().toString();

        //Get entered inputs
        EditText datasize_int = findViewById(R.id.datasize_input);
        EditText transferRate_int = findViewById(R.id.transfer_rate_input);

        String datasize_string_statement = "0";
        int time = 0;

        try {

            if (datasize_string.equals("MB")) {
                datasize = Integer.parseInt(datasize_int.getText().toString());
                datasize_string_statement = String.valueOf(datasize);
            } else if (datasize_string.equals("GB")) {
                datasize = Integer.parseInt(datasize_int.getText().toString()) * 1000;
                datasize_string_statement = String.valueOf(datasize / 1000);
            }


            if (transferRate_string.equals("Mbps")) {
                transferRate = Integer.parseInt(transferRate_int.getText().toString());
                time = (datasize / transferRate) * 8;
            } else if (transferRate_string.equals("MBps")) {
                transferRate = Integer.parseInt(transferRate_int.getText().toString());
                time = datasize / transferRate;
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

}