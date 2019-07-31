package com.vferreirati.bluetooth_query

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
        }
    }

    private fun init(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if(adapter != null) {
            btAdapter = adapter
        }

        return adapter != null
    }

    private fun isEnabled(): Boolean = btAdapter.isEnabled

    private fun askToTurnBluetoothOn() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(intent, CODE_TURN_BLUETOOTH_ON)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return when(requestCode) {
            CODE_TURN_BLUETOOTH_ON -> {
                currentRequestResult!!.success(resultCode == Activity.RESULT_OK)
                currentRequestResult = null
                true
            }
            else -> false
        }
    }

    override fun onRequestPermissionsResult(p0: Int, p1: Array<out String>?, p2: IntArray?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        private const val CODE_TURN_BLUETOOTH_ON = 100

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
