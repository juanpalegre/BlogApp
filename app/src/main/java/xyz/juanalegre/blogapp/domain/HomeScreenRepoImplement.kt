package xyz.juanalegre.blogapp.domain

import kotlinx.coroutines.flow.Flow
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.data.model.Post
import xyz.juanalegre.blogapp.data.remote.HomeScreenDataSource

class HomeScreenRepoImplement(private val dataSource: HomeScreenDataSource): HomeScreenRepo {
    override suspend fun getLatestPost(): Resource<List<Post>> {
        return dataSource.getLatestPost()
    }

    override suspend fun registerLikeButtonState(postId: String, liked: Boolean) {
        return dataSource.registerLikeButtonState(postId, liked)
    }


}