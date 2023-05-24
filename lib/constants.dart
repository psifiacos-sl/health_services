class Constants {
  static const methodChannel = "group.com.psifiacos.health_services/channel";
  static const eventChannel = "group.com.psifiacos.health_services/event";

  ///Methods name
  static const getPlatformVersion = "getPlatformVersion";
  static const hasCapability = "hasCapability";
  static const requestBodySensorsPermission = "requestBodySensorsPermission";
  static const checkBodySensorsPermission = "checkBodySensorsPermission";
  static const registerPassiveListener = "registerPassiveListener";
  static const unregisterPassiveListener = "unregisterPassiveListener";
  static const registerHeartRateMeasurementClient = "registerHeartRateMeasurementClient";
  static const unregisterHeartRateMeasurementClient = "unregisterHeartRateMeasurementClient";

  static const dataType = "dataType";
  static const action = "action";
  static const data = "data";
  static const list = "list";
  static const bootInstant = "bootInstant";
  static const onNewDataPointsReceived = "onNewDataPointsReceived";
  static const onDataReceived = "onDataReceived";
  static const onPermissionLost = "onPermissionLost";
  static const onAvailabilityChanged = "onAvailabilityChanged";

}

enum DataType { HEART_RATE_BPM }

enum HeartRateAccuracy { UNKNOWN, NO_CONTACT, UNRELIABLE, ACCURACY_LOW, ACCURACY_MEDIUM, ACCURACY_HIGH }

enum SensorAvailability { UNKNOWN, AVAILABLE, ACQUIRING, UNAVAILABLE, UNAVAILABLE_DEVICE_OFF_BODY }
