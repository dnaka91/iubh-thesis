package com.github.dnaka91.drinkup.schedule

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dnaka91.drinkup.R
import com.github.dnaka91.drinkup.databinding.ScheduleFragmentBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ScheduleFragment : Fragment() {
    private var _binding: ScheduleFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ScheduleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScheduleFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.start.setOnClickListener {
            editTime("start_time_picker", viewModel.start()) { time ->
                binding.start.text = time.format(TIME_FORMAT)
                viewModel.onStartChanged(time)
            }
        }

        binding.end.setOnClickListener {
            editTime("end_time_picker", viewModel.end()) { time ->
                binding.end.text = time.format(TIME_FORMAT)
                viewModel.onEndChanged(time)
            }
        }

        binding.goal.doAfterTextChanged {
            viewModel.onGoalChanged(it?.toString()?.toInt() ?: 0)
        }

        lifecycleScope.launch {
            viewModel.schedule().let {
                binding.start.text = it.start.format(TIME_FORMAT)
                binding.end.text = it.end.format(TIME_FORMAT)
                binding.goal.setText(it.goal.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_save -> {
            lifecycleScope.launch {
                if (viewModel.save()) {
                    withContext(Dispatchers.Main) {
                        findNavController().popBackStack()
                    }
                }
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun editTime(tag: String, time: LocalTime, f: (LocalTime) -> Unit) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(time.hour)
            .setMinute(time.minute)
            .build()
        picker.show(parentFragmentManager, tag)
        picker.addOnPositiveButtonClickListener {
            f(LocalTime.of(picker.hour, picker.minute))
        }
    }

    companion object {
        private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
    }
}
