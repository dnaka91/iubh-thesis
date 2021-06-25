package com.github.dnaka91.drinkup

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.github.dnaka91.drinkup.alarm.DrinkAlarmActivity
import com.github.dnaka91.drinkup.core.IntakeSize
import com.github.dnaka91.drinkup.databinding.MainFragmentBinding
import com.github.dnaka91.drinkup.databinding.StringListItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = StringListAdapter(requireContext(), this)
        val layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            )
        )

        lifecycleScope.launch {
            updateControls()
            adapter.submitList(viewModel.intakeSizes())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_schedule -> {
            findNavController().navigate(MainFragmentDirections.actionEditSchedule())
            true
        }
        R.id.action_intake_sizes -> {
            findNavController().navigate(MainFragmentDirections.actionShowIntakeSizes())
            true
        }
        R.id.action_alarm_test -> {
            startActivity(DrinkAlarmActivity.newInstance(requireContext()))
            true
        }
        R.id.action_history -> {
            findNavController().navigate(MainFragmentDirections.actionShowHistory())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun confirmIntakeResponse(requestKey: String, bundle: Bundle) {
        if (requestKey == ConfirmIntakeFragment.REQUEST_CODE_CONFIRM_INTAKE && bundle.getBoolean("ok")) {
            val name = bundle.getString("name") ?: ""
            val amount = bundle.getInt("amount", 0)

            if (name.isNotEmpty() && amount > 0) {
                lifecycleScope.launch {
                    viewModel.addRecord(name, amount)
                    viewModel.scheduleAlarm(requireContext())
                    updateControls()
                }
            }
        }
    }

    private suspend fun updateControls() {
        binding.goal.max = viewModel.schedule().goal
        binding.goal.progress = viewModel.progress()
        binding.textView.text = viewModel.progressText(requireContext())
    }

    class StringListAdapter(context: Context, private val fragment: MainFragment) :
        ListAdapter<IntakeSize, StringListAdapter.StringViewHolder>(DiffCallback) {
        private val inflater = LayoutInflater.from(context)

        class StringViewHolder(val binding: StringListItemBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringViewHolder {
            val binding = StringListItemBinding.inflate(inflater, parent, false)
            return StringViewHolder(binding)
        }

        override fun onBindViewHolder(holder: StringViewHolder, position: Int) {
            if (position < itemCount) {
                val intakeSize = getItem(position)
                holder.binding.value.text = intakeSize.name
                holder.binding.root.setOnClickListener {
                    ConfirmIntakeFragment.show(
                        fragment,
                        intakeSize.name,
                        intakeSize.amount,
                        fragment::confirmIntakeResponse,
                    )
                }
            } else {
                holder.binding.value.text = ""
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
}
