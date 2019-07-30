#import "BluetoothQueryPlugin.h"
#import <bluetooth_query/bluetooth_query-Swift.h>

@implementation BluetoothQueryPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBluetoothQueryPlugin registerWithRegistrar:registrar];
}
@end
