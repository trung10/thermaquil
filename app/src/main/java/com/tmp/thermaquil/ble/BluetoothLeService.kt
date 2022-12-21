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
import com.tmp.thermaquil.ble.GattAttributes.PCM_BLOCK_READ
import com.tmp.thermaquil.ble.GattAttributes.PCM_COMMANDS_READ
import com.tmp.thermaquil.ble.GattAttributes.PCM_EVENT_READ
import com.tmp.thermaquil.ble.GattAttributes.PCM_STATUS_READ
import com.tmp.thermaquil.ble.GattAttributes.PCM_SW_READ
import com.tmp.thermaquil.common.Utils
import com.tmp.thermaquil.data.models.AccelerometerData
import com.tmp.thermaquil.data.models.BatteryData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.and

/*
 * This is a service to handle the BLE interactions.
 *
 * */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class BluetoothLeService : Service() {
    private final val TAG = BluetoothLeService::class.java.getSimpleName()

    private var bleManager: BluetoothManager? = null
    private lateinit var bleAdapter: BluetoothAdapter
    private var bleDeviceAddress: String? = null
    private var bleGatt: BluetoothGatt? = null
    private lateinit var notifyCharacteristics: BluetoothGattCharacteristic
    var connectionState: Int = STATE_DISCONNECTED
    var chars = arrayListOf<BluetoothGattCharacteristic>()

    private lateinit var batteryData: BatteryData
    private var batteryLevel: Int = 0
    private var sweepComplete = false

    private var readWriteEnable = true

    private var flow = 0 // 1 prepare, 2 log file

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

        public val UUID_PCM_SERVICE = UUID.fromString(GattAttributes.PCM_SERVICE)

        public val UUID_COMMANDS = UUID.fromString(GattAttributes.PCM_COMMANDS_ATTRIBUTE)
        public val UUID_EVENT = UUID.fromString(GattAttributes.PCM_EVENT_ATTRIBUTE)
        public val UUID_SW_READ = UUID.fromString(GattAttributes.PCM_SW_ATTRIBUTE)

        public val UUID_STATUS_READ = UUID.fromString(GattAttributes.PCM_STATUS_ATTRIBUTE)
        public val UUID_BLOCK_READ = UUID.fromString(GattAttributes.PCM_BLOCK_ATTRIBUTE)

        public val UUID_BATTERY_LEVEL = UUID.fromString(GattAttributes.BATTERY_LEVEL);
        public val UUID_BATTERY_STATUS = UUID.fromString(GattAttributes.BATTERY_STATUS);
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    connectionState = STATE_CONNECTED

                    broadcastUpdate(ACTION_GATT_CONNECTED)
                    Log.i(TAG, "Attempting to start service discovery.")
                    gatt!!.discoverServices()

                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectionState = STATE_DISCONNECTED
                    Log.i(TAG, "Disconnected from GATT server.")
                    broadcastUpdate(ACTION_GATT_DISCONNECTED)
                }
                else -> {
                    Log.i(TAG, "Other State");
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                clearDataArrays()
                val service = gatt!!.services.find { s -> s.uuid == UUID_PCM_SERVICE }

                // Loops through available GATT Services.
                service?.let { s ->
                    Log.d(TAG, "gattService uuid: " + s.uuid)

                    for (gattCharacteristic in service.characteristics) {
                        Log.e(
                            TAG,
                            "gattCharacteristic uuid: " + gattCharacteristic.uuid
                        )
                        if (isDataCharacteristic(gattCharacteristic) != 0) {
                            chars.add(gattCharacteristic)
                        } else {
                            //toast("CONNECTED Fail");
                            Log.d(TAG, "${gattCharacteristic.uuid} is PCM attribute")
                        }
                    }
                }

                // read file
                //requestCharacteristics(gatt);
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        @SuppressLint("MissingPermission")
        fun requestCharacteristics(gatt: BluetoothGatt) {
            //gatt.readCharacteristic(powerChar)

            gatt.readCharacteristic(chars.get(chars.size - 1))
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.e(TAG, "onCharacteristicWrite: ${characteristic?.uuid}")

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Failed write, retrying")
                gatt?.writeCharacteristic(characteristic)
            } else {
                Log.d(TAG, "onCharacteristicWrite LogSate: ${logState}")
                Log.d(TAG, "onCharacteristicWrite: ${characteristic?.value?.get(0)}")
                when (flow) {
                    1 -> {
                        broadcastUpdate(ACTION_PREPARE_SUCCESS)
                        readWriteEnable = true
                    }

                    2 -> {
                        readWriteEnable = true

                        if (logState == 1) {
                            //logState = 2
                            //sendLogFile()
                        } else if (logState == 2) {
                            logState = 3
                            sendLogFile()
                        } else if (logState == 5){
                            logState = 3
                            sendLogFile()
                        }
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {

            Log.e(TAG, "onCharacteristicChanged: ${characteristic?.uuid}")

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
            Log.d(TAG, "onCharacteristicRead status$status")

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(
                    TAG,
                    "onCharacteristicRead characteristic: " + characteristic!!.uuid
                )

                //if (UUID_POWER.equals(characteristic.getUuid())) {

                //if (UUID_POWER.equals(characteristic.getUuid())) {
                Log.d(TAG, "onCharacteristicRead Data read: ")

                val data = characteristic.value

                /*if (data != null && data.size > 0)
                    for (b in data) {
                        val i: Byte = b and 0xFF.toByte()
                        Log.e(TAG, "Byte: ${i.toInt()}")
                    }*/

                when (flow) {
                    1 -> {
                        broadcastUpdate(ACTION_PREPARE_SUCCESS)
                        readWriteEnable = true
                    }

                    2 -> {
                        readWriteEnable = true

                        if (logState == 3) {
                            if (characteristic.uuid == UUID_STATUS_READ){
                                if (data[0].toInt() == 0x01){
                                    logState = 4
                                    sendLogFile()
                                } else if (data[0].toInt() == 0x02) {
                                    logState = 6
                                    sendLogFile()
                                }
                            }
                        } else if (logState == 4) {
                            if (characteristic.uuid == UUID_BLOCK_READ){
                                data.forEach {
                                    file.add(it)
                                }
                                logState = 5
                                sendLogFile()
                            }
                        }
                    }
                }

            } else {

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
            Log.d(TAG, "onDescriptorRead status$status")
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            Log.d(TAG, "onDescriptorWrite status$status")

        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            Log.d(TAG, "onReadRemoteRssi $rssi")
        }

        override fun onServiceChanged(gatt: BluetoothGatt) {
            super.onServiceChanged(gatt)
            Log.d(TAG, "onServiceChanged")
        }
    }

    fun requestCharacteristics(gatt: BluetoothGatt, c: BluetoothGattCharacteristic?): Boolean {
        return gatt.readCharacteristic(c)
    }

    fun isDataCharacteristic(characteristic: BluetoothGattCharacteristic): Int {
        return when (characteristic.uuid) {
            UUID_BATTERY_LEVEL -> {
                0
            }
            UUID_COMMANDS -> {
                PCM_COMMANDS_READ
            }
            UUID_EVENT -> {
                PCM_EVENT_READ
            }
            UUID_SW_READ -> {
                PCM_SW_READ
            }
            UUID_STATUS_READ -> {
                PCM_STATUS_READ
            }

            UUID_BLOCK_READ -> {
                PCM_BLOCK_READ
            }
            else -> {
                0
            }
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
            /*ACCELEROMETER_TIME_READ -> {
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                //                accTime.set(count,Integer.parseInt(characteristic.getStringValue(0).split(",")[0]));
                accTime[count] = Date()
                if (!accTime.contains(null)) {
                    sweepComplete = true
                }
            }*/
            PCM_COMMANDS_READ -> {
                /*Log.e(TAG, "read Test: " + characteristic.getStringValue(0))
                count = characteristic.getStringValue(0).split(",").toTypedArray()[1].toInt()
                //                accTime.set(count,Integer.parseInt(characteristic.getStringValue(0).split(",")[0]));
                accTime[count] = Date()
                if (!accTime.contains(null)) {
                    sweepComplete = true
                }*/
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

    private fun clearDataArrays() {
        //todo
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

    private fun findCommandChar() = this.chars.find { c -> c.uuid == UUID_COMMANDS }
    private fun findStatusChar() = this.chars.find { c -> c.uuid == UUID_STATUS_READ }
    private fun findBlockChar() = this.chars.find { c -> c.uuid == UUID_BLOCK_READ }

    private val file = ArrayList<Byte>()
    private var logState = 2
    fun sendLogFile() {
        Log.d(TAG, "sendLogFile Entry status: ${logState}")
        when (logState) {
            /*1 -> {
                val cha = findStatusChar()
                if (cha == null) {
                    broadcastUpdate(ACTION_ERROR)
                    return
                }

                val bytearray = byteArrayOf(0x03)

                cha.value = bytearray
                bleGatt?.setCharacteristicNotification(cha, true)
                val b = bleGatt?.writeCharacteristic(cha)
                Log.d(TAG, "sendLogFile writeCharacteristic: ${b}")
                readWriteEnable = false
            }*/
            2 -> {
                val cha = findCommandChar()
                if (cha == null) {
                    broadcastUpdate(ACTION_ERROR)
                    return
                }

                val bytearray = byteArrayOf(0x0B, 0x00, 0x00, 0x00, 0x00)
                //bytearray.plus(Utils.intTo4Bytes(0))

                cha.value = bytearray
                bleGatt?.writeCharacteristic(cha)
                readWriteEnable = false
            }
            3 -> {
                val cha = findStatusChar()
                if (cha == null) {
                    broadcastUpdate(ACTION_ERROR)
                    return
                }
                bleGatt?.readCharacteristic(cha)
                readWriteEnable = false

            }
            4 -> {
                val cha = findBlockChar()
                if (cha == null) {
                    broadcastUpdate(ACTION_ERROR)
                    return
                }
                bleGatt?.readCharacteristic(cha)
                readWriteEnable = false
            }
            5 -> {
                val cha = findStatusChar()
                if (cha == null) {
                    broadcastUpdate(ACTION_ERROR)
                    return
                }
                val bytearray = byteArrayOf(0x04)

                cha.value = bytearray
                bleGatt?.writeCharacteristic(cha)
                readWriteEnable = false
            }
            6 -> {
                Log.d(TAG, "Finish")
                Log.d(TAG, "${file.size}")

                Utils.writeFileOnInternalStorage(baseContext, "Text.txt", file)
                logState = 0
            }
        }

        flow = 2
    }

    @SuppressLint("MissingPermission")
    fun prepare(cycle: Int) {

        val cha = findCommandChar()
        if (cha == null) {
            broadcastUpdate(ACTION_ERROR)
            return
        }

        if (cycle == 6) {
            flow = 0
            return
        }

        flow = 1
        if (cycle == 5) {
            val bytearray = byteArrayOf(0x03, cycle.toByte())
            cha.value = bytearray
            bleGatt?.writeCharacteristic(cha)
            readWriteEnable = false
        } else if (cycle % 2 == 0) {
            val bytearray = byteArrayOf(0x01, cycle.toByte())
            bytearray.plus(Utils.floatToBytes(100f))
            bytearray.plus(Utils.intTo4Bytes(900))

            cha.value = bytearray
            bleGatt?.writeCharacteristic(cha)
            readWriteEnable = false
        } else {
            val bytearray = byteArrayOf(0x02, cycle.toByte())
            bytearray.plus(Utils.floatToBytes(20f))
            bytearray.plus(Utils.intTo4Bytes(200))

            cha.value = bytearray
            bleGatt?.writeCharacteristic(cha)
            readWriteEnable = false
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