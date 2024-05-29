package com.newpaper.somewhere.core.firebase_storage.dataSource.more.editProfile

interface EditProfileImageRemoteDataSource {

    suspend fun updateProfileImage(
        userId: String,
        imagePath: String?
    )
}