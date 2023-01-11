package com.tmp.thermaquil.ui.pauseTreatment

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.activities.MainActivity
import com.tmp.thermaquil.activities.MainViewModel
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.data.models.COMMAND
import com.tmp.thermaquil.databinding.PauseTreatmentFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PauseTreatmentFragment : DialogFragment(R.layout.pause_treatment_fragment) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var dataBinding: PauseTreatmentFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = PauseTreatmentFragmentBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(null)
        dialog?.setCanceledOnTouchOutside(false)

        initListener()
    }

    private fun initListener() {
        with(dataBinding){
            btnStop.setOnClickListener {
                (requireActivity() as MainActivity).sendCommand(COMMAND.cmEnd)
                dismiss()
                findNavController().navigate(R.id.action_pauseTreatmentFragment_to_summaryFragment)
            }

            btnResume?.setOnClickListener {
                (requireActivity() as MainActivity).sendCommand(COMMAND.cmResume)
                viewModel.setResume(true)
                findNavController().navigateUp()
            }
        }
    }
}