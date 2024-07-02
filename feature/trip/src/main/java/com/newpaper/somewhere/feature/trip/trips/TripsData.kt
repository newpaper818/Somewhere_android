package com.newpaper.somewhere.feature.trip.trips

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Trip


internal data class TripsUiInfo(
    val spacerValue: Dp = 16.dp,
    val dateTimeFormat: DateTimeFormat = DateTimeFormat(),
    val internetEnabled: Boolean = true,
    val useBottomNavBar: Boolean = true,

    val firstLaunch: Boolean = true,
    private val _firstLaunchToFalse: () -> Unit = {},

    val loadingTrips: Boolean = false,
    private val _setIsLoadingTrips: (loadingTrips: Boolean) -> Unit = {},

    val isEditMode: Boolean = false,
    private val _setIsEditMode: (isEditMode: Boolean?) -> Unit = {},
){
    fun firstLaunchToFalse(){
        _firstLaunchToFalse() }
    fun setIsLoadingTrips(loadingTrips: Boolean){
        _setIsLoadingTrips(loadingTrips) }
    fun setIsEditMode(isEditMode: Boolean?){
        _setIsEditMode(isEditMode) }
}

internal data class TripsData(
    val showingTrips: List<Trip> = listOf(),
    val showingSharedTrips: List<Trip> = listOf(),
    val glance: Glance = Glance(),
)

internal data class TripsDialog(
    val isShowingDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,

    private val _showExitDialogToFalse: () -> Unit = {},
    private val _setShowDeleteDialog: (Boolean) -> Unit = {},

    val selectedTrip: Trip? = null,
    private val _setSelectedTrip: (Trip?) -> Unit = {}
){
    fun showExitDialogToFalse(){
        _showExitDialogToFalse() }
    fun setShowDeleteDialog(showDeleteDialog: Boolean){
        _setShowDeleteDialog(showDeleteDialog) }
    fun setSelectedTrip(trip: Trip?){
        _setSelectedTrip(trip) }
}

internal data class TripsImage(
    private val _downloadImage: (imagePath: String, imageUserId: String, result:(Boolean) -> Unit) -> Unit = {_,_,_ ->},
    private val _addDeletedImages: (imageFiles: List<String>) -> Unit = {},
    private val _organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit = {},
){
    fun downloadImage(imagePath: String, imageUserId: String, result:(Boolean) -> Unit) {
        _downloadImage(imagePath, imageUserId, result) }
    fun addDeletedImages(imageFiles: List<String>) {
        _addDeletedImages(imageFiles) }
    fun organizeAddedDeletedImages(isClickSave: Boolean) {
        _organizeAddedDeletedImages(isClickSave) }
}

internal data class TripsEdit(
    private val _saveTrips: () -> Unit = {},
    private val _unSaveTempTrips: () -> Unit = {},
    private val _onDeleteTrip: (trip: Trip) -> Unit = {},
){
    fun saveTrips(){
        _saveTrips() }
    fun unSaveTempTrips(){
        _unSaveTempTrips() }
    fun onDeleteTrip(trip: Trip){
        _onDeleteTrip(trip) }
}

internal data class TripsNavigate(
    private val _onClickBackButton: () -> Unit = {},
    private val _navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit = { _, _ ->},
    private val _navigateToGlanceSpot: () -> Unit = {},
){
    fun onClickBackButton(){
        _onClickBackButton() }
    fun navigateToTrip(isNewTrip: Boolean, trip: Trip?){
        _navigateToTrip(isNewTrip, trip) }
    fun navigateToGlanceSpot(){
        _navigateToGlanceSpot() }
}