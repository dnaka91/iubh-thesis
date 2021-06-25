package com.github.dnaka91.drinkup.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.dnaka91.drinkup.R
import com.github.dnaka91.drinkup.core.Record
import com.github.dnaka91.drinkup.databinding.HistoryItemBinding
import java.time.format.DateTimeFormatter

class RecordAdapter(private val context: Context) :
    ListAdapter<Record, RecordAdapter.RecordViewHolder>(DiffCallback) {
    private val inflater = LayoutInflater.from(context)

    class RecordViewHolder(val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = HistoryItemBinding.inflate(inflater, parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        if (position < itemCount) {
            val record = getItem(position)
            holder.binding.apply {
                name.text = context.getString(
                    R.string.history_entry_name, record.name, record.amount,
                )
                timestamp.text = record.timestamp.format(TIMESTAMP_FORMAT)
            }
        } else {
            holder.binding.apply {
                name.text = ""
                timestamp.text = ""
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean =
            oldItem == newItem
    }

    companion object {
        private val TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss")
    }
}
