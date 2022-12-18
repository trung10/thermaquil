package com.tmp.thermaquil.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmp.thermaquil.R
import com.tmp.thermaquil.activities.MainActivity
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.common.adapter.StudyAdapter
import com.tmp.thermaquil.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    val TAG = HomeFragment::class.java.simpleName
    private lateinit var dataBinding: FragmentHomeBinding

    private var deviceName: String? = null
    private var deviceAddress: String? = null
    private lateinit var sharedPrefBLE: SharedPreferences
    private lateinit var adapter: StudyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentHomeBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StudyAdapter(arrayListOf()) {

        }

        dataBinding.list?.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = this@HomeFragment.adapter
        }

        with(dataBinding) {
            settings.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_SettingsFragment)
            }

            btnStart.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_passcodeFragment)
                //(requireActivity() as MainActivity).prepare()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // get address
        /*getDeviceAddress()

        if (deviceAddress == null){
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }*/
    }

    private fun getDeviceAddress() {
        sharedPrefBLE =
            requireActivity().getSharedPreferences(getString(R.string.ble_device_key), Context.MODE_PRIVATE)
        deviceName = sharedPrefBLE.getString("name", null)
        deviceAddress = sharedPrefBLE.getString("address", null)
    }

}