package com.example.data_transfer_calculator;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UnitTests {
    @Test
    public void testCalculateTime() {
//        This test ensures that a given total seconds less than 3600/60/60 (hours/minutes/seconds)
//        calculates as intended
        MainActivity mainActivity = new MainActivity();
        double hours = mainActivity.calculateTime(3600)[0];
        double minutes = mainActivity.calculateTime(60)[1];
        double seconds = mainActivity.calculateTime(10)[2];
        assertEquals(1, hours, 0);
        assertEquals(1, minutes, 0);
        assertEquals(10, seconds, 0);
    }

    @Test
    public void testCalculateTimeExtended() {
//        This test is to ensure that if a given seconds amount is greater than 60/3600/3600
//        that seconds/minutes/hours responds accordingly
        MainActivity mainActivity = new MainActivity();
//        4000 Seconds is between 1 and 2 hours
        double hours = mainActivity.calculateTime(4000)[0];
//        3720 Seconds is 1 hour 2 minutes
        double minutes = mainActivity.calculateTime(3720)[1];
//        100 Seconds is 1 minute 40 seconds
        double seconds = mainActivity.calculateTime(100)[2];
//        Casting to int is used as that's whats displayed on screen
        assertEquals(1, (int) hours, 0);
        assertEquals(2, (int) minutes, 0);
        assertEquals(40, (int) seconds, 0);
    }

    @Test
    public void testLessThanHourTotalSeconds() {
//        This test ensures a random number below 3600 (1 hour) is taken as 0 hours
        MainActivity mainActivity = new MainActivity();
        Random random = new Random();
        int random_total_seconds = random.nextInt(3600);
        int hours = mainActivity.calculateTime(random_total_seconds)[0];
        assertEquals(0, hours);
    }

    @Test
    public void testTransferDisplay() {
        MainActivity mainActivity = new MainActivity();
//        Set arbitrary variables for data/transfer sizes
        mainActivity.display_datasize_in_MB = 20;
        mainActivity.datasize_type = "MB";
        mainActivity.transferRate = 5;
        mainActivity.transferRate_type = "Megabytes";

//        Create a sting of what is outputted in the app
        String test_string = String.format("%.0f %s at %.0f %s will take",
                mainActivity.display_datasize_in_MB, mainActivity.datasize_type,
                mainActivity.transferRate, mainActivity.transferRate_type);

//        Test the string is what we want it to be based on the variables provided
        assertEquals("20 MB at 5 Megabytes will take", test_string);
    }

    @Test
    public void testTimeDisplay() {
        MainActivity mainActivity = new MainActivity();
//        2600 Seconds is 43 minutes 20 seconds
        int[] test_ints = mainActivity.calculateTime(2600);
//        Create a sting of what is outputted in the app
        String test_string = String.format("%d Hours %d Minutes %d Seconds", test_ints[0], test_ints[1], test_ints[2]);

//        Test the string is what we want it to be based on the variables provided
        assertEquals("0 Hours 43 Minutes 20 Seconds", test_string);
    }
}