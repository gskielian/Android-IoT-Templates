package org.foodrev.androidthingpi.leds;

import com.google.android.things.pio.PeripheralManagerService;

import org.foodrev.androidthingpi.leds.base.LED;

/**
 * Created by dastechlabs on 7/24/17.
 */

public class BlueLED extends LED {
    public BlueLED(PeripheralManagerService manager) {
        super(manager, "GPIO_33");
    }

    public String toString() {
        return "Blue LED";
    }
}
