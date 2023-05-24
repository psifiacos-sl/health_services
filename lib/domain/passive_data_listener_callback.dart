

import 'package:flutter/cupertino.dart';

import 'data_point_container.dart';

class PassiveDataListenerCallback {
  ValueChanged<DataPointWrapper> onNewDataPointsReceived;
  VoidCallback onPermissionLost;

  PassiveDataListenerCallback({required this.onNewDataPointsReceived, required this.onPermissionLost});

}