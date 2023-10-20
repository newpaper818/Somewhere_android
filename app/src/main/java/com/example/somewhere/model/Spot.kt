package com.example.somewhere.model

import android.location.Location
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.somewhere.enumUtils.SpotType
import com.example.somewhere.enumUtils.TimeFormat
import com.example.somewhere.ui.tripScreenUtils.SEOUL_LOCATION
import com.example.somewhere.utils.getNumToText
import com.example.somewhere.utils.getTimeText
import com.example.somewhere.viewModel.DateTimeFormat
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.LocalTime


/**
 * TODO
 *
 * @property id immutable unique id
 * @property index mutable index of [Spot]. no duplication
 * @property iconText mutable order of spot (MOVE's [iconText] can duplicate)
 * @property spotType
 * @property iconId
 * @property iconColor
 * @property iconBackgroundColor
 * @property date
 * @property location
 * @property zoomLevel
 * @property titleText
 * @property imagePathList
 * @property startTime
 * @property endTime
 * @property budget
 * @property travelDistance
 * @property memo
 */
@JsonClass(generateAdapter = true)
data class Spot(
    val id: Int = 0,
    var index: Int = 0,
    var iconText: Int = 0,

    val spotType: SpotType = SpotType.TOUR,
    @DrawableRes
    val iconId: Int? = null,
    @ColorInt
    val iconColor: Int? = null,
    @ColorInt
    val iconBackgroundColor: Int = 0xffffffff.toInt(),

    val date: LocalDate,

    val location: LatLng? = null,
    val zoomLevel: Float? = null,

    val titleText: String? = null,
    val imagePathList: List<String> = listOf(),

    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    val budget: Float = 0.0f,
    val travelDistance: Float = 0.0f,
    val memo: String? = null
): Cloneable {
    public override fun clone(): Spot {
        return Spot(
            id = id,
            index = index,
            iconText = iconText,
            spotType = spotType,
            iconId = iconId,
            iconColor = iconColor,
            iconBackgroundColor = iconBackgroundColor,
            date = date,
            location = location,
            zoomLevel = zoomLevel,
            titleText= titleText,
            imagePathList = imagePathList,
            startTime = startTime,
            endTime = endTime,
            budget = budget,
            travelDistance = travelDistance,
            memo = memo
        )
    }

    // GET =========================================================================================
    @Composable
    fun getExpandedText(trip: Trip, isEditMode: Boolean): String {
        val categoryText = getSpotTypeText()
        val budgetText = getBudgetText(trip)
        val distanceText = getDistanceText()

        return if (isEditMode) "$categoryText / $budgetText / $distanceText"
                else "$categoryText / $budgetText / $distanceText"
    }

    @Composable
    fun getSpotTypeText(): String {
        return stringResource(id = spotType.textId)
    }

    fun getBudgetText(trip: Trip): String {
        return "${trip.unitOfCurrencyType.symbol} " +
                getNumToText(budget, trip.unitOfCurrencyType.numberOfDecimalPlaces)
    }

    fun getDistanceText(): String {
        return "${getNumToText(travelDistance, 2)}km"
    }

    fun getDateText(dateTimeFormat: DateTimeFormat, includeYear: Boolean): String {
        return com.example.somewhere.utils.getDateText(date, dateTimeFormat ,includeYear = includeYear)
    }

    fun getStartTimeText(timeFormat: TimeFormat): String? {
        return if (startTime != null) {
            getTimeText(startTime, timeFormat)
        } else {
            null
        }
    }

    fun getEndTimeText(timeFormat: TimeFormat): String? {
        return if (endTime != null) {
            getTimeText(endTime, timeFormat)

        } else {
            null
        }
    }

    /**
     * get previous spot of current spot.
     * if don't exist, get previous date's last spot
     */
    fun getPrevSpot(
        dateList: List<Date>,
        currentSpotList: List<Spot>,
        currentDateIndex: Int,
    ): Spot?{
        var prevSpot = currentSpotList.getOrNull(index - 1)
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
     * if don't exist, get next date's first spot
     */
    fun getNextSpot(
        dateList: List<Date>,
        currentSpotList: List<Spot>,
        currentDateIndex: Int,
    ): Spot?{

        var nextSpot = currentSpotList.getOrNull(index + 1)
        if (nextSpot == null){
            val nextDate = dateList.getOrNull(currentDateIndex + 1)
            if (nextDate != null){
                nextSpot = nextDate.spotList.firstOrNull()
            }
        }

        return nextSpot
    }

    fun prevSpotOrDateIsExist(
        currentDateIndex: Int,
    ): Boolean{
        return if (index > 0){
            true
        }
        else currentDateIndex > 0
    }

    fun nextSpotOrDateIsExist(
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
     * @param currentDateIndex this spot's date id
     * @return
     */
    fun getPrevLocation(
        dateList: List<Date>,
        currentDateIndex: Int,
    ): LatLng{
        //if this(current spot) location not null
        if (location != null)
            return location

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

    fun setImage(
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

    fun setSpotType(
        showingTrip: Trip,
        currentDateIndex: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newSpotType: SpotType
    ) {
        val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()

        val isMove = newSpotList[index].spotType.isMove() != newSpotType.isMove()

        if (newSpotType.isNotMove())
            newSpotList[index] = newSpotList[index].copy(spotType = newSpotType)
        else
            newSpotList[index] = newSpotList[index].copy(spotType = newSpotType, location = null, zoomLevel = null)

        //set iconText
        if (isMove) {
            var newIconText = if (index == 0) 0
                            else newSpotList[index - 1].iconText

            for (i in index until newSpotList.size) {
                if(newSpotList[i].spotType.isNotMove())
                    newIconText++

                newSpotList[i].iconText = newIconText
            }
        }


        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setBudget(
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


    fun setTitleText(
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

//    fun setDate(
//        showingTrip: Trip,
//        currentDateIndex: Int,
//        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
//
//        newDateId: Int
//    ){
//        val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
//        newSpotList[index] = newSpotList[index].copy(date = showingTrip.dateList[newDateId].date)
//        val newDateList = showingTrip.dateList.toMutableList()
//        newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
//        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
//    }

    fun setStartTime(
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

    fun setEndTime(
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

    fun setMemoText(
        showingTrip: Trip,
        currentDateIndex: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newMemoText: String?
    ) {
        val memoText: String? =
            if (newMemoText == "") null
            else newMemoText

        val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
        newSpotList[index] = newSpotList[index].copy(memo = memoText)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setLocation(
        showingTrip: Trip,
        currentDateIndex: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newLocation: LatLng?,
        newZoomLevel: Float?
    ) {
        val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
        newSpotList[index] = newSpotList[index].copy(location = newLocation, zoomLevel = newZoomLevel)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }


    // update ======================================================================================
    //TODO move to view model?
    fun updateDistance(
        showingTrip: Trip,
        currentDateIndex: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        spotFrom: LatLng?,
        spotTo: LatLng?
    ) {

        val newTravelDistance: Float

        if (spotFrom != null && spotTo != null) {
            val locationA = Location("a")
            locationA.latitude = spotFrom.latitude
            locationA.longitude = spotFrom.longitude

            val locationB = Location("b")
            locationB.latitude = spotTo.latitude
            locationB.longitude = spotTo.longitude

            newTravelDistance = locationA.distanceTo(locationB) / 1000
        }
        else{
            newTravelDistance = 0f
        }

        val newSpotList = showingTrip.dateList[currentDateIndex].spotList.toMutableList()
        newSpotList[index] = newSpotList[index].copy(travelDistance = newTravelDistance)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[currentDateIndex] = newDateList[currentDateIndex].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }
}
