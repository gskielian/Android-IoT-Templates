package org.foodrev.androidthingpi.leds.base;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by dastechlabs on 7/24/17.
 */

public abstract class LED {

    // LED Status
    private boolean LED_ON;

    // Port Number
    private Gpio mGpio;

    // Reference to Manger Service
    private PeripheralManagerService manager;

    public LED(PeripheralManagerService manager, String gpioName) {
        LED_ON = false;
        this.manager = manager;

        try {
            this.mGpio = manager.openGpio(gpioName);
        } catch (IOException e) {
            Log.w(TAG, "LED: unable to open GPIO: " + gpioName, e);
        }

        try {
            configureOutput();
        } catch (IOException e) {
            Log.w(TAG, "LED: Unable to configure LED", e);
        }
    }

    public void turnOn() {
        try {
            mGpio.setValue(true);
            LED_ON = true;
        } catch (IOException e) {
            Log.w(TAG, "Unable to turn gpio on", e);
        }
    }
    public void turnOff() {
        try {
            mGpio.setValue(false);
            LED_ON = false;
        } catch (IOException e) {
            Log.w(TAG, "Unable to turn gpio off", e);
        }

    }
    public void toggle() {
        if(LED_ON) {
            turnOff();
        } else {
            turnOn();
        }
    }

    void configureOutput() throws IOException {
        //Initialize as Output
        mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        // higher voltage is considered active
        mGpio.setActiveType(Gpio.ACTIVE_HIGH);
    }

    abstract public String toString();
}
