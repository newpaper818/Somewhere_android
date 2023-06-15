package com.example.somewhere.model

import androidx.annotation.ColorInt
import com.example.somewhere.typeUtils.SpotType
import com.example.somewhere.utils.getNumToText
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate


@JsonClass(generateAdapter = true)
data class Date(
    val id: Int = 0,

    @ColorInt
    val iconColor: Int = 0xffff0000.toInt(),

    val date: LocalDate,

    val titleText: String? = null,

    val spotList: List<Spot> = listOf(),
    val memo: String? = null
){
    fun getExpandedText(trip: Trip):String{
        val budgetText = getTotalBudgetText(trip)
        val spotCountText = "${getSpotTypeCount(SpotType.TOUR) + getSpotTypeCount(SpotType.FOOD)} Spot"
        val totalDistanceText = getTotalTravelDistanceText(trip)

        var expandedText = "$spotCountText | $budgetText | $totalDistanceText\nSee more"

        if (spotList == emptyList<Spot>())
            expandedText = "Empty Plan\nSee more"

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

    fun getSpotTypeCount(spotType: SpotType): Int{
        var count = 0
        for (spot in spotList){
            if (spot.spotType == spotType){
                count++
            }
        }
        return count
    }

    fun getDateText(includeYear: Boolean = true): String{
        return com.example.somewhere.utils.getDateText(date, includeYear)
    }

}
