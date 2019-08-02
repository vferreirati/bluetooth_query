// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bluetooth_device.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BluetoothDevice _$BluetoothDeviceFromJson(Map<String, dynamic> json) {
  return BluetoothDevice(
    json['name'] as String,
    json['address'] as String,
  );
}

Map<String, dynamic> _$BluetoothDeviceToJson(BluetoothDevice instance) =>
    <String, dynamic>{
      'name': instance.name,
      'address': instance.address,
    };
