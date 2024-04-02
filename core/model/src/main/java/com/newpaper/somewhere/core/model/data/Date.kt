package com.newpaper.somewhere.core.model.data

import java.time.LocalDate

data class DateFirestore(
    val id: Int = 0,
    var index: Int = 0,
//    var enabled: Boolean = true,

    val color: String = 0xFF493cfa.toInt().toString(),
    var date: String = LocalDate.now().toString(),
    val titleText: String? = null,
    val spotList: List<SpotFirestore> = listOf(),
    val memo: String? = null
)


data class Date(
    var id: Int = 0,
    var index: Int = 0,
    var enabled: Boolean = true,

    val color: Int = 0xFF493cfa.toInt(),
    val onColor: Int = 0xffffffff.toInt(),

    var date: LocalDate,

    val titleText: String? = null,

    val spotList: List<Spot> = listOf(),
    val memo: String? = null
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
            memo = memo
        )
    }
}
