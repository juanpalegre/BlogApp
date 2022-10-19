package xyz.juanalegre.blogapp.domain.auth

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseUser

interface AuthRepo {

    suspend fun signIn(email: String, password: String): FirebaseUser?

    suspend fun signUp(username: String, email: String, password: String): FirebaseUser?

    suspend fun UpdateProfile(imageBitmap: Bitmap, username: String)

}