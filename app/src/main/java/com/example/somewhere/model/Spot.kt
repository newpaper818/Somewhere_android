package com.example.somewhere.model

import android.location.Location
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.somewhere.typeUtils.SpotType
import com.example.somewhere.utils.getNumToText
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime


@JsonClass(generateAdapter = true)
data class Spot(
    val id: Int = 0,

    val spotType: SpotType = SpotType.TOUR,
    @DrawableRes
    val iconId: Int? = null,
    @ColorInt
    val iconColor: Int? = null,
    @ColorInt
    val iconBackgroundColor: Int = 0xffffffff.toInt(),

    val date: LocalDate,
    val location: LatLng? = null,

    val titleText: String? = null,
    val imgPathList: List<String> = listOf(),

    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    //if spotType == MOVE
    val spotFrom: Spot? = null,
    val spotTo: Spot? = null,

    val budget: Float = 0.0f,
    var travelDistance: Float = 0.0f,
    val memo: String? = null
){
    @Composable
    fun getExpandedText(trip: Trip): String {
        val categoryText = getCategoryText()
        val budgetText = getBudgetText(trip)
        val distanceText = getDistanceText()

        return "$categoryText | $budgetText | $distanceText\nSee more"
    }

    @Composable
    fun getCategoryText(): String{
        return stringResource(id = spotType.textId)
    }

    fun getBudgetText(trip: Trip): String {
        return "${trip.unitOfCurrencyType.symbol}${getNumToText(budget, trip.unitOfCurrencyType.numberOfDecimalPlaces)}"
    }

    fun getDistanceText(): String {
        return "${getNumToText(travelDistance, 2)}km"
    }

    fun getDateText(includeYear: Boolean): String{
        return com.example.somewhere.utils.getDateText(date, includeYear)
    }

    fun getTimeText(includeEndTime: Boolean = true): String{
        val df1 = DecimalFormat("00")

        val startHour = startTime?.hour
        val startMinute = startTime?.minute

        var timeText = "${startHour}:${df1.format(startMinute)}"

        if (!includeEndTime)
            return timeText

        if (endTime != null){
            val endHour = endTime.hour
            val endMinute = endTime.minute
            timeText += " ~ ${endHour}:${df1.format(endMinute)}"
        }
        return timeText
    }

    //TODO move to view model?
    fun updateDistance(){
        if (spotFrom != null && spotTo != null &&
            spotFrom.location != null && spotTo.location != null){

            val locationA = Location("a")
            locationA.latitude = spotFrom.location.latitude
            locationA.longitude = spotFrom.location.longitude

            val locationB = Location("b")
            locationB.latitude = spotTo.location.latitude
            locationB.longitude = spotTo.location.longitude

            travelDistance = locationA.distanceTo(locationB) / 1000
            //TODO room db update!!!!!!!
        }
    }

}
