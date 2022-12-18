package com.tmp.thermaquil.ble;

/*
 * This is where you keep all your UUIDs and some other items to differentiate BLE services
 * and characteristics.
 *
 * */
object GattAttributes {
    private val attributes: HashMap<String, String> = HashMap()
    var TEST_ATTRIBUTE = "19B10001-E8F2-537E-4F6C-D104768A1214"
    var GENERIC_ATTRIBUTE = "c1f481dc-1ec6-4477-9e8b-b61cba95fed2"
    var GENERIC_ACCESS = "00001800-0000-1000-8000-00805f9b34fb"
    //var GENERIC_ATTRIBUTE = "00001801-0000-1000-8000-00805f9b34fb"
    var DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb"
    var BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb"
    var BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb"
    var BATTERY_STATUS = "00002a1b-0000-1000-8000-00805f9b34fb"
    var ACCELEROMETER_SERVICE = "00000001-627e-47e5-a3fc-ddabd97aa966"
    var X_ACCELERATION_MEASUREMENT = "00000002-627e-47e5-a3fc-ddabd97aa966"
    var Y_ACCELERATION_MEASUREMENT = "00000003-627e-47e5-a3fc-ddabd97aa966"
    var Z_ACCELERATION_MEASUREMENT = "00000004-627e-47e5-a3fc-ddabd97aa966"
    var X_GYROSCOPE_MEASUREMENT = "00000005-627e-47e5-a3fc-ddabd97aa966"
    var Y_GYROSCOPE_MEASUREMENT = "00000006-627e-47e5-a3fc-ddabd97aa966"
    var Z_GYROSCOPE_MEASUREMENT = "00000007-627e-47e5-a3fc-ddabd97aa966"
    var ACCELEROMETER_TIME_MEASUREMENT = "00000008-627e-47e5-a3fc-ddabd97aa966"
    var DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb"
    var APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb"
    var PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = "00002a04-0000-1000-8000-00805f9b34fb"
    var MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb"
    var SERIAL_NUMBER_STRING = "00002a25-0000-1000-8000-00805f9b34fb"
    var FIRMWARE_REVISION_STRING = "00002a26-0000-1000-8000-00805f9b34fb"
    var HARDWARE_REVISION_STRING = "00002a27-0000-1000-8000-00805f9b34fb"
    var SOFTWARE_REVISION_STRING = "00002a28-0000-1000-8000-00805f9b34fb"
    var MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb"
    var SERVICE_CHANGED = "00002a05-0000-1000-8000-00805f9b34fb"
    var CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    const val X_ACCELERATION_READ = 1
    const val Y_ACCELERATION_READ = 2
    const val Z_ACCELERATION_READ = 3
    const val X_GYROSCOPE_READ = 4
    const val Y_GYROSCOPE_READ = 5
    const val Z_GYROSCOPE_READ = 6
    const val ACCELEROMETER_TIME_READ = 7
    const val BATTERY_LEVEL_READ = 9
    const val TEST = 10
    const val GENERIC = 11
    fun lookup(uuid: String?, defaultName: String): String {
        val name = attributes[uuid]
        return name ?: defaultName
    }

    init {
        // Services
        attributes[ACCELEROMETER_SERVICE] = "Accelerometer Service"
        attributes[BATTERY_SERVICE] = "Battery Service"
        attributes[GENERIC_ACCESS] = "Generic Access"
        attributes[DEVICE_INFORMATION_SERVICE] = "Device Information Service"
        attributes[GENERIC_ATTRIBUTE] = "Generic Attribute"

        // Characteristics
        attributes[X_ACCELERATION_MEASUREMENT] = "X Accelerometer Type"
        attributes[Y_ACCELERATION_MEASUREMENT] = "Y Accelerometer Measurement"
        attributes[Z_ACCELERATION_MEASUREMENT] = "Z Accelerometer Measurement"
        attributes[X_GYROSCOPE_MEASUREMENT] = "X Gyroscope Impedance Measurement"
        attributes[Y_GYROSCOPE_MEASUREMENT] = "Y Gyroscope Measurement"
        attributes[Z_GYROSCOPE_MEASUREMENT] = "Z Gyroscope Measurement"
        attributes[ACCELEROMETER_TIME_MEASUREMENT] = "Accelerometer Time Measurement"
        attributes[BATTERY_LEVEL] = "Battery Level"
        attributes[BATTERY_STATUS] = "Battery Status"
        attributes[DEVICE_NAME] = "Device Name"
        attributes[APPEARANCE] = "Appearance"
        attributes[PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS] =
            "Peripheral Preferred Connection Parameters"
        attributes[MANUFACTURER_NAME_STRING] = "Manufacturer Name String"
        attributes[MODEL_NUMBER_STRING] = "Model Number String"
        attributes[SERIAL_NUMBER_STRING] = "Serial Number String"
        attributes[HARDWARE_REVISION_STRING] = "Hardware Revision String"
        attributes[FIRMWARE_REVISION_STRING] = "Firmware Revision String"
        attributes[SOFTWARE_REVISION_STRING] = "Software Revision String"
        attributes[SERVICE_CHANGED] = "Service Changed"
        attributes[TEST_ATTRIBUTE] = "Test"
    }
}
