package xyz.juanalegre.blogapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import xyz.juanalegre.blogapp.R
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.data.remote.AuthDataSource
import xyz.juanalegre.blogapp.databinding.FragmentRegisterBinding
import xyz.juanalegre.blogapp.domain.auth.AuthRepoImplement
import xyz.juanalegre.blogapp.presentation.AuthViewModel
import xyz.juanalegre.blogapp.presentation.AuthViewModelFactory


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(
        AuthRepoImplement(
        AuthDataSource()
    )
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        signUp()
    }

    private fun signUp() {

        binding.buttonSignUp.setOnClickListener {

            val username = binding.editTextUsername.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

            if (password != confirmPassword) {
                binding.editTextPassword.error = "Password does not match"
                binding.editTextConfirmPassword.error = "Password does not match"
                return@setOnClickListener
            }
            if (username.isEmpty()) {
                binding.editTextUsername.error = "Username field is empty"
            }
            if (email.isEmpty()) {
                binding.editTextEmail.error = "Email field is empty"
            }
            if (password.isEmpty()) {
                binding.editTextPassword.error = "Password field is empty"
            }
            if (confirmPassword.isEmpty()) {
                binding.editTextConfirmPassword.error = "ConfirmPassword field is empty"
            }
            createUser(username, email, password)
        }
    }

    fun createUser(username: String, email: String, password: String) {
        viewModel.signUp(username, email, password).observe(viewLifecycleOwner, Observer { result->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSignUp.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_registerFragment_to_setupProfileFragment)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonSignUp.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${result.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}