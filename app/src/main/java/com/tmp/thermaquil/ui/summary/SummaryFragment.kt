package com.tmp.thermaquil.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.common.adapter.CustomArrayAdapter
import com.tmp.thermaquil.databinding.SummaryFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryFragment : BaseFragment(R.layout.summary_fragment) {

    private val viewModel: SummaryViewModel by viewModels()
    private lateinit var dataBinding: SummaryFragmentBinding
    private val listSeverity = arrayListOf("None", "Mild", "Moderate", "Severe")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = SummaryFragmentBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(dataBinding) {
            spinnerSelectPain.adapter = CustomArrayAdapter(
                requireContext(),
                R.layout.item_spinner, values =
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            )

            spinnerSelectPain.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

            spinnerLightSensitivit.adapter = CustomArrayAdapter(
                requireContext(),
                R.layout.item_spinner, values = listSeverity
            )

            spinnerLightSensitivit.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }

            spinnerSoundSensitivity.adapter = CustomArrayAdapter(
                requireContext(),
                R.layout.item_spinner, values = listSeverity
            )

            spinnerSoundSensitivity.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }

            spinnerNauseavomiting.adapter = CustomArrayAdapter(
                requireContext(),
                R.layout.item_spinner, values = listSeverity
            )

            spinnerNauseavomiting.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }

                }

            btnNext.setOnClickListener {
                // data created
                findNavController().navigate(R.id.action_summaryFragment_to_CompleteFragment)
            }

            settings.setOnClickListener {
                findNavController().navigate(R.id.action_summaryFragment_to_SettingsFragment)
            }
        }
    }
}