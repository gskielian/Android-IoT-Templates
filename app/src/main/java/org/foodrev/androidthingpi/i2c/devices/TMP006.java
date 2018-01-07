package org.foodrev.androidthingpi.i2c.devices;

import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;

import org.foodrev.androidthingpi.i2c.AbstractI2cDevice;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by dastechlabs on 1/6/18.
 */

public class TMP006 extends AbstractI2cDevice {

    // TODO: possible i2c addresses are: 0x40,
    // TODO: create document with final static ints for readable register names

    public TMP006(PeripheralManagerService manager, int i2cAddress) {
        super(manager, i2cAddress);

        //attempt to create I2c device
        createI2cDevice();
    }

    public void createI2cDevice(){
        try {
            mDevice = manager.openI2cDevice(I2C_DEVICE_NAME, i2cAddress);
        } catch (IOException e) {
            Log.w(TAG, "TMP006: unable to access I2c Device", e);
        }


    }

    public float retrieveTemperatureReading() {
        // read two bytes to get raw reading
        byte[] data = new byte[2];
        int rawReading;

        int registerAddress = 0x01;
        try {
            data = readCalibration(registerAddress, data);
        } catch (IOException e) {
            Log.w(TAG, "retrieveTemperatureReading: ", e);
        }

        // bytes are signed in java, convert to unsigned with the following
        //data[0] &= 0xFF;
        //data[1] &= 0xFF;
        // concatenate MSB and LSB to get raw sensor reading
        rawReading = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);

        return convertToCelsius(rawReading);
    }

    private float convertToCelsius(int rawReading) {
        float temperatureCelcius;
        rawReading >>=2;
        temperatureCelcius = rawReading/32.0f;
        return temperatureCelcius;
    }

    // modify contents of a single register
    public void setRegisterFlag(int address) throws IOException {
        // Read one register from slave
        byte value = mDevice.readRegByte(address);
        // Set bit 6
        value |= 0x40;
        // write the updated value back to slave
        mDevice.writeRegByte(address, value);
    }

    // Read a register block
    public byte[] readCalibration(int startAddress, byte[] data) throws IOException {
        // read three consecutive register values
        mDevice.readRegBuffer(startAddress, data, data.length);
        return data;
    }
}
