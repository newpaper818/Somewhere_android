package com.newpaper.somewhere.core.local_image_file.dataSource

import android.content.Context
import android.net.Uri
import com.newpaper.somewhere.core.model.tripData.Trip

interface ImageLocalDataSource {
    fun saveImageToInternalStorage(
        tripId: Int,
        index: Int,
        context: Context,
        uri: Uri,
        isProfileImage: Boolean = false
    ): String?

    /**
     * download to external storage
     *
     * user can see image on gallery app or files
     */
    fun saveImageToExternalStorage(
        context: Context,
        imageFileName: String
    ): Boolean


    fun deleteFilesFromInternalStorage(
        context: Context,
        fileList: List<String>
    )


    fun deleteAllImagesFromInternalStorage(
        context: Context,
    )


    fun deleteUnusedImageFilesForAllTrips(
        context: Context,
        allTripList: List<Trip>
    )


    fun deleteUnusedProfileImageFiles(
        context: Context,
        usingProfileImage: String?
    )
}