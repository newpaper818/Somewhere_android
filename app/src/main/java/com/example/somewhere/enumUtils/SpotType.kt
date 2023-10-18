package com.example.somewhere.enumUtils

import androidx.annotation.StringRes
import com.example.somewhere.R

import com.example.somewhere.enumUtils.SpotType.*
import com.example.somewhere.ui.theme.MyColor
import com.example.somewhere.ui.theme.blackInt
import com.example.somewhere.ui.theme.etcColor
import com.example.somewhere.ui.theme.foodColor
import com.example.somewhere.ui.theme.lodgingColor
import com.example.somewhere.ui.theme.moveColor
import com.example.somewhere.ui.theme.movePointColor
import com.example.somewhere.ui.theme.onEtcColor
import com.example.somewhere.ui.theme.onFoodColor
import com.example.somewhere.ui.theme.onLodgingColor
import com.example.somewhere.ui.theme.onMoveColor
import com.example.somewhere.ui.theme.onMovePointColor
import com.example.somewhere.ui.theme.onTourColor
import com.example.somewhere.ui.theme.tourColor
import com.example.somewhere.ui.theme.whiteInt

enum class SpotTypeGroup(
    @StringRes val textId: Int,
    val color: MyColor,
    val memberList: List<SpotType>
) {
    TOUR(R.string.tour, MyColor(tourColor, onTourColor),
        listOf(
            SpotType.TOUR,
            TOURIST_ATTRACTION, HISTORIC_SITE, ARCHITECTURAL_BUILDING, LANDMARK, RELIGIOUS_SITE, MONUMENT_STATUE,
            ACTIVITY, LEISURE, SPORT, OUTDOOR, ADVENTURE,
            ENTERTAINMENT, FESTIVAL, ART, MUSIC, MOVIE,
            MUSEUM, EXHIBITION,
            PARK, GARDEN, BEACH,
            SHOPPING, STREET_MARKET, GIFT_SHOP
        )
    ),

    MOVE(R.string.move, MyColor(moveColor, onMoveColor),
        listOf(
            SpotType.MOVE,
            AIRPLANE, HELICOPTER, HOT_AIR_BALLOON,
            TRAIN, SUBWAY, TRAM,
            BUS, TAXI, CAR,
            SHIP, YACHT,
            BICYCLE, ELECTRIC_KICKBOARD,
            WALK, RUNNING, HIKING
        )
    ),

    MOVE_POINT(R.string.move_point, MyColor(movePointColor, onMovePointColor),
        listOf(
            SpotType.MOVE_POINT,
            TERMINAL, AIRPORT, BUS_TERMINAL, BUS_STOP, SUBWAY_STATION, TRAIN_STATION, PORT
        )
    ),

    FOOD(R.string.food, MyColor(foodColor, onFoodColor),
        listOf(
            SpotType.FOOD,
            COFFEE_TEA, RESTAURANT, FAST_FOOD, DESSERT, SNACK, BAR_PUB
        )
    ),

    LODGING(R.string.lodging, MyColor(lodgingColor, onLodgingColor),
        listOf(
            SpotType.LODGING,
            HOTEL, MOTEL, GUEST_HOUSE, RESORT, HOSTEL, AIR_BNB, PENSION, CAMPING
        )
    ),

    ETC(R.string.etc, MyColor(etcColor, onEtcColor),
        listOf(
            SpotType.ETC
        )
    )
}

enum class SpotType(
    @StringRes val textId: Int,
    val group: SpotTypeGroup
){
    TOUR(R.string.tour, SpotTypeGroup.TOUR),
    MOVE(R.string.move, SpotTypeGroup.MOVE),
    MOVE_POINT(R.string.move_point, SpotTypeGroup.MOVE_POINT),
    FOOD(R.string.food, SpotTypeGroup.FOOD),
    LODGING(R.string.lodging, SpotTypeGroup.LODGING),
    ETC(R.string.etc, SpotTypeGroup.ETC),

    //MOVE
    AIRPLANE(R.string.airplane, SpotTypeGroup.MOVE),
    HELICOPTER(R.string.helicopter, SpotTypeGroup.MOVE),
    HOT_AIR_BALLOON(R.string.hot_air_balloon, SpotTypeGroup.MOVE),

    TRAIN(R.string.train, SpotTypeGroup.MOVE),
    SUBWAY(R.string.subway, SpotTypeGroup.MOVE),
    TRAM(R.string.tram, SpotTypeGroup.MOVE),

    BUS(R.string.bus, SpotTypeGroup.MOVE),
    TAXI(R.string.taxi, SpotTypeGroup.MOVE),
    CAR(R.string.car, SpotTypeGroup.MOVE),

    SHIP(R.string.ship, SpotTypeGroup.MOVE),
    YACHT(R.string.yacht, SpotTypeGroup.MOVE),

    BICYCLE(R.string.bicycle, SpotTypeGroup.MOVE),
    ELECTRIC_KICKBOARD(R.string.electric_kickboard, SpotTypeGroup.MOVE),

    WALK(R.string.walk, SpotTypeGroup.MOVE),
    RUNNING(R.string.running, SpotTypeGroup.MOVE),
    HIKING(R.string.hiking, SpotTypeGroup.MOVE),



    //MOVE POINT
    TERMINAL(R.string.terminal, SpotTypeGroup.MOVE_POINT),
    AIRPORT(R.string.airport, SpotTypeGroup.MOVE_POINT),
    BUS_TERMINAL(R.string.bus_terminal, SpotTypeGroup.MOVE_POINT),
    BUS_STOP(R.string.bus_stop, SpotTypeGroup.MOVE_POINT),
    SUBWAY_STATION(R.string.subway_station, SpotTypeGroup.MOVE_POINT),
    TRAIN_STATION(R.string.train_station, SpotTypeGroup.MOVE_POINT),
    PORT(R.string.port, SpotTypeGroup.MOVE_POINT),



    //TOUR
    TOURIST_ATTRACTION(R.string.tourist_attraction, SpotTypeGroup.TOUR),
    HISTORIC_SITE(R.string.historic_site, SpotTypeGroup.TOUR),
    ARCHITECTURAL_BUILDING(R.string.architectural_building, SpotTypeGroup.TOUR),
    LANDMARK(R.string.landmark, SpotTypeGroup.TOUR),
    RELIGIOUS_SITE(R.string.religious_site, SpotTypeGroup.TOUR),
    MONUMENT_STATUE(R.string.monument_statue, SpotTypeGroup.TOUR),

    ACTIVITY(R.string.activity, SpotTypeGroup.TOUR),
    LEISURE(R.string.leisure, SpotTypeGroup.TOUR),
    SPORT(R.string.sport, SpotTypeGroup.TOUR),
    OUTDOOR(R.string.outdoor, SpotTypeGroup.TOUR),
    ADVENTURE(R.string.adventure, SpotTypeGroup.TOUR),

    ENTERTAINMENT(R.string.entertainment, SpotTypeGroup.TOUR),
    FESTIVAL(R.string.festival, SpotTypeGroup.TOUR),
    ART(R.string.art, SpotTypeGroup.TOUR),
    MUSIC(R.string.music, SpotTypeGroup.TOUR),
    MOVIE(R.string.movie, SpotTypeGroup.TOUR),

    MUSEUM(R.string.museum, SpotTypeGroup.TOUR),
    EXHIBITION(R.string.exhibition, SpotTypeGroup.TOUR),

    PARK(R.string.park, SpotTypeGroup.TOUR),
    GARDEN(R.string.garden, SpotTypeGroup.TOUR),
    BEACH(R.string.beach, SpotTypeGroup.TOUR),

    SHOPPING(R.string.shopping, SpotTypeGroup.TOUR),
    STREET_MARKET(R.string.street_market, SpotTypeGroup.TOUR),
    GIFT_SHOP(R.string.gift_shop, SpotTypeGroup.TOUR),



    //FOOD
    COFFEE_TEA(R.string.coffee_tea, SpotTypeGroup.FOOD),
    RESTAURANT(R.string.restaurant, SpotTypeGroup.FOOD),
    FAST_FOOD(R.string.fast_food, SpotTypeGroup.FOOD),
    DESSERT(R.string.dessert, SpotTypeGroup.FOOD),
    SNACK(R.string.snack, SpotTypeGroup.FOOD),
    BAR_PUB(R.string.bar_pub, SpotTypeGroup.FOOD),


    //LODGING
    HOTEL(R.string.hotel, SpotTypeGroup.LODGING),
    MOTEL(R.string.motel, SpotTypeGroup.LODGING),
    GUEST_HOUSE(R.string.guest_house, SpotTypeGroup.LODGING),
    RESORT(R.string.resort, SpotTypeGroup.LODGING),
    HOSTEL(R.string.hostel, SpotTypeGroup.LODGING),
    AIR_BNB(R.string.air_bnb, SpotTypeGroup.LODGING),
    PENSION(R.string.pension, SpotTypeGroup.LODGING),
    CAMPING(R.string.camping, SpotTypeGroup.LODGING)


    //ETC


    ;

    fun isMove():Boolean{
        return this.group == SpotTypeGroup.MOVE
    }

    fun isNotMove():Boolean{
        return this.group != SpotTypeGroup.MOVE
    }
}

