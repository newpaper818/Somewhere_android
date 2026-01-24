package com.newpaper.somewhere.core.model.tripData

import com.newpaper.somewhere.core.model.data.MyColor
import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class Date(
    var id: Int = 0,
    var index: Int = 0,
    var enabled: Boolean = true,

    val color: MyColor = MyColor(),

    var date: LocalDate,

    val titleText: String? = null,

    val spotList: List<Spot> = listOf(),
    val memoText: String? = null
): Cloneable {
    public override fun clone(): Date {
        return Date(
            id = id,
            index = index,
            enabled = enabled,
            color = color,
            date = date,
            titleText = titleText,
            spotList = spotList.map{ it.clone() },
            memoText = memoText
        )
    }

    override fun toString(): String {
        return  "(D${index+1}) $date [$titleText]" +
                "\n" +
                spotList.joinToString("\n\n")
    }
}
