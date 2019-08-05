package com.vferreirati.bluetooth_query

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar

class BluetoothQueryPlugin(
        private val registrar: Registrar
) : MethodCallHandler, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {

    private val channel = MethodChannel(registrar.messenger(), "bluetooth_query")
    private val queryChannel = EventChannel(registrar.messenger(), "query_channel")

    private lateinit var btAdapter: BluetoothAdapter
    private var currentRequestResult: Result? = null
    private val gson = Gson()

    /**
     * Stream handler which represents the devices that are found by the bluetooth adapter
     * Saves the reference to the sink when listened to and removes it when removed
     * */
    private val deviceStreamHandler = object: EventChannel.StreamHandler {

        /**
         * Broadcast receiver for BluetoothDevice.ACTION_FOUND broadcasts
         * Adds the device information to the queryStream only and if only if it wasn't found before
         * */
        private val bluetoothReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if(intent.action == BluetoothDevice.ACTION_FOUND) {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    if(device.name != null && device.address != null && foundDevices.find { current -> current.address == device.address } == null) {
                        foundDevices.add(device)
                        devicesEventSink?.success(gson.toJson(FoundDevice(device.name, device.address)))
                    }
                }
            }
        }

        /* Steam sink reference
         * Post devices in this sink to notify the dart side of the application */
        private var devicesEventSink: EventChannel.EventSink? = null

        /* The actual list of devices found
         * Used to filter devices that show up twice */
        private var foundDevices = mutableListOf<BluetoothDevice>()

        override fun onListen(args: Any?, sink: EventChannel.EventSink) {
            registrar.activity().registerReceiver(bluetoothReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            devicesEventSink = sink
        }

        override fun onCancel(args: Any?) {
            registrar.activity().unregisterReceiver(bluetoothReceiver)
            foundDevices.clear()
            devicesEventSink = null
        }
    }

    init {
        channel.setMethodCallHandler(this)
        queryChannel.setStreamHandler(deviceStreamHandler)
    }

    /**
     * Entry point of method calls of this plugin
     * */
    override fun onMethodCall(call: MethodCall, result: Result) {
        when(call.method) {
            "initialize" -> result.success(init())
            "isBluetoothEnabled" -> result.success(isBluetoothEnabled())
            "askToTurnBluetoothOn" -> {
                if(currentRequestResult != null)
                    throw IllegalStateException("Already requesting permission!")
                currentRequestResult = result
                askToTurnBluetoothOn()
            }
            "checkLocationPermission" -> result.success(checkLocationPermission())
            "askLocationPermission" -> {
                if(currentRequestResult != null)
                    throw IllegalStateException("Already requesting permission!")
                currentRequestResult = result
                askLocationPermission()
            }
            "startScan" -> startScan()
        }
    }

    /**
     * Starts the bluetooth query plugin by getting the BluetoothAdapter instance
     * Returns false if the device doesn't support bluetooth
     * */
    private fun init(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if(adapter != null) {
            btAdapter = adapter
        }

        return adapter != null
    }

    /**
     * Check if the device bluetooth is turned on
     * */
    private fun isBluetoothEnabled(): Boolean = btAdapter.isEnabled

    /**
     * Asks the user to turn the bluetooth on
     * */
    private fun askToTurnBluetoothOn() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        registrar.activity().startActivityForResult(intent, CODE_TURN_BLUETOOTH_ON)
    }

    /**
     * Check if the application has access to the devices GPS
     * */
    private fun checkLocationPermission() = ContextCompat.checkSelfPermission(registrar.activity(), Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    /**
     * Asks the user for Location permission
     * */
    private fun askLocationPermission() {
        ActivityCompat.requestPermissions(registrar.activity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }

    /**
     * Start scanning for devices
     * */
    private fun startScan() = btAdapter.startDiscovery()

    /**
     * Android callback
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return when(requestCode) {
            CODE_TURN_BLUETOOTH_ON -> {
                currentRequestResult?.success(resultCode == Activity.RESULT_OK)
                currentRequestResult = null
                true
            }
            else -> false
        }
    }

    /**
     * Android callback
     * */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {
        return when(requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                currentRequestResult?.success(grantResults != null && grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED)
                currentRequestResult = null
                true
            }
            else -> false
        }
    }

    companion object {

        private const val CODE_TURN_BLUETOOTH_ON = 100

        private const val REQUEST_LOCATION_PERMISSION = 200

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val pluginInstance = BluetoothQueryPlugin(registrar)
            registrar.addActivityResultListener(pluginInstance)
            registrar.addRequestPermissionsResultListener(pluginInstance)
        }
    }
}
