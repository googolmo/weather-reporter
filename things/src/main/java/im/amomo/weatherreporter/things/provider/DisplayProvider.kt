package im.amomo.weatherreporter.things.provider

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Color
import com.google.android.things.contrib.driver.apa102.Apa102
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay
import java.io.IOException
import java.util.*

/**
 * Project - WeatherReporter
 * Created by Moyw on 01/06/2017.
 */
class DisplayProvider constructor(val context: Context, val lifecycle: Lifecycle) : LifecycleObserver {

    var _ledstrip: Apa102?= null
    var _display: AlphanumericDisplay?= null

    val _ledstripBrightness: Int = 1

    init {

    }

    fun enable() {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        try {
            _display = AlphanumericDisplay(BoardDefaults.i2cBus)
            _display!!.setEnabled(true)
            _display!!.display("4444")
        } catch (e: IOException) {
            throw RuntimeException("Error!", e)
        }

        try {
            _ledstrip = Apa102(BoardDefaults.spiBus, Apa102.Mode.BGR)
            _ledstrip!!.brightness = _ledstripBrightness
            val colors: IntArray = kotlin.IntArray(7)
            Arrays.fill(colors, Color.RED)
            _ledstrip!!.write(colors)
            _ledstrip!!.write(colors)
        } catch (e: IOException) {
            throw RuntimeException("Error", e)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        try {
            _display?.clear()
            _display?.setEnabled(false)
            _display?.close()
        } finally {
            _display = null
        }

        try {
            _ledstrip?.write(kotlin.IntArray(7))
            _ledstrip?.brightness = 0
            _ledstrip?.close()
        } finally {
            _ledstrip = null
        }
        lifecycle.removeObserver(this)
    }

}
