package com.github.dnaka91.drinkup.sizes

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dnaka91.drinkup.R
import com.github.dnaka91.drinkup.databinding.EditIntakeSizeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class EditIntakeSizeFragment : Fragment() {
    private var _binding: EditIntakeSizeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<EditIntakeSizeViewModel>()
    private val args by navArgs<EditIntakeSizeFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.id = args.id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditIntakeSizeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.name.doAfterTextChanged {
            viewModel.onNameChanged(it?.toString() ?: "")
        }
        binding.name.setText(args.name)

        binding.amount.doAfterTextChanged {
            viewModel.onAmountChanged(it?.toString()?.toInt() ?: 0)
        }

        if (args.amount > 0) {
            binding.amount.setText(args.amount.toString())
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
}
