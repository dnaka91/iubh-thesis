package com.github.dnaka91.drinkup.sizes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.dnaka91.drinkup.R
import com.github.dnaka91.drinkup.core.IntakeSize
import com.github.dnaka91.drinkup.databinding.IntakeSizeItemBinding

class IntakeSizeAdapter(private val fragment: Fragment) :
    ListAdapter<IntakeSize, IntakeSizeAdapter.IntakeSizeViewHolder>(DiffCallback) {
    private val inflater = LayoutInflater.from(fragment.requireContext())

    class IntakeSizeViewHolder(val binding: IntakeSizeItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeSizeViewHolder {
        val binding = IntakeSizeItemBinding.inflate(inflater, parent, false)
        return IntakeSizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntakeSizeViewHolder, position: Int) {
        if (position < itemCount) {
            val size = getItem(position)
            holder.binding.name.text = size.name
            holder.binding.amount.text = fragment.getString(R.string.drink_amount, size.amount)

            holder.binding.root.setOnClickListener {
                fragment.findNavController().navigate(
                    IntakeSizeFragmentDirections.actionEditIntakeSize(
                        size.id,
                        size.name,
                        size.amount
                    )
                )
            }
        } else {
            holder.binding.name.text = ""
            holder.binding.amount.text = ""
            holder.binding.root.setOnClickListener(null)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<IntakeSize>() {
        override fun areItemsTheSame(oldItem: IntakeSize, newItem: IntakeSize): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: IntakeSize, newItem: IntakeSize): Boolean =
            oldItem == newItem
    }
}
