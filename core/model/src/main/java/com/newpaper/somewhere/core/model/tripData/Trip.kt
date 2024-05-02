package com.newpaper.somewhere.core.model.tripData

import com.newpaper.somewhere.core.model.enums.CurrencyType
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class Trip(
    val id: Int = 0,
    var orderId: Int = 0,

    val managerId: String? = null,
    val editable: Boolean = false,

    val unitOfCurrencyType: CurrencyType = CurrencyType.USD,

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