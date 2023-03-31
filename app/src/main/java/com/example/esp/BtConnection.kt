package com.example.esp

import android.bluetooth.BluetoothAdapter
import java.io.File
import java.io.InputStream

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

    fun PngtoByte(fe: String): ByteArray {
        val inputStream: InputStream = File(fe).inputStream()
        val bytes = inputStream.readBytes()
        return bytes
    }

    fun sendMessage(message: String){
        cTread.rThread.sendMessage(PngtoByte(message))
    }
}