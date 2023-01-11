package com.tmp.thermaquil.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.settings_fragment) {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bnSkin.setOnClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_SkinAndHairFragment)
        }

        binding.bnFill.setOnClickListener {
        }

        binding.settings.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}