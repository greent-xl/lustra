package com.example.esp

import android.app.Activity
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.esp.databinding.ActivityControlBinding
//import com.example.learning.R
//import com.example.learning.databinding.ActivityControlBinding

class ControlActivity : AppCompatActivity(), ReceiveThread.Listener {
    private lateinit var binding: ActivityControlBinding
    private lateinit var actListLauncher: ActivityResultLauncher<Intent>
    lateinit var btConnection: BtConnection
    private var listItem: ListItem? = null


    var pickedPhoto : Uri? = null
    var inputText : EditText? = null

    /*fun pickedPhoto (){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        }else{
            val galleryIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntext, 2)
            val imageUri = galleryIntext.data
            val imagePath = getPathFromUri(imageUri)
            btConnection.sendMessage(imageUri.toString())
        }
    }

    fun getPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex ?: return null)
        cursor?.close()
        return path
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            data?.data?.let { uri ->
                val imagePath = getPathFromUri(uri)
                // использование imagePath
            }
        }
    }*/
    fun getPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex ?: return null)
        cursor?.close()
        return path
    }

    fun pickedPhoto (){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        }else{
            val galleryIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntext, 2)
            pickedPhoto = galleryIntext.data
            val imagePath = pickedPhoto?.let { getPathFromUri(it) }
            if (imagePath != null) {
                btConnection.sendMessagePic(imagePath)
            }
        }
    }


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
    companion object {
        lateinit  var appContext: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContext = this@ControlActivity
        binding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBtListResult()
        checkPermissions()
        init()
        binding.apply {
            bA.setOnClickListener{
                pickedPhoto()
            }
            bB.setOnClickListener {
                btConnection.sendMessageText("pussy_scaled.png")
            }
        }
        sendText()
    }
    fun sendText() : String {
        val editText:EditText = findViewById(R.id.InputText)
        val message = editText.text.toString()
        return message
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