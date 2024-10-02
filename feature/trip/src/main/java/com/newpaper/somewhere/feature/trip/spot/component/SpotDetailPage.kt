package com.newpaper.somewhere.feature.trip.spot.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.DeleteItemButton
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.card.trip.DateTimeCard
import com.newpaper.somewhere.core.ui.card.trip.ImageCard
import com.newpaper.somewhere.core.ui.card.trip.InformationCard
import com.newpaper.somewhere.core.ui.card.trip.MemoCard
import com.newpaper.somewhere.core.ui.card.trip.TitleCard
import com.newpaper.somewhere.core.ui.card.trip.budgetItem
import com.newpaper.somewhere.core.ui.card.trip.spotTypeItem
import com.newpaper.somewhere.core.ui.card.trip.travelDistanceItem
import com.newpaper.somewhere.core.utils.convert.getBudgetText
import com.newpaper.somewhere.core.utils.convert.getDistanceText
import com.newpaper.somewhere.core.utils.convert.getSpotTypeText
import com.newpaper.somewhere.core.utils.convert.setImage
import com.newpaper.somewhere.core.utils.convert.setMemoText
import com.newpaper.somewhere.core.utils.convert.setTitleText
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.spot.SpotData
import com.newpaper.somewhere.feature.trip.spot.SpotDialog
import com.newpaper.somewhere.feature.trip.spot.SpotErrorCount
import com.newpaper.somewhere.feature.trip.spot.SpotImage
import com.newpaper.somewhere.feature.trip.spot.SpotNavigate
import com.newpaper.somewhere.feature.trip.spot.SpotUiInfo

@Composable
internal fun SpotDetailPage(
    spotUiInfo: SpotUiInfo,
    spotData: SpotData,
    errorCount: SpotErrorCount,
    dialog: SpotDialog,
    navigate: SpotNavigate,
    image: SpotImage,

    focusManager: FocusManager,
    deleteTime: (isStartTime: Boolean) -> Unit,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    modifier: Modifier = Modifier,
    minHeight: Dp = 0.dp
){
    val showingTrip = if (spotUiInfo.isEditMode) spotData.tempTrip
                        else            spotData.originalTrip

    val dateList = showingTrip.dateList

    val currentDateIndex = spotData.currentDateIndex
    val currentSpotIndex = spotData.currentSpotIndex ?: 0

    val currentDate = dateList[currentDateIndex]
    val currentSpot = currentDate.spotList.getOrNull(currentSpotIndex)

    val startPadding = if (spotUiInfo.use2Panes) spotUiInfo.spacerValue / 2
                        else spotUiInfo.spacerValue

    Column(
        modifier = modifier
            .padding(startPadding, 0.dp, spotUiInfo.spacerValue, 200.dp)
            .heightIn(min = minHeight - 200.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (currentSpot != null) {

            if (!spotUiInfo.use2Panes)
                MySpacerColumn(height = 16.dp)

            //title card
            TitleCard(
                isEditMode = spotUiInfo.isEditMode,
                titleText = currentSpot.titleText,
                onTitleChange = { newTitleText ->
                    currentSpot.setTitleText(
                        showingTrip,
                        currentDateIndex,
                        updateTripState,
                        newTitleText
                    )
                },
                focusManager = focusManager,
                isLongText = {
                    if (it) errorCount.increaseTotalErrorCount()
                    else errorCount.decreaseTotalErrorCount()
                }
            )

            //date time card
            DateTimeCard(
                date = currentDate,
                spot = currentSpot,
                isEditMode = spotUiInfo.isEditMode,
                dateTimeFormat = spotUiInfo.dateTimeFormat,
                onClickDate = { dialog.setShowMoveDateDialog(true) },
                onClickTime = { isStartTime ->
                    dialog.setIsStartTime(isStartTime)
                    dialog.setShowSetTimeDialog(true)
                },
                onClickDeleteTime = { isStartTime ->
                    deleteTime(isStartTime)
                }
            )

            //spotType / budget / distance card
            InformationCard(
                isEditMode = spotUiInfo.isEditMode,
                list = listOf(
                    spotTypeItem.copy(
                        text = currentSpot.getSpotTypeText(),
                        onClick = { dialog.setShowSetSpotTypeDialog(true) }
                    ),
                    budgetItem.copy(
                        text = currentSpot.getBudgetText(
                            showingTrip, showingTrip.unitOfCurrencyType.numberOfDecimalPlaces
                        ),
                        onClick = { dialog.setShowSetBudgetDialog(true) }
                    ),
                    travelDistanceItem.copy(
                        text = currentSpot.getDistanceText(2),
                        onClick =
                        if (currentSpot.spotType.isNotMove()) {
                            { dialog.setShowSetDistanceDialog(true) }
                        } else null
                    )
                )
            )

            MySpacerColumn(height = 16.dp)

            //memo card
            MemoCard(
                isEditMode = spotUiInfo.isEditMode,
                memoText = currentSpot.memoText,
                onMemoChanged = { newMemoText ->
                    currentSpot.setMemoText(
                        showingTrip,
                        currentDateIndex,
                        updateTripState,
                        newMemoText
                    )
                },
                isLongText = {
                    if (it) errorCount.increaseTotalErrorCount()
                    else errorCount.decreaseTotalErrorCount()
                },
                showMemoDialog = { dialog.setShowMemoDialog(true) }
            )

            MySpacerColumn(height = 16.dp)

            //image card
            ImageCard(
                imageUserId = showingTrip.managerId,
                internetEnabled = spotUiInfo.internetEnabled,
                isEditMode = spotUiInfo.isEditMode,
                imagePathList = currentSpot.imagePathList,
                onClickImage = { initialImageIndex ->
                    navigate.navigateToImage(currentSpot.imagePathList, initialImageIndex)
                },
                onAddImages = { imageFiles ->
                    image.addAddedImages(imageFiles)
                    val newImgList = currentSpot.imagePathList + imageFiles
                    currentSpot.setImage(showingTrip, currentDateIndex, updateTripState, newImgList)
                },
                deleteImage = { imageFile ->
                    image.addDeletedImages(listOf(imageFile))
                    val newImgList: MutableList<String> =
                        currentSpot.imagePathList.toMutableList()
                    newImgList.remove(imageFile)
                    currentSpot.setImage(showingTrip, currentDateIndex, updateTripState, newImgList)
                },
                isOverImage = {
                    if (it) errorCount.increaseTotalErrorCount()
                    else errorCount.decreaseTotalErrorCount()
                },
                reorderImageList = image::reorderSpotImageList,
                downloadImage = image::downloadImage,
                saveImageToInternalStorage = image::saveImageToInternalStorage
            )

            MySpacerColumn(height = 64.dp)

            //delete spot button
            DeleteItemButton(
                visible = spotUiInfo.isEditMode,
                text = stringResource(id = R.string.delete_spot),
                onClick = { dialog.setShowDeleteSpotDialog(true) }
            )
        }
    }
}