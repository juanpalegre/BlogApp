package xyz.juanalegre.blogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.FirebaseFirestore
import xyz.juanalegre.blogapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        observeDestinationChange(navController)
    }

    private fun observeDestinationChange(navController: NavController) {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.loginFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                R.id.registerFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                R.id.homeScreenFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.setupProfileFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

}