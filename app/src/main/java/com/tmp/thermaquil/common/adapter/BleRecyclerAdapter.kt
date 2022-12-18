package com.tmp.thermaquil.common.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tmp.thermaquil.R

class BleRecyclerAdapter internal constructor(context: Context?) :
    RecyclerView.Adapter<BleRecyclerAdapter.BleViewHolder>() {
    private val bleDevices: ArrayList<BluetoothDevice>
    private val inflater: LayoutInflater
    private var clickListener: ItemClickListener? = null

    // inflates a row containing a device's info as needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        val view: View = inflater.inflate(R.layout.listitem_device, parent, false)
        return BleViewHolder(view)
    }

    // binds the data to the TextViews in each row
    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        var name: String? = ""
        name = if (bleDevices[position].name == null) {
            "Unknown Device"
        } else {
            bleDevices[position].name
        }
        val address = bleDevices[position].address
        holder.deviceAddressView.text = address
        holder.deviceNameView.text = name
    }

    // total number of rows
    override fun getItemCount(): Int {
        return bleDevices.size
    }

    inner class BleViewHolder(deviceView: View) : RecyclerView.ViewHolder(deviceView),
        View.OnClickListener {
        var deviceAddressView: TextView
        var deviceNameView: TextView
        override fun onClick(view: View) {
            if (clickListener != null) clickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            deviceAddressView = deviceView.findViewById(R.id.device_address)
            deviceNameView = deviceView.findViewById(R.id.device_name)
            deviceView.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getDevice(id: Int): BluetoothDevice {
        return bleDevices[id]
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        clickListener = itemClickListener
    }

    // add a new device to the list
    fun addDevice(device: BluetoothDevice) {
        if (!bleDevices.contains(device)) {
            bleDevices.add(device)
        }
    }

    // clear the list
    fun clear() {
        bleDevices.clear()
    }

    // data is passed into the constructor
    init {
        bleDevices = ArrayList()
        inflater = LayoutInflater.from(context)
    }
}