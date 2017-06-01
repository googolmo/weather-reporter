package im.amomo.weatherreporter

import android.arch.lifecycle.LifecycleActivity
import android.os.Bundle
import im.amomo.weatherreporter.SensorProvider.Callback

/**
 * Skeleton of an Android Things activity.
 *
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 *
 * <pre>`PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
`</pre> *
 *
 *
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.

 * @see [https://github.com/androidthings/contrib-drivers.readme](https://github.com/androidthings/contrib-drivers.readme)
 */
class MainActivity : LifecycleActivity() {

    var _sensorProvider: SensorProvider ?= null
    var _displayProvider: DisplayProvider ?= null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _displayProvider = DisplayProvider(this, lifecycle)
        _sensorProvider = SensorProvider(this, lifecycle, object : Callback {
            override fun onPressure(pressures: IntArray) {
                _displayProvider?._ledstrip?.write(pressures)
            }

            override fun onTemperature(temperature: Float) {
                _displayProvider?._display?.display(temperature.toDouble())
            }
        })
        lifecycle.addObserver(_displayProvider)
        lifecycle.addObserver(_sensorProvider)
        println("MainActivity onCreate()")
    }
}
