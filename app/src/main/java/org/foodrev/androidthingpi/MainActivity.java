package org.foodrev.androidthingpi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.foodrev.androidthingpi.leds.BlueLED;
import org.foodrev.androidthingpi.leds.GreenLED;
import org.foodrev.androidthingpi.leds.RedLED;
import org.foodrev.androidthingpi.leds.base.LED;

import java.util.ArrayList;

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
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

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
                setupFirebase();
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
                        led.turnOn();
                        databaseReference.setValue(led.toString());
                        delay(1000);
                        led.turnOff();
                    }
                }
            }
        });
        thread.start();
    }

    public void delay(int delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("current_led");
    }

}
