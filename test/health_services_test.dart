// import 'package:flutter_test/flutter_test.dart';
// import 'package:health_services/health_services.dart';
// import 'package:health_services/health_services_platform_interface.dart';
// import 'package:health_services/health_services_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';
//
// class MockHealthServicesPlatform
//     with MockPlatformInterfaceMixin
//     implements HealthServicesPlatform {
//
//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }
//
// void main() {
//   final HealthServicesPlatform initialPlatform = HealthServicesPlatform.instance;
//
//   test('$MethodChannelHealthServices is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelHealthServices>());
//   });
//
//   test('getPlatformVersion', () async {
//     HealthServices healthServicesPlugin = HealthServices();
//     MockHealthServicesPlatform fakePlatform = MockHealthServicesPlatform();
//     HealthServicesPlatform.instance = fakePlatform;
//
//     expect(await healthServicesPlugin.getPlatformVersion(), '42');
//   });
// }
