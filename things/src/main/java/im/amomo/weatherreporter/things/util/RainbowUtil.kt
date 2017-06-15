package im.amomo.weatherreporter.things.util

import android.graphics.Color

/**
 * Project - WeatherReporter
 * Created by Moyw on 01/06/2017.
 */
object RainbowUtil {
    val barometerRangeLow: Float = 956F
    val barometerRangeHigh: Float = 1035F

    val rainbowColors: IntArray = kotlin.IntArray(7)

    init {
        for (i in rainbowColors.indices) {
            val hsv: FloatArray = floatArrayOf(i * 360F / rainbowColors.size, 1.0F, 1.0F)
            rainbowColors[i] = Color.HSVToColor(255, hsv)
        }
     }

    fun getWeatherStripColors(pressure: Float): IntArray {
        val t: Float = (pressure - barometerRangeLow) / (barometerRangeHigh - barometerRangeLow)
        var n: Int = Math.ceil((rainbowColors.size * t).toDouble()).toInt()
        n = Math.max(0, Math.min(n, rainbowColors.size))

        val colors: IntArray = kotlin.IntArray(rainbowColors.size)
        for (i in 0..n-1) {
            val ri:Int = rainbowColors.size - 1 - i;
            colors[i] = rainbowColors[ri]
        }

        return colors
    }

}
