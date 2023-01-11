package com.tmp.thermaquil.customView.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tmp.thermaquil.R
import com.tmp.thermaquil.databinding.LayoutSubListModalBottomSheetBinding


class SubListBottomSheetDialogFragment(
    private val title: String,
    private var list: ArrayList<String>,
    private val default: String = "",
    private var updateListener: ((String) -> Unit)? = null
) : BottomSheetDialogFragment() {

    companion object {
        val TAG = SubListBottomSheetDialogFragment::class.simpleName
    }

    private lateinit var binding: LayoutSubListModalBottomSheetBinding
    private lateinit var optionAdapter: OptionAdapter
    //private val viewModel: SubListBottomSheetViewModel by viewModels()

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Log.d("kane", "onAttach: vm = $viewModel ")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_sub_list_modal_bottom_sheet,
            container,
            false
        )

        return binding.root
    }

    private fun initObservers() {

        /*viewModel.selectedValue.observe(viewLifecycleOwner) { value ->
            if (value != -1) optionAdapter.notifyItemChanged(value)
        }*/

        /*viewModel.listItem.observe(viewLifecycleOwner) { options ->
            optionAdapter = OptionAdapter(options) { onSelectOption(it) }
            recyclerViewOptions.adapter = optionAdapter
        }*/

        /*viewModel.param.observe(viewLifecycleOwner) { viewModel.setDetail() }

        viewModel.message.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                getString(message).showToast(requireContext())
            }
        }*/
    }

    private fun onSelectOption(position: Int) {
        /*viewModel.onSelectItem(position).apply {
            optionAdapter.apply {
                notifyItemChanged(first)
                notifyItemChanged(second)
            }
        }*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)

        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    view.viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet =
                    dialog!!.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight =
                    0 // Remove this line to hide a dark background if you manually hide the dialog.
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        })

        initView()
        initListener()
        initObservers()
    }

    private fun initView() {

        binding.radioGroupOptions.forEachIndexed { index, view ->
            (view as AppCompatRadioButton).text = list[index]
        }

        binding.radioGroupOptions.forEach {
            if((it as AppCompatRadioButton).text.toString() == default){
                binding.radioGroupOptions.clearCheck()
                it.isChecked = true
            }
        }

        binding.run {
            lifecycleOwner = this@SubListBottomSheetDialogFragment
        }

        binding.txtFunctionName.text = title
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
            }
        }
        return bottomSheetDialog

    }

    private fun initListener() {
        binding.btnCancel.setOnClickListener {
            this@SubListBottomSheetDialogFragment.dismiss()
        }
        binding.btnOK.setOnClickListener {
            val r = binding.radioGroupOptions.findViewById<AppCompatRadioButton>(binding.radioGroupOptions.checkedRadioButtonId)
            updateListener?.invoke(r.text.toString())
            this@SubListBottomSheetDialogFragment.dismiss()
        }
    }
}