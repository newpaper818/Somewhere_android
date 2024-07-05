package com.newpaper.somewhere.feature.trip.date

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip

internal data class DateUiInfo(
    val use2Panes: Boolean = false,
    val spacerValue: Dp = 16.dp,
    val dateTimeFormat: DateTimeFormat = DateTimeFormat(),
    val internetEnabled: Boolean = true,
    val isFABExpanded: Boolean = true,

    val isEditMode: Boolean = false,
    private val _setIsEditMode: (isEditMode: Boolean?) -> Unit = {},
){
    fun setIsEditMode(isEditMode: Boolean?){
        _setIsEditMode(isEditMode) }
}

internal data class DateData(
    val originalTrip: Trip = Trip(id = 0, managerId = ""),
    val tempTrip: Trip = Trip(id = 0, managerId = ""),
    val showingTrip: Trip,
)

internal data class DateErrorCount(
    val totalErrorCount: Int = 0,
    val spotTitleErrorCount: Int = 0,

    private val _increaseTotalErrorCount: () -> Unit = {},
    private val _decreaseTotalErrorCount: () -> Unit = {},
    private val _increaseSpotTitleErrorCount: () -> Unit = {},
    private val _decreaseSpotTitleErrorCount: () -> Unit = {}
){
    fun increaseTotalErrorCount(){
        _increaseTotalErrorCount() }
    fun decreaseTotalErrorCount(){
        _decreaseTotalErrorCount() }
    fun increaseSpotTitleErrorCount(){
        _increaseSpotTitleErrorCount() }
    fun decreaseSpotTitleErrorCount(){
        _decreaseSpotTitleErrorCount() }
}

internal data class DateDialog(
    val isShowingDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val showMemoDialog: Boolean = false,
    val showSetColorDialog: Boolean = false,
    val showSetTimeDialog: Boolean = false,
    val showSetSpotTypeDialog: Boolean = false,

    private val _setShowExitDialog: (Boolean) -> Unit = {},
    private val _setShowMemoDialog: (Boolean) -> Unit = {},
    private val _setShowSetColorDialog: (Boolean) -> Unit = {},
    private val _setShowSetTimeDialog: (Boolean) -> Unit = {},
    private val _setShowSetSpotTypeDialog: (Boolean) -> Unit = {},

    val selectedSpot: Spot? = null,
    private val _setSelectedSpot: (Spot?) -> Unit = {}
){
    fun setShowExitDialog(showExitDialog: Boolean){
        _setShowExitDialog(showExitDialog) }
    fun setShowMemoDialog(showMemoDialog: Boolean){
        _setShowMemoDialog(showMemoDialog) }
    fun setShowSetColorDialog(showSetColorDialog: Boolean){
        _setShowSetColorDialog(showSetColorDialog) }
    fun setShowSetTimeDialog(showSetTimeDialog: Boolean){
        _setShowSetTimeDialog(showSetTimeDialog) }
    fun setSelectedDate(selectedSpot: Spot?){
        _setSelectedSpot(selectedSpot) }
    fun setShowSetSpotTypeDialog(showSetSpotTypeDialog: Boolean){
        _setShowSetSpotTypeDialog(showSetSpotTypeDialog) }
}

internal data class DateNavigate(
    private val _navigateUp: () -> Unit = {},
    private val _navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit = {_,_ ->},
    private val _navigateToDateMap: () -> Unit = {}
){
    fun navigateUp(){
        _navigateUp() }
    fun navigateToSpot(dateIndex: Int, spotIndex: Int){
        _navigateToSpot(dateIndex, spotIndex) }
    fun navigateToDateMap(){
        _navigateToDateMap() }
}

internal data class DateImage(
    private val _addDeletedImages: (imageFiles: List<String>) -> Unit = {},
    private val _organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit = {},
){
    fun addDeletedImages(imageFiles: List<String>){
        _addDeletedImages(imageFiles) }
    fun organizeAddedDeletedImages(isClickSave: Boolean){
        _organizeAddedDeletedImages(isClickSave) }
}