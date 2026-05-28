package com.newpaper.somewhere.feature.trip.trips

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.newpaper.somewhere.core.model.enums.CurrencyType
import com.newpaper.somewhere.core.model.enums.SpotType
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.designsystem.theme.dateColorList
import com.newpaper.somewhere.feature.trip.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

object TripsMockData {
    fun getMockTrips(context: Context): List<Trip> {
        val nowDateTime = ZonedDateTime.now(ZoneOffset.UTC)

        // Jeju Trip (Existing)
        val jejuStartDate = LocalDate.now()
        val jejuDay1 = Date(
            id = 1, index = 0, date = jejuStartDate,
            color = dateColorList[0],
            titleText = context.getString(R.string.mock_trip_day1_title),
            spotList = listOf(
                Spot(id = 101, index = 0, iconText = 1, date = jejuStartDate, spotType = SpotType.AIRPORT, titleText = context.getString(R.string.mock_trip_jeju_airport), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0), budget = 0.0, location = LatLng(33.5113, 126.4930)),
                Spot(id = 102, index = 1, iconText = 2, date = jejuStartDate, spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day1_spot2_title), startTime = LocalTime.of(11, 30), endTime = LocalTime.of(12, 30), budget = 12000.0, location = LatLng(33.5070, 126.4920)),
                Spot(id = 103, index = 3, iconText = 3, date = jejuStartDate, spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_day1_spot3_title), startTime = LocalTime.of(13, 0), endTime = LocalTime.of(14, 30), budget = 0.0, location = LatLng(33.4623, 126.3105)),
                Spot(id = 104, index = 4, iconText = 4, date = jejuStartDate, spotType = SpotType.COFFEE_TEA, titleText = context.getString(R.string.mock_trip_day1_spot4_title), startTime = LocalTime.of(15, 0), endTime = LocalTime.of(16, 30), budget = 15000.0, location = LatLng(33.4610, 126.3110)),
                Spot(id = 105, index = 5, iconText = 5, date = jejuStartDate, spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_day1_spot5_title), startTime = LocalTime.of(17, 0), budget = 120000.0, location = LatLng(33.4590, 126.3120)),
                Spot(id = 106, index = 6, iconText = 6, date = jejuStartDate, spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day1_spot6_title), startTime = LocalTime.of(18, 30), endTime = LocalTime.of(20, 0), budget = 40000.0, location = LatLng(33.4650, 126.3200))
            )
        )
        val jejuDay2 = Date(
            id = 2, index = 1, date = jejuStartDate.plusDays(1),
            color = dateColorList[1],
            titleText = context.getString(R.string.mock_trip_day2_title),
            spotList = listOf(
                Spot(id = 201, index = 0, iconText = 1, date = jejuStartDate.plusDays(1), spotType = SpotType.MUSEUM, titleText = context.getString(R.string.mock_trip_day2_spot1_title), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 30), budget = 25000.0, location = LatLng(33.3059, 126.2894)),
                Spot(id = 202, index = 1, iconText = 2, date = jejuStartDate.plusDays(1), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day2_spot2_title), startTime = LocalTime.of(12, 0), endTime = LocalTime.of(13, 0), budget = 20000.0, location = LatLng(33.3000, 126.2950)),
                Spot(id = 203, index = 2, iconText = 3, date = jejuStartDate.plusDays(1), spotType = SpotType.GARDEN, titleText = context.getString(R.string.mock_trip_day2_spot3_title), startTime = LocalTime.of(13, 30), endTime = LocalTime.of(15, 30), budget = 9000.0, location = LatLng(33.2901, 126.3684)),
                Spot(id = 204, index = 3, iconText = 4, date = jejuStartDate.plusDays(1), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day2_spot4_title), startTime = LocalTime.of(17, 30), endTime = LocalTime.of(19, 0), budget = 60000.0, location = LatLng(33.2480, 126.5600)),
                Spot(id = 205, index = 4, iconText = 5, date = jejuStartDate.plusDays(1), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_day2_spot5_title), startTime = LocalTime.of(20, 0), budget = 150000.0, location = LatLng(33.2450, 126.5650))
            )
        )
        val jejuDay3 = Date(
            id = 3, index = 2, date = jejuStartDate.plusDays(2),
            color = dateColorList[2],
            titleText = context.getString(R.string.mock_trip_day3_title),
            spotList = listOf(
                Spot(id = 301, index = 0, iconText = 1, date = jejuStartDate.plusDays(2), spotType = SpotType.HIKING, titleText = context.getString(R.string.mock_trip_day3_spot1_title), startTime = LocalTime.of(8, 0), endTime = LocalTime.of(14, 0), budget = 0.0, location = LatLng(33.3414, 126.4770)),
                Spot(id = 302, index = 1, iconText = 2, date = jejuStartDate.plusDays(2), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day3_spot2_title), startTime = LocalTime.of(14, 30), endTime = LocalTime.of(15, 30), budget = 15000.0, location = LatLng(33.2500, 126.5600)),
                Spot(id = 303, index = 2, iconText = 3, date = jejuStartDate.plusDays(2), spotType = SpotType.STREET_MARKET, titleText = context.getString(R.string.mock_trip_day3_spot3_title), startTime = LocalTime.of(16, 30), endTime = LocalTime.of(18, 30), budget = 40000.0, location = LatLng(33.2494, 126.5638)),
                Spot(id = 304, index = 3, iconText = 4, date = jejuStartDate.plusDays(2), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_day3_spot4_title), startTime = LocalTime.of(19, 0), endTime = LocalTime.of(20, 0), budget = 2000.0, location = LatLng(33.2461, 126.5542)),
                Spot(id = 305, index = 4, iconText = 5, date = jejuStartDate.plusDays(2), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day3_spot5_title), startTime = LocalTime.of(20, 30), endTime = LocalTime.of(22, 0), budget = 80000.0, location = LatLng(33.2440, 126.5620))
            )
        )
        val jejuDay4 = Date(
            id = 4, index = 3, date = jejuStartDate.plusDays(3),
            color = dateColorList[3],
            titleText = context.getString(R.string.mock_trip_day4_title),
            spotList = listOf(
                Spot(id = 401, index = 0, iconText = 1, date = jejuStartDate.plusDays(3), spotType = SpotType.LANDMARK, titleText = context.getString(R.string.mock_trip_day4_spot1_title), startTime = LocalTime.of(9, 0), endTime = LocalTime.of(11, 0), budget = 5000.0, location = LatLng(33.4586, 126.9424)),
                Spot(id = 402, index = 1, iconText = 2, date = jejuStartDate.plusDays(3), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_day4_spot2_title), startTime = LocalTime.of(11, 30), endTime = LocalTime.of(13, 0), budget = 0.0, location = LatLng(33.4243, 126.9311)),
                Spot(id = 403, index = 2, iconText = 3, date = jejuStartDate.plusDays(3), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day4_spot3_title), startTime = LocalTime.of(13, 30), endTime = LocalTime.of(14, 30), budget = 18000.0, location = LatLng(33.4700, 126.9200)),
                Spot(id = 404, index = 4, iconText = 4, date = jejuStartDate.plusDays(3), spotType = SpotType.BEACH, titleText = context.getString(R.string.mock_trip_day4_spot4_title), startTime = LocalTime.of(15, 30), endTime = LocalTime.of(17, 30), budget = 0.0, location = LatLng(33.5430, 126.6690)),
                Spot(id = 405, index = 5, iconText = 5, date = jejuStartDate.plusDays(3), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_day4_spot5_title), startTime = LocalTime.of(18, 30), budget = 100000.0, location = LatLng(33.5000, 126.5200)),
                Spot(id = 406, index = 6, iconText = 6, date = jejuStartDate.plusDays(3), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day4_spot6_title), startTime = LocalTime.of(19, 30), endTime = LocalTime.of(21, 0), budget = 50000.0, location = LatLng(33.5100, 126.5300))
            )
        )
        val jejuDay5 = Date(
            id = 5, index = 4, date = jejuStartDate.plusDays(4),
            color = dateColorList[4],
            titleText = context.getString(R.string.mock_trip_day5_title),
            spotList = listOf(
                Spot(id = 501, index = 0, iconText = 1, date = jejuStartDate.plusDays(4), spotType = SpotType.GIFT_SHOP, titleText = context.getString(R.string.mock_trip_day5_spot1_title), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0), budget = 50000.0, location = LatLng(33.5050, 126.4950)),
                Spot(id = 502, index = 1, iconText = 2, date = jejuStartDate.plusDays(4), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_day5_spot2_title), startTime = LocalTime.of(11, 15), endTime = LocalTime.of(12, 15), budget = 10000.0, location = LatLng(33.5080, 126.4900)),
                Spot(id = 503, index = 2, iconText = 3, date = jejuStartDate.plusDays(4), spotType = SpotType.AIRPORT, titleText = context.getString(R.string.mock_trip_jeju_airport), startTime = LocalTime.of(12, 30), endTime = LocalTime.of(14, 0), budget = 0.0, location = LatLng(33.5113, 126.4930))
            )
        )
        val jejuTrip = Trip(
            id = 12345, managerId = "mock", unitOfCurrencyType = CurrencyType.KRW,
            titleText = context.getString(R.string.mock_trip_title),
            startDate = jejuStartDate.toString(), endDate = jejuStartDate.plusDays(4).toString(),
            dateList = listOf(jejuDay1, jejuDay2, jejuDay3, jejuDay4, jejuDay5),
            firstCreatedTime = nowDateTime, lastModifiedTime = nowDateTime, editable = true
        )

        // New York Trip (Detailed)
        val nyStartDate = LocalDate.now().plusDays(15)
        val nyDay1 = Date(
            id = 11, index = 0, date = nyStartDate,
            color = dateColorList[0],
            titleText = context.getString(R.string.mock_trip_ny_day1_title),
            spotList = listOf(
                Spot(id = 1101, index = 0, iconText = 1, date = nyStartDate, spotType = SpotType.AIRPORT, titleText = context.getString(R.string.mock_trip_ny_icn_airport), startTime = LocalTime.of(18, 50), googleMapsPlacesId = "ChIJA-F312OaezURGNV_89NW7Ys", location = LatLng(37.4485, 126.4512)),
                Spot(id = 1102, index = 1, iconText = 1, date = nyStartDate, spotType = SpotType.AIRPLANE, titleText = context.getString(R.string.mock_trip_ny_flight_icn_ewr), startTime = LocalTime.of(21, 55), endTime = LocalTime.of(21, 30), travelDistance = 11101.022f, memoText = "Reservation No. ABCDE"),
                Spot(id = 1103, index = 2, iconText = 2, date = nyStartDate, spotType = SpotType.AIRPORT, titleText = context.getString(R.string.mock_trip_ny_ewr_airport), startTime = LocalTime.of(21, 30), googleMapsPlacesId = "ChIJ-6uTxfZSwokR-VfW-WSM53k", location = LatLng(40.6895, -74.1745)),
                Spot(id = 1104, index = 3, iconText = 2, date = nyStartDate, spotType = SpotType.TAXI, titleText = context.getString(R.string.mock_trip_ny_taxi), travelDistance = 20.439054f),
                Spot(id = 1105, index = 4, iconText = 3, date = nyStartDate, spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), startTime = LocalTime.of(23, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430), memoText = "Hotel deposit $100 with card used for booking")
            )
        )
        val nyDay2 = Date(
            id = 12, index = 1, date = nyStartDate.plusDays(1),
            color = dateColorList[1],
            titleText = context.getString(R.string.mock_trip_ny_day2_title),
            spotList = listOf(
                Spot(id = 1201, index = 0, iconText = 1, date = nyStartDate.plusDays(1), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_breakfast_hotel), startTime = LocalTime.of(8, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430)),
                Spot(id = 1202, index = 1, iconText = 2, date = nyStartDate.plusDays(1), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(9, 30)),
                Spot(id = 1203, index = 2, iconText = 3, date = nyStartDate.plusDays(1), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_battery_park), startTime = LocalTime.of(10, 20), googleMapsPlacesId = "ChIJP9OOXRJawokRce_GIiSXbl4", location = LatLng(40.7033, -74.0170)),
                Spot(id = 1204, index = 3, iconText = 4, date = nyStartDate.plusDays(1), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_ny_statue_of_liberty), startTime = LocalTime.of(11, 0), endTime = LocalTime.of(12, 0), googleMapsPlacesId = "ChIJPTacEpBQwokRKwIlDXelxkA", location = LatLng(40.6892, -74.0445), memoText = "Use QR pass"),
                Spot(id = 1205, index = 4, iconText = 5, date = nyStartDate.plusDays(1), spotType = SpotType.MONUMENT_STATUE, titleText = context.getString(R.string.mock_trip_ny_wall_street_bull), startTime = LocalTime.of(12, 30), googleMapsPlacesId = "ChIJURWoF5pYwokRIfXv4To528c", location = LatLng(40.7056, -74.0134)),
                Spot(id = 1206, index = 5, iconText = 6, date = nyStartDate.plusDays(1), spotType = SpotType.FAST_FOOD, titleText = context.getString(R.string.mock_trip_ny_fast_food), startTime = LocalTime.of(13, 0), endTime = LocalTime.of(14, 0), budget = 15.0, googleMapsPlacesId = "ChIJq6pWOBhawokRGf1Kq_FeCOk", location = LatLng(40.7061, -74.0091)),
                Spot(id = 1207, index = 6, iconText = 7, date = nyStartDate.plusDays(1), spotType = SpotType.TOUR, titleText = context.getString(R.string.mock_trip_ny_ground_zero), startTime = LocalTime.of(14, 0), googleMapsPlacesId = "ChIJTQ1r4kdbwokRgoVS3bJJM1c", location = LatLng(40.7115, -74.0132)),
                Spot(id = 1208, index = 7, iconText = 8, date = nyStartDate.plusDays(1), spotType = SpotType.SHOPPING, titleText = context.getString(R.string.mock_trip_ny_camera_shop), startTime = LocalTime.of(14, 40), googleMapsPlacesId = "ChIJD41aqdNbwokREHcKVDWKKsk", location = LatLng(40.7130, -74.0090)),
                Spot(id = 1209, index = 8, iconText = 9, date = nyStartDate.plusDays(1), spotType = SpotType.DESSERT, titleText = context.getString(R.string.mock_trip_ny_dessert_shop), startTime = LocalTime.of(15, 30), endTime = LocalTime.of(16, 0), budget = 10.0, googleMapsPlacesId = "ChIJaeOQ3IhZwokRgxPZEE3noAM", location = LatLng(40.7215, -73.9972)),
                Spot(id = 1210, index = 9, iconText = 10, date = nyStartDate.plusDays(1), spotType = SpotType.TOUR, titleText = context.getString(R.string.mock_trip_ny_chinatown), startTime = LocalTime.of(16, 30), googleMapsPlacesId = "ChIJDcEvbPVZwokRETEfzXATns8", location = LatLng(40.7158, -73.9970)),
                Spot(id = 1211, index = 10, iconText = 11, date = nyStartDate.plusDays(1), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_ny_restaurant), startTime = LocalTime.of(17, 50), endTime = LocalTime.of(18, 50), budget = 30.0, googleMapsPlacesId = "ChIJTVG07iZawokRq9rPs7ib8bY", location = LatLng(40.7142, -73.9985)),
                Spot(id = 1212, index = 11, iconText = 12, date = nyStartDate.plusDays(1), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430))
            )
        )
        val nyDay3 = Date(
            id = 13, index = 2, date = nyStartDate.plusDays(2),
            color = dateColorList[2],
            titleText = context.getString(R.string.mock_trip_ny_day3_title),
            spotList = listOf(
                Spot(id = 1301, index = 0, iconText = 1, date = nyStartDate.plusDays(2), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_breakfast_hotel), startTime = LocalTime.of(8, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430)),
                Spot(id = 1302, index = 1, iconText = 2, date = nyStartDate.plusDays(2), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(9, 30)),
                Spot(id = 1303, index = 2, iconText = 3, date = nyStartDate.plusDays(2), spotType = SpotType.ART, titleText = context.getString(R.string.mock_trip_ny_museum_met), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0), budget = 25.0, googleMapsPlacesId = "ChIJb8Jg9pZYwokR-qHGtvSkLzs", location = LatLng(40.7794, -73.9632), memoText = "Egyptian Art - 1F\nDendur, American Wing - 1F\nEngelhard Court, European Paintings - 2F\nRm 822-825 (Van Gogh)"),
                Spot(id = 1304, index = 3, iconText = 4, date = nyStartDate.plusDays(2), spotType = SpotType.FOOD, titleText = context.getString(R.string.mock_trip_ny_museum_food), startTime = LocalTime.of(13, 0), budget = 20.0),
                Spot(id = 1305, index = 4, iconText = 5, date = nyStartDate.plusDays(2), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_ny_trump_tower), startTime = LocalTime.of(14, 30), googleMapsPlacesId = "ChIJrc9T9fpYwokRdvjYRHT8nI4", location = LatLng(40.7624, -73.9738)),
                Spot(id = 1306, index = 5, iconText = 6, date = nyStartDate.plusDays(2), spotType = SpotType.LANDMARK, titleText = context.getString(R.string.mock_trip_ny_top_of_the_rock), startTime = LocalTime.of(15, 50), endTime = LocalTime.of(17, 40), budget = 40.0, googleMapsPlacesId = "ChIJe7vKMf9YwokRIMYfDz7iF9o", location = LatLng(40.7587, -73.9787), memoText = "Sunset 17:11"),
                Spot(id = 1307, index = 6, iconText = 7, date = nyStartDate.plusDays(2), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_ny_restaurant), startTime = LocalTime.of(18, 15), endTime = LocalTime.of(19, 0), budget = 40.0, googleMapsPlacesId = "ChIJ3aKC18JZwokRV_edM73h9UI", location = LatLng(40.7600, -73.9840)),
                Spot(id = 1308, index = 7, iconText = 8, date = nyStartDate.plusDays(2), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_ny_times_square), startTime = LocalTime.of(19, 10), endTime = LocalTime.of(19, 20), googleMapsPlacesId = "ChIJmQJIxlVYwokRLgeuocVOGVU", location = LatLng(40.7580, -73.9855)),
                Spot(id = 1309, index = 8, iconText = 9, date = nyStartDate.plusDays(2), spotType = SpotType.SUBWAY_STATION, titleText = context.getString(R.string.mock_trip_ny_subway_station), startTime = LocalTime.of(19, 30), endTime = LocalTime.of(19, 35), googleMapsPlacesId = "ChIJp2gUMatZwokRA0ZoPYhQuQ4", location = LatLng(40.7559, -73.9868)),
                Spot(id = 1310, index = 9, iconText = 9, date = nyStartDate.plusDays(2), spotType = SpotType.SUBWAY, titleText = context.getString(R.string.mock_trip_ny_subway), travelDistance = 3.4279373f),
                Spot(id = 1311, index = 10, iconText = 10, date = nyStartDate.plusDays(2), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), startTime = LocalTime.of(20, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430))
            )
        )
        val nyDay4 = Date(
            id = 14, index = 3, date = nyStartDate.plusDays(3),
            color = dateColorList[3],
            titleText = context.getString(R.string.mock_trip_ny_day4_title),
            spotList = listOf(
                Spot(id = 1401, index = 0, iconText = 1, date = nyStartDate.plusDays(3), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), startTime = LocalTime.of(3, 5), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430), memoText = "Carry ESTA, No guide tip"),
                Spot(id = 1402, index = 1, iconText = 2, date = nyStartDate.plusDays(3), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(3, 55), memoText = "Uber reservation 3:55 pickup in front of hotel"),
                Spot(id = 1403, index = 2, iconText = 2, date = nyStartDate.plusDays(3), spotType = SpotType.TAXI, titleText = context.getString(R.string.mock_trip_ny_taxi), budget = 15.0),
                Spot(id = 1404, index = 3, iconText = 3, date = nyStartDate.plusDays(3), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_meeting_point), startTime = LocalTime.of(4, 30), googleMapsPlacesId = "ChIJfcABUVRYwokRNi9ICBT77AM", location = LatLng(40.7584, -73.9843)),
                Spot(id = 1405, index = 4, iconText = 4, date = nyStartDate.plusDays(3), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_ny_restaurant), startTime = LocalTime.of(12, 0), budget = 60.0, memoText = "Lunch at Observation Deck, Tip $20"),
                Spot(id = 1406, index = 5, iconText = 5, date = nyStartDate.plusDays(3), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_ny_niagara_falls), startTime = LocalTime.of(13, 0), googleMapsPlacesId = "ChIJ6XotQQdD04kRW-OcaB4dvik", location = LatLng(43.0828, -79.0742)),
                Spot(id = 1407, index = 6, iconText = 6, date = nyStartDate.plusDays(3), spotType = SpotType.TOUR, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(15, 0), budget = 50.0, memoText = "Duty free shopping"),
                Spot(id = 1408, index = 7, iconText = 7, date = nyStartDate.plusDays(3), spotType = SpotType.FOOD, titleText = context.getString(R.string.mock_trip_ny_food), budget = 15.0, memoText = "Dinner at rest area"),
                Spot(id = 1409, index = 8, iconText = 8, date = nyStartDate.plusDays(3), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_meeting_point), startTime = LocalTime.of(23, 59), googleMapsPlacesId = "ChIJfcABUVRYwokRNi9ICBT77AM", location = LatLng(40.7584, -73.9843)),
                Spot(id = 1410, index = 9, iconText = 8, date = nyStartDate.plusDays(3), spotType = SpotType.TAXI, titleText = context.getString(R.string.mock_trip_ny_taxi), travelDistance = 3.478671f, budget = 20.0),
                Spot(id = 1411, index = 10, iconText = 9, date = nyStartDate.plusDays(3), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430))
            )
        )
        val nyDay5 = Date(
            id = 15, index = 4, date = nyStartDate.plusDays(4),
            color = dateColorList[4],
            titleText = context.getString(R.string.mock_trip_ny_day5_title),
            spotList = listOf(
                Spot(id = 1501, index = 0, iconText = 1, date = nyStartDate.plusDays(4), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_breakfast_hotel), startTime = LocalTime.of(9, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430)),
                Spot(id = 1502, index = 1, iconText = 2, date = nyStartDate.plusDays(4), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(10, 20)),
                Spot(id = 1503, index = 2, iconText = 3, date = nyStartDate.plusDays(4), spotType = SpotType.ARCHITECTURAL_BUILDING, titleText = context.getString(R.string.mock_trip_ny_flatiron_building), startTime = LocalTime.of(10, 50), googleMapsPlacesId = "ChIJZx8c96NZwokRJklw7SVhKt4", location = LatLng(40.7411, -73.9897)),
                Spot(id = 1504, index = 3, iconText = 4, date = nyStartDate.plusDays(4), spotType = SpotType.BUS_TERMINAL, titleText = context.getString(R.string.mock_trip_ny_bus_tour), startTime = LocalTime.of(11, 0), endTime = LocalTime.of(12, 0), budget = 45.0, googleMapsPlacesId = "ChIJk5Ml9aNZwokRcJRcg4o2ITU", location = LatLng(40.7415, -73.9890), memoText = "24hr operation after first scan"),
                Spot(id = 1505, index = 4, iconText = 5, date = nyStartDate.plusDays(4), spotType = SpotType.FOOD, titleText = context.getString(R.string.mock_trip_ny_restaurant), startTime = LocalTime.of(12, 30), budget = 25.0, googleMapsPlacesId = "ChIJw85ZeK9ZwokRLfXj_uCbn9Q", location = LatLng(40.7435, -73.9915)),
                Spot(id = 1506, index = 5, iconText = 6, date = nyStartDate.plusDays(4), spotType = SpotType.TOUR, titleText = context.getString(R.string.mock_trip_ny_high_line), startTime = LocalTime.of(14, 30), googleMapsPlacesId = "ChIJ5bQPhMdZwokRkTwKhVxhP1g", location = LatLng(40.7480, -74.0048)),
                Spot(id = 1507, index = 6, iconText = 7, date = nyStartDate.plusDays(4), spotType = SpotType.TOUR, titleText = context.getString(R.string.mock_trip_ny_little_island), startTime = LocalTime.of(15, 30), googleMapsPlacesId = "ChIJL-CJmTFZwokRsPcqje_bn7w", location = LatLng(40.7420, -74.0100), memoText = "Sunset 17:14"),
                Spot(id = 1508, index = 7, iconText = 8, date = nyStartDate.plusDays(4), spotType = SpotType.STREET_MARKET, titleText = context.getString(R.string.mock_trip_ny_chelsea_market), startTime = LocalTime.of(17, 30), endTime = LocalTime.of(18, 0), googleMapsPlacesId = "ChIJw2lMFL9ZwokRosAtly52YX4", location = LatLng(40.7423, -74.0060)),
                Spot(id = 1509, index = 8, iconText = 9, date = nyStartDate.plusDays(4), spotType = SpotType.FOOD, titleText = context.getString(R.string.mock_trip_ny_restaurant), startTime = LocalTime.of(18, 0), budget = 30.0),
                Spot(id = 1510, index = 9, iconText = 10, date = nyStartDate.plusDays(4), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430))
            )
        )
        val nyDay6 = Date(
            id = 16, index = 5, date = nyStartDate.plusDays(5),
            color = dateColorList[5],
            titleText = context.getString(R.string.mock_trip_ny_day6_title),
            spotList = listOf(
                Spot(id = 1601, index = 0, iconText = 1, date = nyStartDate.plusDays(5), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_breakfast_hotel), startTime = LocalTime.of(8, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430)),
                Spot(id = 1602, index = 1, iconText = 2, date = nyStartDate.plusDays(5), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(9, 30)),
                Spot(id = 1603, index = 2, iconText = 3, date = nyStartDate.plusDays(5), spotType = SpotType.ART, titleText = context.getString(R.string.mock_trip_ny_museum_guggenheim), startTime = LocalTime.of(10, 15), endTime = LocalTime.of(12, 30), budget = 25.0, googleMapsPlacesId = "ChIJmZ5emqJYwokRuDz79o0coAQ", location = LatLng(40.7829, -73.9589), memoText = "Take elevator to top and walk down"),
                Spot(id = 1604, index = 3, iconText = 4, date = nyStartDate.plusDays(5), spotType = SpotType.FAST_FOOD, titleText = context.getString(R.string.mock_trip_ny_restaurant), startTime = LocalTime.of(12, 15), budget = 25.0, googleMapsPlacesId = "ChIJr_284IRYwokR_Ejcc1v_gtU", location = LatLng(40.7845, -73.9650)),
                Spot(id = 1605, index = 4, iconText = 5, date = nyStartDate.plusDays(5), spotType = SpotType.PARK, titleText = context.getString(R.string.mock_trip_ny_central_park), startTime = LocalTime.of(13, 20), googleMapsPlacesId = "ChIJ4zGFAZpYwokRGUGph3Mf37k", location = LatLng(40.7812, -73.9665), memoText = "Bethesda Fountain"),
                Spot(id = 1606, index = 5, iconText = 6, date = nyStartDate.plusDays(5), spotType = SpotType.SUBWAY_STATION, titleText = context.getString(R.string.mock_trip_ny_subway_station), startTime = LocalTime.of(14, 30), googleMapsPlacesId = "ChIJVZfjPvZYwokR-sLEBmjjniw", location = LatLng(40.7680, -73.9818)),
                Spot(id = 1607, index = 6, iconText = 6, date = nyStartDate.plusDays(5), spotType = SpotType.SUBWAY, titleText = context.getString(R.string.mock_trip_ny_subway), travelDistance = 2.3686407f, budget = 2.90),
                Spot(id = 1608, index = 7, iconText = 7, date = nyStartDate.plusDays(5), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_ny_edge), startTime = LocalTime.of(16, 0), endTime = LocalTime.of(17, 30), budget = 40.0, googleMapsPlacesId = "ChIJ3aqq5Q1ZwokRb9hLO7Gyxgw", location = LatLng(40.7538, -74.0022), memoText = "Sunset 5:09"),
                Spot(id = 1609, index = 8, iconText = 8, date = nyStartDate.plusDays(5), spotType = SpotType.SHOPPING, titleText = context.getString(R.string.mock_trip_ny_electronics_store), budget = 100.0, googleMapsPlacesId = "ChIJI93dPbJZwokRIoOEoivEDQs", location = LatLng(40.7485, -73.9935)),
                Spot(id = 1610, index = 9, iconText = 9, date = nyStartDate.plusDays(5), spotType = SpotType.FOOD, titleText = context.getString(R.string.mock_trip_ny_korean_restaurant), startTime = LocalTime.of(18, 40), budget = 35.0, googleMapsPlacesId = "ChIJ3fZz-6hZwokRvjeitvq-5fY", location = LatLng(40.7475, -73.9860)),
                Spot(id = 1611, index = 10, iconText = 10, date = nyStartDate.plusDays(5), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430))
            )
        )
        val nyDay7 = Date(
            id = 17, index = 6, date = nyStartDate.plusDays(6),
            color = dateColorList[6],
            titleText = context.getString(R.string.mock_trip_ny_day7_title),
            spotList = listOf(
                Spot(id = 1701, index = 0, iconText = 1, date = nyStartDate.plusDays(6), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_breakfast_hotel), startTime = LocalTime.of(8, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430)),
                Spot(id = 1702, index = 1, iconText = 2, date = nyStartDate.plusDays(6), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(9, 30)),
                Spot(id = 1703, index = 2, iconText = 3, date = nyStartDate.plusDays(6), spotType = SpotType.TOUR, titleText = context.getString(R.string.mock_trip_ny_public_library), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(10, 50), googleMapsPlacesId = "ChIJqaiomQBZwokRTHOaUG7fUTs", location = LatLng(40.7532, -73.9822), memoText = "Free entry, go to 3F first"),
                Spot(id = 1704, index = 3, iconText = 4, date = nyStartDate.plusDays(6), spotType = SpotType.PARK, titleText = context.getString(R.string.mock_trip_ny_bryant_park), startTime = LocalTime.of(11, 20), googleMapsPlacesId = "ChIJqaiomQBZwokRTHOaUG7fUTs", location = LatLng(40.7536, -73.9832)),
                Spot(id = 1705, index = 4, iconText = 5, date = nyStartDate.plusDays(6), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_ny_restaurant), startTime = LocalTime.of(12, 0), budget = 25.0, googleMapsPlacesId = "ChIJ28_VhFVYwokRigKkUoPI6Ec", location = LatLng(40.7570, -73.9820)),
                Spot(id = 1706, index = 5, iconText = 6, date = nyStartDate.plusDays(6), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_ny_times_square), startTime = LocalTime.of(13, 0), googleMapsPlacesId = "ChIJmQJIxlVYwokRLgeuocVOGVU", location = LatLng(40.7580, -73.9855)),
                Spot(id = 1707, index = 6, iconText = 7, date = nyStartDate.plusDays(6), spotType = SpotType.SHOPPING, titleText = context.getString(R.string.mock_trip_ny_shoe_store), startTime = LocalTime.of(14, 0), budget = 60.0, googleMapsPlacesId = "ChIJ5c8pkVtZwokRyTLXAZpYZow", location = LatLng(40.7545, -73.9880)),
                Spot(id = 1708, index = 7, iconText = 8, date = nyStartDate.plusDays(6), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_ny_empire_state), startTime = LocalTime.of(15, 30), budget = 45.0, googleMapsPlacesId = "ChIJaXQRs6lZwokRY6EFpJnhNNE", location = LatLng(40.7484, -73.9857)),
                Spot(id = 1709, index = 8, iconText = 9, date = nyStartDate.plusDays(6), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_ny_times_square), startTime = LocalTime.of(17, 0), googleMapsPlacesId = "ChIJmQJIxlVYwokRLgeuocVOGVU", location = LatLng(40.7580, -73.9855)),
                Spot(id = 1710, index = 9, iconText = 10, date = nyStartDate.plusDays(6), spotType = SpotType.FAST_FOOD, titleText = context.getString(R.string.mock_trip_ny_fast_food), startTime = LocalTime.of(18, 0), budget = 20.0, googleMapsPlacesId = "ChIJk0y1SqpZwokR7BJIt2nqkwo", location = LatLng(40.7565, -73.9865)),
                Spot(id = 1711, index = 10, iconText = 11, date = nyStartDate.plusDays(6), spotType = SpotType.TOUR, titleText = context.getString(R.string.mock_trip_ny_grand_central), startTime = LocalTime.of(19, 0), googleMapsPlacesId = "ChIJ-b2RmVlZwokRpb1pwEQjss0", location = LatLng(40.7527, -73.9772)),
                Spot(id = 1712, index = 11, iconText = 12, date = nyStartDate.plusDays(6), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430))
            )
        )
        val nyDay8 = Date(
            id = 18, index = 7, date = nyStartDate.plusDays(7),
            color = dateColorList[7],
            titleText = context.getString(R.string.mock_trip_ny_day8_title),
            spotList = listOf(
                Spot(id = 1801, index = 0, iconText = 1, date = nyStartDate.plusDays(7), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_breakfast_hotel), startTime = LocalTime.of(8, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430)),
                Spot(id = 1802, index = 1, iconText = 2, date = nyStartDate.plusDays(7), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(9, 30)),
                Spot(id = 1803, index = 2, iconText = 3, date = nyStartDate.plusDays(7), spotType = SpotType.TOURIST_ATTRACTION, titleText = context.getString(R.string.mock_trip_ny_dumbo), startTime = LocalTime.of(10, 20), googleMapsPlacesId = "ChIJ-7A-bzFawokRw8mvHCzZsc4", location = LatLng(40.7033, -73.9881)),
                Spot(id = 1804, index = 3, iconText = 4, date = nyStartDate.plusDays(7), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_ny_restaurant), startTime = LocalTime.of(11, 30), budget = 25.0, googleMapsPlacesId = "ChIJN7nw03hbwokRHRJaEsdT6_Q", location = LatLng(40.7040, -73.9910)),
                Spot(id = 1805, index = 4, iconText = 5, date = nyStartDate.plusDays(7), spotType = SpotType.SHOPPING, titleText = context.getString(R.string.mock_trip_ny_music_store), startTime = LocalTime.of(12, 30), budget = 50.0, googleMapsPlacesId = "ChIJCentDGRbwokRDiloa9nfVgI", location = LatLng(40.7010, -73.9890)),
                Spot(id = 1806, index = 5, iconText = 6, date = nyStartDate.plusDays(7), spotType = SpotType.COFFEE_TEA, titleText = context.getString(R.string.mock_trip_ny_cafe), budget = 20.0, googleMapsPlacesId = "ChIJkRWC9WFZwokRfa5Uzor3e-g", location = LatLng(40.7140, -73.9650)),
                Spot(id = 1807, index = 6, iconText = 7, date = nyStartDate.plusDays(7), spotType = SpotType.PARK, titleText = context.getString(R.string.mock_trip_ny_domino_park), startTime = LocalTime.of(15, 40), googleMapsPlacesId = "ChIJX7hLk0FZwokRFhRcjtylakg", location = LatLng(40.7170, -73.9675), memoText = "Sunset 17:18"),
                Spot(id = 1808, index = 7, iconText = 8, date = nyStartDate.plusDays(7), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_ny_steak_house), startTime = LocalTime.of(18, 15), budget = 100.0, googleMapsPlacesId = "ChIJR_bK295bwokR8gM6QgEdmkY", location = LatLng(40.7095, -73.9590)),
                Spot(id = 1809, index = 8, iconText = 9, date = nyStartDate.plusDays(7), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430))
            )
        )
        val nyDay9 = Date(
            id = 19, index = 8, date = nyStartDate.plusDays(8),
            color = dateColorList[8],
            titleText = context.getString(R.string.mock_trip_ny_day9_title),
            spotList = listOf(
                Spot(id = 1901, index = 0, iconText = 1, date = nyStartDate.plusDays(8), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_breakfast_hotel), startTime = LocalTime.of(8, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430)),
                Spot(id = 1902, index = 1, iconText = 2, date = nyStartDate.plusDays(8), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(9, 30)),
                Spot(id = 1903, index = 2, iconText = 3, date = nyStartDate.plusDays(8), spotType = SpotType.MUSEUM, titleText = context.getString(R.string.mock_trip_ny_museum_natural_history), startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0), budget = 25.0, googleMapsPlacesId = "ChIJCXoPsPRYwokRsV1MYnKBfaI", location = LatLng(40.7813, -73.9740)),
                Spot(id = 1904, index = 3, iconText = 4, date = nyStartDate.plusDays(8), spotType = SpotType.RESTAURANT, titleText = context.getString(R.string.mock_trip_ny_restaurant), startTime = LocalTime.of(12, 30), budget = 30.0, googleMapsPlacesId = "ChIJZ3o4EVZYwokRf3evFti1FLs", location = LatLng(40.7830, -73.9800)),
                Spot(id = 1905, index = 4, iconText = 5, date = nyStartDate.plusDays(8), spotType = SpotType.SHOPPING, titleText = context.getString(R.string.mock_trip_ny_macys), startTime = LocalTime.of(14, 30), budget = 150.0, googleMapsPlacesId = "ChIJ3xjWra5ZwokRrwJ0KZ4yKNs", location = LatLng(40.7505, -73.9876)),
                Spot(id = 1906, index = 5, iconText = 6, date = nyStartDate.plusDays(8), spotType = SpotType.FOOD, titleText = context.getString(R.string.mock_trip_ny_fast_food), startTime = LocalTime.of(18, 0), budget = 15.0, googleMapsPlacesId = "ChIJNzfuJ65ZwokREXfAYA37Hyk", location = LatLng(40.7485, -73.9885)),
                Spot(id = 1907, index = 6, iconText = 7, date = nyStartDate.plusDays(8), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430))
            )
        )
        val nyDay10 = Date(
            id = 20, index = 9, date = nyStartDate.plusDays(9),
            color = dateColorList[9],
            titleText = context.getString(R.string.mock_trip_ny_day10_title),
            spotList = listOf(
                Spot(id = 2001, index = 0, iconText = 1, date = nyStartDate.plusDays(9), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_hotel), startTime = LocalTime.of(8, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430), memoText = "Checkout by 11:00, store luggage"),
                Spot(id = 2002, index = 1, iconText = 2, date = nyStartDate.plusDays(9), spotType = SpotType.MOVE_POINT, titleText = context.getString(R.string.mock_trip_ny_departure), startTime = LocalTime.of(9, 30)),
                Spot(id = 2003, index = 2, iconText = 3, date = nyStartDate.plusDays(9), spotType = SpotType.PARK, titleText = context.getString(R.string.mock_trip_ny_gantry_park), startTime = LocalTime.of(10, 0), googleMapsPlacesId = "ChIJ7eAADSJawokRObgUbl1g6mk", location = LatLng(40.7455, -73.9587)),
                Spot(id = 2004, index = 3, iconText = 4, date = nyStartDate.plusDays(9), spotType = SpotType.ART, titleText = context.getString(R.string.mock_trip_ny_museum_moma), startTime = LocalTime.of(11, 0), budget = 30.0, googleMapsPlacesId = "ChIJKxDbe_lYwokRVf__s8CPn-o", location = LatLng(40.7614, -73.9776)),
                Spot(id = 2005, index = 4, iconText = 5, date = nyStartDate.plusDays(9), spotType = SpotType.FAST_FOOD, titleText = context.getString(R.string.mock_trip_ny_fast_food), startTime = LocalTime.of(12, 0), budget = 20.0, googleMapsPlacesId = "ChIJwfFb4flYwokRP78kZ3ER5a8", location = LatLng(40.7630, -73.9750)),
                Spot(id = 2006, index = 5, iconText = 6, date = nyStartDate.plusDays(9), spotType = SpotType.TOUR, titleText = context.getString(R.string.mock_trip_ny_washington_sq_park), startTime = LocalTime.of(13, 0), googleMapsPlacesId = "ChIJjX494pBZwokRGH620d9eYfo", location = LatLng(40.7308, -73.9973)),
                Spot(id = 2007, index = 6, iconText = 7, date = nyStartDate.plusDays(9), spotType = SpotType.SHOPPING, titleText = context.getString(R.string.mock_trip_ny_soho), startTime = LocalTime.of(14, 0), budget = 100.0, googleMapsPlacesId = "ChIJ8-JRXoxZwokRGPiQ9Ek0L84", location = LatLng(40.7233, -74.0030)),
                Spot(id = 2008, index = 7, iconText = 8, date = nyStartDate.plusDays(9), spotType = SpotType.FOOD, titleText = context.getString(R.string.mock_trip_ny_food), startTime = LocalTime.of(17, 30), budget = 30.0),
                Spot(id = 2009, index = 8, iconText = 9, date = nyStartDate.plusDays(9), spotType = SpotType.HOTEL, titleText = context.getString(R.string.mock_trip_ny_airport_departure), startTime = LocalTime.of(19, 0), googleMapsPlacesId = "ChIJEYl7DyhZwokR8OBIl2a6NwI", location = LatLng(40.7513, -73.9430)),
                Spot(id = 2010, index = 9, iconText = 9, date = nyStartDate.plusDays(9), spotType = SpotType.MOVE, titleText = context.getString(R.string.mock_trip_ny_airport_express), travelDistance = 20.558727f, budget = 25.0, memoText = "Subway -> Walk -> Express Bus -> Newark Terminal B")
            )
        )
        val nyDay11 = Date(
            id = 21, index = 10, date = nyStartDate.plusDays(10),
            color = dateColorList[10],
            titleText = context.getString(R.string.mock_trip_ny_day11_title),
            spotList = listOf(
                Spot(id = 2101, index = 0, iconText = 0, date = nyStartDate.plusDays(10), spotType = SpotType.AIRPLANE, titleText = context.getString(R.string.mock_trip_ny_flight_icn_ewr), startTime = LocalTime.of(0, 1), travelDistance = 11100.283f, memoText = "EWR - ICN / YP132")
            )
        )
        val nyDay12 = Date(
            id = 22, index = 11, date = nyStartDate.plusDays(11),
            color = dateColorList[11],
            titleText = context.getString(R.string.mock_trip_ny_day12_title),
            spotList = listOf(
                Spot(id = 2201, index = 0, iconText = 1, date = nyStartDate.plusDays(11), spotType = SpotType.AIRPORT, titleText = context.getString(R.string.mock_trip_ny_icn_airport), startTime = LocalTime.of(5, 40), googleMapsPlacesId = "ChIJWfpeOoOaezUR1L5cy5agS40", location = LatLng(37.4485, 126.4512))
            )
        )

        val nyTrip = Trip(
            id = 67890, managerId = "mock", unitOfCurrencyType = CurrencyType.USD,
            titleText = context.getString(R.string.mock_trip_ny_title),
            startDate = nyStartDate.toString(), endDate = nyStartDate.plusDays(11).toString(),
            memoText = context.getString(R.string.mock_trip_ny_memo),
            dateList = listOf(nyDay1, nyDay2, nyDay3, nyDay4, nyDay5, nyDay6, nyDay7, nyDay8, nyDay9, nyDay10, nyDay11, nyDay12),
            firstCreatedTime = nowDateTime, lastModifiedTime = nowDateTime, editable = true
        )

        return listOf(jejuTrip, nyTrip)
    }
}
