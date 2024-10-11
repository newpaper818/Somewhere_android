package com.newpaper.somewhere.core.data.moshi

import com.newpaper.somewhere.core.data.moshi.adaptor.LocalDateAdaptor
import com.newpaper.somewhere.core.data.moshi.adaptor.LocalTimeAdaptor
import com.newpaper.somewhere.core.data.moshi.adaptor.ZonedDateTimeAdaptor
import com.newpaper.somewhere.core.model.tripData.Trip
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject

private const val MOSHI_TAG = "Moshi"

class MoshiApi @Inject constructor(

): SerializationDataSource{
    override fun jsonToTrip(
        json: String
    ): Trip? {

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(LocalDateAdaptor())
            .add(LocalTimeAdaptor())
            .add(ZonedDateTimeAdaptor())
            .build()

        val adaptor: JsonAdapter<Trip> = moshi.adapter(Trip::class.java)
        return adaptor.fromJson(json)
    }
}