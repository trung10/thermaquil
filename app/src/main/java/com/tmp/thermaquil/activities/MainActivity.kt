package com.tmp.thermaquil.activities

import android.Manifest.permission
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.base.activities.BaseActivity
import com.tmp.thermaquil.ble.BluetoothLeService
import com.tmp.thermaquil.common.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private var loadingLayout: FrameLayout? = null
    private val REQUEST_BLUETOOTH_ADMIN_ID = 1
    private val REQUEST_LOCATION_ID = 2
    private val REQUEST_BLUETOOTH_ID = 3

    private var bleService: BluetoothLeService? = null
    private var bleAdapter: BluetoothAdapter? = null

    private var deviceAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("Frank", "MainActivity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Use this check to determine whether BLE is supported on the device.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }

        loadingLayout = findViewById(R.id.loadingLayout)

        //connectDevice()
    }

    override fun onResume() {
        Log.d(TAG, "onResume entry")
        super.onResume()
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())

        if (bleService != null && deviceAddress != null) {
            val result = bleService!!.connect(deviceAddress!!)
            Log.d(TAG, "Connect request result=$result")
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy entry")
        if (bleService != null) {
            bleService!!.disconnect()
            unbindService(serviceConnection)
            bleService = null
        }
        super.onDestroy()
    }

    private fun bleCheck() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Bluetooth permission has not been granted.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.BLUETOOTH),
                REQUEST_BLUETOOTH_ID
            )
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Bluetooth admin permission has not been granted.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.BLUETOOTH_ADMIN),
                REQUEST_BLUETOOTH_ADMIN_ID
            )
        }
    }

    private fun locationCheck() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Location permission has not been granted.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_ID
            )
        }
    }

    override fun showLoading(isShow: Boolean) {
        //todo
        loadingLayout?.visibility = View.GONE//if (isShow) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        grantResults.forEach { code ->
            if (REQUEST_LOCATION_ID != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun connectDevice() {
        Log.d(TAG, "connectDevice entry")

        bleCheck()
        locationCheck()

        val sharedPrefBLE = getSharedPreferences(getString(R.string.ble_device_key), MODE_PRIVATE)
        val deviceName = sharedPrefBLE.getString("name", null)
        deviceAddress = sharedPrefBLE.getString("address", null)

        Log.d(TAG, "deviceName $deviceName deviceAddress $deviceAddress")

        if (deviceAddress == null)
            return

        // Initializes a Bluetooth adapter.
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bleAdapter = bluetoothManager.adapter
        // Checks if Bluetooth is supported on the device.
        // Checks if Bluetooth is supported on the device.
        if (bleAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        showLoading(true)
        establishServiceConnection(deviceAddress)
    }

    private fun establishServiceConnection(address: String?) {
        if (address != null) {
            Log.d(TAG, "MAC: $address")
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    // Code to manage Service lifecycle.
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            Log.e(TAG, "onServiceConnected entry")
            bleService = (service as BluetoothLeService.LocalBinder).getService()
            if (bleService != null && !bleService!!.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                finish()
            }
            // Automatically connects to the device upon successful start-up initialization.
            Log.d(TAG, "onServiceConnected connect")
            deviceAddress!!.let {
                bleService!!.connect(it)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bleService = null
        }
    }

    // Handles various events fired by the Service.
    private val mGattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            when (intent.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED-> {
                    showLoading(false)
                    toast("CONNECTED")
                    Log.d(TAG, "ACTION_GATT_CONNECTED")
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    Log.d(TAG, "ACTION_GATT_DISCONNECTED")
                    showLoading(false)
                    toast("DISCONNECTED")
                }
                BluetoothLeService.ACTION_DATA_READ_COMPLETED -> {
                    Log.d(TAG, "Data Read Completed")
                    showLoading(false)
                    //updateUI()
                }
                BluetoothLeService.ACTION_DATA_AVAILABLE -> {
                    if (intent.getStringExtra(BluetoothLeService.ACTION_BATTERY_LEVEL) != null) {
                        Log.d(
                            TAG,
                            "Battery level on main activity: " + intent.getStringExtra(
                                BluetoothLeService.ACTION_BATTERY_LEVEL
                            )
                        )
                    }
                }
                BluetoothLeService.ACTION_ERROR -> {
                    toast("Error when sending")
                }
                BluetoothLeService.ACTION_PREPARE_SUCCESS -> {
                    if (cycle < 5){
                        cycle++
                        prepare()
                    } else {
                        showLoading(false)
                        toast("prepared")
                    }
                }
            }
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_READ_COMPLETED)
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        intentFilter.addAction(BluetoothLeService.ACTION_ERROR)
        intentFilter.addAction(BluetoothLeService.ACTION_PREPARE_SUCCESS)
        return intentFilter
    }

    private var cycle = 1
    fun prepare() {
        bleService?.let {
            showLoading(true)
            it.prepare(cycle)
        }
    }

}