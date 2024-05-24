package com.newpaper.somewhere.feature.more.about

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.more.AppVersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AboutUiState(
    val isLatestAppVersion: Boolean? = null
)

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val appVersionRepository: AppVersionRepository
): ViewModel() {

    private val _aboutUiState: MutableStateFlow<AboutUiState> =
        MutableStateFlow(AboutUiState())
    val aboutUiState = _aboutUiState.asStateFlow()

    suspend fun updateIsLatestAppVersion(
        currentAppVersionCode: Int
    ){
        val latestAppVersionCode = appVersionRepository.getLatestAppVersionCode()

        if (latestAppVersionCode != null) {
            val isLatestAppVersion = latestAppVersionCode <= currentAppVersionCode

            _aboutUiState.update {
                it.copy(
                    isLatestAppVersion = isLatestAppVersion
                )
            }
        }
    }
}