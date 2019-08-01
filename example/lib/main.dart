import 'package:flutter/material.dart';

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

  void _isBluetoothEnabled(BuildContext context) async {
    final isEnabled = await BluetoothQuery.isBluetoothEnabled();
    final snack = SnackBar(content: Text("Bluetooth is on: $isEnabled"));
    Scaffold.of(context).showSnackBar(snack);
  }

  void _askToTurnBluetoothOn(BuildContext context) async {
    final turnedOn = await BluetoothQuery.askToTurnBluetoothOn();
    final snack = SnackBar(content: Text('Bluetooth is turned on: $turnedOn'));
    Scaffold.of(context).showSnackBar(snack);
  }

  void _checkLocationPermissions(BuildContext context) async {
    final hasPermission = await BluetoothQuery.checkLocationPermission();
    final snack = SnackBar(content: Text('Has location permission: $hasPermission'));
    Scaffold.of(context).showSnackBar(snack);
  }

  void _askLocationPermission(BuildContext context) async {
    final granted = await BluetoothQuery.askLocationPermission();
    final snack = SnackBar(content: Text('Location permission granted: $granted'));
    Scaffold.of(context).showSnackBar(snack);
  }

  void _startScan(BuildContext context) {
    BluetoothQuery.startScan().listen((device) {
      final snack = SnackBar(content: Text('Device name: ${device.name} | Device address: ${device.address}'));
      Scaffold.of(context).showSnackBar(snack);
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Bluetooth Query Plugin Demo'),
          centerTitle: true,
        ),
        body: Builder(
          builder: (context) => Container(
            alignment: AlignmentDirectional.center,
            margin: EdgeInsets.only(top: 20),
            child: Column(
              mainAxisSize: MainAxisSize.max,
              children: <Widget>[
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: FlatButton(
                    child: Text('Check bluetooth state'),
                    onPressed: () => _isBluetoothEnabled(context),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: FlatButton(
                    child: Text('Ask to turn bluetooth on'),
                    onPressed: () => _askToTurnBluetoothOn(context),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: FlatButton(
                    child: Text('Check location permissions'),
                    onPressed: () => _checkLocationPermissions(context),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: FlatButton(
                    child: Text('Ask location permission'),
                    onPressed: () => _askLocationPermission(context),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: FlatButton(
                    child: Text('Start scan'),
                    onPressed: () => _startScan(context),
                  ),
                ),
              ],
            ),
          ),
        )
      ),
    );
  }
}
