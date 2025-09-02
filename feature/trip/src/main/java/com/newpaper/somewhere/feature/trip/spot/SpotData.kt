package com.newpaper.somewhere.feature.trip.spot

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.maps.android.compose.CameraPositionState
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip

internal data class SpotUiInfo(
    val use2Panes: Boolean = false,
    val spacerValue: Dp = 16.dp,
    val dateTimeFormat: DateTimeFormat = DateTimeFormat(),
    val internetEnabled: Boolean = true,
    val isDarkMapTheme: Boolean = false,

    val userSwiping: Boolean = false,
    private val _setUserSwiping: (userSwiping: Boolean) -> Unit = {},

    val isEditMode: Boolean = false,
    private val _setIsEditMode: (isEditMode: Boolean?) -> Unit = {}
){
    fun setIsEditMode(isEditMode: Boolean?){
        _setIsEditMode(isEditMode) }
    fun setUserSwiping(userSwiping: Boolean){
        _setUserSwiping(userSwiping) }
}

internal data class SpotData(
    val originalTrip: Trip = Trip(id = 0, managerId = ""),
    val tempTrip: Trip = Trip(id = 0, managerId = ""),

    val currentDateIndex: Int = 0,
    val currentSpotIndex: Int? = null,

    private val _setCurrentDateIndex: (currentDateIndex: Int) -> Unit = {},
    private val _setCurrentSpotIndex: (currentSpotIndex: Int?) -> Unit = {}
){
    fun setCurrentDateIndex(currentDateIndex: Int){
        _setCurrentDateIndex(currentDateIndex) }
    fun setCurrentSpotIndex(currentSpotIndex: Int?){
        _setCurrentSpotIndex(currentSpotIndex) }
}

internal data class SpotState @OptIn(ExperimentalFoundationApi::class) constructor(
    val scrollState: LazyListState,
    val progressBarState: LazyListState,
    val spotPagerState: PagerState,
)

internal data class SpotMap(
    val fusedLocationClient: FusedLocationProviderClient,
    val cameraPositionState: CameraPositionState,

    val mapSize: IntSize = IntSize.Zero,
    val userLocationEnabled: Boolean = false,
    val isMapExpand: Boolean = false,
    private val _onMapLoaded: () -> Unit,
    private val _setMapSize: (mapSize: IntSize) -> Unit = {},
    private val _setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit = {},
    private val _setIsMapExpanded: (isMapExpand: Boolean) -> Unit = {},

    val spotFrom: Spot? = null,
    val spotTo: Spot? = null
){
    fun onMapLoaded(){
        _onMapLoaded() }
    fun setMapSize(mapSize: IntSize){
        _setMapSize(mapSize) }
    fun setUserLocationEnabled(userLocationEnabled: Boolean){
        _setUserLocationEnabled(userLocationEnabled) }
    fun setIsMapExpanded(isMapExpand: Boolean){
        _setIsMapExpanded(isMapExpand) }
}

internal data class SpotErrorCount(
    val totalErrorCount: Int = 0,

    private val _increaseTotalErrorCount: () -> Unit = {},
    private val _decreaseTotalErrorCount: () -> Unit = {}
){
    fun increaseTotalErrorCount(){
        _increaseTotalErrorCount() }
    fun decreaseTotalErrorCount(){
        _decreaseTotalErrorCount() }
}

internal data class SpotDialog(
    val isShowingDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val showMemoDialog: Boolean = false,
    val showMoveDateDialog: Boolean = false,
    val showSetTimeDialog: Boolean = false,
    val showSetSpotTypeDialog: Boolean = false,
    val showSetBudgetDialog: Boolean = false,
    val showSetDistanceDialog: Boolean = false,
    val showSetLocationDialog: Boolean = false, //full screen dialog
    val showDeleteSpotDialog: Boolean = false,

    private val _setShowExitDialog: (Boolean) -> Unit = {},
    private val _setShowMemoDialog: (Boolean) -> Unit = {},
    private val _setShowMoveDateDialog: (Boolean) -> Unit = {},
    private val _setShowSetTimeDialog: (Boolean) -> Unit = {},
    private val _setShowSetSpotTypeDialog: (Boolean) -> Unit = {},
    private val _setShowSetBudgetDialog: (Boolean) -> Unit = {},
    private val _setShowSetDistanceDialog: (Boolean) -> Unit = {},
    private val _setShowSetLocationDialog: (Boolean) -> Unit = {},
    private val _setDeleteSpotDialog: (Boolean) -> Unit = {},

    val isStartTime: Boolean = true,
    private val _setIsStartTime: (isStartTime: Boolean) -> Unit = {}
){
    fun setShowExitDialog(showExitDialog: Boolean){
        _setShowExitDialog(showExitDialog) }
    fun setShowMemoDialog(showMemoDialog: Boolean){
        _setShowMemoDialog(showMemoDialog) }
    fun setShowMoveDateDialog(showMoveDateDialog: Boolean){
        _setShowMoveDateDialog(showMoveDateDialog) }
    fun setShowSetTimeDialog(showSetTimeDialog: Boolean){
        _setShowSetTimeDialog(showSetTimeDialog) }
    fun setShowSetSpotTypeDialog(showSetSpotTypeDialog: Boolean){
        _setShowSetSpotTypeDialog(showSetSpotTypeDialog) }
    fun setShowSetBudgetDialog(showSetBudgetDialog: Boolean){
        _setShowSetBudgetDialog(showSetBudgetDialog) }
    fun setShowSetDistanceDialog(showSetDistanceDialog: Boolean){
        _setShowSetDistanceDialog(showSetDistanceDialog) }
    fun setShowSetLocationDialog(showSetLocationDialog: Boolean){
        _setShowSetLocationDialog(showSetLocationDialog) }
    fun setShowDeleteSpotDialog(showDeleteSpotDialog: Boolean){
        _setDeleteSpotDialog(showDeleteSpotDialog) }
    fun setIsStartTime(isStartTime: Boolean){
        _setIsStartTime(isStartTime) }
}

internal data class SpotNavigate(
    private val _onClickBackButton: () -> Unit = {},
    private val _navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit = { _, _ -> },
){
    fun onClickBackButton(){
        _onClickBackButton() }
    fun navigateToImage(imageList: List<String>, initialImageIndex: Int){
        _navigateToImage(imageList, initialImageIndex) }
}

internal data class SpotImage(
    private val _saveImageToInternalStorage: (index: Int, uri: Uri) -> String? = { _, _ -> null},
    private val _downloadImage: (imagePath: String, imageUserId: String, result: (Boolean) -> Unit) -> Unit = {_,_,_ ->},
    private val _addAddedImages: (imageFiles: List<String>) -> Unit = {},
    private val _addDeletedImages: (imageFiles: List<String>) -> Unit = {},
    private val _organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit = {},
    private val _reorderSpotImageList: (currentIndex: Int, destinationIndex: Int) -> Unit = { _, _ ->}
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
    fun reorderSpotImageList(currentIndex: Int, destinationIndex: Int){
        _reorderSpotImageList(currentIndex, destinationIndex) }
}