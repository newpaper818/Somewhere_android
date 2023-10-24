package com.newpaper.somewhere.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.newpaper.somewhere.R
import com.newpaper.somewhere.enumUtils.CurrencyType
import com.newpaper.somewhere.utils.getDateText
import com.newpaper.somewhere.utils.getNumToText
import com.newpaper.somewhere.viewModel.DateTimeFormat
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.Period

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
            id = id,
            orderId = orderId,
            unitOfCurrencyType = unitOfCurrencyType,
            titleText = titleText,
            dateList = dateList.map{ it.clone() },
            memoText = memoText,
            imagePathList = imagePathList,
            firstCreatedTime = firstCreatedTime,
            lastModifiedTime = lastModifiedTime
        )
    }

    // get =========================================================================================

    fun getStartDateText(dateTimeFormat: DateTimeFormat, includeYear: Boolean): String?{
        return if (dateList.isNotEmpty())
            getDateText(dateList.first().date, dateTimeFormat, includeYear = includeYear)
        else
            null
    }

    fun getEndDateText(dateTimeFormat: DateTimeFormat, includeYear: Boolean): String?{
        val lastEnabledDate = getLastEnabledDate()

        return if (lastEnabledDate != null) {
            getDateText(lastEnabledDate.date, dateTimeFormat, includeYear = includeYear)
        }
        else
            null
    }

    fun getLastEnabledDate(): Date?{
        if(dateList.isEmpty()){
            return null
        }
        else if (dateList.last().enabled){
            return dateList.last()
        }
        else {
            //binary search
            val lastEnabledDate: Date
            var firstIndex = 0
            var lastIndex = dateList.lastIndex

            var currIndex = (0 + lastIndex) / 2

            while (true) {
                if (dateList[currIndex].enabled) {
                    if (!dateList[currIndex + 1].enabled) {
                        lastEnabledDate = dateList[currIndex]
                        break
                    }
                    firstIndex = currIndex
                    currIndex = (firstIndex + lastIndex) / 2
                } else {
                    if (dateList[currIndex - 1].enabled) {
                        lastEnabledDate = dateList[currIndex - 1]
                        break
                    }
                    lastIndex = currIndex
                    currIndex = (firstIndex + lastIndex) / 2
                }
            }
            return lastEnabledDate
        }
    }

    fun getStartEndDateText(dateTimeFormat: DateTimeFormat, includeYear: Boolean): String?{
        val startDate = dateList.firstOrNull()
        val lastDate = getLastEnabledDate()

        //if both date is not null
        return if (startDate != null && lastDate != null){

            //if same date (1 day)
            if (startDate == lastDate){
                startDate.getDateText(dateTimeFormat, true)
            }

            //if not same date (2 days or over)
            else {
                val startDateText = startDate.getDateText(dateTimeFormat, true)

                val lastDateText = if (startDate.date.year == lastDate.date.year)
                    lastDate.getDateText(dateTimeFormat, false)
                else
                    lastDate.getDateText(dateTimeFormat, true)

                "$startDateText - $lastDateText"
            }
        }
        else
            null
    }

    @Composable
    fun getDurationText(): String?{
        val lastEnabledDate = getLastEnabledDate()

        if (dateList.isNotEmpty() && lastEnabledDate != null) {
            val period = Period.between(dateList.first().date, lastEnabledDate.date.plusDays(1))

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

        return "${unitOfCurrencyType.symbol} $budget"
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
        dateIndex: Int,
        spotIndex: Int,

        newDateIndex: Int,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    ){
        //set date
        val originalSpotList = showingTrip.dateList[dateIndex].spotList.toMutableList()
        originalSpotList[spotIndex] = originalSpotList[spotIndex].copy(date = showingTrip.dateList[newDateIndex].date)

        //add spot to new date
        val newSpotList = showingTrip.dateList[newDateIndex].spotList.toMutableList()
        newSpotList.add(originalSpotList[spotIndex])

        //remove spot from original
        originalSpotList.removeAt(spotIndex)

        //update spot list
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[dateIndex] = newDateList[dateIndex].copy(spotList = originalSpotList.toList())
        newDateList[newDateIndex] = newDateList[newDateIndex].copy(spotList = newSpotList.toList())

        //sort index
        newDateList[dateIndex].sortSpotListIndex()
        newDateList[newDateIndex].sortSpotListIndex()

        //sort iconText
        newDateList[dateIndex].sortSpotListIconText()
        newDateList[newDateIndex].sortSpotListIconText()

        //update state
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }
}
