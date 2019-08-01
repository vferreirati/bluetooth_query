package com.vferreirati.bluetooth_query

data class FoundDevice(
        val name: String,
        val address: String
) {
    fun toMap(): Map<String, String> {
        return HashMap<String, String>().apply {
            put("name", name)
            put("address", address)
        }
    }
}