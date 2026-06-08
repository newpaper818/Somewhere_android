package com.newpaper.somewhere.core.datastore.dataSource

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.getProviderIdFromString
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
import javax.inject.Inject

private const val USER_DATA_STORE_TAG = "UserDataStore"

class UserDataStoreApi @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserLocalDataSource {

    private companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val PROFILE_IMAGE_PATH = stringPreferencesKey("profile_image_path")
        val PROVIDER_IDS = stringPreferencesKey("provider_ids") // comma separated
        val IS_USING_SOMEWHERE_PRO = booleanPreferencesKey("is_using_somewhere_pro")
        val LAST_UPDATED_TIME = stringPreferencesKey("last_updated_time")
    }

    override suspend fun getCachedUserData(): Pair<UserData?, String?> {
        val preferences = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e(USER_DATA_STORE_TAG, "Error reading user preferences.", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.firstOrNull() ?: return Pair(null, null)

        val userId = preferences[USER_ID] ?: return Pair(null, null)
        val userName = preferences[USER_NAME]
        val email = preferences[USER_EMAIL]
        val profileImagePath = preferences[PROFILE_IMAGE_PATH]
        val providerIdsString = preferences[PROVIDER_IDS] ?: ""
        val isUsingSomewherePro = preferences[IS_USING_SOMEWHERE_PRO] ?: false
        val lastUpdatedTime = preferences[LAST_UPDATED_TIME]

        val providerIds = providerIdsString.split(",")
            .filter { it.isNotEmpty() }
            .mapNotNull { getProviderIdFromString(it) }

        val userData = UserData(
            userId = userId,
            userName = userName,
            email = email,
            profileImagePath = profileImagePath,
            providerIds = providerIds,
            isUsingSomewherePro = isUsingSomewherePro
        )

        return Pair(userData, lastUpdatedTime)
    }

    override suspend fun saveUserData(userData: UserData, lastUpdatedTime: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userData.userId
            userData.userName?.let { preferences[USER_NAME] = it } ?: preferences.remove(USER_NAME)
            userData.email?.let { preferences[USER_EMAIL] = it } ?: preferences.remove(USER_EMAIL)
            userData.profileImagePath?.let { preferences[PROFILE_IMAGE_PATH] = it } ?: preferences.remove(PROFILE_IMAGE_PATH)
            preferences[PROVIDER_IDS] = userData.providerIds.joinToString(",") { it.id }
            preferences[IS_USING_SOMEWHERE_PRO] = userData.isUsingSomewherePro
            preferences[LAST_UPDATED_TIME] = lastUpdatedTime
        }
    }

    override suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_NAME)
            preferences.remove(USER_EMAIL)
            preferences.remove(PROFILE_IMAGE_PATH)
            preferences.remove(PROVIDER_IDS)
            preferences.remove(IS_USING_SOMEWHERE_PRO)
            preferences.remove(LAST_UPDATED_TIME)
        }
    }
}
