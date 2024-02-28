package com.soriano.christianjose.block6.p1.tsikottracker.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.soriano.christianjose.block6.p1.tsikottracker.R

class ForgotPasswordDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("We've just sent you a password reset link to your email address. Please check your inbox (and possibly your spam) to reset your password.")
            .setPositiveButton(getString(R.string.ok)) { _,_ ->
                findNavController().popBackStack(R.id.forgotPasswordFragment, true)
            }
            .create()

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}