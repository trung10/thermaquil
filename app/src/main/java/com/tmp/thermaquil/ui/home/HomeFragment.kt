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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmp.thermaquil.R
import com.tmp.thermaquil.activities.MainActivity
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.common.adapter.StudyAdapter
import com.tmp.thermaquil.customView.dialog.BottomSheetDialogHandler
import com.tmp.thermaquil.data.models.*
import com.tmp.thermaquil.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {
    val TAG = HomeFragment::class.java.simpleName
    private lateinit var dataBinding: FragmentHomeBinding

    private var deviceName: String? = null
    private var deviceAddress: String? = null
    private lateinit var sharedPrefBLE: SharedPreferences
    private lateinit var adapter: StudyAdapter

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "HomeFragment")
        dataBinding = FragmentHomeBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).connectDevice()

        adapter = StudyAdapter(
            Data.fakeTreaments
        ) {
            Log.d(TAG, "Click: ${it.name}")
            showLoading(true)
            when (it.name) {
                "prepare" -> {
                    (requireActivity() as MainActivity).sendCommand(COMMAND.cmPrepare)
                }
                "Get logs file" -> {
                    (requireActivity() as MainActivity).sendCommand(COMMAND.cmGetLog)
                }
                "Start" -> {
                    (requireActivity() as MainActivity).sendCommand(COMMAND.cmStart)
                }
                "end" -> {
                    (requireActivity() as MainActivity).sendCommand(COMMAND.cmEnd)
                }
                "resume" -> {
                    (requireActivity() as MainActivity).sendCommand(COMMAND.cmResume)
                }
                "pause" -> {
                    (requireActivity() as MainActivity).sendCommand(COMMAND.cmPause)
                }
                /*"set duration" -> {
                    (requireActivity() as MainActivity).setDuration(50 * 60)
                }
                "set hot" -> {
                    (requireActivity() as MainActivity).setHot(150f)
                }
                "set cold" -> {
                    (requireActivity() as MainActivity).setCold(15f)
                }
                "power on" -> {
                    (requireActivity() as MainActivity).powerOn()
                }
                "power off" -> {
                    (requireActivity() as MainActivity).powerOff()
                }
                "Cold/Hot Cycle Switch" -> {
                    (requireActivity() as MainActivity).switch()
                }
                "Set real time" -> {
                    (requireActivity() as MainActivity).setRealTime(100f)
                }
                "71" -> {
                    (requireActivity() as MainActivity).set71()
                }
                "72" -> {
                    (requireActivity() as MainActivity).set72()
                }
                "73" -> {
                    (requireActivity() as MainActivity).set73()
                }*/
                else ->{
                    //viewModel.sendData(it)
                }
            }
        }

        dataBinding.list?.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = this@HomeFragment.adapter
        }

        with(dataBinding) {
            settings.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_SettingsFragment)
            }

            btnStart.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_passcodeFragment)
                (requireActivity() as MainActivity).sendCommand(COMMAND.cmPower, true)
            }
        }

        if ((requireActivity() as MainActivity).deviceAddress == null) {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
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
            requireActivity().getSharedPreferences(
                getString(R.string.ble_device_key),
                Context.MODE_PRIVATE
            )
        deviceName = sharedPrefBLE.getString("name", null)
        deviceAddress = sharedPrefBLE.getString("address", null)
    }

}