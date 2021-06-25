package com.github.dnaka91.drinkup.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.dnaka91.drinkup.MainActivity
import com.github.dnaka91.drinkup.R
import com.github.dnaka91.drinkup.databinding.DrinkAlarmFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DrinkAlarmFragment : Fragment() {
    private var _binding: DrinkAlarmFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DrinkAlarmViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DrinkAlarmFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ok.setOnClickListener {
            requireActivity().finish()
            startActivity(MainActivity.newInstance(requireContext()))
        }

        binding.skip.setOnClickListener {
            requireActivity().finishAffinity()
        }

        lifecycleScope.launchWhenCreated {
            val amount = viewModel.drinkAmount()

            withContext(Dispatchers.Main) {
                binding.message.text = getString(R.string.drink_alarm_message, amount)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DrinkAlarmFragment()
    }
}
