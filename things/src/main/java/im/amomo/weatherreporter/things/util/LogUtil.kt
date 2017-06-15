package im.amomo.weatherreporter.things.util

import android.util.Log
import com.google.firebase.crash.FirebaseCrash

/**
 * Project - WeatherReporter
 * Created by Moyw on 14/06/2017.
 */
fun LogUtil.log(msg: () -> String) {
//    if (Log.isLoggable(tag(), Log.INFO)) {
        Log.i(tag(), msg())
//    }
}

fun LogUtil.error(e: Throwable, report: Boolean, msg:() -> String) {
    Log.e(tag(), msg(), e)
    if (report) {
        FirebaseCrash.report(e)
    }
}

fun LogUtil.error(e: Throwable, msg: () -> String) {
    error(e, false, msg)
}

interface LogUtil {
    fun tag() : String
}
