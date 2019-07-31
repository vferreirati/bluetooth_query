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
import java.lang.IllegalArgumentException

class BluetoothQueryPlugin(
        private val context: Context,
        private val activity: Activity
) : MethodCallHandler {

    private var btAdapter: BluetoothAdapter? = null

    override fun onMethodCall(call: MethodCall, result: Result) {
        when(call.method) {
            "initialize" -> result.success(init())
            "isEnabled" -> result.success(isEnabled())
            "hasBluetoothPermission" -> result.success(checkIfHasBluetoothPermission())
            "askToTurnBluetoothOn" -> askToTurnBluetoothOn()
            "askBluetoothPermission" -> askBluetoothPermission()
        }
    }

    private fun init(): Boolean {
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        if(btAdapter == null) {
            throw IllegalArgumentException("This device doesn't support bluetooth")
        }

        return true
    }

    private fun isEnabled(): Boolean {
        return btAdapter!!.isEnabled
    }

    private fun checkIfHasBluetoothPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) == PERMISSION_GRANTED
    }

    // TODO: This method should be a stream, this way the dart client can listen for the result
    private fun askToTurnBluetoothOn() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(intent, CODE_TURN_BLUETOOTH_ON)
    }

    // TODO: This method should be a stream, this way the dart client can listen for the result
    private fun askBluetoothPermission() {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH), CODE_BLUETOOTH_PERMISSION)
    }

    companion object {

        private const val CODE_TURN_BLUETOOTH_ON = 100

        private const val CODE_BLUETOOTH_PERMISSION = 200

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "bluetooth_query")
            val pluginInstance = BluetoothQueryPlugin(registrar.context(), registrar.activity())

            channel.setMethodCallHandler(pluginInstance)
        }
    }
}
