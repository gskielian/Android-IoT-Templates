package org.foodrev.androidthingpi.i2c.devices;

import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;

import org.foodrev.androidthingpi.i2c.AbstractI2cDevice;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by dastechlabs on 1/6/18.
 */

public class STM32L4 extends AbstractI2cDevice {

    // TODO: possible i2c addresses are: 0x40,
    // TODO: create document with final static ints for readable register names

    public STM32L4(PeripheralManagerService manager, int i2cAddress) {
        super(manager, i2cAddress);

        //attempt to create I2c device
        createI2cDevice();
    }

    public void createI2cDevice(){
        try {
            mDevice = manager.openI2cDevice(I2C_DEVICE_NAME, i2cAddress);
        } catch (IOException e) {
            Log.w(TAG, "STM32L4: unable to access I2c Device", e);
        }
    }

    /**
     * revtries number of bytes
     * @param registerAddress
     * @return
     */
    public byte[] retrieveRegisterReading(int registerAddress, int numberOfBytesToRead) {
        // read two bytes to get raw reading
        byte[] data = new byte[numberOfBytesToRead];
        int rawReading;

        try {
            data = readCalibration(registerAddress, data);
        } catch (IOException e) {
            Log.w(TAG, "retrieve Register Reading: ", e);
        }

        // bytes are signed in java, convert to unsigned with the following
        // data[0] &= 0xFF;
        // data[1] &= 0xFF;
        // concatenate MSB and LSB to get raw sensor reading
        //rawReading = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);

        return data;
    }

    /**
     * send the bits you want to set e.g. 0x01 will set via "OR" with this value
     * hence 0x00 will turn into 0x01 b/c 0x00 | 0x01 = 0x01
     *
     * @param address - register you want to modify
     * @param setBit - bits you want to set
     * @throws IOException - should be handled appropriately in context of app
     */
    public void setRegisterBits(int address, int setBit) throws IOException {
        // Read one register from slave
        byte value = mDevice.readRegByte(address);
        // Set bit 6, i.e. 0b00000100
        //value |= 0x40;

        value |= setBit;

        // write the updated value back to slave
        mDevice.writeRegByte(address, value);
    }

    /**
     * send the bits you want to unset e.g. 0x01 will unset xor with 0x01
     * hence 0xFF will turn into 0xFE
     *
     * @param address - register you want to modify
     * @param unSetBit - bits you want to unset
     * @throws IOException - should be handled appropriately in context of app
     */
    public void unSetRegisterBits(int address, int unSetBit) throws IOException {
        // Read one register from slave
        byte value = mDevice.readRegByte(address);
        // Set bit 6, i.e. 0b00000100
        //value |= 0x40;

        value ^= unSetBit;

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
