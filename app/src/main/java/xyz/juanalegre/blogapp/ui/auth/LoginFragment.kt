package xyz.juanalegre.blogapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import xyz.juanalegre.blogapp.R
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.core.hideKeyboard
import xyz.juanalegre.blogapp.data.remote.AuthDataSource
import xyz.juanalegre.blogapp.databinding.FragmentLoginBinding
import xyz.juanalegre.blogapp.domain.auth.AuthRepoImplement
import xyz.juanalegre.blogapp.presentation.AuthViewModel
import xyz.juanalegre.blogapp.presentation.AuthViewModelFactory


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(AuthRepoImplement(
        AuthDataSource()
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        isUserLoggedIn()
        doLogin()
        goToSignUpPage()
    }

    private fun isUserLoggedIn(){
        firebaseAuth.currentUser?.let {user->
            if (user.displayName.isNullOrEmpty()){
                findNavController().navigate(R.id.action_loginFragment_to_setupProfileFragment)
            }
            else{
                findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
            }
        }
    }

    private fun doLogin(){
        binding.buttonSignIn.setOnClickListener {
            it.hideKeyboard()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            validateCredentials(email, password)
            signIn(email, password)
        }
    }

    private fun goToSignUpPage(){
        binding.textSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun validateCredentials(email: String, password: String){
        if (email.isEmpty()){
            binding.editTextEmail.error = "Email is empty"
            return
        }
        if (password.isEmpty()){
            binding.editTextPassword.error = "Password is empty"
            return
        }
    }

    private fun signIn(email: String, password: String){
        viewModel.signIn(email, password).observe(viewLifecycleOwner, Observer {result->
            when (result) {

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSignIn.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (result.data?.displayName == null){
                        findNavController().navigate(R.id.action_loginFragment_to_setupProfileFragment)
                    }
                    else{
                        findNavController().navigate(R.id.action_loginFragment_to_homeScreenFragment)
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonSignIn.isEnabled = true
                    Toast.makeText(requireContext(), "Error ${result.exception}", Toast.LENGTH_LONG).show()
                }

            }
        })
    }

}