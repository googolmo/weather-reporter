package im.amomo.weatherreporter

import android.os.Build

/**
 * Project - WeatherReporter
 * Created by Moyw on 01/06/2017.
 */

object BoardDefaults {
    private val DEVICE_RPI3 = "rpi3"
    private val DEVICE_IMX7 = "imx7d_pico"

    val i2cBus: String
        get() {
            when (Build.DEVICE) {
                DEVICE_RPI3 -> return "I2C1"
                DEVICE_IMX7 -> return "I2C1"
                else -> throw IllegalArgumentException("Unsupported device: " + Build.DEVICE)
            }
        }

    val spiBus: String
        get() {
            when (Build.DEVICE) {
                DEVICE_RPI3 -> return "SPI0.0"
                DEVICE_IMX7 -> return "SPI3.1"
                else -> throw IllegalArgumentException("Unsupported device " + Build.DEVICE)
            }
        }
}
