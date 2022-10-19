package xyz.juanalegre.blogapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.domain.HomeScreenRepo
import java.lang.Exception

class HomeScreenViewModel(private val repo: HomeScreenRepo): ViewModel() {

    fun fetchLatestPost() = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        kotlin.runCatching {
            repo.getLatestPost()
        }.onSuccess { postList->
            emit(postList)
        }
        .onFailure { throwable->
            emit(Resource.Failure(Exception(throwable.message)))
        }
    }

    fun registerLikeButtonState(postId: String, liked: Boolean) = liveData(viewModelScope.coroutineContext + Dispatchers.IO){
        emit(Resource.Loading())
        kotlin.runCatching {
            repo.registerLikeButtonState(postId, liked)
        }.onSuccess {
            emit(Resource.Success(Unit))
        }.onFailure {throwable->
            emit(Resource.Failure(Exception(throwable.message)))
        }
    }
}

class HomeScreenViewModelFactory(private val repo: HomeScreenRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(HomeScreenRepo::class.java).newInstance(repo)
    }
}