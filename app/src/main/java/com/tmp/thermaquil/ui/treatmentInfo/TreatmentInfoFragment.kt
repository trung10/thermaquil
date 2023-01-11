package com.tmp.thermaquil.ui.treatmentInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.databinding.FragmentDeviceInfoBinding
import com.tmp.thermaquil.databinding.FragmentTreamentInfoBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TreatmentInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TreatmentInfoFragment : BaseFragment(R.layout.fragment_treament_info) {

    private lateinit var dataBinding: FragmentTreamentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = FragmentTreamentInfoBinding.inflate(inflater)
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
                findNavController().navigate(R.id.action_treatmentInfoFragment_to_SettingsFragment)
            }

            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_treatmentInfoFragment_to_currentPainFragment)
            }
        }
    }
}