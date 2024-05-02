package com.newpaper.somewhere.core.firebase_firestore.dataSource.more.profile

interface EditProfileRemoteDataSource {

    /**
     * update user name and user profile image
     */
    fun updateUserProfile(
        appUserId: String?,
        newUserName: String?,
        newProfileImage: String?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}