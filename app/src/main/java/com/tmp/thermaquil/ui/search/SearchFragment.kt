package com.tmp.thermaquil.ui.search

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tmp.thermaquil.R
import com.tmp.thermaquil.activities.MainActivity
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.common.adapter.BleRecyclerAdapter
import com.tmp.thermaquil.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment(), BleRecyclerAdapter.ItemClickListener {
    //private lateinit var viewModel: TreatmentViewModel
    private lateinit var dataBinding: FragmentSearchBinding
    private var recyclerAdapter: BleRecyclerAdapter? = null
    private var bleAdapter: BluetoothAdapter? = null
    private var scanning = false

    private var handler: Handler? = null
    private val REQUEST_ENABLE_BT = 4

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentSearchBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        //viewModel = ViewModelProvider(this).get(TreatmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun initView() {
        handler = Handler()

        initializeLayout()

        // Initializes a Bluetooth recyclerAdapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.

        // Initializes a Bluetooth recyclerAdapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        val bluetoothManager =
            requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bleAdapter = bluetoothManager.adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        scanning = false
        bleAdapter!!.stopLeScan(mBleScanCallback)
    }

    override fun onItemClick(view: View?, position: Int) {
        onPause()
        invalidateScanMenu()

        val name: String
        name = if (recyclerAdapter!!.getDevice(position).name == null) {
            "Unknown Device"
        } else {
            recyclerAdapter!!.getDevice(position).name
        }

        val device = recyclerAdapter!!.getDevice(position) ?: return

        val sharedPref: SharedPreferences =
            requireActivity().getSharedPreferences(
                getString(R.string.ble_device_key),
                Context.MODE_PRIVATE
            )

        val prefBleDeviceEditor = sharedPref.edit()
        prefBleDeviceEditor.putString("name", device.name)
        prefBleDeviceEditor.putString("address", device.address)
        prefBleDeviceEditor.apply()

        //val intent = Intent(this, MainActivity::class.java)

        if (scanning) {
            bleAdapter!!.stopLeScan(mBleScanCallback)
            scanning = false
        }

        (requireActivity() as MainActivity).connectDevice()
        findNavController().navigateUp()
        //todo
        //startActivity(intent)

    }

    override fun onResume() {
        super.onResume()
        //isBleOn()
        // Clear list view recyclerAdapter.
        recyclerAdapter!!.clear()
        scanBleDevice(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            findNavController().navigateUp()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        scanBleDevice(false)
    }

    private val mBleScanCallback =
        LeScanCallback { device, rssi, scanRecord ->
            requireActivity().runOnUiThread(Runnable {
                recyclerAdapter!!.addDevice(device)
                recyclerAdapter!!.notifyDataSetChanged()
            })
        }

    private fun scanBleDevice(enable: Boolean) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler!!.postDelayed({
                scanning = false
                bleAdapter!!.stopLeScan(mBleScanCallback)
                invalidateScanMenu()
            }, SCAN_PERIOD)
            scanning = true
            bleAdapter!!.startLeScan(mBleScanCallback)
        } else {
            scanning = false
            bleAdapter!!.stopLeScan(mBleScanCallback)
            invalidateScanMenu()
        }
    }

    // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
    // fire an intent to display a dialog asking the user to grant permission to enable it.
    private fun isBleOn() {
        if (!bleAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requireActivity().startActivityForResult(
                enableBtIntent,
                REQUEST_ENABLE_BT
            )
        }
    }

    private fun invalidateScanMenu() {
        /*scanProgressBar!!.visibility = View.GONE
        stopView!!.visibility = View.GONE
        scanView!!.visibility = View.VISIBLE*/
    }

    @SuppressLint("WrongConstant")
    fun initializeLayout() {
        // Initialize the action bar

        val recyclerView: RecyclerView = dataBinding.bleRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerAdapter = BleRecyclerAdapter(context)
        recyclerAdapter!!.setClickListener(this)
        recyclerView.adapter = recyclerAdapter
    }

}