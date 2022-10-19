package xyz.juanalegre.blogapp.data.remote

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import xyz.juanalegre.blogapp.data.model.Post
import xyz.juanalegre.blogapp.data.model.Poster
import java.io.ByteArrayOutputStream
import java.util.*

class CameraDataSource {

    suspend fun uploadPhoto(imageBitmap: Bitmap, description: String){
        val user = FirebaseAuth.getInstance().currentUser
        val randomName = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().reference.child("${user?.uid}/posts/$randomName")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var downloadUrl = ""
        withContext(Dispatchers.IO) {
           downloadUrl = imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()
        }
        user?.let {
            it.displayName?.let { displayName ->
                FirebaseFirestore.getInstance().collection("post").add(Post(
                    poster = Poster(username = displayName, uid = user.uid, profile_picture = it.photoUrl.toString()),
                    post_image = downloadUrl,
                    post_description = description,
                    likes = 0
                ))
            }
        }
    }

}