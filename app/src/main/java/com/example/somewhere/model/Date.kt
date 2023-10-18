package com.example.somewhere.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.somewhere.R
import com.example.somewhere.enumUtils.SpotTypeGroup
import com.example.somewhere.ui.theme.MyColor
import com.example.somewhere.utils.getNumToText
import com.example.somewhere.viewModel.DateTimeFormat
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.JsonClass
import java.time.LocalDate


@JsonClass(generateAdapter = true)
data class Date(
    val id: Int = 0,
    var index: Int = 0,
    var enabled: Boolean = true,

    val color: MyColor = MyColor(),

    var date: LocalDate,

    val titleText: String? = null,

    val spotList: List<Spot> = listOf(),
    val memo: String? = null
): Cloneable {

    public override fun clone(): Date{
        return Date(
            id = id,
            index = index,
            enabled = enabled,
            color = color,
            date = date,
            titleText = titleText,
            spotList = spotList.map{ it.clone() },
            memo = memo
        )
    }

    // get =========================================================================================

    @Composable
    fun getExpandedText(trip: Trip, isEditMode: Boolean):String{
        val budgetText = getTotalBudgetText(trip)

        val spotCountText = "${getSpotTypeGroupCountExcludeMove()} " + stringResource(id = R.string.spots)
        val totalDistanceText = getTotalTravelDistanceText(trip)

        var expandedText = "$spotCountText | $budgetText | $totalDistanceText"

        if (spotList == emptyList<Spot>())
            expandedText = stringResource(id = R.string.dates_no_plan)

//        if(!isEditMode)
//            expandedText += "\nSee more"

        return expandedText
    }

    fun getTotalBudgetText(trip: Trip): String{
        val budget = getNumToText(getTotalBudget(), trip.unitOfCurrencyType.numberOfDecimalPlaces)

        return "${trip.unitOfCurrencyType.symbol} $budget"
    }

    fun getTotalBudget(): Float{
        var total = 0.0f
        for (spot in spotList){
            total += spot.budget
        }
        return total
    }

    fun getTotalTravelDistanceText(trip: Trip): String{
        val distance = getNumToText(getTotalTravelDistance(), 2)
        return "${distance}km"
    }

    fun getTotalTravelDistance(): Float{
        var total = 0.0f
        for (spot in spotList){
            total += spot.travelDistance
        }
        return total
    }

    fun getSpotTypeGroupCount(spotTypeGroup: SpotTypeGroup): Int{
        var count = 0
        for (spot in spotList){
            if (spot.spotType.group == spotTypeGroup){
                count++
            }
        }
        return count
    }

    private fun getSpotTypeGroupCountExcludeMove(): Int{
        var count = 0
        for (spot in spotList){
            if (spot.spotType.isNotMove()){
                count++
            }
        }
        return count
    }

    fun getDateText(dateTimeFormat: DateTimeFormat, includeYear: Boolean = true): String{
        return com.example.somewhere.utils.getDateText(date, dateTimeFormat, includeYear = includeYear)
    }

    /**
     * get spotList's last location.
     * If spotList is empty or don't have any location, return null
     *
     * @param startSpotIndex start searching index of spotList. if null, it will set to spotList last index.
     * @return get spotList's last location
     */
    fun getLastLocation(startSpotIndex: Int? = null): LatLng? {
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
    fun setTitleText(
        showingTrip: Trip,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
        newTitleText: String?
    ) {
        val titleText: String? =
            if (newTitleText == "") null
            else newTitleText

        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[id] = newDateList[id].copy(titleText = titleText)
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setMemoText(
        showingTrip: Trip,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
        newMemoText: String?
    ) {
        val memoText: String? =
            if (newMemoText == "") null
            else newMemoText

        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[id] = newDateList[id].copy(memo = memoText)
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    fun setColor(
        showingTrip: Trip,
        updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
        newColor: MyColor
    ) {
        val newDateList = showingTrip.dateList.toMutableList()
        newDateList[id] = newDateList[id].copy(color = newColor)
        updateTripState(true, showingTrip.copy(dateList = newDateList.toList()))
    }

    //sort =========================================================================================
    fun sortSpotListIndex(){
        spotList.forEachIndexed { index, spot ->
            spot.index = index
        }
    }

}
