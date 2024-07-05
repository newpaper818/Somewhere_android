package com.newpaper.somewhere.feature.trip.spot

import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.feature.trip.CommonTripUiStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SpotUiState(
    val currentDateIndex: Int = 0,
    val currentSpotIndex: Int? = null,

    val mapSize: IntSize = IntSize.Zero,
    val isMapExpanded: Boolean = false,

    val isShowingDialog: Boolean = false,
    val showMemoDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val showMoveDateDialog: Boolean = false,
    val showSetTimeDialog: Boolean = false,
    val showSetSpotTypeDialog: Boolean = false,
    val showSetBudgetDialog: Boolean = false,
    val showSetDistanceDialog: Boolean = false,
    val showSetLocationDialog: Boolean = false, //full screen dialog
    val showDeleteSpotDialog: Boolean = false,

    val isStartTime: Boolean = true,

    val totalErrorCount: Int = 0,
    val spotTitleErrorCount: Int = 0
)

@HiltViewModel
class SpotViewModel @Inject constructor(
    private val commonTripUiStateRepository: CommonTripUiStateRepository,
): ViewModel() {
    private val _spotUiState: MutableStateFlow<SpotUiState> =
        MutableStateFlow(SpotUiState())

    val spotUiState = _spotUiState.asStateFlow()

    private val commonTripUiState = commonTripUiStateRepository.commonTripUiState


    //current date/spot index
    fun setCurrentDateIndex(currentDateIndex: Int) {
        _spotUiState.update {
            it.copy(currentDateIndex = currentDateIndex)
        }
    }
    fun setCurrentSpotIndex(currentSpotIndex: Int?) {
        _spotUiState.update {
            it.copy(currentSpotIndex = currentSpotIndex)
        }
    }

    //map
    fun setMapSize(mapSize: IntSize) {
        _spotUiState.update {
            it.copy(mapSize = mapSize)
        }
    }

    fun setIsMapExpanded(isMapExpanded: Boolean) {
        _spotUiState.update {
            it.copy(isMapExpanded = isMapExpanded)
        }
    }


    //dialog
    private fun setIsShowingDialog() {
        val isShowingDialog = _spotUiState.value.showMemoDialog ||
                _spotUiState.value.showExitDialog || _spotUiState.value.showMoveDateDialog ||
                _spotUiState.value.showSetTimeDialog || _spotUiState.value.showSetSpotTypeDialog ||
                _spotUiState.value.showSetBudgetDialog || _spotUiState.value.showSetDistanceDialog ||
                _spotUiState.value.showSetLocationDialog

        _spotUiState.update {
            it.copy(isShowingDialog = isShowingDialog)
        }
    }

    fun setShowMemoDialog(showMemoDialog: Boolean) {
        _spotUiState.update {
            it.copy(showMemoDialog = showMemoDialog)
        }
        setIsShowingDialog()
    }

    fun setShowExitDialog(showExitDialog: Boolean) {
        _spotUiState.update {
            it.copy(showExitDialog = showExitDialog)
        }
        setIsShowingDialog()
    }

    fun setShowMoveDateDialog(showMoveDateDialog: Boolean) {
        _spotUiState.update {
            it.copy(showMoveDateDialog = showMoveDateDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetTimeDialog(showSetTimeDialog: Boolean) {
        _spotUiState.update {
            it.copy(showSetTimeDialog = showSetTimeDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetSpotTypeDialog(showSetSpotTypeDialog: Boolean) {
        _spotUiState.update {
            it.copy(showSetSpotTypeDialog = showSetSpotTypeDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetBudgetDialog(showSetBudgetDialog: Boolean) {
        _spotUiState.update {
            it.copy(showSetBudgetDialog = showSetBudgetDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetDistanceDialog(showSetDistanceDialog: Boolean) {
        _spotUiState.update {
            it.copy(showSetDistanceDialog = showSetDistanceDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetLocationDialog(showSetLocationDialog: Boolean) {
        _spotUiState.update {
            it.copy(showSetLocationDialog = showSetLocationDialog)
        }
        setIsShowingDialog()
    }
    fun setShowDeleteSpotDialog(showDeleteSpotDialog: Boolean) {
        _spotUiState.update {
            it.copy(showDeleteSpotDialog = showDeleteSpotDialog)
        }
        setIsShowingDialog()
    }

    //isStartTime
    fun setIsStartTime(isStartTime: Boolean) {
        _spotUiState.update {
            it.copy(isStartTime = isStartTime)
        }
    }


    //error count
    fun initAllErrorCount(){
        _spotUiState.update {
            it.copy(totalErrorCount = 0, spotTitleErrorCount = 0)
        }
    }

    fun increaseTotalErrorCount(){
        _spotUiState.update {
            it.copy(totalErrorCount = it.totalErrorCount + 1)
        }
    }

    fun decreaseTotalErrorCount(){
        _spotUiState.update {
            it.copy(totalErrorCount = it.totalErrorCount - 1)
        }
    }

    fun increaseSpotTitleErrorCount(){
        _spotUiState.update {
            it.copy(spotTitleErrorCount = it.spotTitleErrorCount + 1)
        }
    }

    fun decreaseSpotTitleErrorCount(){
        _spotUiState.update {
            it.copy(spotTitleErrorCount = it.spotTitleErrorCount - 1)
        }
    }






    fun reorderSpotImageList(
        currentIndex: Int,
        destinationIndex: Int
    ){
        val tempTrip = commonTripUiState.value.tripInfo.tempTrip
        val dateIndex = _spotUiState.value.currentDateIndex
        val spotIndex = _spotUiState.value.currentSpotIndex

        if (spotIndex != null) {

            val imagePathList =
                tempTrip?.dateList?.get(dateIndex)?.spotList?.get(spotIndex)?.imagePathList

            if (imagePathList != null) {
                val newImagePathList = imagePathList.toMutableList()

                //reorder
                val imagePath = newImagePathList[currentIndex]
                newImagePathList.removeAt(currentIndex)
                newImagePathList.add(destinationIndex, imagePath)

                //update ui state
                val newTempSpot =
                    tempTrip.dateList[dateIndex].spotList[spotIndex].copy(imagePathList = newImagePathList)
                val newTempSpotList = tempTrip.dateList[dateIndex].spotList.toMutableList()
                newTempSpotList[spotIndex] = newTempSpot
                val newTempDate = tempTrip.dateList[dateIndex].copy(spotList = newTempSpotList)
                val newTempDateList = tempTrip.dateList.toMutableList()
                newTempDateList[dateIndex] = newTempDate
                val newTempTrip = tempTrip.copy(dateList = newTempDateList)

                commonTripUiStateRepository._commonTripUiState.update {
                    it.copy(
                        tripInfo = it.tripInfo.copy(tempTrip = newTempTrip)
                    )
                }
            }
        }
    }
}