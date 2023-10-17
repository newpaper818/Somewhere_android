package com.example.somewhere.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.somewhere.R
import com.example.somewhere.enumUtils.CurrencyType
import com.example.somewhere.utils.getDateText
import com.example.somewhere.utils.getNumToText
import com.example.somewhere.viewModel.DateTimeFormat
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.Period
import java.util.Locale

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    var orderId: Int = 0,

    val unitOfCurrencyType: CurrencyType = CurrencyType.USD,

    val titleText: String? = null,

    val dateList: List<Date> = listOf(),
    val memoText: String? = null,
    val imagePathList: List<String> = listOf(),

    val firstCreatedTime: LocalDateTime = LocalDateTime.now(),
    val lastModifiedTime: LocalDateTime = LocalDateTime.now()
): Cloneable {
    public override fun clone(): Trip{
        return Trip(
            id, orderId, unitOfCurrencyType, titleText, dateList.map{ it.clone() }, memoText,
            imagePathList, firstCreatedTime, lastModifiedTime
        )
    }

    // get =========================================================================================

    fun getStartDateText(locale: Locale, dateTimeFormat: DateTimeFormat, includeYear: Boolean): String?{
        return if (dateList.isNotEmpty())
            getDateText(dateList.first().date, dateTimeFormat, includeYear = includeYear)
        else
            null
    }

    fun getEndDateText(locale: Locale, dateTimeFormat: DateTimeFormat, includeYear: Boolean): String?{
        return if (dateList.isNotEmpty())
            getDateText(dateList.last().date, dateTimeFormat, includeYear = includeYear)
        else
            null
    }

    fun getStartEndDateText(locale: Locale, dateTimeFormat: DateTimeFormat, includeYear: Boolean): String?{
        val startDateText = getStartDateText(locale, dateTimeFormat, includeYear)
        val endDateText = getEndDateText(locale, dateTimeFormat, includeYear)

        return if (startDateText == null || endDateText == null)
            null
        else
            if (startDateText == endDateText)
                startDateText
            else
                "$startDateText - $endDateText"
    }

    @Composable
    fun getDurationText(): String?{
        if (dateList.isNotEmpty()) {
            val period = Period.between(dateList.first().date, dateList.last().date.plusDays(1))

            val years = period.years
            val months = period.months
            val days = period.days

            val yearsText =
                if (years > 0){
                    if (years > 1)  stringResource(id = R.string.years, years)
                    else            stringResource(id = R.string.year, years)
                }
                else ""

            val monthsText =
                if (months > 0) {
                    if (months > 1) stringResource(id = R.string.months, months)
                    else            stringResource(id = R.string.month, months)
                }
                else ""

            val daysText =
                if (days > 0) {
                    if (days > 1)   stringResource(id = R.string.days, days)
                    else            stringResource(id = R.string.day, days)
                }
                else ""
            return "$yearsText $monthsText $daysText"
        }
        else
            return null
    }

    fun getTotalBudgetText(): String{
        val budget = getNumToText(getTotalBudget(), unitOfCurrencyType.numberOfDecimalPlaces)

        return "${unitOfCurrencyType.symbol} ${budget}"
    }

    private fun getTotalBudget(): Float{
        var totalBudget = 0.0f

        for (date in dateList){
            totalBudget += date.getTotalBudget()
        }

        return totalBudget
    }

    fun getTotalTravelDistanceText(): String{
        val distance = getNumToText(getTotalTravelDistance(), 2)

        return "${distance}km"
    }

    private fun getTotalTravelDistance(): Float{
        var totalDistance = 0.0f

        for (date in dateList){
            totalDistance += date.getTotalTravelDistance()
        }

        return totalDistance
    }


    fun getFirstLocation(): LatLng?{
        for (date in dateList){
            for (spot in date.spotList){
                if (spot.location != null)
                    return spot.location
            }
        }
        return null
    }

    fun getAllImagesPath(): List<String>{
        val tripImages = imagePathList
        var spotImages: List<String> = listOf()
        dateList.forEach { date ->
            date.spotList.forEach { spot ->
                spotImages = spotImages + spot.imagePathList
            }
        }

        return tripImages + spotImages
    }

    // set =========================================================================================
    fun setTitleText(
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
        newTitleText: String?
    ) {
        val titleText: String? =
            if (newTitleText == "") null
            else newTitleText

        updateTripState(true, this.copy(titleText = titleText))
    }

    fun setMemoText(
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
        newMemoText: String?
    ) {
        val memoText: String? =
            if (newMemoText == "") null
            else newMemoText

        updateTripState(true, this.copy(memoText = memoText))
    }

    fun setImage(
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
        newImgList: List<String>
    ) {
        updateTripState(true, this.copy(imagePathList = newImgList))
    }

    fun setCurrencyType(
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
        newCurrencyType: CurrencyType
    ) {
        updateTripState(true, this.copy(unitOfCurrencyType = newCurrencyType))
    }

    // =============================================================================================
    fun moveSpotToDate(
        showingTrip: Trip,
        dateId: Int,
        spotId: Int,

        newDateId: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    ){
        //set date
        val originalSpotList = showingTrip.dateList[dateId].spotList.toMutableList()
        originalSpotList[spotId] = originalSpotList[spotId].copy(date = showingTrip.dateList[newDateId].date)

        //add spot to new date
        val newSpotList = showingTrip.dateList[newDateId].spotList.toMutableList()
        newSpotList.add(originalSpotList[spotId])

        //remove spot from original
        originalSpotList.removeAt(spotId)

        //update spot list
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateId] = newDateList[dateId].copy(spotList = originalSpotList.toList())
        newDateList[newDateId] = newDateList[newDateId].copy(spotList = newSpotList.toList())

        //sort id
        newDateList[dateId].sortSpotListIndex()
        newDateList[newDateId].sortSpotListIndex()

        //update state
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }
}
