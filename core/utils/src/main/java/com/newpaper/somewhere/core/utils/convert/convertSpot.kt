package com.newpaper.somewhere.core.utils.convert

import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.LatLng
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.enums.SpotType
import com.newpaper.somewhere.core.model.enums.TimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.getNumToText
import com.newpaper.somewhere.core.utils.getTimeText
import java.time.LocalTime

/**seoul location, initial location*/
val SEOUL_LOCATION = LatLng(37.55, 126.98)

// GET =========================================================================================
@Composable
fun Spot.getExpandedText(trip: Trip, isEditMode: Boolean): String {
    val categoryText = getSpotTypeText()
    val budgetText = getBudgetText(trip, 0)
    val distanceText = getDistanceText(0)

    return if (isEditMode) "$categoryText   $distanceText   $budgetText"
    else "$categoryText   $distanceText   $budgetText"
}

@Composable
fun Spot.getSpotTypeText(): String {
    return stringResource(id = spotType.textId)
}

fun Spot.getBudgetText(trip: Trip, numberOfDecimalPlaces: Int): String {
    return "${trip.unitOfCurrencyType.symbol} " +
            getNumToText(budget, numberOfDecimalPlaces)
}

fun Spot.getDistanceText(numberOfDecimalPlaces: Int): String {
    return "${getNumToText(travelDistance, numberOfDecimalPlaces)}km"
}

fun Spot.getDateText(dateTimeFormat: DateTimeFormat, includeYear: Boolean): String {
    return com.newpaper.somewhere.core.utils.getDateText(date, dateTimeFormat ,includeYear = includeYear)
}

fun Spot.getStartTimeText(timeFormat: TimeFormat): String? {
    return startTime?.let { getTimeText(it, timeFormat) }
}

fun Spot.getEndTimeText(timeFormat: TimeFormat): String? {
    return endTime?.let { getTimeText(it, timeFormat) }
}

fun Spot.isFirstSpot(
    dateId: Int,
    dateList: List<Date>,
    spotList: List<Spot>
): Boolean{
    return spotType.isNotMove()
            && this == (spotList.firstOrNull() ?: false)
            && getPrevSpot(dateList, dateId)?.spotType?.isNotMove() ?: true
}

fun Spot.isLastSpot(
    dateId: Int,
    dateList: List<Date>,
    spotList: List<Spot>
): Boolean{
    return spotType.isNotMove()
            && this == (spotList.lastOrNull() ?: false)
            && getNextSpot(dateList, dateId)?.spotType?.isNotMove() ?: true
}

/**
 * get previous spot of current spot.
 * if don't exist(first index), get previous date's last spot
 */
fun Spot.getPrevSpot(
    dateList: List<Date>,
    currentDateIndex: Int,
): Spot?{

    val currentSpotList = dateList.getOrNull(currentDateIndex)?.spotList
    var prevSpot = currentSpotList?.getOrNull(index - 1)
    if (prevSpot == null){
        val prevDate = dateList.getOrNull(currentDateIndex - 1)
        if (prevDate != null){
            prevSpot = prevDate.spotList.lastOrNull()
        }
    }

    return prevSpot
}

/**
 * get next spot of current spot.
 * if don't exist(last index), get next date's first spot
 */
fun Spot.getNextSpot(
    dateList: List<Date>,
    currentDateIndex: Int,
): Spot?{
    val currentSpotList = dateList.getOrNull(currentDateIndex)?.spotList
    var nextSpot = currentSpotList?.getOrNull(index + 1)
    if (nextSpot == null){
        val nextDate = dateList.getOrNull(currentDateIndex + 1)
        if (nextDate != null){
            nextSpot = nextDate.spotList.firstOrNull()
        }
    }

    return nextSpot
}

/**
 * get Pair(previous spot, prev spot's date index) of current spot.
 * if don't exist(first index), get previous date's last spot
 */
fun Spot.getPrevSpotWithDateIndex(
    dateList: List<Date>,
    currentDateIndex: Int,
): Pair<Spot?, Int>{
    var dateIndex = currentDateIndex

    val currentSpotList = dateList.getOrNull(dateIndex)?.spotList
    var prevSpot = currentSpotList?.getOrNull(index - 1)
    if (prevSpot == null){
        dateIndex--
        val prevDate = dateList.getOrNull(dateIndex)
        if (prevDate != null){
            prevSpot = prevDate.spotList.lastOrNull()
        }
    }

    return Pair(prevSpot, dateIndex)
}

/**
 * get Pair(next spot, next spot's date index) of current spot.
 * if don't exist(last index), get next date's first spot
 */
fun Spot.getNextSpotWithDateIndex(
    dateList: List<Date>,
    currentDateIndex: Int,
): Pair<Spot?, Int>{
    var dateIndex = currentDateIndex

    val currentSpotList = dateList.getOrNull(dateIndex)?.spotList
    var nextSpot = currentSpotList?.getOrNull(index + 1)
    if (nextSpot == null){
        dateIndex++
        val nextDate = dateList.getOrNull(dateIndex)
        if (nextDate != null){
            nextSpot = nextDate.spotList.firstOrNull()
        }
    }

    return Pair(nextSpot, dateIndex)
}

fun Spot.prevSpotOrDateIsExist(
    currentDateIndex: Int,
): Boolean{
    return if (index > 0){
        true
    }
    else currentDateIndex > 0
}

fun Spot.nextSpotOrDateIsExist(
    currentSpotList: List<Spot>,
    currentDateList: List<Date>,
    currentDateIndex: Int,
): Boolean{
    return if (index < currentSpotList.lastIndex){
        true
    }
    else currentDateIndex < currentDateList.lastIndex
}

/**
 * get last location form this spot.
 * searching backward.
 *
 * @param dateList trip's dateList
 * @param currentDateIndex this spot's date index
 * @return
 */
fun Spot.getPrevLocation(
    dateList: List<Date>,
    currentDateIndex: Int,
): LatLng {
    //if this(current spot) location not null
    if (location != null)
        return location as LatLng

    //if this(current spot) location null
    var dateIndex = currentDateIndex

    while (dateIndex >= 0){
        val startingSpotIndex = if (dateIndex == currentDateIndex) index
                                else                     null

        val dateLastLocation = dateList[dateIndex].getLastLocation(startingSpotIndex)

        if (dateLastLocation != null)
            return dateLastLocation
        else
            dateIndex -= 1
    }

    //if location doesn't exist on this Trip, return seoul location
    return SEOUL_LOCATION
}

// set =========================================================================================

fun Spot.setImage(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newImgList: List<String>
) {
    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(imagePathList = newImgList)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Spot.setSpotType(
    showingTrip: Trip,
    dateList: List<Date>,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newSpotType: SpotType
) {
    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()

    //not MOVE -> MOVE or MOVE -> not MOVE
    val setIconText = newSpotList[index].spotType.isMove() != newSpotType.isMove()

    if (newSpotType.isNotMove() && setIconText)
        newSpotList[index] = newSpotList[index].copy(spotType = newSpotType, travelDistance = 0f)
    else if (newSpotType.isNotMove() && !setIconText)
        newSpotList[index] = newSpotList[index].copy(spotType = newSpotType)
    else {
        //update travel distance
        val travelDistance = getTravelDistance(dateList, currentDateIndex)
        newSpotList[index] = newSpotList[index].copy(
            spotType = newSpotType, location = null, zoomLevel = null, travelDistance = travelDistance ?: 0f)
    }

    //set iconText
    if (setIconText) {
        var newIconText = if (index == 0) 0
        else newSpotList[index - 1].iconText

        for (i in index .. newSpotList.lastIndex) {
            if(newSpotList[i].spotType.isNotMove())
                newIconText++

            newSpotList[i].iconText = newIconText
        }
    }


    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Spot.setBudget(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newBudget: Float
) {
    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(budget = newBudget)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Spot.setTravelDistance(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newTravelDistance: Float
) {
    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(travelDistance = newTravelDistance)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}


fun Spot.setTitleText(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newTitleText: String?
) {
    val newTitleText1: String? =
        if (newTitleText == "") null
        else newTitleText

    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(titleText = newTitleText1)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Spot.setStartTime(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newStartTime: LocalTime?
) {
    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(startTime = newStartTime)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Spot.setEndTime(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newEndTime: LocalTime?
) {
    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(endTime = newEndTime)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Spot.setMemoText(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newMemoText: String?
) {
    val memoText: String? =
        if (newMemoText == "") null
        else newMemoText

    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(memoText = memoText)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

fun Spot.setLocation(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newLocation: LatLng?,
    newZoomLevel: Float?
){
    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(location = newLocation, zoomLevel = newZoomLevel)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))

}

//==============================================================================================
fun Spot.getTravelDistance(
    dateList: List<Date>,
    dateIndex: Int
): Float?{
    val prevSpot = getPrevSpot(dateList, dateIndex)
    val nextSpot = getNextSpot(dateList, dateIndex)

    return calcTravelDistanceFrom2Location(prevSpot?.location, nextSpot?.location)
}

/**
 * if spot is MOVE, get travel distance from prev spot and next spot
 *
 * @param showingTrip
 * @param currentDateIndex
 * @return
 */
fun Spot.updateTravelDistanceCurrentSpot(
    showingTrip: Trip,
    currentDateIndex: Int,
): Trip{
    var newTrip = showingTrip

    if (spotType.isMove()){
        val newTravelDistance = getTravelDistance(newTrip.dateList, currentDateIndex)
        Log.d("aaa", "            is move / $newTravelDistance")

        //update travel distance
        val newSpotList1 = newTrip.dateList[currentDateIndex].spotList.toMutableList()
        newSpotList1[index] = newSpotList1[index].copy(travelDistance = newTravelDistance ?: 0f)
        val newDateList1 = newTrip.dateList.toMutableList()
        newDateList1[currentDateIndex] = newDateList1[currentDateIndex].copy(spotList = newSpotList1.toList())
        newTrip = newTrip.copy(dateList = newDateList1.toList())
    }

    return newTrip
}

/**
 * update travel distances: current spot's previous spot, current spot's next spot
 *
 * @param showingTrip
 * @param currentDateIndex
 * @return
 */
fun Spot.updateTravelDistancePrevNextSpot(
    showingTrip: Trip,
    currentDateIndex: Int,
): Trip{
    var newTrip = showingTrip

    //update travel distance
    val (prevSpot, prevSpotDateIndex) = newTrip.dateList[currentDateIndex].spotList[index].getPrevSpotWithDateIndex(newTrip.dateList, currentDateIndex)
    if (prevSpot?.spotType?.isMove() == true){
        newTrip = prevSpot.updateTravelDistanceCurrentSpot(newTrip, prevSpotDateIndex)
    }

    val (nextSpot, nextSpotDateIndex) = newTrip.dateList[currentDateIndex].spotList[index].getNextSpotWithDateIndex(newTrip.dateList, currentDateIndex)
    if (nextSpot?.spotType?.isMove() == true){
        newTrip = nextSpot.updateTravelDistanceCurrentSpot(newTrip, nextSpotDateIndex)
    }

    return newTrip
}

/**
 * update travel distances: current spot, current spot's previous spot, current spot's next spot
 *
 * @param showingTrip
 * @param currentDateIndex
 * @return
 */
fun Spot.updateTravelDistanceCurrPrevNextSpot(
    showingTrip: Trip,
    currentDateIndex: Int,
): Trip{
    var newTrip = showingTrip

    //update travel distance
    if (newTrip.dateList[currentDateIndex].spotList[index].spotType.isMove()){
        Log.d("aaa", "     1")
        newTrip = newTrip.dateList[currentDateIndex].spotList[index].updateTravelDistanceCurrentSpot(newTrip, currentDateIndex)
    }

    val (prevSpot, prevSpotDateIndex) = newTrip.dateList[currentDateIndex].spotList[index].getPrevSpotWithDateIndex(newTrip.dateList, currentDateIndex)
    if (prevSpot?.spotType?.isMove() == true){
        Log.d("aaa", "     2")
        newTrip = prevSpot.updateTravelDistanceCurrentSpot(newTrip, prevSpotDateIndex)
    }

    val (nextSpot, nextSpotDateIndex) = newTrip.dateList[currentDateIndex].spotList[index].getNextSpotWithDateIndex(newTrip.dateList, currentDateIndex)
    if (nextSpot?.spotType?.isMove() == true){
        Log.d("aaa", "     3")
        newTrip = nextSpot.updateTravelDistanceCurrentSpot(newTrip, nextSpotDateIndex)
    }

    return newTrip
}

fun Spot.setLocationAndUpdateTravelDistance(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    newLocation: LatLng?,
    newZoomLevel: Float?,
) {
    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(location = newLocation, zoomLevel = newZoomLevel)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    var newTrip = showingTrip.copy(dateList = newDateList.toList())
    updateTripState(true, newTrip)

    //update travel distance
    newTrip = updateTravelDistancePrevNextSpot(newTrip, currentDateIndex)
    updateTripState(true, newTrip)
}


// update ======================================================================================
//TODO move to view model? delete?
fun Spot.updateDistance(
    showingTrip: Trip,
    currentDateIndex: Int,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    spotFrom: LatLng?,
    spotTo: LatLng?
) {
    val newTravelDistance = calcTravelDistanceFrom2Location(spotFrom, spotTo) ?: 0f

    val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
    newSpotList[index] = newSpotList[index].copy(travelDistance = newTravelDistance)
    val newDateList = showingTrip.dateList.toMutableList()
    newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
    updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
}

private fun Spot.calcTravelDistanceFrom2Location(
    from: LatLng?,
    to: LatLng?
): Float?{
    return if (from != null && to != null) {
        val locationA = Location("a")
        locationA.latitude = from.latitude
        locationA.longitude = from.longitude

        val locationB = Location("b")
        locationB.latitude = to.latitude
        locationB.longitude = to.longitude

        locationA.distanceTo(locationB) / 1000
    }
    else{
        null
    }
}