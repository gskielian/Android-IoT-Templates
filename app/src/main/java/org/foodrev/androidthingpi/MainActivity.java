package org.foodrev.androidthingpi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;

import org.foodrev.androidthingpi.leds.BlueLED;
import org.foodrev.androidthingpi.leds.GreenLED;
import org.foodrev.androidthingpi.leds.RedLED;
import org.foodrev.androidthingpi.leds.base.LED;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Hooray!", "onCreate: we did it!");
        createLightsThread();
    }

    public void createLightsThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PeripheralManagerService manager = new PeripheralManagerService();

                LED redLED = new RedLED(manager);
                LED greenLED = new GreenLED(manager);
                LED blueLED = new BlueLED(manager);

                ArrayList<LED> ledArray = new ArrayList<>();

                ledArray.add(redLED);
                ledArray.add(greenLED);
                ledArray.add(blueLED);

                while (true) {
                    for (LED led : ledArray) {
                        led.toggle();
                        delay(100);
                    }
                }
            }
        });
        thread.start();
    }

    public void delay(int delayMillis) {
        try {
            Thread.sleep(delayMillis);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

    }



}
