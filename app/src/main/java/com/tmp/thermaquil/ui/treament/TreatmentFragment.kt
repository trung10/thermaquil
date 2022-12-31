package com.tmp.thermaquil.ui.treament

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.activities.MainActivity
import com.tmp.thermaquil.activities.MainViewModel
import com.tmp.thermaquil.base.dialogs.WarringDialog
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.common.EventObserver
import com.tmp.thermaquil.data.models.COMMAND
import com.tmp.thermaquil.data.models.Data
import com.tmp.thermaquil.data.models.SEND_STATE
import com.tmp.thermaquil.databinding.TreatmentFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TreatmentFragment : BaseFragment() {

    private val viewModel: TreatmentViewModel by viewModels()
    private lateinit var dataBinding: TreatmentFragmentBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val tempStep = 0.5F


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = TreatmentFragmentBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading(true)

        dataBinding.status?.text = getString(R.string.pre_condition)

        with(dataBinding) {
            btnPause?.setOnClickListener {
                (requireActivity() as MainActivity).sendCommand(COMMAND.cmPause)
                findNavController().navigate(R.id.action_treatmentFragment_to_pauseTreatmentFragment)
                viewModel.pause()
                //ConfirmDialog(requireContext(), null, "222", "2222", "!", "3").show()
            }

            btnSwitch?.setOnClickListener {
                WarringDialog(
                    requireContext(),
                    "You are trying to skip the" +
                            "ongoing treatment cycle",
                    "This may affect the treatment effect",
                    R.string.ok,
                    R.string.cancel
                ) {
                    if (it) {
                        (requireActivity() as MainActivity).sendCommand(COMMAND.cmSwitch)
                        viewModel.skip()
                    }

                }.show()
            }

            back?.setOnClickListener {
                (requireActivity() as MainActivity).sendCommand(COMMAND.cmEnd)
                findNavController().navigateUp()
            }

            settings?.setOnClickListener {
                findNavController().navigate(R.id.action_treatmentFragment_to_SettingsFragment)
            }

            btnDegreeDown?.setOnClickListener {
                if (viewModel.temp.value!! <= Data.Warning_Cold) {
                    WarringDialog(
                        requireContext(),
                        "Warning - Low temperatures",
                        "Low temperatures may increase the" +
                                "risk of skin injury",
                        R.string.apply,
                        R.string.cancel
                    ) {
                        if (it) changeDegree(-tempStep)
                    }.show()
                } else {
                    changeDegree(-tempStep)
                }
            }

            btnDegreeUp?.setOnClickListener {
                if (viewModel.temp.value!! >= Data.Warning_High) {
                    WarringDialog(
                        requireContext(),
                        "Warning - High temperatures",
                        "High temperatures may increase the" +
                                "risk of skin injury",
                        R.string.apply,
                        R.string.cancel
                    ) {
                        if (it) changeDegree(tempStep)
                    }.show()
                } else {
                    changeDegree(tempStep)
                }
            }

            btnTimeDown?.setOnClickListener {
                /*WarringDialog(requireContext(),
                    "You are trying to decrease" +
                            "the treatment cycle time.",
                    "This may affect the treatment effect",
                    R.string.ok,
                    R.string.cancel){
                    if (it) changeTime(-5)
                }.show()*/
                changeTime(-5)
            }

            btnTimeUp?.setOnClickListener {
                changeTime(5)
            }
        }

        obserer()

        Handler().postDelayed({
            (requireActivity() as MainActivity).sendCommand(COMMAND.cm71)
            mainViewModel.setReadyState(true)
        }, 3000)
    }

    private fun obserer() {
        viewModel.time.observe(viewLifecycleOwner) {
            dataBinding.txtTime?.text = it
        }

        viewModel.temp.observe(viewLifecycleOwner) {
            dataBinding.txtDegree?.text = "${it}°F"
        }

        mainViewModel.sendState.observe(viewLifecycleOwner) {
            when (it) {
                SEND_STATE.SUCCESS -> {
                    viewModel.sendSuccess()
                }
                SEND_STATE.FAIL -> {
                    viewModel.sendFail()
                }
            }
        }

        viewModel.dataTempSend.observe(viewLifecycleOwner) {
            Log.d("Trung", "ssss $it")
            (requireActivity() as MainActivity).sendCommand(COMMAND.cmSetTemp, it)
        }

        viewModel.dataTimeSend.observe(viewLifecycleOwner) {
            (requireActivity() as MainActivity).sendCommand(COMMAND.cmDuration, it)
        }

        viewModel.endTreatment.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_treatmentFragment_to_summaryFragment)
        }

        mainViewModel.resume.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.resume()
                mainViewModel.setResume(false)
            }
        }

        mainViewModel.ready.observe(viewLifecycleOwner) { message ->
            if (message) {
                Log.d("Trung", "ready: $message")
                (requireActivity() as MainActivity).sendCommand(COMMAND.cmStart)
                showLoading(false)
                viewModel.startTreatment()
                dataBinding.status?.text = getString(R.string.auto_treatment)
            }
        }
    }

    private fun changeTime(i: Int) {
        viewModel.changeTime(i)
    }

    private fun changeDegree(i: Float) {
        viewModel.changeDegree(i)
    }
}