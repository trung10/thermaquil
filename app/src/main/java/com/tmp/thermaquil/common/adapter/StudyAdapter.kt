package com.tmp.thermaquil.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tmp.thermaquil.R
import com.tmp.thermaquil.data.models.SubmissionData
import com.tmp.thermaquil.databinding.ItemDateDataBinding

class StudyAdapter(val list: ArrayList<SubmissionData>, val callback: (item: SubmissionData) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val b =  DataBindingUtil.inflate<ItemDateDataBinding>(LayoutInflater.from(parent.context),
            R.layout.item_date_data,
            parent, false)

        return DateDataViewHolder(b)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DateDataViewHolder).bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class DateDataViewHolder(private val binding: ItemDateDataBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btn.setOnClickListener {
                callback.invoke(list[adapterPosition])
            }
        }

        fun bind(submissionData: SubmissionData) {
            binding.date.text = submissionData.name
        }
    }

}