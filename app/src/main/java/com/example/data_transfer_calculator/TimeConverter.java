package com.example.data_transfer_calculator;

import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

class TimeConverter {
    private static void calculateStatements(MainActivity mainActivity) {
        //Gets the value of the inputs and performs calculations based on spinner state

        //Try to perform calculations
        try {
            //Datasize calculations
            switch (mainActivity.datasize_notation) {
                case "KB":
                case "Kilobytes":
                    mainActivity.datasize_in_MB = Double.parseDouble(mainActivity.datasize_text_input.getText().toString()) / 1000;
                    //Number that is displayed on screen
                    mainActivity.display_datasize_in_MB = mainActivity.datasize_in_MB * 1000;
                    break;

                case "MB":
                case "Megabytes":
                    mainActivity.datasize_in_MB = Double.parseDouble(mainActivity.datasize_text_input.getText().toString());
                    //Number that is displayed on screen
                    mainActivity.display_datasize_in_MB = mainActivity.datasize_in_MB;
                    break;

                case "GB":
                case "Gigabytes":
                    mainActivity.datasize_in_MB = Double.parseDouble(mainActivity.datasize_text_input.getText().toString()) * 1000;
                    //Number that is displayed on screen
                    mainActivity.display_datasize_in_MB = mainActivity.datasize_in_MB / 1000;
                    break;

                case "TB":
                case "Terabytes":
                    mainActivity.datasize_in_MB = Double.parseDouble(mainActivity.datasize_text_input.getText().toString()) * 1000000;
                    //Number that is displayed on screen
                    mainActivity.display_datasize_in_MB = mainActivity.datasize_in_MB / 1000000;
                    break;
            }

            //Transfer rate calculations
            mainActivity.transferRate = Double.parseDouble(mainActivity.transferRate_text_input.getText().toString());
            switch (mainActivity.transferRate_notation) {
                case "KBps":
                case "Kilobytes p/s":
                    mainActivity.total_seconds = (mainActivity.datasize_in_MB / mainActivity.transferRate) * 1000;
                    break;

                case "MBps":
                case "Megabytes p/s":
                    mainActivity.total_seconds = (mainActivity.datasize_in_MB / mainActivity.transferRate);
                    break;

                case "GBps":
                case "Gigabytes p/s":
                    mainActivity.total_seconds = (mainActivity.datasize_in_MB / mainActivity.transferRate) / 1000;
                    break;
            }

            //Catch non-number and divide by 0 errors and display toast accordingly
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(mainActivity, "Enter numbers into both fields", Toast.LENGTH_SHORT);
            toast.show();
        } catch (ArithmeticException e) {
            Toast toast = Toast.makeText(mainActivity, "Enter numbers other than 0", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    static int[] calculateTime(int total_seconds) {
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

    static void displayTime(MainActivity mainActivity) {
        //Updates the text views on screen based on user inputs

        //Call methods to acquire all relevant variables
        getLayoutVariables(mainActivity);
        calculateStatements(mainActivity);

        //Initialise used text views
        TextView transfer_statement = mainActivity.findViewById(R.id.transfer_statement);
        TextView time_statement = mainActivity.findViewById(R.id.time_statement);
        String data_statement;
        String calculate_statement;

        //If precision mode on, display data_statement accordingly
        if (AppUtilities.getPrecisionPref(mainActivity)) {
            data_statement = String.format(Locale.ENGLISH,
                    "<html><font color=blue>%.2f</font> %s at <font color=blue>%.2f</font> %s will take</html>",
                    mainActivity.display_datasize_in_MB, mainActivity.datasize_notation, mainActivity.transferRate, mainActivity.transferRate_notation);
            transfer_statement.setText(Html.fromHtml(data_statement, 0));
        }

        //If precision mode off, display data_statement accordingly
        else {
            data_statement = String.format(Locale.ENGLISH,
                    "<html><font color=blue>%.0f</font> %s at <font color=blue>%.0f</font> %s will take</html>",
                    mainActivity.display_datasize_in_MB, mainActivity.datasize_notation, mainActivity.transferRate, mainActivity.transferRate_notation);
            transfer_statement.setText(Html.fromHtml(data_statement, 0));
        }

        //Display calculated time
        calculate_statement = String.format(Locale.ENGLISH,
                "<html>~ <b>%d</b> Hours <b>%d</b> Minutes <b>%d</b> Seconds",
                calculateTime((int) mainActivity.total_seconds)[0], calculateTime((int) mainActivity.total_seconds)[1], calculateTime((int) mainActivity.total_seconds)[2]);
        time_statement.setText(Html.fromHtml(calculate_statement, 0));
    }

    static void getLayoutVariables(MainActivity mainActivity) {
        //Get the spinners
        mainActivity.datasize_spinner = mainActivity.findViewById(R.id.spinner_datasize);
        mainActivity.transferRate_spinner = mainActivity.findViewById(R.id.spinner_transfer_rates);

        //Get current selected item from spinners
        mainActivity.datasize_notation = mainActivity.datasize_spinner.getSelectedItem().toString();
        mainActivity.transferRate_notation = mainActivity.transferRate_spinner.getSelectedItem().toString();

        //Get current entered inputs
        mainActivity.datasize_text_input = mainActivity.findViewById(R.id.datasize_input);
        mainActivity.transferRate_text_input = mainActivity.findViewById(R.id.transfer_rate_input);
    }
}
