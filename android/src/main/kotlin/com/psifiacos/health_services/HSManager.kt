package com.psifiacos.health_services

import android.content.Context
import android.content.pm.PackageManager
import android.os.SystemClock
import io.flutter.embedding.android.FlutterFragmentActivity
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.PassiveListenerCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.data.HeartRateAccuracy
import androidx.health.services.client.data.PassiveListenerConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

object HSManager {

    private var hsClient: HealthServicesClient? = null

    private val passiveListenerCallback = object : PassiveListenerCallback {
        override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
            post {
                logi(TAG, Constants.onNewDataPointsReceived)
                val bootInstant =
                    Instant.ofEpochMilli(System.currentTimeMillis() - SystemClock.elapsedRealtime())
                HealthServicesPlugin.event.sendEvent(JSONObject().apply {
                    put(Constants.action, Constants.onNewDataPointsReceived)
                    put(Constants.data, JSONObject().apply {
                        put(Constants.bootInstant, bootInstant.toEpochMilli())
                        put(Constants.list, JSONArray().apply {
                            dataPoints.getData(DataType.HEART_RATE_BPM).forEach{ sample ->
                                val jsonObject = JSONObject()
                                jsonObject.put("accuracy", sample.accuracy?.let { acc ->
                                    (acc as HeartRateAccuracy).sensorStatus.name
                                })
                                jsonObject.put("dataType", sample.dataType.name)
                                jsonObject.put("value", sample.value)
                                jsonObject.put("instant", sample.getTimeInstant(bootInstant).toEpochMilli())
                                jsonObject.put("timeDurationFromBoot", sample.timeDurationFromBoot.seconds)
                                jsonObject.put("metadata", HealthServicesPlugin.gson.toJson(sample.metadata))
                                put(jsonObject)
                            }
                        })
                    })
                }.toString())
            }
        }

        override fun onPermissionLost() {
            logi(TAG, Constants.onPermissionLost)
        }
    }

    private val heartRateMeasureClientCallback = object : MeasureCallback {
        override fun onAvailabilityChanged(dataType: DeltaDataType<*, *>, availability: Availability) {
            if (availability is DataTypeAvailability) {
                logi(TAG, "${Constants.onAvailabilityChanged}: ${availability.name}")
                HealthServicesPlugin.event.sendEvent(JSONObject().apply {
                    put(Constants.action, Constants.onAvailabilityChanged)
                    put(Constants.data, availability.name)
                }.toString())
            }
        }

        override fun onDataReceived(data: DataPointContainer) {
            post {
                logi(TAG, Constants.onDataReceived)
                val bootInstant =
                    Instant.ofEpochMilli(System.currentTimeMillis() - SystemClock.elapsedRealtime())
                HealthServicesPlugin.event.sendEvent(JSONObject().apply {
                    put(Constants.action, Constants.onDataReceived)
                    put(Constants.data, JSONObject().apply {
                        put(Constants.bootInstant, bootInstant.toEpochMilli())
                        put(Constants.list, JSONArray().apply {
                            data.getData(DataType.HEART_RATE_BPM).forEach{ sample ->
                                val jsonObject = JSONObject()
                                jsonObject.put("accuracy", sample.accuracy?.let { acc ->
                                    (acc as HeartRateAccuracy).sensorStatus.name
                                })
                                jsonObject.put("dataType", sample.dataType.name)
                                jsonObject.put("value", sample.value)
                                jsonObject.put("instant", sample.getTimeInstant(bootInstant).toEpochMilli())
                                jsonObject.put("timeDurationFromBoot", sample.timeDurationFromBoot.seconds)
                                jsonObject.put("metadata", HealthServicesPlugin.gson.toJson(sample.metadata))
                                put(jsonObject)
                            }
                        })
                    })
                }.toString())
            }
        }
    }

    private var hSCycleObserver: HSCycleObserver? = null
    private val handler = CoroutineExceptionHandler {_, e ->
        loge(TAG, "${e.message}")
    }
    private val scope by lazy {
        CoroutineScope(Dispatchers.IO + handler)
    }

    fun getOrCreate(ctx: Context): Boolean {
        try {
            hsClient = HealthServices.getClient(ctx)
        } catch (e: Exception) {
            loge(e.toString())
            return false
        }
        return true
    }

    fun hasCapability(dataType: DataType<*, *>, response: (Boolean) -> Unit) {
        scope.launch {
            val capabilities = hsClient?.measureClient?.getCapabilitiesAsync()?.await()
            capabilities?.let {
                response(dataType in it.supportedDataTypesMeasure)
            } ?: response(false)
        }
    }

    fun requestBodySensorsPermission(
        response: (records: Boolean) -> Unit
    ) {
        hSCycleObserver?.launchRequestPermissions(android.Manifest.permission.BODY_SENSORS) {
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                hSCycleObserver?.launchRequestPermissions(android.Manifest.permission.BODY_SENSORS) { it2 ->
                    response(it2)
                }
            } else {
                response(it)
            }
        }
    }

    fun checkBodySensorsPermission(ctx: Context, response: (Boolean) -> Unit) {
        scope.launch {
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                response(ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.BODY_SENSORS) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.BODY_SENSORS_BACKGROUND) ==
                        PackageManager.PERMISSION_GRANTED)
            } else {
                response(ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.BODY_SENSORS) ==
                        PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    fun registerPassiveListener(dataTypes: Set<DataType<*, *>>) {
        logi(TAG, "Registering Passive Listener")
        val passiveListenerConfig = PassiveListenerConfig.builder()
            .setDataTypes(dataTypes)
            .build()
        hsClient?.passiveMonitoringClient?.setPassiveListenerCallback(passiveListenerConfig, passiveListenerCallback)
    }

    fun unregisterPassiveListeners(response: (Boolean) -> Unit) {
        scope.launch {
            logi(TAG, "Unregistering Passive Listeners")
            hsClient?.passiveMonitoringClient?.clearPassiveListenerCallbackAsync()?.await()
            response(true)
        }
    }

    fun registerMeasureClientHeartRate() {
        logi(TAG, "Registering measure client: Heart Rate")
        hsClient?.measureClient?.registerMeasureCallback(DataType.Companion.HEART_RATE_BPM, heartRateMeasureClientCallback)
    }

    fun unregisterMeasureClientHeartRate(response: (Boolean) -> Unit) {
        scope.launch {
            logi(TAG, "Unregistering measure client: Heart Rate")
            hsClient?.measureClient?.unregisterMeasureCallbackAsync(DataType.Companion.HEART_RATE_BPM, heartRateMeasureClientCallback)?.await()
            response(true)
        }
    }

    fun addLifecycleObserver(act: FlutterFragmentActivity) {
        hSCycleObserver?.let {
            act.lifecycle.addObserver(it)
        }
    }

    fun initLifecycleObserver(act: FlutterFragmentActivity) {
        if (hSCycleObserver == null)
            hSCycleObserver = HSCycleObserver(act.activityResultRegistry)
    }

    fun removeLifecycleObserver(act: FlutterFragmentActivity) {
        hSCycleObserver?.let {
            act.lifecycle.removeObserver(it)
        }
    }

}