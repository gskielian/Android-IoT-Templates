package org.foodrev.androidthingpi.leds;

import com.google.android.things.pio.PeripheralManagerService;

import org.foodrev.androidthingpi.leds.base.LED;

/**
 * Created by dastechlabs on 7/24/17.
 */

public class RedLED extends LED {

    public RedLED(PeripheralManagerService manager) {
        super(manager, "GPIO_35");
    }

    public String toString() {
        return "Red LED";
    }
}
