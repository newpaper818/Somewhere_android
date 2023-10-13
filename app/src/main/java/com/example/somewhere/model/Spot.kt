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
import java.util.Locale


@JsonClass(generateAdapter = true)
data class Spot(
    var id: Int = 0,
    var orderId: Int = 0,

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
    var travelDistance: Float = 0.0f,
    val memo: String? = null
): Cloneable {
    public override fun clone(): Spot {
        return Spot(
            id, orderId, spotType, iconId, iconColor, iconBackgroundColor, date, location, zoomLevel,
            titleText, imagePathList, startTime, endTime, budget, travelDistance, memo
        )
    }

    // get =========================================================================================
    @Composable
    fun getExpandedText(trip: Trip, isEditMode: Boolean): String {
        val categoryText = getSpotTypeText()
        val budgetText = getBudgetText(trip)
        val distanceText = getDistanceText()

        return if (isEditMode) "$categoryText | $budgetText | $distanceText"
                else "$categoryText | $budgetText | $distanceText\nSee more"
    }

    @Composable
    fun getSpotTypeText(): String {
        return stringResource(id = spotType.textId)
    }

    fun getBudgetText(trip: Trip): String {
        return "${trip.unitOfCurrencyType.symbol} ${
            getNumToText(
                budget,
                trip.unitOfCurrencyType.numberOfDecimalPlaces
            )
        }"
    }

    fun getDistanceText(): String {
        return "${getNumToText(travelDistance, 2)}km"
    }

    fun getDateText(locale: Locale, dateTimeFormat: DateTimeFormat, includeYear: Boolean): String {
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
     * get previous spot of current spot
     * if don't exist, get previous date's last spot
     */
    fun getPrevSpot(
        dateList: List<Date>,
        spotList: List<Spot>,
        dateId: Int,
    ): Spot?{
        var prevSpot = spotList.getOrNull(id - 1)
        if (prevSpot == null){
            val prevDate = dateList.getOrNull(dateId - 1)
            if (prevDate != null){
                prevSpot = prevDate.spotList.lastOrNull()
            }
        }

        return prevSpot
    }

    fun getNextSpot(
        dateList: List<Date>,
        spotList: List<Spot>,
        dateId: Int,
    ): Spot?{

        var nextSpot = spotList.getOrNull(id + 1)
        if (nextSpot == null){
            val nextDate = dateList.getOrNull(dateId + 1)
            if (nextDate != null){
                nextSpot = nextDate.spotList.firstOrNull()
            }
        }

        return nextSpot
    }

    /**
     * get last location form this spot.
     * searching backward.
     *
     * @param dateList trip's dateList
     * @param dateId this spot's date id
     * @return
     */
    fun getPrevLocation(
        dateList: List<Date>,
        dateId: Int,
    ): LatLng{
        //if this(current spot) location not null
        if (location != null)
            return location

        //if this(current spot) location null
        var dateIndex = dateId

        while (dateIndex >= 0){
            val startingSpotIndex = if (dateIndex == dateId) id
                                    else                     null

            val dateLastLocation = dateList[dateIndex].getLastLocation(startingSpotIndex)

            if (dateLastLocation != null)
                return dateLastLocation
            else
                dateIndex -= 1
        }

        return SEOUL_LOCATION
    }

    // set =========================================================================================

    fun setImage(
        showingTrip: Trip,
        dateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newImgList: List<String>
    ) {
        val newSpotList = showingTrip.dateList[dateId].spotList.toMutableList()
        newSpotList[id] = newSpotList[id].copy(imagePathList = newImgList)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setSpotType(
        showingTrip: Trip,
        dateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newSpotType: SpotType
    ) {
        val newSpotList = showingTrip.dateList[dateId].spotList.toMutableList()

        val reIndex = newSpotList[id].spotType.isMove() != newSpotType.isMove()

        if (newSpotType.isNotMove())
            newSpotList[id] = newSpotList[id].copy(spotType = newSpotType)
        else
            newSpotList[id] = newSpotList[id].copy(spotType = newSpotType, location = null, zoomLevel = null)

        //index orderId
        if (reIndex) {
            var newOrderId = if (id == 0) 0
                            else newSpotList[id - 1].orderId

            for (i in id until newSpotList.size) {
                if(newSpotList[i].spotType.isNotMove())
                    newOrderId++

                newSpotList[i].orderId = newOrderId
            }
        }


        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setBudget(
        showingTrip: Trip,
        dateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newBudget: Float
    ) {
        val newSpotList = showingTrip.dateList[dateId].spotList.toMutableList()
        newSpotList[id] = newSpotList[id].copy(budget = newBudget)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }


    fun setTitleText(
        showingTrip: Trip,
        dateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newTitleText: String?
    ) {
        val titleText: String? =
            if (newTitleText == "") null
            else newTitleText

        val newSpotList = showingTrip.dateList[dateId].spotList.toMutableList()
        newSpotList[id] = newSpotList[id].copy(titleText = titleText)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setDate(
        showingTrip: Trip,
        dateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newDateId: Int
    ){
        val newSpotList = showingTrip.dateList[dateId].spotList.toMutableList()
        newSpotList[id] = newSpotList[id].copy(date = showingTrip.dateList[newDateId].date)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setStartTime(
        showingTrip: Trip,
        dateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newStartTime: LocalTime?
    ) {
        val newSpotList = showingTrip.dateList[dateId].spotList.toMutableList()
        newSpotList[id] = newSpotList[id].copy(startTime = newStartTime)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setEndTime(
        showingTrip: Trip,
        dateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newEndTime: LocalTime?
    ) {
        val newSpotList = showingTrip.dateList[dateId].spotList.toMutableList()
        newSpotList[id] = newSpotList[id].copy(endTime = newEndTime)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setMemoText(
        showingTrip: Trip,
        dateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newMemoText: String?
    ) {
        val memoText: String? =
            if (newMemoText == "") null
            else newMemoText

        val newSpotList = showingTrip.dateList[dateId].spotList.toMutableList()
        newSpotList[id] = newSpotList[id].copy(memo = memoText)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setLocation(
        showingTrip: Trip,
        dateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

        newLocation: LatLng?,
        newZoomLevel: Float?
    ) {
        val newSpotList = showingTrip.dateList[dateId].spotList.toMutableList()
        newSpotList[id] = newSpotList[id].copy(location = newLocation, zoomLevel = newZoomLevel)
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = newSpotList.toList())
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }


    // update ======================================================================================
    //TODO move to view model?
    fun updateDistance(spotFrom: LatLng?, spotTo: LatLng?) {

        if (spotFrom != null && spotTo != null) {
            val locationA = Location("a")
            locationA.latitude = spotFrom.latitude
            locationA.longitude = spotFrom.longitude

            val locationB = Location("b")
            locationB.latitude = spotTo.latitude
            locationB.longitude = spotTo.longitude

            travelDistance = locationA.distanceTo(locationB) / 1000
        }
        else{
            travelDistance = 0f
        }
    }
}
