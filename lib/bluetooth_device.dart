class BluetoothDevice {
  String name;
  String address;

  BluetoothDevice({
    this.name, 
    this.address
  });

  static BluetoothDevice fromJson(Map<dynamic, dynamic> json) {
    return BluetoothDevice()
    ..address = json['address']
    ..name = json['name'];
  }
}