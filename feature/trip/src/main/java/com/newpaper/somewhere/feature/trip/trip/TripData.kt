package com.newpaper.somewhere.feature.trip.trip

import android.net.Uri
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Trip

internal data class TripUiInfo(
    val use2Panes: Boolean = false,
    val spacerValue: Dp = 0.dp,
    val loadingTrip: Boolean = false,
    val dateTimeFormat: DateTimeFormat = DateTimeFormat(),
    val internetEnabled: Boolean = true,

    val isEditMode: Boolean = false,
    val setIsEditMode: (editMode: Boolean?) -> Unit = {},

    val showBottomSaveCancelBar: Boolean = false,
    val setShowBottomSaveCancelBar: (Boolean) -> Unit = {}
)

internal data class TripInfo(
    val originalTrip: Trip = Trip(id = 0, managerId = ""),
    val tempTrip: Trip = Trip(id = 0, managerId = ""),
    val isNewTrip: Boolean = false
)

internal data class TripErrorCount(
    val totalErrorCount: Int = 0,
    val dateTitleErrorCount: Int = 0,

    val increaseTotalErrorCount: () -> Unit = {},
    val decreaseTotalErrorCount: () -> Unit = {},
    val increaseDateTitleErrorCount: () -> Unit = {},
    val decreaseDateTitleErrorCount: () -> Unit = {}
)

internal data class TripDialog(
    val showExitDialog: Boolean = false,
    val showMemoDialog: Boolean = false,
    val showSetCurrencyDialog: Boolean = false,
    val showSetColorDialog: Boolean = false,

    val setShowExitDialog: (Boolean) -> Unit = {},
    val setShowMemoDialog: (Boolean) -> Unit = {},
    val setShowSetCurrencyDialog: (Boolean) -> Unit = {},
    val setShowSetColorDialog: (Boolean) -> Unit = {},

    val selectedDate: Date? = null,
    val setSelectedDate: (Date?) -> Unit = {}
)

internal data class TripNavigate(
    val navigateUp: () -> Unit = {},
    val navigateToInviteFriend: () -> Unit = {},
    val navigateToInvitedFriends: () -> Unit = {},
    val navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit = { _, _ -> },
    val navigateToDate: (dateIndex: Int) -> Unit = {},
    val navigateToTripMap: () -> Unit = {},
    val navigateUpAndDeleteNewTrip: (deleteTrip: Trip) -> Unit = {}
)

internal data class TripImage(
    val saveImageToInternalStorage: (index: Int, uri: Uri) -> String? = { _, _ -> null},
    val downloadImage: (imagePath: String, imageUserId: String, result: (Boolean) -> Unit) -> Unit = {_,_,_ ->},
    val addAddedImages: (imageFiles: List<String>) -> Unit = {},
    val addDeletedImages: (imageFiles: List<String>) -> Unit = {},
    val organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit = {},
    val reorderTripImageList: (currentIndex: Int, destinationIndex: Int) -> Unit = {_,_ ->},
)