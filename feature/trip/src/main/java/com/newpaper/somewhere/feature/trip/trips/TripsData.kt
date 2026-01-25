package com.newpaper.somewhere.feature.trip.trips

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.enums.TripsDisplayMode
import com.newpaper.somewhere.core.model.tripData.Trip


internal data class TripsUiInfo(
    val use2Panes: Boolean = false,
    val spacerValue: Dp = 16.dp,
    val dateTimeFormat: DateTimeFormat = DateTimeFormat(),
    val internetEnabled: Boolean = true,
    val useBottomNavBar: Boolean = true,

    val firstLaunch: Boolean = true,
    private val _firstLaunchToFalse: () -> Unit = {},

    val loadingTrips: Boolean = false,
    private val _setIsLoadingTrips: (loadingTrips: Boolean) -> Unit = {},

    val tripsDisplayMode: TripsDisplayMode = TripsDisplayMode.ACTIVE,
    private val _setTripsDisplayMode: (tripsDisplayMode: TripsDisplayMode) -> Unit = {},

    val isTripsSortOrderByLatest: Boolean = true,
    private val _setIsTripsSortOrderByLatest: (isTripsSortOrderByLatest: Boolean) -> Unit = {},

    val isEditMode: Boolean = false,
    private val _setIsEditMode: (isEditMode: Boolean?) -> Unit = {},
){
    fun firstLaunchToFalse(){
        _firstLaunchToFalse() }
    fun setIsLoadingTrips(loadingTrips: Boolean){
        _setIsLoadingTrips(loadingTrips) }
    fun setTripsDisplayMode(tripsDisplayMode: TripsDisplayMode){
        _setTripsDisplayMode(tripsDisplayMode) }
    fun setIsTripsSortOrderByLatest(isTripsSortOrderByLatest: Boolean){
        _setIsTripsSortOrderByLatest(isTripsSortOrderByLatest) }
    fun setIsEditMode(isEditMode: Boolean?){
        _setIsEditMode(isEditMode) }
}

internal data class TripsData(
    val showingTrips: List<Trip> = listOf(),
    val showingSharedTrips: List<Trip> = listOf(),
    val glanceSpots: GlanceSpots = GlanceSpots(),
)

internal data class TripsDialog(
    val isShowingDialog: Boolean = false,
    val showTripCreationOptionsDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,

    private val _setShowTripCreationOptionsDialog: (Boolean) -> Unit = {},
    private val _showExitDialogToFalse: () -> Unit = {},
    private val _setShowDeleteDialog: (Boolean) -> Unit = {},

    val selectedTrip: Trip? = null,
    private val _setSelectedTrip: (Trip?) -> Unit = {}
){
    fun setShowTripCreationOptionsDialog(showTripCreationOptionsDialog: Boolean){
        _setShowTripCreationOptionsDialog(showTripCreationOptionsDialog) }
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
    private val _navigateToTripAi: () -> Unit = {},
    private val _navigateToGlanceSpot: (GlanceSpot) -> Unit = {},
    private val _navigateToSubscription: () -> Unit = {},
){
    fun onClickBackButton(){
        _onClickBackButton() }
    fun navigateToTrip(isNewTrip: Boolean, trip: Trip?){
        _navigateToTrip(isNewTrip, trip) }
    fun navigateToTripAi(){
        _navigateToTripAi() }
    fun navigateToGlanceSpot(glanceSpot: GlanceSpot){
        _navigateToGlanceSpot(glanceSpot) }
    fun navigateToSubscription(){
        _navigateToSubscription() }
}