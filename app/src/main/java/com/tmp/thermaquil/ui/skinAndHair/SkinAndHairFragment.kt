package com.tmp.thermaquil.ui.skinAndHair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tmp.thermaquil.R
import com.tmp.thermaquil.base.dialogs.WarringDialog
import com.tmp.thermaquil.base.fragment.BaseFragment
import com.tmp.thermaquil.databinding.SkinAndHairFragmentBinding

class SkinAndHairFragment : BaseFragment(R.layout.skin_and_hair_fragment) {

    private val viewModel: SkinAndHairViewModel by viewModels()
    private lateinit var dataBinding: SkinAndHairFragmentBinding

    private val skinList = arrayListOf("Normal", "Sensitive", "Highly Sensitive")
    private val hairList = arrayListOf("No Hair", "Thin", "Medium", "Dense")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = SkinAndHairFragmentBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(dataBinding) {
            btnCold.setOnClickListener {

                when (coldValue.text.toString()) {
                    skinList[0] -> coldValue.setText(skinList[1])
                    skinList[1] -> coldValue.setText(skinList[2])
                    skinList[2] -> coldValue.setText(skinList[0])
                }
            }

            btnHeat.setOnClickListener {
                when (heatValue.text.toString()) {
                    skinList[0] -> heatValue.setText(skinList[1])
                    skinList[1] -> heatValue.setText(skinList[2])
                    skinList[2] -> heatValue.setText(skinList[0])
                }
            }

            btnHair.setOnClickListener {
                when (hairValue.text.toString()) {
                    hairList[0] -> hairValue.setText(hairList[1])
                    hairList[1] -> hairValue.setText(hairList[2])
                    hairList[2] -> hairValue.setText(hairList[3])
                    hairList[3] -> hairValue.setText(hairList[0])
                }
            }

            settings.setOnClickListener {
                findNavController().navigateUp()
            }

            back.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}