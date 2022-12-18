package com.tmp.thermaquil.base.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.tmp.thermaquil.R

class WarringDialog constructor(
    context: Context,
    private val title: String,
    private val message: String?,
    private val apply: Int,
    private val cancel: Int,
    private val callback: (isPos: Boolean) -> Unit?
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_warring)

        window?.decorView?.setBackgroundResource(android.R.color.transparent)

        setCanceledOnTouchOutside(false)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = title

        message?.let {
            val tvContent = findViewById<TextView>(R.id.tvContent)
            tvContent.visibility = View.VISIBLE
            tvContent.text = message
        }

        val btnNegative = findViewById<Button>(R.id.btnNegative)
        btnNegative.text = context.getText(cancel)
        btnNegative.setOnClickListener {
            callback.invoke(false)
            dismiss()
        }

        val btnPositive = findViewById<Button>(R.id.btnPositive)
        btnPositive.text = context.getText(apply)
        btnPositive.setOnClickListener {
            callback.invoke(true)
            dismiss()
        }

    }
}