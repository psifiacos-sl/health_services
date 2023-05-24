import 'package:flutter/cupertino.dart';
import 'package:health_services/constants.dart';
import 'package:health_services/domain/measure_client_callback.dart';
import 'package:health_services/domain/passive_data_listener_callback.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'health_services_method_channel.dart';

abstract class HealthServicesPlatform extends PlatformInterface {

  HealthServicesPlatform() : super(token: _token);

  static final Object _token = Object();

  static HealthServicesPlatform _instance = MethodChannelHealthServices();

  static HealthServicesPlatform get instance => _instance;

  static set instance(HealthServicesPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion();

  Future<bool> hasCapability(DataType dataType);

  Future<bool> requestBodySensorsPermission();

  Future<bool> checkBodySensorsPermission();

  void registerPassiveListener(List<DataType> dataTypes, {required PassiveDataListenerCallback callback});

  Future<bool> unregisterPassiveListeners();

  void registerHeartRateMeasurementClient({required MeasureClientCallback? callback});

  Future<bool> unregisterHeartRateMeasurementClient();
}
