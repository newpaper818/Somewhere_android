package com.newpaper.somewhere.core.data.repository.more

import com.newpaper.somewhere.core.firebase_firestore.dataSource.more.editProfile.EditProfileRemoteDataSource
import com.newpaper.somewhere.core.firebase_storage.dataSource.more.editProfile.EditProfileImageRemoteDataSource
import javax.inject.Inject

class EditProfileRepository @Inject constructor(
    private val editProfileRemoteDataSource: EditProfileRemoteDataSource,
    private val editProfileImageRemoteDataSource: EditProfileImageRemoteDataSource
){
    suspend fun updateProfileImage(
        userId: String,
        imagePath: String?
    ){
        editProfileImageRemoteDataSource.updateProfileImage(
            userId = userId,
            imagePath = imagePath
        )
    }

    fun updateUserProfile(
        appUserId: String?,
        newUserName: String?,
        newProfileImagePath: String?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        editProfileRemoteDataSource.updateUserProfile(
            appUserId = appUserId,
            newUserName = newUserName,
            newProfileImagePath = newProfileImagePath,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}