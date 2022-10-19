package xyz.juanalegre.blogapp.domain.camera

import android.graphics.Bitmap
import xyz.juanalegre.blogapp.data.remote.CameraDataSource

class CameraRepoImplement(private val dataSource: CameraDataSource): CameraRepo {
    override suspend fun uploadPhoto(imageBitmap: Bitmap, description: String) {
        dataSource.uploadPhoto(imageBitmap, description)
    }
}