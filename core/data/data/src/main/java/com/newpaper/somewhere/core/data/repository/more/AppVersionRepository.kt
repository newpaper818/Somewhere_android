package com.newpaper.somewhere.core.data.repository.more

import com.newpaper.somewhere.core.firebase_firestore.dataSource.more.appVersion.AppVersionRemoteDataSource
import javax.inject.Inject

class AppVersionRepository @Inject constructor(
    private val appVersionRemoteDataSource: AppVersionRemoteDataSource
) {
    suspend fun getLatestAppVersionCode(

    ): Int? {
        return appVersionRemoteDataSource.getLatestAppVersionCode()
    }
}