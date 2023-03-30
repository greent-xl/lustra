package com.example.esp

import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.esp.databinding.ActivityControlBinding

class ControlActivity : AppCompatActivity(), ReceiveThread.Listener {
    private lateinit var binding: ActivityControlBinding
    private lateinit var actListLauncher: ActivityResultLauncher<Intent>
    lateinit var btConnection: BtConnection
    private var listItem: ListItem? = null
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_PRIVILEGED
    )
    private val PERMISSIONS_LOCATION = arrayOf<String>(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        android.Manifest.permission.BLUETOOTH_PRIVILEGED
    )
    /*companion object {
        var PERMISSIONS = arrayOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.READ_PHONE_STATE
        )
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBtListResult()
        checkPermissions()
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

    private fun checkPermissions() {
        val permission1 =
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 =
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH)
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                1
            )
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_LOCATION,
                1
            )
        }
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