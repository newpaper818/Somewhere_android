package com.newpaper.somewhere.core.firebase_firestore.model

import com.newpaper.somewhere.core.firebase_firestore.stringToMyColor
import com.newpaper.somewhere.core.model.data.MyColor
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import java.time.LocalDate

internal data class DateFirestore(
    val id: Int = 0,
    var index: Int = 0,
    val color: String = MyColor().toString(),
    var date: String = LocalDate.now().toString(),
    val titleText: String? = null,
    val spotList: List<SpotFirestore> = listOf(),
    val memoText: String? = null
){
    fun toDate(): Date{

        val newSpotList: MutableList<Spot> = mutableListOf()

        spotList.forEach { spot ->
            newSpotList.add(spot.toSpot())
        }

        return Date(
            id = id,
            index = index,

            color = stringToMyColor(color),
            date = LocalDate.parse(date),
            titleText = titleText,
            spotList = newSpotList,
            memoText = memoText
        )
    }
}

internal fun Date.toDateFirestore(): DateFirestore {
    val spotListFirestore: MutableList<SpotFirestore> = mutableListOf()

    spotList.forEach { spot ->
        spotListFirestore.add(spot.toSpotFirestore())
    }

    return DateFirestore(
        id = id,
        index = index,
        color = color.toString(),
        date = date.toString(),
        titleText = titleText,
        spotList = spotListFirestore,
        memoText = memoText
    )
}