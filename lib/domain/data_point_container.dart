import 'dart:convert';

import 'package:health_services/constants.dart';

abstract class DataPointContainer<T> {
  final DataType dataType;
  final T accuracy;
  final int instant, timeDurationFromBoot;
  final num value;
  final Map? metadata;

  DataPointContainer(
      {required this.dataType,
      required this.accuracy,
      required this.value,
      required this.instant,
      required this.timeDurationFromBoot,
      this.metadata});
}

class HeartRateDataPointContainer
    extends DataPointContainer<HeartRateAccuracy> {
  HeartRateDataPointContainer(
      {required super.accuracy,
      required super.instant,
      required super.timeDurationFromBoot,
      required super.value,
      super.metadata})
      : super(dataType: DataType.HEART_RATE_BPM);

  factory HeartRateDataPointContainer.fromJson(Map<String, dynamic> json) {
    return HeartRateDataPointContainer(
        accuracy: HeartRateAccuracy.values
            .firstWhere((element) => element.name == json['accuracy']),
        instant: json['instant'],
        timeDurationFromBoot: json['timeDurationFromBoot'],
        value: json['value'],
        metadata: jsonDecode(json['metadata']));
  }
}

class DataPointWrapper {
  final List<DataPointContainer> data;
  final int bootInstant;

  DataPointWrapper({required this.data, required this.bootInstant});

  factory DataPointWrapper.fromJson(Map<String, dynamic> json) {
    return DataPointWrapper(
        data: (json[Constants.list] as List<dynamic>)
            .map((e) => HeartRateDataPointContainer.fromJson(e))
            .toList(),
        bootInstant: json[Constants.bootInstant]);
  }
}
