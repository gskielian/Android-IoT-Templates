package org.foodrev.androidthingpi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.foodrev.androidthingpi.i2c.AbstractI2cDevice;
import org.foodrev.androidthingpi.i2c.devices.TMP006;
import org.foodrev.androidthingpi.leds.RedLED;
import org.foodrev.androidthingpi.leds.base.LED;

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
    private LED redLED;
    private TMP006 tmp006;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Hooray!", "onCreate: we did it!");
        setupFirebase();
        //createLightsThread();
        createTemperatureSampleThread();
    }


    @Override protected void onDestroy(){
        super.onDestroy();
        tmp006.closeI2cDevice();
    }


    private void createTemperatureSampleThread() {
        Thread thread = new Thread(new Runnable() {
            float temperature;
            @Override
            public void run() {
                PeripheralManagerService manager = new PeripheralManagerService();
                tmp006 = new TMP006(manager, 0x40);

                for (;;) {
                    temperature = tmp006.retrieveTemperatureReading();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex)  {
                        Log.w(TAG, "run: ", ex);
                    }
                    databaseReference.child("temperature").setValue(String.valueOf(temperature));
                }
            }
        });
        thread.start();
    }

    private void createLightsThread() {
        Thread thread = new Thread(new Runnable() {


            @Override
            public void run() {
                databaseReference.child("faucet").setValue("off");
                PeripheralManagerService manager = new PeripheralManagerService();


                redLED = new RedLED(manager);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // double check to ensure child is there
                        if(dataSnapshot.hasChild("faucet")) {
                            // if on, then perform sequence
                            if (dataSnapshot.child("faucet").getValue().toString().equals("on")) {
                                redLED.turnOn();
                                delay(5000);
                                redLED.turnOff();
                                databaseReference.child("faucet").setValue("off");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                databaseReference.addValueEventListener(valueEventListener);
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
        databaseReference = firebaseDatabase.getReference("/");
    }

}
