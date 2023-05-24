package com.psifiacos.health_services

import android.content.Context
import androidx.annotation.NonNull
import com.google.gson.Gson
import com.psifiacos.health_services.domain.EventChannelHandler

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.embedding.android.FlutterFragmentActivity

/** HealthServicesPlugin */
class HealthServicesPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  private lateinit var channel: MethodChannel
  private var activity: FlutterFragmentActivity? = null
  private var context: Context? = null
  private var activityBinding: ActivityPluginBinding? = null

  companion object {
    val gson = Gson()
    lateinit var event : EventChannelHandler
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(
      flutterPluginBinding.binaryMessenger, Constants.binaryMethodChannel
    )
    event = EventChannelHandler(flutterPluginBinding.binaryMessenger, Constants.binaryEventChannel)
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
    logi(TAG, "onAttachedToEngine")
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    context = null
    logi(TAG, "onDetachedFromEngine")
  }

  override fun onAttachedToActivity(@NonNull binding: ActivityPluginBinding) {
    activity = binding.getActivity() as FlutterFragmentActivity
    activityBinding = binding
    val result = initLifecycleObserver()
    if (result) addLifecycleObserver()
    logi(TAG, "onAttachedToActivity")
  }

  override fun onDetachedFromActivityForConfigChanges() {
    // destroyed to change configuration.
    // This call will be followed by onReattachedToActivityForConfigChanges().
    activity = null
    removeLifecycleObserver()
    logi(TAG, "onDetachedFromActivityForConfigChanges")
  }

  override fun onReattachedToActivityForConfigChanges(@NonNull binding: ActivityPluginBinding) {
    // after a configuration change.
    activity = binding.getActivity() as FlutterFragmentActivity
    activityBinding = binding
    val result = initLifecycleObserver()
    if (result) addLifecycleObserver()
    logi(TAG, "onReattachedToActivityForConfigChanges")
  }

  override fun onDetachedFromActivity() {
    // Clean up references.
    activity = null
    removeLifecycleObserver()
    logi(TAG, "onDetachedFromActivity")
  }

  private fun initLifecycleObserver(): Boolean {
    activity?.let { act ->
      HSManager.initLifecycleObserver(act)
      return true
    }
    return false
  }

  private fun addLifecycleObserver(): Boolean {
    activity?.let { act ->
      HSManager.addLifecycleObserver(act)
      return true
    }
    return false
  }

  private fun removeLifecycleObserver(): Boolean {
    activity?.let { act ->
      HSManager.removeLifecycleObserver(act)
      return true
    }
    return false

  }


  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method) {
      Constants.getPlatformVersion -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }
      Constants.hasCapability -> {
        context?.let {ctx ->
          val argument = call.argument(Constants.dataType) as String?
          argument?.let {
            if(HSManager.getOrCreate(ctx)) {
              HSManager.hasCapability(dataTypeFromString(argument)) { res ->
                result.success(res)
              }
            } else {
              result.success(false)
            }
          } ?: run {
            result.error(Constants.missingArgument, "No Arguments", -1)
          }
        } ?: run {
          result.error(Constants.missingContext, "No Context", -1)
        }
      }
      Constants.requestBodySensorsPermission -> {
        context?.let {ctx ->
          if(HSManager.getOrCreate(ctx)) {
            HSManager.checkBodySensorsPermission(ctx) {
              if(!it) {
                HSManager.requestBodySensorsPermission { req ->
                  result.success(req)
                }
              } else {
                result.success(it)
              }
            }
          } else {
            result.success(false)
          }
        } ?: run {
          result.error(Constants.missingContext, "No Context", -1)
        }
      }
      Constants.checkBodySensorsPermission -> {
        context?.let {ctx ->
          if(HSManager.getOrCreate(ctx)) {
            HSManager.checkBodySensorsPermission(ctx) {
              result.success(it)
            }
          } else {
            result.success(false)
          }
        } ?: run {
          result.error(Constants.missingContext, "No Context", -1)
        }
      }
      Constants.registerPassiveListener -> {
        context?.let {ctx ->
          val argument = call.argument(Constants.dataType) as List<String>?
          argument?.let {
            if(HSManager.getOrCreate(ctx)) {
              HSManager.registerPassiveListener(it.map { dataType -> dataTypeFromString(dataType) }.toSet())
            }
          } ?: run {
            result.error(Constants.missingArgument, "No Arguments", -1)
          }
        } ?: run {
          result.error(Constants.missingContext, "No Context", -1)
        }
      }
      Constants.unregisterPassiveListener -> {
        context?.let {ctx ->
          if(HSManager.getOrCreate(ctx)) {
            HSManager.unregisterPassiveListeners {
              result.success(it)
            }
          }
        } ?: run {
          result.error(Constants.missingContext, "No Context", -1)
        }
      }
      Constants.registerHeartRateMeasurementClient -> {
        context?.let { ctx ->
          if(HSManager.getOrCreate(ctx)) {
            HSManager.registerMeasureClientHeartRate()
          }
        } ?: run {
          result.error(Constants.missingContext, "No Context", -1)
        }
      }
      Constants.unregisterHeartRateMeasurementClient -> {
        context?.let { ctx ->
          if(HSManager.getOrCreate(ctx)) {
            HSManager.unregisterMeasureClientHeartRate {
              result.success(it)
            }
          }
        } ?: run {
          result.error(Constants.missingContext, "No Context", -1)
        }
      }
      else -> {
        loge("Method not Implemented")
        result.notImplemented()
      }
    }
  }
}
