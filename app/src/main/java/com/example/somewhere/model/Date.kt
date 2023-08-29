package com.example.somewhere.model

import androidx.annotation.ColorInt
import com.example.somewhere.typeUtils.SpotTypeGroup
import com.example.somewhere.utils.getNumToText
import com.squareup.moshi.JsonClass
import java.time.LocalDate


@JsonClass(generateAdapter = true)
data class Date(
    var id: Int = 0,

    @ColorInt
    val iconColor: Int = 0xffff0000.toInt(),

    val date: LocalDate,

    val titleText: String? = null,

    val spotList: List<Spot> = listOf(),
    val memo: String? = null
): Cloneable {

    public override fun clone(): Date{
        return Date(
            id, iconColor, date, titleText, spotList.map{ it.clone() }, memo
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

        return "${trip.unitOfCurrencyType.symbol}${budget}"
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

    fun getDateText(includeYear: Boolean = true): String{
        return com.example.somewhere.utils.getDateText(date, includeYear)
    }

    // set =========================================================================================


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
