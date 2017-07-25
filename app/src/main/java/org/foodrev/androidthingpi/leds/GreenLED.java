package org.foodrev.androidthingpi.leds;

import com.google.android.things.pio.PeripheralManagerService;

import org.foodrev.androidthingpi.leds.base.LED;

/**
 * Created by dastechlabs on 7/24/17.
 */

public class GreenLED extends LED {
    public GreenLED(PeripheralManagerService manager) {
        super(manager, "BCM19");
    }

    public String toString() {
        return "Green LED";
    }
}
