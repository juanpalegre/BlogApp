package xyz.juanalegre.blogapp.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.domain.auth.AuthRepo

class AuthViewModel(private val repo: AuthRepo): ViewModel() {

    fun signIn(email: String, password: String) = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.signIn(email, password)))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun signUp(username: String, email: String, password: String) = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.signUp(username, email, password)))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun updateUserProfile(imageBitmap: Bitmap, username: String) = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.UpdateProfile(imageBitmap, username)))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

}

class AuthViewModelFactory(private val repo: AuthRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repo) as T
    }
}