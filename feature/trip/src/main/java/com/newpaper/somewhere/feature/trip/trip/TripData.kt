package com.newpaper.somewhere.feature.trip.trip

import android.net.Uri
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip

internal data class TripUiInfo(
    val use2Panes: Boolean = false,
    val spacerValue: Dp = 16.dp,
    val useBlurEffect: Boolean = true,
    val loadingTrip: Boolean = false,
    val dateTimeFormat: DateTimeFormat = DateTimeFormat(),
    val internetEnabled: Boolean = true,

    val isEditMode: Boolean = false,
    private val _setIsEditMode: (isEditMode: Boolean?) -> Unit = {}
){
    fun setIsEditMode(isEditMode: Boolean?){
        _setIsEditMode(isEditMode) }
}

internal data class TripData(
    val originalTrip: Trip = Trip(id = 0, managerId = ""),
    val tempTrip: Trip = Trip(id = 0, managerId = ""),
    val isNewTrip: Boolean = false
)

internal data class TripErrorCount(
    val totalErrorCount: Int = 0,
    val dateTitleErrorCount: Int = 0,

    private val _increaseTotalErrorCount: () -> Unit = {},
    private val _decreaseTotalErrorCount: () -> Unit = {},
    private val _increaseDateTitleErrorCount: () -> Unit = {},
    private val _decreaseDateTitleErrorCount: () -> Unit = {}
){
    fun increaseTotalErrorCount(){
        _increaseTotalErrorCount() }
    fun decreaseTotalErrorCount(){
        _decreaseTotalErrorCount() }
    fun increaseDateTitleErrorCount(){
        _increaseDateTitleErrorCount() }
    fun decreaseDateTitleErrorCount(){
        _decreaseDateTitleErrorCount() }
}

internal data class TripDialog(
    val isShowingDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val showSetDateRangeDialog: Boolean = false,
    val showMemoDialog: Boolean = false,
    val showSetCurrencyDialog: Boolean = false,
    val showSetColorDialog: Boolean = false,
    val showSetTimeDialog: Boolean = false,
    val showSetSpotTypeDialog: Boolean = false,

    private val _setShowExitDialog: (Boolean) -> Unit = {},
    private val _setShowDateRangeDialog: (Boolean) -> Unit = {},
    private val _setShowMemoDialog: (Boolean) -> Unit = {},
    private val _setShowSetCurrencyDialog: (Boolean) -> Unit = {},
    private val _setShowSetColorDialog: (Boolean) -> Unit = {},
    private val _setShowSetTimeDialog: (Boolean) -> Unit = {},
    private val _setShowSetSpotTypeDialog: (Boolean) -> Unit = {},

    val selectedDate: Date? = null,
    private val _setSelectedDate: (Date?) -> Unit = {},

    val selectedSpot: Spot? = null,
    private val _setSelectedSpot: (Spot?) -> Unit = {}
){
    fun setShowExitDialog(showExitDialog: Boolean){
        _setShowExitDialog(showExitDialog) }
    fun setShowDateRangeDialog(showSetDateRangeDialog: Boolean){
        _setShowDateRangeDialog(showSetDateRangeDialog) }
    fun setShowMemoDialog(showMemoDialog: Boolean){
        _setShowMemoDialog(showMemoDialog) }
    fun setShowSetCurrencyDialog(showSetCurrencyDialog: Boolean){
        _setShowSetCurrencyDialog(showSetCurrencyDialog) }
    fun setShowSetColorDialog(showSetColorDialog: Boolean){
        _setShowSetColorDialog(showSetColorDialog) }
    fun setShowSetTimeDialog(showSetTimeDialog: Boolean){
        _setShowSetTimeDialog(showSetTimeDialog) }
    fun setShowSetSpotTypeDialog(showSetSpotTypeDialog: Boolean){
        _setShowSetSpotTypeDialog(showSetSpotTypeDialog) }

    fun setSelectedDate(selectedDate: Date?){
        _setSelectedDate(selectedDate) }
    fun setSelectedSpot(selectedSpot: Spot?){
        _setSelectedSpot(selectedSpot) }
}

internal data class TripNavigate(
    private val _navigateUp: () -> Unit = {},
    private val _navigateToShareTrip: (imageList: List<String>, initialImageIndex: Int) -> Unit = { _, _ -> },
    private val _navigateToInviteFriend: () -> Unit = {},
    private val _navigateToInvitedFriends: () -> Unit = {},
    private val _navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit = { _, _ -> },
    private val _navigateToDate: (dateIndex: Int) -> Unit = {},
    private val _navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit = { _, _ -> },
    private val _navigateToTripMap: () -> Unit = {},
    private val _navigateUpAndDeleteNewTrip: (deleteTrip: Trip) -> Unit = {}
){
    fun navigateUp(){
        _navigateUp() }
    fun navigateToShareTrip(imageList: List<String>, initialImageIndex: Int){
        _navigateToShareTrip(imageList, initialImageIndex) }
    fun navigateToInviteFriend(){
        _navigateToInviteFriend() }
    fun navigateToInvitedFriends(){
        _navigateToInvitedFriends() }
    fun navigateToImage(imageList: List<String>, initialImageIndex: Int){
        _navigateToImage(imageList, initialImageIndex) }
    fun navigateToDate(dateIndex: Int){
        _navigateToDate(dateIndex) }
    fun navigateToSpot(dateIndex: Int, spotIndex: Int){
        _navigateToSpot(dateIndex, spotIndex) }
    fun navigateToTripMap(){
        _navigateToTripMap() }
    fun navigateUpAndDeleteNewTrip(deleteTrip: Trip){
        _navigateUpAndDeleteNewTrip(deleteTrip) }
}

internal data class TripImage(
    private val _saveImageToInternalStorage: (index: Int, uri: Uri) -> String? = { _, _ -> null},
    private val _downloadImage: (imagePath: String, imageUserId: String, result: (Boolean) -> Unit) -> Unit = {_,_,_ ->},
    private val _addAddedImages: (imageFiles: List<String>) -> Unit = {},
    private val _addDeletedImages: (imageFiles: List<String>) -> Unit = {},
    private val _organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit = {},
    private val _reorderTripImageList: (currentIndex: Int, destinationIndex: Int) -> Unit = {_,_ ->}
){
    fun saveImageToInternalStorage(index: Int, uri: Uri): String?{
        return _saveImageToInternalStorage(index, uri) }
    fun downloadImage(imagePath: String, imageUserId: String, result: (Boolean) -> Unit){
        _downloadImage(imagePath, imageUserId, result) }
    fun addAddedImages(imageFiles: List<String>){
        _addAddedImages(imageFiles) }
    fun addDeletedImages(imageFiles: List<String>){
        _addDeletedImages(imageFiles) }
    fun organizeAddedDeletedImages(isClickSave: Boolean){
        _organizeAddedDeletedImages(isClickSave) }
    fun reorderTripImageList(currentIndex: Int, destinationIndex: Int){
        _reorderTripImageList(currentIndex, destinationIndex) }
}