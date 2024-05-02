package com.newpaper.somewhere.core.data.repository

import android.content.Context
import com.newpaper.somewhere.core.firebase_storage.dataSource.ImageRemoteDataSource
import com.newpaper.somewhere.core.local_image_file.dataSource.ImageLocalDataSource
import java.io.File
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val imageLocalDataSource: ImageLocalDataSource
) {
    //get image local or download from remote
    suspend fun getImage(
        imagePath: String,
        context: Context,

        profileUserId: String? = null,
        tripManagerId: String? = null
    ): File? {
        val imageFile = File(context.filesDir, imagePath)
        val imageFileExit = imageFile.exists()

        //image exit in local storage
        if (imageFileExit)
            return imageFile


        //image not exit in local storage -> download image from remote
        // download profile image
        if (imagePath.contains("profile_")){
            return if (
                profileUserId != null
                && imageRemoteDataSource.downloadProfileImage(
                        profileUserId = profileUserId,
                        imagePath = imagePath
                    )
                ) imageFile
                else null
        }

        // download trip image
        else {
            return if (
                tripManagerId != null
                && imageRemoteDataSource.downloadTripImage(
                    tripManagerId = tripManagerId,
                    imagePath = imagePath
                )
            ) imageFile
            else null
        }
    }

    suspend fun downloadImage(
        imagePath: String,
        profileUserId: String? = null,
        tripManagerId: String? = null
    ): Boolean {
        //download image from remote
        // download profile image
        if (imagePath.contains("profile_")){
            return (profileUserId != null
                    && imageRemoteDataSource.downloadProfileImage(
                profileUserId = profileUserId,
                imagePath = imagePath
            ))
        }

        // download trip image
        else {
            return (tripManagerId != null
                    && imageRemoteDataSource.downloadTripImage(
                tripManagerId = tripManagerId,
                imagePath = imagePath
            ))
        }
    }

}