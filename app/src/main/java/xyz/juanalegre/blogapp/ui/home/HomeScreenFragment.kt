package xyz.juanalegre.blogapp.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.google.firebase.Timestamp
import xyz.juanalegre.blogapp.R
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.data.model.Post
import xyz.juanalegre.blogapp.data.remote.HomeScreenDataSource
import xyz.juanalegre.blogapp.databinding.FragmentHomeScreenBinding
import xyz.juanalegre.blogapp.domain.HomeScreenRepoImplement
import xyz.juanalegre.blogapp.presentation.HomeScreenViewModel
import xyz.juanalegre.blogapp.presentation.HomeScreenViewModelFactory
import xyz.juanalegre.blogapp.ui.home.adapter.HomeScreenAdapter
import xyz.juanalegre.blogapp.ui.home.adapter.OnPostClickListener


class HomeScreenFragment : Fragment(R.layout.fragment_home_screen), OnPostClickListener {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by viewModels<HomeScreenViewModel> {
        HomeScreenViewModelFactory(HomeScreenRepoImplement(HomeScreenDataSource())) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)

        viewModel.fetchLatestPost().observe(viewLifecycleOwner, Observer {result ->
            when(result){

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if(result.data.isEmpty()){
                        binding.emptyContainer.visibility = View.VISIBLE
                        return@Observer
                    } else{
                        binding.emptyContainer.visibility = View.GONE
                    }
                    binding.rvHome.adapter = HomeScreenAdapter(result.data, this)
                    Log.d("Firebase", "${result.data}")
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    override fun onLikeButtonClick(post: Post, liked: Boolean){
        viewModel.registerLikeButtonState(post.id, liked).observe(viewLifecycleOwner){result->
            when(result){

                is Resource.Loading -> {
                }
                is Resource.Success -> {

                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show()
                }


            }
        }
    }

}