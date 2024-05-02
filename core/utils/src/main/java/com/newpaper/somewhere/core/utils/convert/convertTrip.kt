package com.newpaper.somewhere.core.utils.convert

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.LatLng
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.CurrencyType
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.R
import com.newpaper.somewhere.core.utils.getDateText
import com.newpaper.somewhere.core.utils.getNumToText
import java.time.LocalDate
import java.time.Period


//same with firestore-common/stringValue
/**managerId*/
const val MANAGER_ID = "managerId"

/**editable*/
const val EDITABLE = "editable"

// get =========================================================================================

fun Trip.getStartDateText(dateTimeFormat: DateTimeFormat, includeYear: Boolean): String?{
    return if (dateList.isNotEmpty())
        getDateText(dateList.first().date, dateTimeFormat, includeYear = includeYear)
    else
        null
}

fun Trip.getEndDateText(dateTimeFormat: DateTimeFormat, includeYear: Boolean): String?{
    val lastEnabledDate = getLastEnabledDate()

    return if (lastEnabledDate != null) {
        getDateText(lastEnabledDate.date, dateTimeFormat, includeYear = includeYear)
    }
    else
        null
}

fun Trip.getLastEnabledDate(): Date?{
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

fun Trip.getStartEndDateText(dateTimeFormat: DateTimeFormat): String?{

    //if both date is not null
    return if (startDate != null && endDate != null){

        val startDate1 = LocalDate.parse(startDate)
        val endDate1 = LocalDate.parse(endDate)

        //if same date (1 day)
        if (startDate1 == endDate1){
            getDateText(
                date = startDate1,
                dateTimeFormat = dateTimeFormat,
                includeYear = true
            )
        }

        //if not same date (2 days or over)
        else {
            val startDateText = getDateText(
                date = startDate1,
                dateTimeFormat = dateTimeFormat,
                includeYear = true
            )

            val lastDateText = if (startDate1.year == endDate1.year)
                getDateText(
                    date = endDate1,
                    dateTimeFormat = dateTimeFormat,
                    includeYear = false
                )
            else
                getDateText(
                    date = endDate1,
                    dateTimeFormat = dateTimeFormat,
                    includeYear = true
                )

            "$startDateText - $lastDateText"
        }
    }
    else
        null
}

@Composable
fun Trip.getDurationText(): String?{
    val lastEnabledDate = getLastEnabledDate()

    if (dateList.isNotEmpty() && lastEnabledDate != null) {
        val period = Period.between(dateList.first().date, lastEnabledDate.date.plusDays(1))

        val years = period.years
        val months = period.months
        val days = period.days

        val yearsText =
            if (years > 0){
                if (years > 1)  stringResource(id = R.string.years, years) + " "
                else            stringResource(id = R.string.year, years) + " "
            }
            else ""

        val monthsText =
            if (months > 0) {
                if (months > 1) stringResource(id = R.string.months, months) + " "
                else            stringResource(id = R.string.month, months) + " "
            }
            else ""

        val daysText =
            if (days > 0) {
                if (days > 1)   stringResource(id = R.string.days, days)
                else            stringResource(id = R.string.day, days)
            }
            else ""
        return "$yearsText$monthsText$daysText"
    }
    else
        return null
}

fun Trip.getTotalBudgetText(): String{
    val budget = getNumToText(getTotalBudget(dateList), unitOfCurrencyType.numberOfDecimalPlaces)

    return "${unitOfCurrencyType.symbol} $budget"
}

private fun getTotalBudget(
    dateList: List<Date>
): Float{
    var totalBudget = 0.0f

    for (date in dateList){
        totalBudget += date.getTotalBudget()
    }

    return totalBudget
}

fun Trip.getTotalTravelDistanceText(): String{
    val distance = getNumToText(getTotalTravelDistance(dateList), 2)

    return "${distance}km"
}

private fun getTotalTravelDistance(
    dateList: List<Date>
): Float{
    var totalDistance = 0.0f

    for (date in dateList){
        totalDistance += date.getTotalTravelDistance()
    }

    return totalDistance
}


fun Trip.getFirstLocation(): LatLng?{
    for (date in dateList){
        for (spot in date.spotList){
            if (spot.location != null)
                return spot.location
        }
    }
    return null
}

fun Trip.getAllImagesPath(): List<String>{
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
fun Trip.setTitleText(
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    newTitleText: String?
) {
    val titleText: String? =
        if (newTitleText == "") null
        else newTitleText

    updateTripState(true, this.copy(titleText = titleText))
}

fun Trip.setMemoText(
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    newMemoText: String?
) {
    val memoText: String? =
        if (newMemoText == "") null
        else newMemoText

    updateTripState(true, this.copy(memoText = memoText))
}

fun Trip.setImage(
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    newImgList: List<String>
) {
    updateTripState(true, this.copy(imagePathList = newImgList))
}

fun Trip.setCurrencyType(
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    newCurrencyType: CurrencyType
) {
    updateTripState(true, this.copy(unitOfCurrencyType = newCurrencyType))
}

fun Trip.setSharingTo(
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    userDataList: List<UserData>
){
    val newSharingTo: MutableList<Map<String, Any>> = mutableListOf()

    userDataList.forEach {
        newSharingTo.add(
            mapOf(
                EDITABLE to it.allowEdit,
                MANAGER_ID to it.userId
            )
        )
    }

    updateTripState(true, this.copy(sharingTo = newSharingTo.toList()))
    updateTripState(false, this.copy(sharingTo = newSharingTo.toList()))
}

// =============================================================================================
fun Trip.moveSpotToDate(
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

    var newTrip = showingTrip.copy(dateList = newDateList.toList())

    if (newTrip.dateList[dateIndex].spotList.getOrNull(spotIndex) != null)
        newTrip = newTrip.dateList[dateIndex].spotList[spotIndex].updateTravelDistanceCurrPrevNextSpot(newTrip, dateIndex)

    else if (newTrip.dateList[dateIndex].spotList.isNotEmpty())
        newTrip = newTrip.dateList[dateIndex].spotList.last().updateTravelDistanceCurrPrevNextSpot(newTrip, dateIndex)

    newTrip = newTrip.dateList[newDateIndex].spotList.last().updateTravelDistanceCurrPrevNextSpot(newTrip,newDateIndex)

    //update state
    updateTripState(true, newTrip)
}






