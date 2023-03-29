package com.example.esp

import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.esp.databinding.ActivityControlBinding
import com.example.esp.databinding.ActivityMainBinding
import java.util.UUID

class ControlActivity : AppCompatActivity(), ReceiveThread.Listener {
    private lateinit var binding: ActivityControlBinding
    private lateinit var actListLauncher: ActivityResultLauncher<Intent>
    lateinit var btConnection: BtConnection
    private var listItem: ListItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBtListResult()
        init()
        binding.apply {
            bA.setOnClickListener{
                btConnection.sendMessage("A")
            }
            bB.setOnClickListener {
                btConnection.sendMessage("B")
            }
        }
    }

    private fun init(){
        val btManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val btAdapter = btManager.adapter
        btConnection = BtConnection(btAdapter, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.control_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_list){
            actListLauncher.launch(Intent(this, BtListActivity::class.java))
        }else if(item.itemId == R.id.id_connect){
            listItem.let {
                btConnection.connect(it?.mac!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onBtListResult(){
        actListLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
                if(it.resultCode == RESULT_OK){
                    listItem = it.data?.getSerializableExtra(BtListActivity.DEVICE_KEY) as ListItem
                }
        }
    }

    override fun onReceive(message: String) {
        runOnUiThread {
            binding.tvMessage.text = message
        }
    }
}