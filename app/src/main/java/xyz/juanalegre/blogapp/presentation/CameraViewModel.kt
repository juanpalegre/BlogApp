package xyz.juanalegre.blogapp.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.domain.auth.AuthRepo
import xyz.juanalegre.blogapp.domain.camera.CameraRepo
import xyz.juanalegre.blogapp.domain.camera.CameraRepoImplement

class CameraViewModel(private val repo: CameraRepo): ViewModel() {

    fun uploadPhoto(imageBitmap: Bitmap, description: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.uploadPhoto(imageBitmap, description)))
        } catch (e:Exception){
            emit(Resource.Failure(e))
        }
    }

}

class CameraViewModelFactory(private val repo: CameraRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(CameraRepo::class.java).newInstance(repo)
    }
}