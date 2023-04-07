package com.example.esp

import android.bluetooth.BluetoothAdapter
import android.content.res.AssetManager
import android.widget.EditText

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
        //val inputStream: InputStream = File(fe).inputStream()
        val context = ControlActivity.appContext
        val assetManager: AssetManager = context.assets
        val inputStream = assetManager.open(fe)
        //val inputStream: InputStream =
        val bytes = inputStream.readBytes()
        return bytes
    }

    fun sendT(message: String) : ByteArray {
        return message.toByteArray()
    }

    fun sendMessagePic(message: String){
        cTread.rThread.sendMessage("0".toByteArray())
        cTread.rThread.sendMessage("jk".toByteArray())
        cTread.rThread.sendMessage(PngtoByte(message))
        cTread.rThread.sendMessage("jk".toByteArray())
    }
    fun sendMessageText(message: String){
        cTread.rThread.sendMessage("1".toByteArray())
        cTread.rThread.sendMessage("jk".toByteArray())
        cTread.rThread.sendMessage(message.toByteArray())
        cTread.rThread.sendMessage("jk".toByteArray())
    }
}