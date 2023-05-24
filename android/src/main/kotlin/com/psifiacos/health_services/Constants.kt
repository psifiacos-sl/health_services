package com.psifiacos.health_services


const val TAG: String = "HSPlugin"
class Constants {
    companion object {
        const val binaryMethodChannel = "group.com.psifiacos.health_services/channel"
        const val binaryEventChannel = "group.com.psifiacos.health_services/event"
        const val registerPermissionsObserver = "registerPermissionsObserver"
        ///Methods name
        const val getPlatformVersion = "getPlatformVersion"
        const val hasCapability = "hasCapability"
        const val requestBodySensorsPermission = "requestBodySensorsPermission"
        const val checkBodySensorsPermission = "checkBodySensorsPermission"
        const val registerPassiveListener = "registerPassiveListener"
        const val unregisterPassiveListener = "unregisterPassiveListener"
        const val registerHeartRateMeasurementClient = "registerHeartRateMeasurementClient"
        const val unregisterHeartRateMeasurementClient = "unregisterHeartRateMeasurementClient"

        //Keys
        const val missingContext = "MissingContext"
        const val missingArgument = "missingArgument"
        const val dataType = "dataType"
        const val action = "action"
        const val data = "data"
        const val list = "list"
        const val bootInstant = "bootInstant"
        const val onNewDataPointsReceived = "onNewDataPointsReceived"
        const val onDataReceived = "onDataReceived"
        const val onPermissionLost = "onPermissionLost"
        const val onAvailabilityChanged = "onAvailabilityChanged"
    }

}