package com.newpaper.somewhere.core.data.repository

import com.newpaper.somewhere.core.datastore.dataSource.SettingLocalDataSource
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.Theme
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val settingLocalDataSource: SettingLocalDataSource
) {
    suspend fun updateAppSettingValue(
        onSet: (Theme, DateTimeFormat) -> Unit
    ) {
        settingLocalDataSource.updateAppSettingValue(onSet = onSet)
    }

}