package com.newpaper.somewhere.core.firebase_firestore.dataSource.signIn

import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ProviderId

interface SignInRemoteDataSource {
    /**
     * register user in remote db
     * @return user registration success or not
     */
    suspend fun registerUser(
        userData: UserData
    ): Boolean

}