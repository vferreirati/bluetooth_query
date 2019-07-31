import 'dart:async';

import 'package:flutter/services.dart';

class BluetoothQuery {
  static const MethodChannel _channel =
      const MethodChannel('bluetooth_query');

  /// Starts the bluetooth query plugin by getting the BluetoothAdapter instance
  /// Returns false if the device doesn't support bluetooth
  static Future<bool> initialize() async {
    return await _channel.invokeMethod('initialize');
  }

  /// Check if the device bluetooth is turned on
  static Future<bool> isEnabled() async {
    return await _channel.invokeMethod('isEnabled');
  }

  /// Asks the user to turn the bluetooth on
  static Future<bool> askToTurnBluetoothOn() async {
    return await _channel.invokeMethod('askToTurnBluetoothOn');
  }

  /// Check if the application has access to the devices GPS
  static Future<bool> checkLocationPermission() async {
    return await _channel.invokeMethod('checkLocationPermission');
  }

  /// Asks the user for Location permission
  static Future<bool> askLocationPermission() async {
    return await _channel.invokeMethod('askLocationPermission');
  }
}
