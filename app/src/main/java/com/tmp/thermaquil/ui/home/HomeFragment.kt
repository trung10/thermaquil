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
import com.tmp.thermaquil.data.models.SubmissionData
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
        Log.d(TAG, "HomeFragment")
        dataBinding = FragmentHomeBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).connectDevice()

        adapter = StudyAdapter(
            arrayListOf(
                SubmissionData(21.2.toLong(), "prepare", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "Start", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "end", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "resume", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "pause", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "set hot", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "set cold", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "set duration", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "power on", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "power off", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "Get logs file", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "Cold/Hot Cycle Switch", 1, arrayListOf()),
                SubmissionData(21.2.toLong(), "Set real time", 1, arrayListOf())
                )
        ) {
            Log.d(TAG, "Click: ${it.name}")
            showLoading(true)
            when (it.name) {
                "prepare" -> {
                    (requireActivity() as MainActivity).prepare()
                }
                "Get logs file" -> {
                    (requireActivity() as MainActivity).sendFile()
                }
                "Start" -> {
                    (requireActivity() as MainActivity).start()
                }
                "end" -> {
                    (requireActivity() as MainActivity).end()
                }
                "resume" -> {
                    (requireActivity() as MainActivity).resume()
                }
                "pause" -> {
                    (requireActivity() as MainActivity).pause()
                }
                "set duration" -> {
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
                //findNavController().navigate(R.id.action_homeFragment_to_passcodeFragment)
                //(requireActivity() as MainActivity).sendFile()
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