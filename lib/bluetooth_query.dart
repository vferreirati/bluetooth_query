import 'dart:async';

import 'package:flutter/services.dart';

class BluetoothQuery {
  static const MethodChannel _channel =
      const MethodChannel('bluetooth_query');

  static Future<bool> initialize() async {
    return await _channel.invokeMethod('initialize');
  }

  static Future<bool> isEnabled() async {
    return await _channel.invokeMethod('isEnabled');
  }

  static Future<bool> askToTurnBluetoothOn() async {
    return await _channel.invokeMethod('askToTurnBluetoothOn');
  }

  static Future<bool> checkLocationPermission() async {
    return await _channel.invokeMethod('checkLocationPermission');
  }

  static Future<bool> askLocationPermission() async {
    return await _channel.invokeMethod('askLocationPermission');
  }
}
