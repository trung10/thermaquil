package com.tmp.thermaquil.ui.pauseTreatment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tmp.thermaquil.databinding.PauseTreatmentFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PauseTreatmentFragment : Fragment() {

    private lateinit var viewModel: PauseTreatmentViewModel
    private lateinit var dataBinding: PauseTreatmentFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = PauseTreatmentFragmentBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PauseTreatmentViewModel::class.java)

        initListener()
    }

    private fun initListener() {
        with(dataBinding){
            btnStart.setOnClickListener {

            }

            btnStop.setOnClickListener {

            }

            bntSetting.setOnClickListener {

            }

            btnSun.setOnClickListener {

            }
        }
    }
}