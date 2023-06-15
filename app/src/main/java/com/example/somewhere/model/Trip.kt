package com.example.somewhere.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.somewhere.typeUtils.CurrencyType
import com.example.somewhere.utils.getDateText
import com.example.somewhere.utils.getNumToText
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime
import java.time.Period

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val unitOfCurrencyType: CurrencyType = CurrencyType.USD,

    val titleText: String? = null,

    val dateList: List<Date> = listOf(),
    val memoText: String? = null,
    val imagePath: String? = null,

    val firstCreatedTime: LocalDateTime = LocalDateTime.now(),
    val lastModifiedTime: LocalDateTime = LocalDateTime.now()
){
    fun getStartDateText(): String?{
        return if (dateList.isNotEmpty())
            getDateText(dateList.first().date, true)
        else
            null
    }

    fun getEndDateText(): String?{
        return if (dateList.isNotEmpty())
            getDateText(dateList.last().date, true)
        else
            null
    }

    fun getDurationText(): String?{
        if (dateList.isNotEmpty()) {
            val period = Period.between(dateList.first().date, dateList.last().date.plusDays(1))

            val years = period.years
            val months = period.months
            val days = period.days

            val yearsText =
                if (years > 0){
                    if (years > 1)  "${years}years"
                    else            "${years}year"
                }
                else ""

            val monthsText =
                if (months > 0) {
                    if (months > 1) "${months}months"
                    else            "${months}month"
                }
                else ""

            val daysText =
                if (days > 0) {
                    if (days > 1)   "${days}days"
                    else            "${days}day"
                }
                else ""
            return "$yearsText $monthsText $daysText"
        }
        else
            return null
    }

    fun getTotalBudgetText(): String{
        val budget = getNumToText(getTotalBudget(), unitOfCurrencyType.numberOfDecimalPlaces)

        return "${unitOfCurrencyType.symbol}${budget}"
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
}
