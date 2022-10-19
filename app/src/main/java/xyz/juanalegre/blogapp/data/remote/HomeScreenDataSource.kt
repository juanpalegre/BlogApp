package xyz.juanalegre.blogapp.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import xyz.juanalegre.blogapp.core.Resource
import xyz.juanalegre.blogapp.data.model.Post
import java.lang.Exception

class HomeScreenDataSource {

    suspend fun getLatestPost(): Resource<List<Post>>{

        val postList = mutableListOf<Post>()

        withContext(Dispatchers.IO){
            val querySnapshot = FirebaseFirestore.getInstance().collection("post")
                .orderBy("created_at", Query.Direction.DESCENDING).get().await()

            for(post in querySnapshot.documents){
                post.toObject(Post::class.java)?.let { firebasePost ->

                    FirebaseAuth.getInstance().currentUser?.let {safeUser->
                        isPostLiked(post.id, safeUser.uid)
                    }

                    firebasePost.apply { created_at = post.getTimestamp(
                        "created_at",
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)?.toDate()
                        id = post.id

                    }
                    postList.add(firebasePost)
                }
            }
        }
        return Resource.Success(postList)
    }

    private suspend fun isPostLiked(postId: String, uid: String): Boolean{
        val post = FirebaseFirestore.getInstance().collection("postLikes").document(postId).get().await()
        if(!post.exists()) return false
        val likeArray: List<String> = post.get("likes") as List<String>
        return likeArray.contains(uid)
    }

    fun registerLikeButtonState(postId: String, liked: Boolean) {

        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val postRef = FirebaseFirestore.getInstance().collection("post").document(postId)
        val postLikeRef = FirebaseFirestore.getInstance().collection("postLikes").document(postId)

        val database = FirebaseFirestore.getInstance()

        database.runTransaction {transaction->
            val snapshot = transaction.get(postRef)
            val likeCounts = snapshot.getLong("likes")
            if(likeCounts != null) {
                if (likeCounts >= 0) {
                    if (liked) {
                        if (transaction.get(postLikeRef).exists()) {
                            transaction.update(postLikeRef, "likes", FieldValue.arrayUnion(uid))
                        } else {
                            transaction.set(
                                postLikeRef,
                                hashMapOf("likes" to arrayListOf(uid)),
                                SetOptions.merge()
                            )
                        }

                        transaction.update(postRef, "likes", increment)
                    } else {
                        transaction.update(postRef, "likes", decrement)
                        transaction.update(postLikeRef, "likes", FieldValue.arrayRemove(uid))
                    }
                }
            }
        }.addOnFailureListener {
            throw Exception(it.message)
        }
    }

}