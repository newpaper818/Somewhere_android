package com.newpaper.somewhere.core.model.data

import com.newpaper.somewhere.core.model.enums.CurrencyType
import com.newpaper.somewhere.core.model.enums.DateFormat
import com.newpaper.somewhere.core.model.enums.SpotType
import java.time.LocalDate
import java.time.LocalTime

data class SpotFirestore(
    val id: Int = 0,
    var index: Int = 0,
    var iconText: Int = 0,

    val spotType: String = "TOUR",

    var date: String = LocalDate.now().toString(),

    val location: GeoPoint? = null,
    val zoomLevel: Float? = null,

    val titleText: String? = null,
    val imagePathList: List<String> = listOf(),

    val startTime: String? = null,
    val endTime: String? = null,

    val budget: Float = 0.0f,
    val travelDistance: Float = 0.0f,
    val memo: String? = null
)

/**
 * TODO
 *
 * @property id immutable unique id
 * @property index mutable index of [Spot]. no duplication
 * @property iconText mutable order of spot (MOVE's [iconText] can duplicate)
 * @property spotType
 * @property iconId
 * @property iconColor
 * @property iconBackgroundColor
 * @property date
 * @property location
 * @property zoomLevel
 * @property titleText
 * @property imagePathList
 * @property startTime
 * @property endTime
 * @property budget
 * @property travelDistance
 * @property memo
 */
//@JsonClass(generateAdapter = true)
data class Spot(
    val id: Int = 0,
    var index: Int = 0,
    var iconText: Int = 0,

    val spotType: SpotType? = null,
    val iconId: Int? = null,
    val iconColor: Int? = null,
    val iconBackgroundColor: Int = 0xffffffff.toInt(),

    var date: LocalDate,

    val location: MyLatLng? = null,
    val zoomLevel: Float? = null,

    val titleText: String? = null,
    val imagePathList: List<String> = listOf(),

    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,

    val budget: Float = 0.0f,
    val travelDistance: Float = 0.0f,
    val memo: String? = null
): Cloneable {
    public override fun clone(): Spot {
        return Spot(
            id = id,
            index = index,
            iconText = iconText,
            spotType = spotType,
            iconId = iconId,
            iconColor = iconColor,
            iconBackgroundColor = iconBackgroundColor,
            date = date,
            location = location,
            zoomLevel = zoomLevel,
            titleText= titleText,
            imagePathList = imagePathList,
            startTime = startTime,
            endTime = endTime,
            budget = budget,
            travelDistance = travelDistance,
            memo = memo
        )
    }
}