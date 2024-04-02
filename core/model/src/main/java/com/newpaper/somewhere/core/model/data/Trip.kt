package com.newpaper.somewhere.core.model.data

import com.newpaper.somewhere.core.model.enums.CurrencyType
import com.newpaper.somewhere.core.model.enums.getCurrencyTypeFromString
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class TripFirestore(
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
        val item = sharingTo.find { it["managerId"] == appUserId }
        val newEditable = item?.get("editable") as? Boolean

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

data class Trip(
    val id: Int = 0,
    var orderId: Int = 0,

    val managerId: String? = null,
    val editable: Boolean = false,

    val unitOfCurrencyType: CurrencyType? = null,

    val titleText: String? = null,
    var dateList: List<Date> = listOf(),
    var startDate: String? = null,
    var endDate: String? = null,

    val memoText: String? = null,
    val imagePathList: List<String> = listOf(),

    val firstCreatedTime: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),
    val lastModifiedTime: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC),

    val sharingTo: List<Map<String, Any>> = listOf()
): Cloneable {
    public override fun clone(): Trip {
        return Trip(
            id = id,
            orderId = orderId,
            managerId = managerId,
            editable = editable,
            unitOfCurrencyType = unitOfCurrencyType,
            titleText = titleText,
            dateList = dateList.map{ it.clone() },
            startDate = startDate,
            endDate = endDate,
            memoText = memoText,
            imagePathList = imagePathList,
            firstCreatedTime = firstCreatedTime,
            lastModifiedTime = lastModifiedTime,
            sharingTo = sharingTo
        )
    }
}
