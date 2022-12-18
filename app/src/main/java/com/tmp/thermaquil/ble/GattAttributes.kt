package com.tmp.thermaquil.ble;

/*
 * This is where you keep all your UUIDs and some other items to differentiate BLE services
 * and characteristics.
 *
 * */
object GattAttributes {
    private val attributes: HashMap<String, String> = HashMap()

    //Service
    val PCM_SERVICE = "25b28cef-a1a7-4eca-b3bf-8b91e3925bd8"
    //characteristics
    val PCM_COMMANDS_ATTRIBUTE = "c1f481dc-1ec6-4477-9e8b-b61cba95fed2"
    val PCM_EVENT_ATTRIBUTE = "D76F7FD4-8501-4142-AB81-D464ED0B2435"
    val PCM_SW_ATTRIBUTE = "9D59C9D8-0E7A-4120-95F1-7A47A7D3CD65"
    val PCM_BLOCK_ATTRIBUTE = "f84b529a-8f13-4b84-8420-9e0436c7ac56"
    val PCM_STATUS_ATTRIBUTE = "b57e5d26-6251-4c1e-b565-3b565da1f29b"

    //Decriptor

    // simple data
    var BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb"
    var BATTERY_STATUS = "00002a1b-0000-1000-8000-00805f9b34fb"
    var CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"

    const val PCM_COMMANDS_READ = 1
    const val PCM_EVENT_READ = 2
    const val PCM_SW_READ = 3
    const val PCM_BLOCK_READ = 4
    const val PCM_STATUS_READ = 5

    fun lookup(uuid: String?, defaultName: String): String {
        val name = attributes[uuid]
        return name ?: defaultName
    }

    init {
        // Services
        attributes[PCM_SERVICE] = "PCM Service"

        // Characteristics
        attributes[PCM_COMMANDS_ATTRIBUTE] = "PCM command"
        attributes[PCM_EVENT_ATTRIBUTE] = "PCM Event"
        attributes[PCM_SW_ATTRIBUTE] = "PCM SW"
        attributes[PCM_BLOCK_ATTRIBUTE] = "PCM Bloc"
        attributes[PCM_STATUS_ATTRIBUTE] = "PCM Status"
    }
}
