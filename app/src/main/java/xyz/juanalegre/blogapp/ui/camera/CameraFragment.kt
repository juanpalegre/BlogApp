package xyz.juanalegre.blogapp.ui.camera

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import xyz.juanalegre.blogapp.R
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.data.remote.CameraDataSource
import xyz.juanalegre.blogapp.databinding.FragmentCameraBinding
import xyz.juanalegre.blogapp.domain.camera.CameraRepoImplement
import xyz.juanalegre.blogapp.presentation.CameraViewModel
import xyz.juanalegre.blogapp.presentation.CameraViewModelFactory


class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding
    private val REQUEST_IMAGE_CAPTURE = 2
    private var bitmap: Bitmap? = null
    private val viewModel by viewModels<CameraViewModel> {
        CameraViewModelFactory(CameraRepoImplement(CameraDataSource()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Didn't found any app to take photo", Toast.LENGTH_SHORT).show()
        }

        binding.buttonUploadPhoto.setOnClickListener {
            bitmap?.let {bitmap->
                viewModel.uploadPhoto(bitmap, binding.editTextPhotoDescription.text.toString().trim())
                    .observe(viewLifecycleOwner, Observer { result->
                        when(result){
                            is Resource.Loading -> {
                                Toast.makeText(requireContext(), "Uploading photo...", Toast.LENGTH_SHORT).show()
                            }
                            is Resource.Success -> {
                                findNavController().navigate(R.id.action_cameraFragment_to_homeScreenFragment)
                            }
                            is Resource.Failure -> {
                                Toast.makeText(requireContext(), "Upload failed...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.imageAddPhoto.setImageBitmap(imageBitmap)
            bitmap = imageBitmap
        }
    }

}