package com.newpaper.somewhere.feature.trip.trips

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.newpaper.somewhere.core.model.enums.CurrencyType
import com.newpaper.somewhere.core.model.enums.SpotType
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.feature.trip.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

object TripsMockData {
    fun getMockTrip(context: Context): Trip {
        val startDate = LocalDate.now()
        val nowDateTime = ZonedDateTime.now(ZoneOffset.UTC)

        val day1 = Date(
            id = 1,
            index = 0,
            date = startDate,
            titleText = context.getString(R.string.mock_trip_day1_title),
            spotList = listOf(
                Spot(id = 101, index = 0, iconText = 1, date = startDate, spotType = SpotType.AIRPORT, titleText = context.getString(R.string.mock_trip_jeju_airport), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0), budget = 0.0, location = LatLng(33.5113, 126.4930)),
                Spot(id = 102, index = 1, iconText = 2, date = startDate, spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day1_spot2_title), startTime = LocalTime.of(11, 30), endTime = LocalTime.of(12, 30), budget = 12000.0, location = LatLng(33.5070, 126.4920)),
                Spot(id = 103, index = 3, iconText = 3, date = startDate, spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_day1_spot3_title), startTime = LocalTime.of(13, 0), endTime = LocalTime.of(14, 30), budget = 0.0, location = LatLng(33.4623, 126.3105)),
                Spot(id = 104, index = 4, iconText = 4, date = startDate, spotType = SpotType.COFFEE_TEA, titleText = context.getString(R.string.mock_trip_day1_spot4_title), startTime = LocalTime.of(15, 0), endTime = LocalTime.of(16, 30), budget = 15000.0, location = LatLng(33.4610, 126.3110)),
                Spot(id = 105, index = 5, iconText = 5, date = startDate, spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_day1_spot5_title), startTime = LocalTime.of(17, 0), budget = 120000.0, location = LatLng(33.4590, 126.3120)),
                Spot(id = 106, index = 6, iconText = 6, date = startDate, spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day1_spot6_title), startTime = LocalTime.of(18, 30), endTime = LocalTime.of(20, 0), budget = 40000.0, location = LatLng(33.4650, 126.3200))
            )
        )

        val day2 = Date(
            id = 2,
            index = 1,
            date = startDate.plusDays(1),
            titleText = context.getString(R.string.mock_trip_day2_title),
            spotList = listOf(
                Spot(id = 201, index = 0, iconText = 1, date = startDate.plusDays(1), spotType = SpotType.MUSEUM, titleText = context.getString(R.string.mock_trip_day2_spot1_title), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 30), budget = 25000.0, location = LatLng(33.3059, 126.2894)),
                Spot(id = 202, index = 1, iconText = 2, date = startDate.plusDays(1), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day2_spot2_title), startTime = LocalTime.of(12, 0), endTime = LocalTime.of(13, 0), budget = 20000.0, location = LatLng(33.3000, 126.2950)),
                Spot(id = 203, index = 2, iconText = 3, date = startDate.plusDays(1), spotType = SpotType.GARDEN, titleText = context.getString(R.string.mock_trip_day2_spot3_title), startTime = LocalTime.of(13, 30), endTime = LocalTime.of(15, 30), budget = 9000.0, location = LatLng(33.2901, 126.3684)),
                Spot(id = 204, index = 3, iconText = 4, date = startDate.plusDays(1), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day2_spot4_title), startTime = LocalTime.of(17, 30), endTime = LocalTime.of(19, 0), budget = 60000.0, location = LatLng(33.2480, 126.5600)),
                Spot(id = 205, index = 4, iconText = 5, date = startDate.plusDays(1), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_day2_spot5_title), startTime = LocalTime.of(20, 0), budget = 150000.0, location = LatLng(33.2450, 126.5650))
            )
        )

        val day3 = Date(
            id = 3,
            index = 2,
            date = startDate.plusDays(2),
            titleText = context.getString(R.string.mock_trip_day3_title),
            spotList = listOf(
                Spot(id = 301, index = 0, iconText = 1, date = startDate.plusDays(2), spotType = SpotType.HIKING, titleText = context.getString(R.string.mock_trip_day3_spot1_title), startTime = LocalTime.of(8, 0), endTime = LocalTime.of(14, 0), budget = 0.0, location = LatLng(33.3414, 126.4770)),
                Spot(id = 302, index = 1, iconText = 2, date = startDate.plusDays(2), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day3_spot2_title), startTime = LocalTime.of(14, 30), endTime = LocalTime.of(15, 30), budget = 15000.0, location = LatLng(33.2500, 126.5600)),
                Spot(id = 303, index = 2, iconText = 3, date = startDate.plusDays(2), spotType = SpotType.STREET_MARKET, titleText = context.getString(R.string.mock_trip_day3_spot3_title), startTime = LocalTime.of(16, 30), endTime = LocalTime.of(18, 30), budget = 40000.0, location = LatLng(33.2494, 126.5638)),
                Spot(id = 304, index = 3, iconText = 4, date = startDate.plusDays(2), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_day3_spot4_title), startTime = LocalTime.of(19, 0), endTime = LocalTime.of(20, 0), budget = 2000.0, location = LatLng(33.2461, 126.5542)),
                Spot(id = 305, index = 4, iconText = 5, date = startDate.plusDays(2), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day3_spot5_title), startTime = LocalTime.of(20, 30), endTime = LocalTime.of(22, 0), budget = 80000.0, location = LatLng(33.2440, 126.5620))
            )
        )

        val day4 = Date(
            id = 4,
            index = 3,
            date = startDate.plusDays(3),
            titleText = context.getString(R.string.mock_trip_day4_title),
            spotList = listOf(
                Spot(id = 401, index = 0, iconText = 1, date = startDate.plusDays(3), spotType = SpotType.LANDMARK, titleText = context.getString(R.string.mock_trip_day4_spot1_title), startTime = LocalTime.of(9, 0), endTime = LocalTime.of(11, 0), budget = 5000.0, location = LatLng(33.4586, 126.9424)),
                Spot(id = 402, index = 1, iconText = 2, date = startDate.plusDays(3), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_day4_spot2_title), startTime = LocalTime.of(11, 30), endTime = LocalTime.of(13, 0), budget = 0.0, location = LatLng(33.4243, 126.9311)),
                Spot(id = 403, index = 2, iconText = 3, date = startDate.plusDays(3), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day4_spot3_title), startTime = LocalTime.of(13, 30), endTime = LocalTime.of(14, 30), budget = 18000.0, location = LatLng(33.4700, 126.9200)),
                Spot(id = 404, index = 4, iconText = 4, date = startDate.plusDays(3), spotType = SpotType.BEACH, titleText = context.getString(R.string.mock_trip_day4_spot4_title), startTime = LocalTime.of(15, 30), endTime = LocalTime.of(17, 30), budget = 0.0, location = LatLng(33.5430, 126.6690)),
                Spot(id = 405, index = 5, iconText = 5, date = startDate.plusDays(3), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_day4_spot5_title), startTime = LocalTime.of(18, 30), budget = 100000.0, location = LatLng(33.5000, 126.5200)),
                Spot(id = 406, index = 6, iconText = 6, date = startDate.plusDays(3), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day4_spot6_title), startTime = LocalTime.of(19, 30), endTime = LocalTime.of(21, 0), budget = 50000.0, location = LatLng(33.5100, 126.5300))
            )
        )

        val day5 = Date(
            id = 5,
            index = 4,
            date = startDate.plusDays(4),
            titleText = context.getString(R.string.mock_trip_day5_title),
            spotList = listOf(
                Spot(id = 501, index = 0, iconText = 1, date = startDate.plusDays(4), spotType = SpotType.GIFT_SHOP, titleText = context.getString(R.string.mock_trip_day5_spot1_title), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0), budget = 50000.0, location = LatLng(33.5050, 126.4950)),
                Spot(id = 502, index = 1, iconText = 2, date = startDate.plusDays(4), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day5_spot2_title), startTime = LocalTime.of(11, 15), endTime = LocalTime.of(12, 15), budget = 10000.0, location = LatLng(33.5080, 126.4900)),
                Spot(id = 503, index = 2, iconText = 3, date = startDate.plusDays(4), spotType = SpotType.AIRPORT, titleText = context.getString(R.string.mock_trip_jeju_airport), startTime = LocalTime.of(12, 30), endTime = LocalTime.of(14, 0), budget = 0.0, location = LatLng(33.5113, 126.4930))
            )
        )

        return Trip(
            id = 12345,
            managerId = "mock",
            unitOfCurrencyType = CurrencyType.KRW,
            titleText = context.getString(R.string.mock_trip_title),
            startDate = startDate.toString(),
            endDate = startDate.plusDays(4).toString(),
            dateList = listOf(day1, day2, day3, day4, day5),
            firstCreatedTime = nowDateTime,
            lastModifiedTime = nowDateTime,
            editable = true
        )
    }
}
