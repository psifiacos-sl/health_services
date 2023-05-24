import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:health_services/domain/data_point_container.dart';

import 'constants.dart';
import 'domain/measure_client_callback.dart';
import 'domain/passive_data_listener_callback.dart';
import 'health_services_platform_interface.dart';

class MethodChannelHealthServices implements HealthServicesPlatform {
  final MethodChannel _methodChannel =
      const MethodChannel(Constants.methodChannel);
  final EventChannel _eventChannel = const EventChannel(Constants.eventChannel);

  PassiveDataListenerCallback? passiveDataListenerCallback;
  MeasureClientCallback? heartRateMeasureClient;

  MethodChannelHealthServices() {
    _eventChannel.receiveBroadcastStream().listen((r) {
      final json = jsonDecode(r);
      final action = json[Constants.action];
      final data = json[Constants.data];
      switch (action) {
        case Constants.onNewDataPointsReceived:
          passiveDataListenerCallback
              ?.onNewDataPointsReceived(DataPointWrapper.fromJson(data));
          break;
        case Constants.onDataReceived:
          heartRateMeasureClient
              ?.onDataReceived(DataPointWrapper.fromJson(data));
          break;
        case Constants.onAvailabilityChanged:
          heartRateMeasureClient?.onAvailabilityChanged(SensorAvailability
              .values
              .firstWhere((element) => element.name == data));
          break;
        case Constants.onPermissionLost:
          passiveDataListenerCallback?.onPermissionLost();
          break;
      }
    });
  }

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await _methodChannel.invokeMethod<String>(Constants.getPlatformVersion);
    return version;
  }

  @override
  Future<bool> hasCapability(DataType dataType) async {
    final result = await _methodChannel.invokeMethod<bool>(
        Constants.hasCapability, {Constants.dataType: dataType.name});
    return result ?? false;
  }

  @override
  Future<bool> requestBodySensorsPermission() async {
    final result = await _methodChannel
        .invokeMethod<bool>(Constants.requestBodySensorsPermission);
    return result ?? false;
  }

  @override
  Future<bool> checkBodySensorsPermission() async {
    final result = await _methodChannel
        .invokeMethod<bool>(Constants.checkBodySensorsPermission);
    return result ?? false;
  }

  @override
  void registerPassiveListener(List<DataType> dataTypes,
      {required PassiveDataListenerCallback callback}) {
    passiveDataListenerCallback = callback;
    _methodChannel.invokeMethod(Constants.registerPassiveListener,
        {Constants.dataType: dataTypes.map((e) => e.name).toList()});
  }

  @override
  void unregisterPassiveListeners() {
    passiveDataListenerCallback = null;
    _methodChannel.invokeMethod(Constants.unregisterPassiveListener);
  }

  @override
  void registerHeartRateMeasurementClient(
      {required MeasureClientCallback? callback}) {
    heartRateMeasureClient = callback;
    _methodChannel.invokeMethod(Constants.registerHeartRateMeasurementClient);
  }

  @override
  void unregisterHeartRateMeasurementClient() {
    heartRateMeasureClient = null;
    _methodChannel.invokeMethod(Constants.unregisterHeartRateMeasurementClient);
  }
}
