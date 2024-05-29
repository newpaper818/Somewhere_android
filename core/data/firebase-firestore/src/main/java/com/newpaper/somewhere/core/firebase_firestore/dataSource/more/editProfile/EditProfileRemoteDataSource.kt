package com.newpaper.somewhere.core.firebase_firestore.dataSource.more.editProfile

interface EditProfileRemoteDataSource {

    /**
     * update user name and user profile image
     */
    fun updateUserProfile(
        appUserId: String?,
        newUserName: String?,
        newProfileImagePath: String?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}