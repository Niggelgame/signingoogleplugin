import 'dart:async';

import 'package:flutter/services.dart';

class Googlesigninplugin {
  static const MethodChannel _channel =
      const MethodChannel('googlesigninplugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> signIn(String clientID) async {
    const channel = BasicMessageChannel<String>('foo', StringCodec());
    channel.setMessageHandler((String message) async {
      print('Received: $message');
      return "received";
    });
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("clientID", ()=>clientID);
    final String clientToken = await _channel.invokeMethod("signIn", args);
    print(clientToken);
    return clientToken;
  }
}
