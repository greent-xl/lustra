package com.example.esp

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

class ConnectThread(private val device: BluetoothDevice, private val listener: ReceiveThread.Listener) : Thread(){
    val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    var mSocket: BluetoothSocket? = null
    lateinit var rThread: ReceiveThread

    init {
        try {
            mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        }catch (i: IOException){

        }
    }

    override fun run() {
        try {
            listener.onReceive("Connecting...")
            mSocket?.connect()
            listener.onReceive("Connected")
            rThread = ReceiveThread(mSocket!!, listener)
            rThread.start()
        }catch (i: IOException){
            listener.onReceive("Can not connect to device")
            closeConnection()
        }
    }

    fun closeConnection(){
        try {
            mSocket?.close()
        }catch (i: IOException){

        }
    }
}