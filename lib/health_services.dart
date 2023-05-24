
import 'package:flutter/cupertino.dart';

import 'constants.dart';
import 'domain/measure_client_callback.dart';
import 'domain/passive_data_listener_callback.dart';
import 'health_services_platform_interface.dart';

class HealthServices {

  Future<String?> getPlatformVersion() {
    return HealthServicesPlatform.instance.getPlatformVersion();
  }

  Future<bool> hasCapability(DataType dataType) async {
    return await HealthServicesPlatform.instance.hasCapability(dataType);
  }

  Future<bool> requestBodySensorsPermission() async {
    return await HealthServicesPlatform.instance.requestBodySensorsPermission();
  }

  Future<bool> checkBodySensorsPermission() async {
    return await HealthServicesPlatform.instance.checkBodySensorsPermission();
  }

  void registerPassiveListener(List<DataType> dataTypes,
      {required PassiveDataListenerCallback callback}) {
    HealthServicesPlatform.instance.registerPassiveListener(dataTypes, callback: callback);
  }

  Future<bool> unregisterPassiveListeners() async {
    return await HealthServicesPlatform.instance.unregisterPassiveListeners();
  }

  void registerHeartRateMeasurementClient(
      {required MeasureClientCallback? callback}) {
    HealthServicesPlatform.instance.registerHeartRateMeasurementClient(callback: callback);
  }

  Future<bool> unregisterHeartRateMeasurementClient() async {
    return await HealthServicesPlatform.instance.unregisterHeartRateMeasurementClient();
  }
}
