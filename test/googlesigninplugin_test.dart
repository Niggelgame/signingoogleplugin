import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:googlesigninplugin/googlesigninplugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('googlesigninplugin');

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
    expect(await Googlesigninplugin.platformVersion, '42');
  });
}
