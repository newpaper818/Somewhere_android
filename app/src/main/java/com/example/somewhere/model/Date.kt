package com.example.somewhere.model

import com.example.somewhere.enumUtils.SpotTypeGroup
import com.example.somewhere.ui.theme.MyColor
import com.example.somewhere.utils.getNumToText
import com.example.somewhere.viewModel.DateTimeFormat
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.util.Locale


@JsonClass(generateAdapter = true)
data class Date(
    var id: Int = 0,

    val color: MyColor = MyColor(),

    val date: LocalDate,

    val titleText: String? = null,

    val spotList: List<Spot> = listOf(),
    val memo: String? = null
): Cloneable {

    public override fun clone(): Date{
        return Date(
            id, color, date, titleText, spotList.map{ it.clone() }, memo
        )
    }

    // get =========================================================================================

    fun getExpandedText(trip: Trip, isEditMode: Boolean):String{
        val budgetText = getTotalBudgetText(trip)
        val spotCountText = "${getSpotTypeGroupCount(SpotTypeGroup.TOUR) + getSpotTypeGroupCount(SpotTypeGroup.FOOD)} Spot"
        val totalDistanceText = getTotalTravelDistanceText(trip)

        var expandedText = "$spotCountText | $budgetText | $totalDistanceText"

        if (spotList == emptyList<Spot>())
            expandedText = "Empty Plan"

        if(!isEditMode)
            expandedText += "\nSee more"

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

    fun getDateText(locale: Locale, dateTimeFormat: DateTimeFormat, includeYear: Boolean = true): String{
        return com.example.somewhere.utils.getDateText(date, dateTimeFormat, includeYear = includeYear)
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
    fun sortSpotListId(){
//        for ((id, spot) in spotList.withIndex()){
//            spot.id = id
//        }
        spotList.forEachIndexed { index, value ->
            value.id = index
        }
    }

}
