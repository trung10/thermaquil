package com.tmp.thermaquil.ui.passcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.common.toast
import com.tmp.thermaquil.databinding.PasscodeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasscodeFragment : Fragment() {

    companion object {
        fun newInstance() = PasscodeFragment()
    }

    private val viewModel: PasscodeViewModel by viewModels()
    private lateinit var dataBinding: PasscodeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = PasscodeFragmentBinding.inflate(inflater)
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
                findNavController().navigate(R.id.action_passcodeFragment_to_SettingsFragment)
            }

            btnNext.setOnClickListener {
                if (otpView.otp.isNotEmpty()){
                    findNavController().navigate(R.id.action_passcodeFragment_to_currentPainFragment)
                } else {
                    toast("Please enter passcode")
                }
            }
        }
    }

}