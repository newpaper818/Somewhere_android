package com.newpaper.somewhere.core.data.repository.image

import android.content.Context
import android.net.Uri
import com.newpaper.somewhere.core.firebase_storage.dataSource.image.ImageRemoteDataSource
import com.newpaper.somewhere.core.local_image_file.dataSource.ImageLocalDataSource
import com.newpaper.somewhere.core.model.tripData.Trip
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CommonImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val imageLocalDataSource: ImageLocalDataSource
) {



    fun uploadImagesToRemote(
        tripManagerId: String,
        imagePaths: List<String>
    ){
        imageRemoteDataSource.uploadTripImages(
            tripManagerId = tripManagerId,
            imagePaths = imagePaths
        )
    }







    fun deleteImagesFromRemote(
        tripManagerId: String,
        imagePaths: List<String>
    ){
        imageRemoteDataSource.deleteTripImages(
            tripManagerId = tripManagerId,
            imagePaths = imagePaths
        )
    }

    fun deleteImagesFromInternalStorage(
        images: List<String>
    ){
        imageLocalDataSource.deleteFilesFromInternalStorage(
            files = images
        )
    }

    fun deleteAllImagesFromInternalStorage(
    ){
        imageLocalDataSource.deleteAllImagesFromInternalStorage()
    }

    fun deleteUnusedImageFilesForAllTrips(
        allTrips: List<Trip>
    ){
        imageLocalDataSource.deleteUnusedImageFilesForAllTrips(
            allTrips = allTrips
        )
    }

    fun deleteUnusedProfileImageFiles(
        usingProfileImage: String?
    ){
        imageLocalDataSource.deleteUnusedProfileImageFiles(
            usingProfileImage = usingProfileImage
        )
    }






    fun saveImageToInternalStorage(
        tripId: Int,
        index: Int,
        uri: Uri,
        isProfileImage: Boolean = false
    ): String? {
        return imageLocalDataSource.saveImageToInternalStorage(
            tripId = tripId,
            index = index,
            uri = uri,
            isProfileImage = isProfileImage
        )
    }

    fun saveImageToExternalStorage(
        imageFileName: String
    ): Boolean {
        return imageLocalDataSource.saveImageToExternalStorage(
            imageFileName = imageFileName
        )
    }
}