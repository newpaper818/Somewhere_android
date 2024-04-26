package com.newpaper.somewhere.core.firebase_storage.dataSource

interface ImageRemoteDataSource {

    /**
     * download trip image to local
     * @return true when download success
     */
    suspend fun downloadTripImage(
        tripManagerId: String,
        imagePath: String
    ): Boolean




    /**
     * download profile image to local
     * @return true when download success
     */
    suspend fun downloadProfileImage(
        userId: String,
        imagePath: String
    ): Boolean




    /**
     * upload trip images to remote
     */
    fun uploadTripImages(
        managerId: String,
        imagePathList: List<String>
    )


    /**
     * delete existing profile image
     * and upload new profile image
     */
    suspend fun updateProfileImage(
        userId: String,
        imagePath: String?
    )


    /**
     * delete trip images
     */
    fun deleteTripImages(
        tripManagerId: String,
        imagePathList: List<String>
    )
}