package com.psifiacos.health_services
import android.util.Log
import androidx.health.services.client.data.DataType

fun loge(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg)
    }
}

fun loge(tag: String, msg: String, e: Throwable) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg, e)
    }
}

fun loge(msg: String) = loge(TAG, msg)

fun loge(msg: String, e: Throwable) = loge(TAG, msg, e)

fun logi(tag: String, msg: String) {
    if (BuildConfig.DEBUG) {
        Log.i(tag, msg)
    }
}

fun dataTypeFromString(dataType: String): DataType<*,*> {
    return DataType.HEART_RATE_BPM
}
