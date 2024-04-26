package com.newpaper.somewhere.core.firebase_firestore.dataSource.more.appVersion

interface AppVersionRemoteDataSource {

    /**
     * get latest app version code from remote
     *
     * @return latest app version code or null(error)
     */
    suspend fun getLatestAppVersionCode(): Int?
}