package com.example.esp

import android.bluetooth.BluetoothAdapter

class BtConnection(private val adapter: BluetoothAdapter, private val listener: ReceiveThread.Listener) {
    lateinit var cTread: ConnectThread
    fun connect(mac: String){
        if(adapter.isEnabled && mac.isNotEmpty()) {
            val device = adapter.getRemoteDevice(mac)
            device.let {
                cTread = ConnectThread(it, listener)
                cTread.start()
            }
        }
    }
    fun sendMessage(message: String){
        cTread.rThread.sendMessage(message.toByteArray())
    }

}