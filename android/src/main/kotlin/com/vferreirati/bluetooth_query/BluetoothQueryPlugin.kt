package com.vferreirati.bluetooth_query

import android.bluetooth.BluetoothAdapter
import android.content.Context
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.IllegalArgumentException

class BluetoothQueryPlugin(
        private val context: Context
) : MethodCallHandler {

    private var btAdapter: BluetoothAdapter? = null

    override fun onMethodCall(call: MethodCall, result: Result) {
        when(call.method) {
            "initialize" -> result.success(init())
            "isEnabled" -> result.success(isEnabled())
        }
    }

    private fun init(): Boolean {
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        if(btAdapter == null) {
            throw IllegalArgumentException("Error getting bluetooth adapter")
        }

        return true
    }

    private fun isEnabled(): Boolean {
        return btAdapter!!.isEnabled
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "bluetooth_query")
            channel.setMethodCallHandler(BluetoothQueryPlugin(registrar.context()))
        }
    }
}
