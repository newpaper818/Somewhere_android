package com.newpaper.somewhere.core.utils.convert

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.LatLng
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.MyColor
import com.newpaper.somewhere.core.model.enums.SpotTypeGroup
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.R
import com.newpaper.somewhere.core.utils.getNumToText

// get =========================================================================================
@Composable
fun Date.getExpandedText(trip: Trip, isEditMode: Boolean):String{
    val budgetText = getTotalBudgetText(trip, 0)

    val spotCountText = "${getSpotTypeGroupCountExcludeMove(spotList)} " + stringResource(id = R.string.spots)
    val totalDistanceText = getTotalTravelDistanceText(0)

    var expandedText = "$spotCountText   $totalDistanceText   $budgetText"

    if (spotList == emptyList<Spot>())
        expandedText = stringResource(id = R.string.dates_no_plan)

//        if(!isEditMode)
//            expandedText += "\nSee more"

    return expandedText
}

private fun getSpotTypeGroupCountExcludeMove(
    spotList: List<Spot>
): Int{
    var count = 0
    for (spot in spotList){
        if (spot.spotType.isNotMove()){
            count++
        }
    }
    return count
}

fun Date.getTotalBudgetText(trip: Trip, numberOfDecimalPlaces: Int): String{
    val budget = getNumToText(getTotalBudget(), numberOfDecimalPlaces)

    return "${trip.unitOfCurrencyType.symbol} $budget"
}

fun Date.getTotalBudget(): Float{
    var total = 0.0f
    for (spot in spotList){
        total += spot.budget
    }
    return total
}

fun Date.getTotalTravelDistanceText(numberOfDecimalPlaces: Int): String{
    val distance = getNumToText(getTotalTravelDistance(), numberOfDecimalPlaces)
    return "${distance}km"
}

fun Date.getTotalTravelDistance(): Float{
    var total = 0.0f
    for (spot in spotList){
        total += spot.travelDistance
    }
    return total
}

fun Date.getSpotTypeGroupCount(spotTypeGroup: SpotTypeGroup): Int{
    var count = 0
    for (spot in spotList){
        if (spot.spotType.group == spotTypeGroup){
            count++
        }
    }
    return count
}



fun Date.getDateText(dateTimeFormat: DateTimeFormat, includeYear: Boolean = true): String{
    return com.newpaper.somewhere.core.utils.getDateText(date, dateTimeFormat, includeYear = includeYear)
}

/**
 * get spotList's last location.
 * If spotList is empty or don't have any location, return null
 *
 * @param startSpotIndex start searching index of spotList. if null, it will set to spotList last index.
 * @return get spotList's last location
 */
fun Date.getLastLocation(startSpotIndex: Int? = null): LatLng? {
    if (spotList.isNotEmpty()){
        var index = if (startSpotIndex == null) (spotList.size - 1)
        else {
            minOf(startSpotIndex, (spotList.size - 1))
        }

        while (index >= 0){
            val spot = spotList[index]
            if (spot.location != null)
                return spot.location
            else
                index -= 1
        }

        return null
    }
    else
        return null
}

// set =========================================================================================
fun Date.setTitleText(
    showingTrip: Trip,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    newTitleText: String?
) {
    val titleText: String? =
        if (newTitleText == "") null
        else newTitleText

    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[index] = newDateList[index].copy(titleText = titleText)
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Date.setMemoText(
    showingTrip: Trip,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    newMemoText: String?
) {
    val memoText: String? =
        if (newMemoText == "") null
        else newMemoText

    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[index] = newDateList[index].copy(memo = memoText)
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Date.setColor(
    showingTrip: Trip,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    newColor: MyColor
) {
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[index] = newDateList[index].copy(color = newColor)
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Date.setAllSpotDate(

){
    for (spot in spotList){
        spot.date = date
    }
}

//sort =========================================================================================
fun Date.sortSpotListIndex(){
    spotList.forEachIndexed { index, spot ->
        spot.index = index
    }
}

fun Date.sortSpotListIconText(){
    var iconText = 0

    spotList.forEach { spot ->
        if (spot.spotType.isNotMove())
            iconText++
        spot.iconText = iconText
    }
}