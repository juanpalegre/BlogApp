package xyz.juanalegre.blogapp.data.remote

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import xyz.juanalegre.blogapp.data.model.User
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class AuthDataSource {

    suspend fun SignIn(email: String, password: String): FirebaseUser?{
        val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        return authResult.user
    }

    suspend fun SignUp(username: String, email: String, password: String): FirebaseUser?{
        val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
        authResult.user?.uid?.let {uid->
            FirebaseFirestore.getInstance().collection("users").document(uid).set(User(email, username)).await()
        }
        return authResult.user
    }

    suspend fun updateUserProfile(imageBitmap: Bitmap, username: String){
        val user = FirebaseAuth.getInstance().currentUser
        val imageRef = FirebaseStorage.getInstance().reference.child("${user?.uid}/profile_picture")
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var downloadUrl = ""
        withContext(Dispatchers.IO) {
            downloadUrl = imageRef.putBytes(baos.toByteArray()).await().storage.downloadUrl.await().toString()
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(Uri.parse(downloadUrl))
                .build()
            user?.updateProfile(profileUpdates)?.await()
        }
    }
}