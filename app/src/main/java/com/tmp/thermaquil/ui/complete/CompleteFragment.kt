package com.tmp.thermaquil.ui.complete

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tmp.thermaquil.R
import com.tmp.thermaquil.common.adapter.StudyAdapter
import com.tmp.thermaquil.data.models.Data
import com.tmp.thermaquil.data.models.SubmissionData
import com.tmp.thermaquil.databinding.CompleteFragmentBinding
import com.tmp.thermaquil.databinding.FragmentHomeBinding

class CompleteFragment : Fragment() {

    private lateinit var adapter: StudyAdapter
    private lateinit var dataBinding: CompleteFragmentBinding

    companion object {
        fun newInstance() = CompleteFragment()
    }

    private lateinit var viewModel: CompleteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = CompleteFragmentBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StudyAdapter(Data.fakeTreaments2) {
            //todo submit data
        }

        Data.fakeTreaments.add(SubmissionData(1, 21.2.toLong(), "December 25, 2022", 1, arrayListOf()))

        dataBinding.list.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = this@CompleteFragment.adapter
        }

        dataBinding.btnFinish.setOnClickListener {
            findNavController().navigate(R.id.action_CompleteFragment_to_homeFragment)
        }
    }

}