package im.amomo.weatherreporter

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver
import im.amomo.weatherreporter.util.RainbowUtil
import java.io.IOException

/**
 * Project - WeatherReporter
 * Created by Moyw on 01/06/2017.
 */

class SensorProvider constructor(context: Context, lifecycle: Lifecycle, callback: Callback)
    : LifecycleObserver, SensorEventListener {

    var _context: Context = context
    var _lifecycle: Lifecycle = lifecycle
    var _sensorManager: SensorManager?= null
    var _callback: Callback = callback
    private var _environmentalSensorDriver: Bmx280SensorDriver? = null

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val value: Float? = event?.values?.get(0)
        if (event?.sensor?.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            updateTemperatureDisplay(value)
        }
        if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
            updateBarometerDisplay(value)
        }
    }

    init {

    }

    private fun updateTemperatureDisplay(temperature: Float?) {
        if (temperature != null) {
            _callback.onTemperature(temperature)
        }
    }

    private fun updateBarometerDisplay(pressure: Float?) {
        if (pressure != null) {
            try {

                _callback.onPressure(RainbowUtil.getWeatherStripColors(pressure))
            } catch (e: IOException) {

            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        _sensorManager = _context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        try {
            _environmentalSensorDriver = Bmx280SensorDriver(BoardDefaults.i2cBus)
            _environmentalSensorDriver!!.registerTemperatureSensor()
            _environmentalSensorDriver!!.registerPressureSensor()
        } catch (e: IOException) {
            throw RuntimeException("Error", e)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        println("onStart")
        val temperature: Sensor ?= _sensorManager
                ?.getDynamicSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE)?.get(0)
        _sensorManager?.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL)
        _sensorManager?.registerListener(this
                , _sensorManager?.getDynamicSensorList(Sensor.TYPE_PRESSURE)?.get(0)
                , SensorManager.SENSOR_DELAY_NORMAL)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        _sensorManager?.unregisterListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        if (_environmentalSensorDriver != null) {
            try {
                _environmentalSensorDriver!!.close()
            } finally {
                _environmentalSensorDriver = null
            }
        }
    }

    interface Callback {
        fun onTemperature(temperature: Float)
        fun onPressure(pressures: IntArray)
    }

}

