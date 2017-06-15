package im.amomo.weatherreporter.things.provider

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver
import com.google.firebase.crash.FirebaseCrash
import im.amomo.weatherreporter.things.util.RainbowUtil.getWeatherStripColors
import java.io.IOException

/**
 * Project - WeatherReporter
 * Created by Moyw on 01/06/2017.
 */

class SensorProvider constructor(val context: Context
                                 , val lifecycle: Lifecycle
                                 , val callback: SensorProvider.Callback)
    : LifecycleObserver, SensorEventListener {

    private var _environmentalSensorDriver: Bmx280SensorDriver? = null
    private var _sensorManager: SensorManager ? = null

    fun enable() {
        lifecycle.addObserver(this)
    }

    override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: android.hardware.SensorEvent?) {
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
            callback.onTemperature(temperature)
        }
    }

    private fun updateBarometerDisplay(pressure: Float?) {
        if (pressure != null) {
            try {
                callback.onPressure(getWeatherStripColors(pressure))
            } catch (e: IOException) {

            }
        }
    }

    @android.arch.lifecycle.OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        _sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        try {
            _environmentalSensorDriver = Bmx280SensorDriver(BoardDefaults.i2cBus)
            _environmentalSensorDriver!!.registerTemperatureSensor()
            _environmentalSensorDriver!!.registerPressureSensor()
        } catch (e: IOException) {
            FirebaseCrash.report(e)
            throw RuntimeException("Error", e)
        }
    }

    @android.arch.lifecycle.OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        val temperatureList: List<Sensor> ? = _sensorManager
                ?.getDynamicSensorList(Sensor.TYPE_AMBIENT_TEMPERATURE)

        val temperature: Sensor?= if (temperatureList != null && temperatureList.isNotEmpty()) {
            temperatureList[0]
        } else {
            null
        }

        if (temperature != null) {
            _sensorManager?.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL)
        }

        val pressureList: List<Sensor> ?= _sensorManager?.getDynamicSensorList(Sensor.TYPE_PRESSURE)

        val pressure: Sensor? = if (pressureList != null && pressureList.isNotEmpty()) {
            pressureList[0]
        } else {
            null
        }

        if (pressure != null) {
            _sensorManager?.registerListener(this
                    , pressure
                    , SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    @android.arch.lifecycle.OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        _sensorManager?.unregisterListener(this)
    }

    @android.arch.lifecycle.OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        if (_environmentalSensorDriver != null) {
            try {
                _environmentalSensorDriver!!.close()
            } finally {
                _environmentalSensorDriver = null
            }
        }
        lifecycle.removeObserver(this)
    }

    interface Callback {
        fun onTemperature(temperature: Float)
        fun onPressure(pressures: IntArray)
    }

}

