package com.example.ecommerce_rifqi.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.databinding.FragmentChangePasswordBinding
import com.example.ecommerce_rifqi.ui.LoadingDialog
import com.example.ecommerce_rifqi.ui.view.ChangePasswordViewModel
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.utils.ViewModelFactory

class FragmentChangePassword : Fragment(R.layout.fragment_change_password) {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ChangePasswordViewModel

    lateinit var sharedPref: PreferencesHelper

    private lateinit var loading: LoadingDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangePasswordBinding.bind(view)

        sharedPref = PreferencesHelper(requireContext())

        loading = LoadingDialog(requireActivity())

        binding.apply {

            etOldPass.doOnTextChanged { text, start, before, count ->
                if (etOldPass.text.toString().isNotEmpty()) {
                    inputOldPass.error = null
                    inputOldPass.isErrorEnabled = false
                } else inputOldPass.error = "Old password cannot be empty!"
            }

            etNewPass.doOnTextChanged { text, start, before, count ->
                if (etNewPass.text.toString().isNotEmpty()) {
                    inputNewPass.error = null
                    inputNewPass.isErrorEnabled = false
                } else inputNewPass.error = "New password cannot be empty!"
            }

            etConfirmNewPass.doOnTextChanged { text, start, before, count ->
                if (etConfirmNewPass.text.toString().isNotEmpty()) {
                    inputConfirmNewPass.error = null
                    inputConfirmNewPass.isErrorEnabled = false
                } else inputConfirmNewPass.error = "New password cannot be empty!"

                if (etConfirmNewPass.text.toString() != etNewPass.text.toString()) {
                    inputConfirmNewPass.error = "Passwords not match!"

                } else {
                    inputConfirmNewPass.error = null
                    inputConfirmNewPass.isErrorEnabled = false
                }
            }

            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSave.setOnClickListener {

                loading.startLoading()
                val id = sharedPref.getString(Constant.PREF_ID)

                if (etOldPass.text.toString().isEmpty()) {
                    inputOldPass.error = "Name cannot be empty!"
                } else if (etNewPass.text.toString().isEmpty()) {
                    inputNewPass.error = "Email cannot be empty!"
                } else if (etConfirmNewPass.text.toString().isEmpty()) {
                    inputConfirmNewPass.error = "Password cannot be empty!"
                }

                if (
                    etOldPass.text?.isNotEmpty() == true &&
                    etNewPass.text?.isNotEmpty() == true &&
                    etConfirmNewPass.text?.isNotEmpty() == true
                ){
                    changePassword(
                        id!!.toInt(),
                        etOldPass.text.toString(),
                        etNewPass.text.toString(),
                        etConfirmNewPass.text.toString()
                    )
                } else{
                    loading.isDismiss()
                    showMessage("Data tidak boleh kosong!")
                }


            }
        }
    }

    private fun changePassword(id: Int, oldPass: String, newPass: String, confirmPass: String) {
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[ChangePasswordViewModel::class.java]
        viewModel.setChangePass(id, oldPass, newPass, confirmPass)
        viewModel.changeSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                loading.isDismiss()
                showMessage(response.success.message)
                findNavController().navigate(R.id.action_fragmentChangePassword_to_fragmentProfile)
            }
        }
        viewModel.toast.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                loading.isDismiss()
                showMessage(response)
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}