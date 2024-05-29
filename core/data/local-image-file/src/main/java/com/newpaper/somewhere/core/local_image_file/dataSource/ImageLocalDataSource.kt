package com.newpaper.somewhere.core.local_image_file.dataSource

import android.net.Uri
import com.newpaper.somewhere.core.model.tripData.Trip

interface ImageLocalDataSource {
    /**
     * when user choose image from gallery
     *
     * before upload image to remote
     */
    fun saveImageToInternalStorage(
        tripId: Int,
        index: Int,
        uri: Uri,
        isProfileImage: Boolean = false
    ): String?

    /**
     * download to external storage
     *
     * user can see image on gallery app or files
     */
    fun saveImageToExternalStorage(
        imageFileName: String
    ): Boolean


    fun deleteFilesFromInternalStorage(
        files: List<String>
    )


    fun deleteAllImagesFromInternalStorage()


    fun deleteUnusedImageFilesForAllTrips(
        allTrips: List<Trip>
    )


    fun deleteUnusedProfileImageFiles(
        usingProfileImage: String?
    )
}