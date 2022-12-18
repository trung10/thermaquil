package com.tmp.thermaquil.ble;

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.tmp.thermaquil.R
import com.tmp.thermaquil.ble.GattAttributes.ACCELEROMETER_TIME_READ
import com.tmp.thermaquil.ble.GattAttributes.BATTERY_LEVEL_READ
import com.tmp.thermaquil.ble.GattAttributes.GENERIC
import com.tmp.thermaquil.ble.GattAttributes.TEST
import com.tmp.thermaquil.ble.GattAttributes.X_ACCELERATION_READ
import com.tmp.thermaquil.ble.GattAttributes.X_GYROSCOPE_READ
import com.tmp.thermaquil.ble.GattAttributes.Y_ACCELERATION_READ
import com.tmp.thermaquil.ble.GattAttributes.Y_GYROSCOPE_READ
import com.tmp.thermaquil.ble.GattAttributes.Z_ACCELERATION_READ
import com.tmp.thermaquil.ble.GattAttributes.Z_GYROSCOPE_READ
import com.tmp.thermaquil.common.Utils
import com.tmp.thermaquil.data.models.AccelerometerData
import com.tmp.thermaquil.data.models.BatteryData
import java.util.*
import kotlin.experimental.and

/*
 * This is a service to handle the BLE interactions.
 *
 * */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class BluetoothLeService: Service() {
    private final val TAG = BluetoothLeService::class.java.getSimpleName()

    private var bleManager: BluetoothManager? = null
    private lateinit var bleAdapter: BluetoothAdapter
    private var bleDeviceAddress: String? = null
    private var bleGatt: BluetoothGatt? = null
    private lateinit var notifyCharacteristics: BluetoothGattCharacteristic
    private var connectionState: Int = STATE_DISCONNECTED
    var chars = arrayListOf<BluetoothGattCharacteristic>()

    /*private lateinit var testChar: BluetoothGattCharacteristic
    private lateinit var powerChar: BluetoothGattCharacteristic*/

    private lateinit var accelerometerData: AccelerometerData
    private var xAcc = arrayListOf<Int?>()
    private var yAcc = arrayListOf<Int?>()
    private var zAcc = arrayListOf<Int?>()
    private var xGyro = arrayListOf<Int?>()
    private var yGyro = arrayListOf<Int?>()
    private var zGyro = arrayListOf<Int?>()
    private var accTime = arrayListOf<Date?>()

    private lateinit var batteryData: BatteryData
    private var batteryLevel: Int = 0
    private var sweepComplete = false

    private var writeEnable = true

    companion object {
        private final val STATE_DISCONNECTED = 0
        private final val STATE_CONNECTING = 1
        private final val STATE_CONNECTED = 2

        public val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
        public val ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
        public val ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
        public val ACTION_DATA_READ_COMPLETED = "ACTION_DATA_READ_COMPLETED";
        public val ACTION_BATTERY_LEVEL = "ACTION_BATTERY_LEVEL";
        public val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
        public val ACTION_ERROR = "ACTION_ERROR";
        public val ACTION_PREPARE_SUCCESS = "ACTION_PREPARE_SUCCESS";

        public val UUID_X_ACCELERATION = UUID.fromString(GattAttributes.X_ACCELERATION_MEASUREMENT);
        public val UUID_Y_ACCELERATION = UUID.fromString(GattAttributes.Y_ACCELERATION_MEASUREMENT);
        public val UUID_Z_ACCELERATION = UUID.fromString(GattAttributes.Z_ACCELERATION_MEASUREMENT);
        public val UUID_X_GYROSCOPE = UUID.fromString(GattAttributes.X_GYROSCOPE_MEASUREMENT);
        public val UUID_Y_GYROSCOPE = UUID.fromString(GattAttributes.Y_GYROSCOPE_MEASUREMENT);
        public val UUID_Z_GYROSCOPE = UUID.fromString(GattAttributes.Z_GYROSCOPE_MEASUREMENT);
        public val UUID_ACCELERATION_TIME =
            UUID.fromString(GattAttributes.ACCELEROMETER_TIME_MEASUREMENT);

        public val UUID_BATTERY_LEVEL = UUID.fromString(GattAttributes.BATTERY_LEVEL);
        public val UUID_BATTERY_STATUS = UUID.fromString(GattAttributes.BATTERY_STATUS);

        public val UUID_TEST = UUID.fromString(GattAttributes.TEST_ATTRIBUTE);
        public val UUID_GENERIC = UUID.fromString(GattAttributes.GENERIC_ATTRIBUTE);
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED

                broadcastUpdate(ACTION_GATT_CONNECTED)
                Log.i(TAG, "Attempting to start service discovery.")
                Log.i(TAG, "Connected to GATT server.")
                //toast("Connected to GATT server.");
                gatt!!.discoverServices();
                Log.i(TAG, "Connected to GATT server.")

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED
                Log.i(TAG, "Disconnected from GATT server.")
                //toast("Disconnected from GATT server.");

                broadcastUpdate(ACTION_GATT_DISCONNECTED)
            } else {
                Log.i(TAG, "Other State");
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                clearDataArrays()
                val services = gatt!!.services

                // Loops through available GATT Services.
                for (gattService in services) {
                    val gattCharacteristicsList = gattService.characteristics
                    Log.e(TAG, "gattService uuid: " + gattService.uuid)


                    // Loops through available Characteristics.
                    for (gattCharacteristic in gattCharacteristicsList) {
                        Log.e(
                            TAG,
                            "gattCharacteristic uuid: " + gattCharacteristic.uuid
                        )
                        if (isDataCharacteristic(gattCharacteristic) != 0) {
                            if (isDataCharacteristic(gattCharacteristic) == GENERIC) {
                                chars.add(gattCharacteristic)
                            }
                        } else {
                            //toast("CONNECTED Fail");
                            Log.e(TAG, "CONNECTED Fai")
                        }
                    }
                }

                //requestCharacteristics(gatt);
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        @SuppressLint("MissingPermission")
        fun requestCharacteristics(gatt: BluetoothGatt) {
            //gatt.readCharacteristic(powerChar)

            gatt.readCharacteristic(chars.get(chars.size -1))
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.e(TAG, "onCharacteristicWrite: ")

            if(status != BluetoothGatt.GATT_SUCCESS){
                Log.d("onCharacteristicWrite", "Failed write, retrying")
                gatt?.writeCharacteristic(characteristic)
            } else {
                broadcastUpdate(ACTION_PREPARE_SUCCESS)
                writeEnable = true
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {

            Log.e(TAG, "onCharacteristicChanged: ")

            val data = characteristic?.value

            if (data != null && data.size > 0)
                for (b in data) {
                    val i: Byte = b and 0xFF.toByte()
                    Log.e(TAG, "Byte: ${i.toInt()}")
                }

        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.e(TAG, "onCharacteristicRead status$status")

            Log.e(
                TAG,
                "onCharacteristicRead characteristic: " + characteristic!!.uuid
            )

            //if (UUID_POWER.equals(characteristic.getUuid())) {

            //if (UUID_POWER.equals(characteristic.getUuid())) {
            Log.e(TAG, "onCharacteristicRead Data read: ")

            val data = characteristic.value

            if (data != null && data.size > 0)
                for (b in data) {
                val i: Byte = b and 0xFF.toByte()
                Log.e(TAG, "Byte: ${i.toInt()}")
            }
            //}

            /*if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

                switch (isDataCharacteristic(characteristic)) {
                    case X_ACCELERATION_READ:
                    case Y_ACCELERATION_READ:
                    case Z_ACCELERATION_READ:
                    case X_GYROSCOPE_READ:
                    case Y_GYROSCOPE_READ:
                    case Z_GYROSCOPE_READ:
                    case ACCELEROMETER_TIME_READ:
                        if (sweepComplete) {
                            chars.remove(chars.get(chars.size() - 1));
                            sweepComplete = false;
                        }

                        break;

                    default:
                        chars.remove(chars.get(chars.size() - 1));
                        break;
                }

                if (chars.size() > 0) {
                    requestCharacteristics(gatt);

                } else {
                    Log.i(TAG, "Gatt server data read completed.");
                    saveAgmData();
                    broadcastUpdate(ACTION_DATA_READ_COMPLETED);
                    disconnect();
                }
            }*/
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorRead(gatt, descriptor, status)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
        }
    }

    fun requestCharacteristics(gatt: BluetoothGatt, c: BluetoothGattCharacteristic?): Boolean {
        return gatt.readCharacteristic(c)
    }

    fun isDataCharacteristic(characteristic: BluetoothGattCharacteristic): Int {
        return if (UUID_BATTERY_LEVEL == characteristic.uuid) {
            BATTERY_LEVEL_READ
        } else if (UUID_X_ACCELERATION == characteristic.uuid) {
            X_ACCELERATION_READ
        } else if (UUID_Y_ACCELERATION == characteristic.uuid) {
            Y_ACCELERATION_READ
        } else if (UUID_Z_ACCELERATION == characteristic.uuid) {
            Z_ACCELERATION_READ
        } else if (UUID_X_GYROSCOPE == characteristic.uuid) {
            X_GYROSCOPE_READ
        } else if (UUID_Y_GYROSCOPE == characteristic.uuid) {
            Y_GYROSCOPE_READ
        } else if (UUID_Z_GYROSCOPE == characteristic.uuid) {
            Z_GYROSCOPE_READ
        } else if (UUID_ACCELERATION_TIME == characteristic.uuid) {
            ACCELEROMETER_TIME_READ
        } else if (UUID_TEST == characteristic.uuid) {
            TEST
        } else if (UUID_GENERIC == characteristic.uuid) {
            GENERIC
        } else {
            0
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)
        val charWhat = isDataCharacteristic(characteristic)
        val count: Int
        when (charWhat) {
            BATTERY_LEVEL_READ -> {
                batteryLevel =
                    characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                Log.d(
                    TAG,
                    String.format("Received battery level: %d", batteryLevel)
                )
                intent.putExtra(ACTION_BATTERY_LEVEL, batteryLevel.toString())
            }
            X_ACCELERATION_READ -> {
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                xAcc[count] = characteristic.getStringValue(0).split(",").toTypedArray()[0].toInt()
                if (!xAcc.contains(null)) {
                    sweepComplete = true
                }
                Log.d(
                    TAG, String.format(
                        "Received x acceleration level: %d",
                        xAcc[count]
                    )
                )
            }
            Y_ACCELERATION_READ -> {
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                yAcc[count] = characteristic.getStringValue(0).split(",").toTypedArray()[0].toInt()
                if (!yAcc.contains(null)) {
                    sweepComplete = true
                }
                Log.d(
                    TAG, String.format(
                        "Received y acceleration level: %d",
                        yAcc[count]
                    )
                )
            }
            Z_ACCELERATION_READ -> {
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                zAcc[count] = characteristic.getStringValue(0).split(",").toTypedArray()[0].toInt()
                if (!zAcc.contains(null)) {
                    sweepComplete = true
                }
                Log.d(
                    TAG, String.format(
                        "Received z acceleration level: %d",
                        zAcc[count]
                    )
                )
            }
            X_GYROSCOPE_READ -> {
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                xGyro[count] = characteristic.getStringValue(0).split(",").toTypedArray()[0].toInt()
                if (!xGyro.contains(null)) {
                    sweepComplete = true
                }
                Log.d(
                    TAG, String.format(
                        "Received x gyroscope level: %d",
                        xGyro[count]
                    )
                )
            }
            Y_GYROSCOPE_READ -> {
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                yGyro[count] = characteristic.getStringValue(0).split(",").toTypedArray()[0].toInt()
                if (!yGyro.contains(null)) {
                    sweepComplete = true
                }
                Log.d(
                    TAG, String.format(
                        "Received y gyroscope level: %d",
                        yGyro[count]
                    )
                )
            }
            Z_GYROSCOPE_READ -> {
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                zGyro[count] = characteristic.getStringValue(0).split(",").toTypedArray()[0].toInt()
                if (!zGyro.contains(null)) {
                    sweepComplete = true
                }
                Log.d(
                    TAG, String.format(
                        "Received z gyroscope level: %d",
                        zGyro[count]
                    )
                )
            }
            ACCELEROMETER_TIME_READ -> {
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                //                accTime.set(count,Integer.parseInt(characteristic.getStringValue(0).split(",")[0]));
                accTime[count] = Date()
                if (!accTime.contains(null)) {
                    sweepComplete = true
                }
            }
            TEST -> {
                Log.e(TAG, "read Test: " + characteristic.getStringValue(0))
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                //                accTime.set(count,Integer.parseInt(characteristic.getStringValue(0).split(",")[0]));
                accTime[count] = Date()
                if (!accTime.contains(null)) {
                    sweepComplete = true
                }
            }
            else -> {
                // For all other profiles, writes the data formatted in HEX.
                val data = characteristic.value
                if (data != null && data.size > 0) {
                    val stringBuilder = StringBuilder(data.size)
                    for (byteChar in data) stringBuilder.append(String.format("%02X ", byteChar))
                }
            }
        }
        sendBroadcast(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    private fun saveAgmData() {
        accelerometerData = AccelerometerData(xAcc, yAcc, zAcc, xGyro, yGyro, zGyro, accTime)
        val sharedPref = getSharedPreferences(getString(R.string.agm_key), MODE_PRIVATE)
        val prefBleDeviceEditor = sharedPref.edit()
        prefBleDeviceEditor.putString(
            "x_acc_avg",
            java.lang.String.valueOf(accelerometerData.xAccelerationAvg)
        )
        prefBleDeviceEditor.putString(
            "y_acc_avg",
            java.lang.String.valueOf(accelerometerData.yAccelerationAvg)
        )
        prefBleDeviceEditor.putString(
            "z_acc_avg",
            java.lang.String.valueOf(accelerometerData.zAccelerationAvg)
        )
        prefBleDeviceEditor.apply()
    }

    private fun clearDataArrays() {
        xAcc = ArrayList()
        yAcc = ArrayList()
        zAcc = ArrayList()
        xGyro = ArrayList()
        yGyro = ArrayList()
        zGyro = ArrayList()
        accTime = ArrayList()
        for (i in 0..4) {
            xAcc.add(i, null)
            yAcc.add(i, null)
            zAcc.add(i, null)
            xGyro.add(i, null)
            yGyro.add(i, null)
            zGyro.add(i, null)
            accTime.add(i, null)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind entry")
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close()
        return super.onUnbind(intent)
    }

    private val binder: IBinder = LocalBinder()

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    fun initialize(): Boolean {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (bleManager == null) {
            bleManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            if (bleManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.")
                return false
            }
        }
        bleAdapter = bleManager!!.adapter
        if (bleAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    fun connect(address: String): Boolean {
        Log.w(TAG, "connect entry")
        if (bleAdapter == null || address == null) {
            Log.w(
                TAG,
                "BluetoothAdapter not initialized or unspecified address."
            )
            return false
        }

        // Previously connected device.  Try to reconnect.
        if (bleDeviceAddress != null && address == bleDeviceAddress && bleGatt != null) {
            Log.d(TAG, "Trying to use an existing bleGatt for connection.")
            return if (bleGatt!!.connect()) {
                connectionState = STATE_CONNECTING
                Log.d(TAG, "Connecting...")
                true
            } else {
                Log.d(TAG, "Can't connect...")
                false
            }
        }
        val device = bleAdapter.getRemoteDevice(address)
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.")
            return false
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bleGatt = device.connectGatt(this, false, gattCallback)
        Log.d(TAG, "Trying to create a new connection.")
        bleDeviceAddress = address
        connectionState = STATE_CONNECTING
        return true
    }

    /*fun send1() {
        if (testChar == null) {
            Toast.makeText(this, "Cannot find 19B10001 char", Toast.LENGTH_SHORT).show()
            return
        }
        Log.e(TAG, "send 1")
        val bytearray = byteArrayOf(0x01.toByte())
        testChar.value = bytearray
        bleGatt?.writeCharacteristic(testChar)
    }

    fun send0() {
        if (testChar == null) {
            Toast.makeText(this, "Cannot find 19B10001 char", Toast.LENGTH_SHORT).show()
            return
        }
        Log.e(TAG, "send 0")
        val bytearray = byteArrayOf(0x00.toByte())
        testChar.value = bytearray
        bleGatt?.writeCharacteristic(testChar)
    }


    fun sendPowerOn() {
        Log.e(TAG, "sendPowerOn")
        if (powerChar == null) {
            Log.e(TAG, "return")
            return
        }
        val bytearray = byteArrayOf(0x07.toByte(), 0x01)
        powerChar.value = bytearray
        bleGatt?.writeCharacteristic(powerChar)
        Log.e(TAG, "Read char: " + requestCharacteristics(bleGatt!!, powerChar))
    }

    fun sendPowerOff() {
        if (powerChar == null) {
            Log.e(TAG, "return")
            return
        }
        Log.e(TAG, "sendPowerOff")
        val bytearray = byteArrayOf(0x07.toByte(), 0x00)
        powerChar.value = bytearray
        bleGatt?.writeCharacteristic(powerChar)
        Log.e(TAG, "Read char: " + requestCharacteristics(bleGatt!!, powerChar))
    }*/

    @SuppressLint("MissingPermission")
    fun prepare(cycle: Int) {
        if (chars.size == 0) {
            broadcastUpdate(ACTION_ERROR)
            return
        }

        val cha = chars[0]

        if (cycle == 5){
            val bytearray = byteArrayOf(0x03, cycle.toByte())
            cha.value = bytearray
            bleGatt?.writeCharacteristic(cha)
            writeEnable = false
        } else if (cycle % 2 == 0) {
            val bytearray = byteArrayOf(0x01, cycle.toByte())
            bytearray.plus(Utils.floatToBytes(100f))
            bytearray.plus(Utils.intTo4Bytes(900))

            cha.value = bytearray
            bleGatt?.writeCharacteristic(cha)
            writeEnable = false
        } else {
            val bytearray = byteArrayOf(0x02, cycle.toByte())
            bytearray.plus(Utils.floatToBytes(20f))
            bytearray.plus(Utils.intTo4Bytes(200))

            cha.value = bytearray
            bleGatt?.writeCharacteristic(cha)
            writeEnable = false
        }

    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    fun disconnect() {
        if (bleAdapter == null || bleGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bleGatt?.disconnect()
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    fun close() {
        if (bleGatt == null) {
            return
        }
        bleGatt!!.disconnect()
        bleGatt!!.discoverServices()
        bleGatt!!.close()
        bleGatt = null
    }

    /**
     * Request a read on a given `BluetoothGattCharacteristic`. The read result is reported
     * asynchronously through the `BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)`
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic?) {
        if (bleAdapter == null || bleGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bleGatt!!.readCharacteristic(characteristic)
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enabled: Boolean
    ) {
        if (bleAdapter == null || bleGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bleGatt!!.setCharacteristicNotification(characteristic, enabled)

        // For only characteristics that are meant to notify
        if (UUID_BATTERY_LEVEL == characteristic.uuid || UUID_BATTERY_STATUS == characteristic.uuid) {
            val descriptor =
                characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bleGatt!!.writeDescriptor(descriptor)
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after `BluetoothGatt#discoverServices()` completes successfully.
     *
     * @return A `List` of supported services.
     */
    fun getSupportedGattServices(): List<BluetoothGattService?>? {
        return if (bleGatt == null) null else bleGatt!!.services
    }

}