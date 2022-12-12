package com.tmp.thermaquil.ui.treament

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tmp.thermaquil.R
import com.tmp.thermaquil.databinding.PauseTreatmentFragmentBinding
import com.tmp.thermaquil.databinding.TreatmentFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TreatmentFragment : Fragment() {

    private lateinit var viewModel: TreatmentViewModel
    private lateinit var dataBinding: TreatmentFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = TreatmentFragmentBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TreatmentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}