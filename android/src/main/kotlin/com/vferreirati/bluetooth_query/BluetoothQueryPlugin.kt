package com.vferreirati.bluetooth_query

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar

class BluetoothQueryPlugin(
        private val context: Context,
        private val activity: Activity
) : MethodCallHandler, PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {

    private lateinit var btAdapter: BluetoothAdapter
    private var currentRequestResult: Result? = null

    override fun onMethodCall(call: MethodCall, result: Result) {
        when(call.method) {
            "initialize" -> result.success(init())
            "isEnabled" -> result.success(isEnabled())
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
    private fun isEnabled(): Boolean = btAdapter.isEnabled

    /**
     * Asks the user to turn the bluetooth on
     * */
    private fun askToTurnBluetoothOn() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(intent, CODE_TURN_BLUETOOTH_ON)
    }

    /**
     * Check if the application has access to the devices GPS
     * */
    private fun checkLocationPermission() = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    /**
     * Asks the user for Location permission
     * */
    private fun askLocationPermission() {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }

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
            val channel = MethodChannel(registrar.messenger(), "bluetooth_query")
            val pluginInstance = BluetoothQueryPlugin(registrar.context(), registrar.activity())

            registrar.addActivityResultListener(pluginInstance)
            registrar.addRequestPermissionsResultListener(pluginInstance)
            channel.setMethodCallHandler(pluginInstance)
        }
    }
}
