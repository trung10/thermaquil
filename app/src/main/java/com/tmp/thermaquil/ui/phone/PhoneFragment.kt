package com.tmp.thermaquil.ui.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.common.toast
import com.tmp.thermaquil.databinding.PhoneFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneFragment : Fragment() {

    private val viewModel: PhoneViewModel by viewModels()
    private lateinit var dataBinding: PhoneFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = PhoneFragmentBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(dataBinding){
            btnNext.setOnClickListener {

                when (viewModel.getState()){
                    1 -> {
                        val p = edtPhone.text.toString()
                        if (p.isNullOrEmpty()) {
                            // error
                            toast("Please enter your phone number")
                        } else {
                            edtPhone.visibility = View.GONE
                            group.isGone = false
                            viewModel.updatePhone(p)
                        }
                    }

                    2 -> {
                        val otp = otpView.otp
                        if (otp.isNullOrEmpty()) {
                            // error
                            toast("OTP Invalidate")
                        } else {
                            viewModel.updatePhone(otp)
                            findNavController().navigate(R.id.action_phoneFragment_to_homeFragment)
                        }
                    }
                }
            }
        }
    }

}