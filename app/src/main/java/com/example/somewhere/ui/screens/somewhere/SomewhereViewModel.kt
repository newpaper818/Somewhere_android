package com.example.somewhere.ui.screens.somewhere

import androidx.lifecycle.ViewModel
import com.example.somewhere.db.TripRepository
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screens.main.MainDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

data class SomewhereUiState(
    val isEditMode: Boolean = false,
    val currentScreen: NavigationDestination = MainDestination,
    val topAppBarTitle: String = ""
)

class SomewhereViewModel(
    private val tripsRepository: TripRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(
        SomewhereUiState()
    )

    val uiState: StateFlow <SomewhereUiState> = _uiState

    fun changeEditMode(editMode: Boolean? = null) {
        _uiState.update {
            it.copy(isEditMode = editMode ?: !it.isEditMode)
        }
    }

    fun updateCurrentScreen(screenTo: NavigationDestination) {
        _uiState.update {
            it.copy(currentScreen = screenTo)
        }
    }

    fun updateTopAppBarTitle(title: String) {
        _uiState.update {
            it.copy(topAppBarTitle = title)
        }
    }

//    suspend fun addNewTrip(nowTime: LocalDateTime){
//        tripsRepository.insertTrip(Trip(firstCreatedTime = nowTime, lastModifiedTime = nowTime))
//    }
//
//    suspend fun getTrip(time: LocalDateTime): Trip?{
//        return tripsRepository.getTripStream(time)
//            .first()
//    }
}

