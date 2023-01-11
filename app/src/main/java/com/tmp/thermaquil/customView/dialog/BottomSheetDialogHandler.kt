package com.tmp.thermaquil.customView.dialog

import androidx.fragment.app.FragmentManager
import java.util.*

class BottomSheetDialogHandler {
    companion object {
        private val TAG = BottomSheetDialogHandler::class.simpleName
        //private var step2Dialog: ProgressStep2Dialog? = null
        //private lateinit var progressDialog: ProgressBottomSheetDialogFragment
        //private lateinit var progressDialog1: ProgressCreateReportDialog

        fun showCalendarDialog(manager: FragmentManager, currentDate: Calendar, onDone: (Calendar) -> Unit) {
            //CalendarBottomSheet(currentDate, onDone).show(manager, CalendarBottomSheet.TAG)
        }

        fun showDatePickerDialog(manager: FragmentManager, currentDate: Date, onDone: (Calendar) -> Unit) {
            //DatePickerBottomSheet(currentDate, onDone).show(manager, DatePickerBottomSheet.TAG)
        }

        fun showListDialog(manager: FragmentManager, title: String, array: ArrayList<String>, default: String, callback: ((String) -> Unit)? = null) {
            SubListBottomSheetDialogFragment(title, array, default, callback).apply { show(manager, SubListBottomSheetDialogFragment.TAG) }
        }

    }
}