package xyz.juanalegre.blogapp.domain

import kotlinx.coroutines.flow.Flow
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.data.model.Post

interface HomeScreenRepo {

    suspend fun getLatestPost(): Resource<List<Post>>

    suspend fun registerLikeButtonState(postId: String, liked: Boolean)
}