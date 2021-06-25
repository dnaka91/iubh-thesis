package com.github.dnaka91.drinkup

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener

class ConfirmIntakeFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = requireActivity().let {
        val name = arguments?.getString(ARG_NAME)!!
        val amount = arguments?.getInt(ARG_AMOUNT)!!

        AlertDialog.Builder(it)
            .setTitle(R.string.confirm_intake_title)
            .setMessage(getString(R.string.confirm_intake_message, name, amount))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                setFragmentResult(
                    REQUEST_CODE_CONFIRM_INTAKE, bundleOf(
                        "ok" to true,
                        "name" to name,
                        "amount" to amount,
                    )
                )
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                setFragmentResult(
                    REQUEST_CODE_CONFIRM_INTAKE, bundleOf(
                        "ok" to false,
                    )
                )
            }
            .create()
    }

    companion object {
        fun show(target: Fragment, name: String, amount: Int, listener: (String, Bundle) -> Unit) {
            ConfirmIntakeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                    putInt(ARG_AMOUNT, amount)
                }
                target.setFragmentResultListener(REQUEST_CODE_CONFIRM_INTAKE, listener)
            }.show(target.parentFragmentManager, "intake_confirm_dialog")
        }

        private const val ARG_NAME = "intake_size_name"
        private const val ARG_AMOUNT = "intake_size_amount"

        const val REQUEST_CODE_CONFIRM_INTAKE = "confirm_intake"
    }
}
