package com.tmp.thermaquil.ui.passcode

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.activities.MainActivity
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.common.toast
import com.tmp.thermaquil.data.models.COMMAND
import com.tmp.thermaquil.data.models.Data
import com.tmp.thermaquil.databinding.PasscodeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PasscodeFragment : BaseFragment(R.layout.passcode_fragment) {

    companion object {
        fun newInstance() = PasscodeFragment()
    }

    private val viewModel: PasscodeViewModel by viewModels()
    private lateinit var dataBinding: PasscodeFragmentBinding
    private var handler: Handler? = null

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

        handler = Handler()

        with(dataBinding) {
            back.setOnClickListener {
                findNavController().navigateUp()
            }

            settings.setOnClickListener {
                findNavController().navigate(R.id.action_passcodeFragment_to_SettingsFragment)
            }

            btnNext.setOnClickListener {
                if (otpView.otp.isNotEmpty() && otpView.otp == "1111"){
                    (requireActivity() as MainActivity).sendCommand(COMMAND.cmPrepare, Data.defaultTreatment)
                    findNavController().navigate(R.id.action_passcodeFragment_to_treatmentInfoFragment)

                } else {
                    if (otpView.otp.isEmpty()) {
                        toast("Please enter your passcode")
                    } else {
                        toast("Invalid passcode")
                    }
                }
            }
        }
    }

}