package com.newpaper.somewhere.core.data.repository.image

import com.newpaper.somewhere.core.firebase_storage.dataSource.ImageRemoteDataSource
import com.newpaper.somewhere.core.local_image_file.dataSource.ImageLocalDataSource
import javax.inject.Inject

class GetImageRepository @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val imageLocalDataSource: ImageLocalDataSource
) {
    fun downloadImage(
        imagePath: String,
        imageUserId: String,
        result: (Boolean) -> Unit
    ) {
        //download image from remote
        // download profile image
        if (imagePath.contains("profile_")){
            imageRemoteDataSource.downloadProfileImage(
                profileUserId = imageUserId,
                imagePath = imagePath,
                result = result
            )
        }

        // download trip image
        else {
            imageRemoteDataSource.downloadTripImage(
                tripManagerId = imageUserId,
                imagePath = imagePath,
                result = result
            )
        }
    }
}