package com.newpaper.somewhere.feature.trip.trip

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.convert.setAllSpotDate
import com.newpaper.somewhere.feature.trip.CommonTripUiStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

data class TripUiState(
    val loadingTrip: Boolean = true,

    val isShowingDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val showSetDateRangeDialog: Boolean = false,
    val showSetCurrencyDialog: Boolean = false,
    val showMemoDialog: Boolean = false,
    val showSetColorDialog: Boolean = false,
    val showSetTimeDialog: Boolean = false,
    val showSetSpotTypeDialog: Boolean = false,

    val selectedDate: Date? = null,
    val selectedSpot: Spot? = null,

    val totalErrorCount: Int = 0,
    val dateTitleErrorCount: Int = 0,
)

@HiltViewModel
class TripViewModel @Inject constructor(
    private val commonTripUiStateRepository: CommonTripUiStateRepository,
): ViewModel() {

    private val _tripUiState: MutableStateFlow<TripUiState> =
        MutableStateFlow(TripUiState())

    val tripUiState = _tripUiState.asStateFlow()

    private val commonTripUiState = commonTripUiStateRepository.commonTripUiState




    fun setLoadingTrip(loadingTrip: Boolean) {
        _tripUiState.update {
            it.copy(loadingTrip = loadingTrip)
        }
    }

    private fun setIsShowingDialog(){
        val isShowingDialog = _tripUiState.value.showExitDialog ||
                _tripUiState.value.showSetDateRangeDialog || _tripUiState.value.showSetCurrencyDialog ||
                _tripUiState.value.showMemoDialog || _tripUiState.value.showSetColorDialog ||
                _tripUiState.value.showSetTimeDialog || _tripUiState.value.showSetSpotTypeDialog

        _tripUiState.update {
            it.copy(isShowingDialog = isShowingDialog)
        }
    }

    fun setShowDateRangeDialog(showSetDateRangeDialog: Boolean) {
        _tripUiState.update {
            it.copy(showSetDateRangeDialog = showSetDateRangeDialog)
        }
        setIsShowingDialog()
    }

    //dialog
    fun setShowExitDialog(showExitDialog: Boolean) {
        _tripUiState.update {
            it.copy(showExitDialog = showExitDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetCurrencyDialog(showSetCurrencyDialog: Boolean) {
        _tripUiState.update {
            it.copy(showSetCurrencyDialog = showSetCurrencyDialog)
        }
        setIsShowingDialog()
    }

    fun setShowMemoDialog(showMemoDialog: Boolean) {
        _tripUiState.update {
            it.copy(showMemoDialog = showMemoDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetColorDialog(showSetColorDialog: Boolean) {
        _tripUiState.update {
            it.copy(showSetColorDialog = showSetColorDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetTimeDialog(showSetTimeDialog: Boolean) {
        _tripUiState.update {
            it.copy(showSetTimeDialog = showSetTimeDialog)
        }
        setIsShowingDialog()
    }

    fun setShowSetSpotTypeDialog(showSetSpotTypeDialog: Boolean) {
        _tripUiState.update {
            it.copy(showSetSpotTypeDialog = showSetSpotTypeDialog)
        }
        setIsShowingDialog()
    }

    fun setSelectedDate(date: Date?){
        _tripUiState.update {
            it.copy(selectedDate = date)
        }
    }

    fun setSelectedSpot(spot: Spot?){
        _tripUiState.update {
            it.copy(selectedSpot = spot)
        }
    }

    //error count
    fun initAllErrorCount(){
        _tripUiState.update {
            it.copy(totalErrorCount = 0, dateTitleErrorCount = 0)
        }
    }

    fun increaseTotalErrorCount(){
        _tripUiState.update {
            it.copy(totalErrorCount = it.totalErrorCount + 1)
        }
    }

    fun decreaseTotalErrorCount(){
        _tripUiState.update {
            it.copy(totalErrorCount = it.totalErrorCount - 1)
        }
    }

    fun increaseDateTitleErrorCount(){
        _tripUiState.update {
            it.copy(dateTitleErrorCount = it.dateTitleErrorCount + 1)
        }
    }

    fun decreaseDateTitleErrorCount(){
        _tripUiState.update {
            it.copy(dateTitleErrorCount = it.dateTitleErrorCount - 1)
        }
    }












    /**
     * @param updateTripState { newTrip -> commonTripViewModel.updateTripState(toTempTrip, newTrip)}
     */
    fun updateTripDurationAndTripState(
        toTempTrip: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        updateTripState: (Trip) -> Unit
    ){
        if (commonTripUiState.value.tripInfo.trip != null
            && commonTripUiState.value.tripInfo.tempTrip != null) {

            val currentTrip: Trip = if (toTempTrip) commonTripUiState.value.tripInfo.tempTrip!!
                                    else            commonTripUiState.value.tripInfo.trip!!

            var newTrip = currentTrip

            //if dateList is empty (first create)
            if (currentTrip.dateList.isEmpty()){
                val dateList: MutableList<Date> = mutableListOf()
                var currDate = startDate
                var index = 0

                val endDateAfter1Day = endDate.plusDays(1)

                while (currDate != endDateAfter1Day) {

                    //add Date in dateList
                    dateList.add(Date(id = index, index = index, date = currDate))
                    index++
                    currDate = currDate.plusDays(1)
                }

                newTrip = newTrip.copy(
                    startDate = startDate.toString(),
                    endDate = endDate.toString(),
                    dateList = dateList
                )
            }

            //if same date range with before
            else if (currentTrip.dateList.first().date == startDate && currentTrip.dateList.last().date == endDate)
                return

            //if different date range with before
            else{
                val dateList = currentTrip.dateList.toMutableList()

                var currDate = startDate
                var index = 0
                var maxId = 0

                for (date in dateList){
                    date.date = currDate
                    date.enabled = currDate <= endDate
                    date.id = index
                    date.index = index

                    if (maxId < date.id)
                        maxId = date.id

                    currDate = currDate.plusDays(1)
                    index++
                }

                //create new
                while (currDate <= endDate){
                    maxId++
                    dateList.add(Date(id = maxId, index = index, date = currDate))

                    currDate = currDate.plusDays(1)
                    index++
                }

                newTrip = newTrip.copy(
                    startDate = startDate.toString(),
                    endDate = endDate.toString(),
                    dateList = dateList
                )
            }

            //update all spot's date
            for (date in newTrip.dateList) {
                date.setAllSpotDate()
            }

            updateTripState(newTrip)
        }
    }


    fun reorderTripImageList(
        currentIndex: Int,
        destinationIndex: Int
    ){
        val imagePathList = commonTripUiState.value.tripInfo.tempTrip?.imagePathList

        if (imagePathList != null){
            val newImagePathList = imagePathList.toMutableList()

            //reorder
            val imagePath = newImagePathList[currentIndex]
            newImagePathList.removeAt(currentIndex)
            newImagePathList.add(destinationIndex, imagePath)

            //update ui state
            val newTempTrip = commonTripUiState.value.tripInfo.tempTrip!!.copy(imagePathList = newImagePathList)

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(tempTrip = newTempTrip)
                )
            }
        }
    }

    fun reorderDateList(
        currentIndex: Int,
        destinationIndex: Int
    ){
        if (commonTripUiState.value.tripInfo.tempTrip?.dateList != null){

            val newDateList = commonTripUiState.value.tripInfo.tempTrip!!.dateList.toMutableList()

            var currDate = newDateList.first().date

            //reorder
            val date = newDateList[currentIndex]
            newDateList.removeAt(currentIndex)
            newDateList.add(destinationIndex, date)

            //update date index
            newDateList.forEach{
                if (it.enabled) {
                    it.index = newDateList.indexOf(it)
                    it.date = currDate
                    currDate = currDate.plusDays(1)
                }
            }

            //update ui state
            val newTempTrip = commonTripUiState.value.tripInfo.tempTrip!!.copy(dateList = newDateList)

            commonTripUiStateRepository._commonTripUiState.update {
                it.copy(
                    tripInfo = it.tripInfo.copy(tempTrip = newTempTrip)
                )
            }
        }
    }
}