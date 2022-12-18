package com.tmp.thermaquil.ui.deviceInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.databinding.FragmentDeviceInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceInfoFragment : BaseFragment() {
    private lateinit var dataBinding: FragmentDeviceInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = FragmentDeviceInfoBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(dataBinding) {
            back.setOnClickListener {
                findNavController().navigateUp()
            }

            settings.setOnClickListener {
                findNavController().navigate(R.id.action_deviceInfoFragment_to_SettingsFragment)
            }

            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_deviceInfoFragment_to_treatmentFragment)
            }
        }
    }
}