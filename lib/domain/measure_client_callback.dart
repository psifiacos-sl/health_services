
import 'package:flutter/cupertino.dart';
import 'package:health_services/constants.dart';

import 'data_point_container.dart';

class MeasureClientCallback {
  ValueChanged<DataPointWrapper> onDataReceived;
  ValueChanged<SensorAvailability> onAvailabilityChanged;

  MeasureClientCallback({required this.onAvailabilityChanged, required this.onDataReceived});
}