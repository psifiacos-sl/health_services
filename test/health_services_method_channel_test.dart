import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:health_services/health_services_method_channel.dart';

void main() {
  MethodChannelHealthServices platform = MethodChannelHealthServices();
  const MethodChannel channel = MethodChannel('health_services');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
