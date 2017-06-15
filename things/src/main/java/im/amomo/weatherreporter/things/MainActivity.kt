package im.amomo.weatherreporter.things

import im.amomo.weatherreporter.things.provider.ButtonGpioProvider
import im.amomo.weatherreporter.things.provider.DisplayProvider
import im.amomo.weatherreporter.things.provider.SensorProvider

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
class MainActivity: android.arch.lifecycle.LifecycleActivity() {

    val _tag: String = "MainActivity"


    var _sensorProvider: SensorProvider ?= null
    var _displayProvider: DisplayProvider ?= null
    var _buttonGpioProvider: ButtonGpioProvider ? = null


    protected override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _displayProvider = DisplayProvider(this, lifecycle)
        _sensorProvider = SensorProvider(this, lifecycle, object : SensorProvider.Callback {
            override fun onPressure(pressures: IntArray) {
                _displayProvider?._ledstrip?.write(pressures)
            }

            override fun onTemperature(temperature: Float) {
                _displayProvider?._display?.display(temperature.toDouble())
            }
        })
        _buttonGpioProvider = ButtonGpioProvider(this, lifecycle)
        _sensorProvider?.enable()
        _displayProvider?.enable()
        _buttonGpioProvider?.enable()


    }
}
