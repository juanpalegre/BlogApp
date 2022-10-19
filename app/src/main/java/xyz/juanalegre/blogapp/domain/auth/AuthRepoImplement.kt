package xyz.juanalegre.blogapp.domain.auth

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseUser
import xyz.juanalegre.blogapp.data.remote.AuthDataSource

class AuthRepoImplement(private val dataSource: AuthDataSource): AuthRepo {

    override suspend fun signIn(email: String, password: String): FirebaseUser? =
        dataSource.SignIn(email, password)

    override suspend fun signUp(username: String, email: String, password: String): FirebaseUser? =
        dataSource.SignUp(username, email, password)

    override suspend fun UpdateProfile(imageBitmap: Bitmap, username: String) {
        dataSource.updateUserProfile(imageBitmap, username)
    }


}