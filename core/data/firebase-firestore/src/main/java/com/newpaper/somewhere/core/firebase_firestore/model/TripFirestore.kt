package com.newpaper.somewhere.core.firebase_firestore.model

import com.newpaper.somewhere.core.firebase_common.EDITABLE
import com.newpaper.somewhere.core.firebase_common.MANAGER_ID
import com.newpaper.somewhere.core.model.enums.getCurrencyTypeFromString
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Trip
import java.time.ZoneOffset
import java.time.ZonedDateTime

internal data class TripFirestore(
    val id: Int = 0,
    var orderId: Int = 0,

    val unitOfCurrencyType: String = "USD",

    val titleText: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    val memoText: String? = null,
    val imagePathList: List<String> = listOf(),

    val firstCreatedTime: String = ZonedDateTime.now(ZoneOffset.UTC).toString(),
    val lastModifiedTime: String = ZonedDateTime.now(ZoneOffset.UTC).toString(),

    val sharingTo: List<Map<String, Any>> = listOf()
){
    /**
     * if want to get editable from firestore, [appUserId] must not null and [editable] must be null
     */
    fun toTrip(
        managerId: String,
        appUserId: String? = null,
        dateList: List<Date>,
        editable: Boolean? = null
    ): Trip {
        val item = sharingTo.find { it[MANAGER_ID] == appUserId }
        val newEditable = item?.get(EDITABLE) as? Boolean

        return Trip(
            id = id,
            orderId = orderId,
            managerId = managerId,
            editable = editable ?: newEditable ?: (appUserId == managerId),
            unitOfCurrencyType = getCurrencyTypeFromString(unitOfCurrencyType),
            titleText = titleText,
            dateList = dateList,
            startDate = startDate,
            endDate = endDate,
            memoText = memoText,
            imagePathList = imagePathList,
            firstCreatedTime = ZonedDateTime.parse(firstCreatedTime),
            lastModifiedTime = ZonedDateTime.parse(lastModifiedTime),
            sharingTo = sharingTo
        )
    }
}

internal fun Trip.toTripFirestore(): TripFirestore =
    TripFirestore(
        id = id,
        orderId = orderId,
        unitOfCurrencyType = unitOfCurrencyType.toString(),
        titleText = titleText,
        startDate = startDate,
        endDate = endDate,
        memoText = memoText,
        imagePathList = imagePathList,
        firstCreatedTime = firstCreatedTime.toString(),
        lastModifiedTime = lastModifiedTime.toString(),
        sharingTo = sharingTo
    )