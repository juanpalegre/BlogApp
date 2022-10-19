package xyz.juanalegre.blogapp.ui.auth

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import xyz.juanalegre.blogapp.R
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.data.remote.AuthDataSource
import xyz.juanalegre.blogapp.databinding.FragmentSetupProfileBinding
import xyz.juanalegre.blogapp.domain.auth.AuthRepoImplement
import xyz.juanalegre.blogapp.presentation.AuthViewModel
import xyz.juanalegre.blogapp.presentation.AuthViewModelFactory


class SetupProfileFragment : Fragment(R.layout.fragment_setup_profile) {

    private lateinit var binding: FragmentSetupProfileBinding
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(
        AuthRepoImplement(
            AuthDataSource()
        )
    ) }
    private val REQUEST_IMAGE_CAPTURE = 1
    private var bitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupProfileBinding.bind(view)

        binding.profileImage.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException){
                Toast.makeText(requireContext(), "Didn't found any app to take photo", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCreateProfile.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val alertDialog = AlertDialog.Builder(requireContext()).setTitle("Uploading photo...").create()
            bitmap?.let {
                if (username.isNotEmpty()){
                    viewModel.updateUserProfile(imageBitmap = it, username = username)
                        .observe(viewLifecycleOwner, Observer {result ->
                        when(result){
                            is Resource.Loading -> {
                                alertDialog.show()
                            }
                            is Resource.Success -> {
                                alertDialog.dismiss()
                                findNavController().navigate(R.id.action_setupProfileFragment_to_homeScreenFragment)
                            }
                            is Resource.Failure -> {
                                alertDialog.show()
                            }
                        }
                    })
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.profileImage.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
        }
    }

}