package com.newpaper.somewhere.core.datastore.dataSource

import com.newpaper.somewhere.core.model.data.UserData

interface UserLocalDataSource {
    /**
     * Get cached user data and the last updated time (ISO8601 string).
     */
    suspend fun getCachedUserData(): Pair<UserData?, String?>

    /**
     * Save user data and current time to cache.
     */
    suspend fun saveUserData(userData: UserData, lastUpdatedTime: String)

    /**
     * Clear all cached user data.
     */
    suspend fun clearUserData()
}
