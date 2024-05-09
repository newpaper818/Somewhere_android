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
//    fun getImage(
//        imagePath: String,
//        context: Context,
//
//        profileUserId: String? = null,
//        tripManagerId: String? = null
//    ): File? {
//        val imageFile = File(context.filesDir, imagePath)
//        val imageFileExit = imageFile.exists()
//
//        //image exit in local storage
//        if (imageFileExit)
//            return imageFile
//
//
//        //image not exit in local storage -> download image from remote
//        // download profile image
//        if (imagePath.contains("profile_")){
//
//            if (profileUserId != null){
//                imageRemoteDataSource.downloadProfileImage(
//                    profileUserId = profileUserId,
//                    imagePath = imagePath,
//                    result = {
//                        if (it){
//                            return imageFile
//                        }
//                        else null
//                    }
//                )
//            }
//            else
//                return null
//        }
//
//        // download trip image
//        else {
//            return if (
//                tripManagerId != null
//                && imageRemoteDataSource.downloadTripImage(
//                    tripManagerId = tripManagerId,
//                    imagePath = imagePath
//                )
//            ) imageFile
//            else null
//        }
//    }

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