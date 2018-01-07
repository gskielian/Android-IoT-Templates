package org.foodrev.androidthingpi.i2c;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by dastechlabs on 1/6/18.
 */

abstract public class AbstractI2cDevice {
    protected PeripheralManagerService manager;

    // i2c device name
    protected String I2C_DEVICE_NAME = "I2C1";
    protected I2cDevice mDevice;

    // to be set by method in class
    protected int i2cAddress;

    public AbstractI2cDevice(PeripheralManagerService manager, int i2cAddress){
        this.manager = manager;
        this.i2cAddress = i2cAddress;
    }

    public void closeI2cDevice() {
        if(mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "closeI2cDevice: unable to close I2c Device", e);
            }
        }
    }
}
