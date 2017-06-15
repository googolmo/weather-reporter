package im.amomo.weatherreporter.things.provider

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManagerService
import im.amomo.weatherreporter.things.util.LogUtil
import im.amomo.weatherreporter.things.util.error
import im.amomo.weatherreporter.things.util.log
import java.io.IOException

/**
 * Project - WeatherReporter
 * Created by Moyw on 14/06/2017.
 */
class ButtonGpioProvider constructor(val context: Context
                                     , val lifecycle: Lifecycle)
    : LifecycleObserver, GpioCallback(), LogUtil {
    override fun tag(): String = "ButtonGpioProvider"

    val buttonNameA = "GPIO_174"
    val buttonNameB = "GPIO_175"
    val buttonNameC = "GPIO_39"
    val ledNameRed = "GPIO_34"
    val ledNameGreen = "GPIO_32"
    val ledNameBlue = "GPIO_37"

    val buttonGpioNames: Array<String> = arrayOf(buttonNameA, buttonNameB, buttonNameC)
    val ledGpioNames: Array<String> = arrayOf(ledNameRed, ledNameGreen, ledNameBlue)

    var _service: PeripheralManagerService ?= null

    var _buttonGpios: Array<Gpio?> ?= null

    var _ledGpios: Array<Gpio?> ?= null

    fun enable() {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        _service = PeripheralManagerService()
        log { "Available GPIO = ${_service?.gpioList}" }
        _buttonGpios = Array(3, { i ->
            var _button:Gpio ?= null
            try {
                _button = _service?.openGpio(buttonGpioNames[i])
                _button?.setDirection(Gpio.DIRECTION_IN)
                _button?.setEdgeTriggerType(Gpio.EDGE_BOTH)
                _button?.setActiveType(Gpio.ACTIVE_LOW)
                _button?.registerGpioCallback(this)
            } catch (e: IOException) {
                error(e, true, {"Error open GPIO"})
            }
            _button
        })
        _ledGpios = Array(3, { i ->
            var _led: Gpio ?= null
            try {
                _led = _service?.openGpio(ledGpioNames[i])
                _led?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            } catch (e: Exception) {
                error(e, true, {"Error open GPIO"})
            }
            _led
        })

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        _buttonGpios?.forEach {
            it?.unregisterGpioCallback(this)
            try {
                it?.close()
            } catch (e: IOException) {
                error(e, true, {"Error closing GPIO"})
            }
        }
        _ledGpios?.forEach {
            try {
                it?.close()
            } catch (e: IOException) {
                error(e, true, {"Error closing GPIO"})
            }
        }
        lifecycle.removeObserver(this)
    }

    override fun onGpioEdge(gpio: Gpio?): Boolean {
        var index:Int ?= _buttonGpios?.indexOf(gpio)
        if (index != null) {
             try {
                var _value : Boolean ?= gpio?.value
                if (_value != null) {
                    _ledGpios?.get(index)?.value = _value
                }
            } catch (e: IOException) {
                error(e, true, {"Error Reading GPIO"})
            }
        }
        return true
    }
}