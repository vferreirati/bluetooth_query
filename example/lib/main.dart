import 'package:flutter/material.dart';
import 'dart:async';

import 'package:bluetooth_query/bluetooth_query.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
    _initBluetooth();
  }

  void _initBluetooth() {
    BluetoothQuery.initialize();
  }

  void _askToTurnBluetoothOn() async {
    final turnedOn = await BluetoothQuery.askToTurnBluetoothOn();
    print('Bluetooth is turned on: $turnedOn');
  }

  void _checkLocationPermissions() async {
    final hasPermission = await BluetoothQuery.checkLocationPermission();
    print('Has location permission: $hasPermission');
  }

  void _askLocationPermission() async {
    final granted = await BluetoothQuery.askLocationPermission();
    print('Location permission granted: $granted');
  }

  void _startScan() {
    print('Starting scan');
    BluetoothQuery.startScan();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Bluetooth Query Plugin Demo'),
        ),
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          mainAxisSize: MainAxisSize.max,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: FlatButton(
                child: Text('Ask to turn bluetooth on'),
                onPressed: _askToTurnBluetoothOn,
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: FlatButton(
                child: Text('Check location permissions'),
                onPressed: _checkLocationPermissions,
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: FlatButton(
                child: Text('Ask location permission'),
                onPressed: _askLocationPermission,
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: FlatButton(
                child: Text('Start scan'),
                onPressed: _startScan,
              ),
            ),
          ],
        )
      ),
    );
  }
}
