package com.tmp.thermaquil.ui.treament

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.base.dialogs.ConfirmDialog
import com.tmp.thermaquil.base.dialogs.WarringDialog
import com.tmp.thermaquil.databinding.PauseTreatmentFragmentBinding
import com.tmp.thermaquil.databinding.TreatmentFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class TreatmentFragment : Fragment() {

    private val viewModel: TreatmentViewModel by viewModels()
    private lateinit var dataBinding: TreatmentFragmentBinding
    private var time = 10 * 60
    private var degree = 100

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

        with(dataBinding) {
            btnPause?.setOnClickListener {
                findNavController().navigate(R.id.action_treatmentFragment_to_pauseTreatmentFragment)
                //ConfirmDialog(requireContext(), null, "222", "2222", "!", "3").show()
            }

            btnSwitch?.setOnClickListener {
                WarringDialog(requireContext(),
                "You are trying to skip the" +
                        "ongoing treatment cycle",
                "This may affect the treatment effect",
                R.string.cancel,
                R.string.ok){
                }.show()
            }

            back?.setOnClickListener {
                findNavController().navigate(R.id.action_treatmentFragment_to_homeFragment)
            }

            settings?.setOnClickListener {
                findNavController().navigate(R.id.action_treatmentFragment_to_SettingsFragment)
            }

            btnDegreeDown?.setOnClickListener {
                if (degree <= 32) {
                    WarringDialog(requireContext(),
                    "Warning - Low temperatures",
                    "Low temperatures may increase the" +
                            "risk of skin injury",
                    R.string.apply,
                    R.string.cancel){
                        if (it) changeDegree(-1)
                    }.show()
                }
                changeDegree(-1)
            }

            btnDegreeUp?.setOnClickListener {
                if (degree >= 118) {
                    WarringDialog(requireContext(),
                        "Warning - High temperatures",
                        "High temperatures may increase the" +
                                "risk of skin injury",
                        R.string.apply,
                        R.string.cancel){
                        if (it) changeDegree(-1)
                    }.show()
                }

                changeDegree(1)
            }

            btnTimeDown?.setOnClickListener {
                WarringDialog(requireContext(),
                    "You are trying to decrease" +
                            "the treatment cycle time.",
                    "This may affect the treatment effect",
                    R.string.ok,
                    R.string.cancel){
                    if (it) changeTime(-5)
                }.show()
            }

            btnTimeUp?.setOnClickListener {
                changeTime(5)
            }
        }
    }

    private fun changeTime(i: Int) {
        if (time - i < 0)
            return
        time += i

        val m: Int = time/60
        val s = time - m * 60

        dataBinding.txtTime?.text = String.format(Locale.US, "%02d:%02d", m, s)
    }

    private fun changeDegree(i: Int){
        degree += i
        dataBinding.txtDegree?.text = "${degree}°F"
    }
}