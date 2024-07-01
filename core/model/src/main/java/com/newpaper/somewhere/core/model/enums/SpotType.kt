package com.newpaper.somewhere.core.model.enums

import androidx.annotation.StringRes
import com.newpaper.somewhere.core.model.R
import com.newpaper.somewhere.core.model.data.MyColor

private const val blackInt = 0xff000000.toInt()
private const val whiteInt = 0xffffffff.toInt()

//spot type group color
private const val tourColor =       0xff493cfa.toInt()
private const val moveColor =       0xff47de9f.toInt()
private const val movePointColor =  0xff26b502.toInt()
private const val foodColor =       0xFFedb25f.toInt()
private const val lodgingColor =    0xff901aeb.toInt()
private const val etcColor =        0xff8c8c8c.toInt()

private const val onTourColor =     whiteInt
private const val onMoveColor =     blackInt
private const val onMovePointColor = blackInt
private const val onFoodColor =     blackInt
private const val onLodgingColor =  whiteInt
private const val onEtcColor =      blackInt


fun getSpotTypeList(
    spotTypeGroup: SpotTypeGroup
): List<SpotType>{
    return when (spotTypeGroup){
        SpotTypeGroup.TOUR -> listOf(
            SpotType.TOUR,
            SpotType.TOURIST_ATTRACTION, SpotType.HISTORIC_SITE, SpotType.ARCHITECTURAL_BUILDING, SpotType.LANDMARK, SpotType.RELIGIOUS_SITE, SpotType.MONUMENT_STATUE,
            SpotType.ACTIVITY, SpotType.LEISURE, SpotType.SPORT, SpotType.OUTDOOR, SpotType.ADVENTURE,
            SpotType.ENTERTAINMENT, SpotType.FESTIVAL, SpotType.ART, SpotType.MUSIC, SpotType.MOVIE,
            SpotType.MUSEUM, SpotType.EXHIBITION,
            SpotType.PARK, SpotType.GARDEN, SpotType.BEACH,
            SpotType.SHOPPING, SpotType.STREET_MARKET, SpotType.GIFT_SHOP
        )
        SpotTypeGroup.MOVE -> listOf(
            SpotType.MOVE,
            SpotType.AIRPLANE, SpotType.HELICOPTER, SpotType.HOT_AIR_BALLOON,
            SpotType.TRAIN, SpotType.SUBWAY, SpotType.TRAM,
            SpotType.BUS, SpotType.TAXI, SpotType.CAR,
            SpotType.SHIP, SpotType.YACHT, SpotType.CABLE_CAR,
            SpotType.BICYCLE, SpotType.ELECTRIC_KICKBOARD,
            SpotType.WALK, SpotType.RUNNING, SpotType.HIKING
        )
        SpotTypeGroup.MOVE_POINT -> listOf(
            SpotType.MOVE_POINT,
            SpotType.TERMINAL, SpotType.AIRPORT, SpotType.BUS_TERMINAL, SpotType.BUS_STOP, SpotType.SUBWAY_STATION, SpotType.TRAIN_STATION, SpotType.PORT
        )
        SpotTypeGroup.FOOD -> listOf(
            SpotType.FOOD,
            SpotType.COFFEE_TEA, SpotType.RESTAURANT, SpotType.FAST_FOOD, SpotType.DESSERT, SpotType.SNACK, SpotType.BAR_PUB
        )
        SpotTypeGroup.LODGING -> listOf(
            SpotType.LODGING,
            SpotType.HOTEL, SpotType.MOTEL, SpotType.GUEST_HOUSE, SpotType.RESORT, SpotType.HOSTEL, SpotType.AIR_BNB, SpotType.PENSION, SpotType.CAMPING
        )
        SpotTypeGroup.ETC -> listOf(
            SpotType.ETC, SpotType.HOME
        )
    }
}



enum class SpotTypeGroup(
    @StringRes val textId: Int,
    val color: MyColor
) {
    TOUR(R.string.tour, MyColor(tourColor, onTourColor)),

    MOVE(R.string.move, MyColor(moveColor, onMoveColor)),

    MOVE_POINT(R.string.move_point, MyColor(movePointColor, onMovePointColor)),

    FOOD(R.string.food, MyColor(foodColor, onFoodColor)),

    LODGING(R.string.lodging, MyColor(lodgingColor, onLodgingColor)),

    ETC(R.string.etc, MyColor(etcColor, onEtcColor))
}

enum class SpotType(
    val iconText: String,
    @StringRes val textId: Int,
    val group: SpotTypeGroup
){
    TOUR("🧳", R.string.tour, SpotTypeGroup.TOUR),
    MOVE("🚶", R.string.move, SpotTypeGroup.MOVE),
    MOVE_POINT("🏁", R.string.move_point, SpotTypeGroup.MOVE_POINT),
    FOOD("🥪", R.string.food, SpotTypeGroup.FOOD),
    LODGING("🛌", R.string.lodging, SpotTypeGroup.LODGING),
    ETC("😀", R.string.etc, SpotTypeGroup.ETC),

    //MOVE
    AIRPLANE("✈️", R.string.airplane, SpotTypeGroup.MOVE),
    HELICOPTER("🚁", R.string.helicopter, SpotTypeGroup.MOVE),
    HOT_AIR_BALLOON("🎈", R.string.hot_air_balloon, SpotTypeGroup.MOVE),

    TRAIN("🚅", R.string.train, SpotTypeGroup.MOVE),
    SUBWAY("🚇", R.string.subway, SpotTypeGroup.MOVE),
    TRAM("🚋", R.string.tram, SpotTypeGroup.MOVE),

    BUS("🚌", R.string.bus, SpotTypeGroup.MOVE),
    TAXI("🚕", R.string.taxi, SpotTypeGroup.MOVE),
    CAR("🚗", R.string.car, SpotTypeGroup.MOVE),

    SHIP("🛳️", R.string.ship, SpotTypeGroup.MOVE),
    YACHT("🛥️", R.string.yacht, SpotTypeGroup.MOVE),
    CABLE_CAR("🚠", R.string.cable_car, SpotTypeGroup.MOVE),

    BICYCLE("🚴", R.string.bicycle, SpotTypeGroup.MOVE),
    ELECTRIC_KICKBOARD("🛴", R.string.electric_kickboard, SpotTypeGroup.MOVE),

    WALK("🚶", R.string.walk, SpotTypeGroup.MOVE),
    RUNNING("🏃", R.string.running, SpotTypeGroup.MOVE),
    HIKING("🥾", R.string.hiking, SpotTypeGroup.MOVE),



    //MOVE POINT
    TERMINAL("🏢", R.string.terminal, SpotTypeGroup.MOVE_POINT),
    AIRPORT("🛫", R.string.airport, SpotTypeGroup.MOVE_POINT),
    BUS_TERMINAL("🚍", R.string.bus_terminal, SpotTypeGroup.MOVE_POINT),
    BUS_STOP("🚏", R.string.bus_stop, SpotTypeGroup.MOVE_POINT),
    SUBWAY_STATION("🚉", R.string.subway_station, SpotTypeGroup.MOVE_POINT),
    TRAIN_STATION("🛤️", R.string.train_station, SpotTypeGroup.MOVE_POINT),
    PORT("⚓️", R.string.port, SpotTypeGroup.MOVE_POINT),



    //TOUR
    TOURIST_ATTRACTION("🗽", R.string.tourist_attraction, SpotTypeGroup.TOUR),
    HISTORIC_SITE("🏛️", R.string.historic_site, SpotTypeGroup.TOUR),
    ARCHITECTURAL_BUILDING("🏢", R.string.architectural_building, SpotTypeGroup.TOUR),
    LANDMARK("🗼", R.string.landmark, SpotTypeGroup.TOUR),
    RELIGIOUS_SITE("🙏", R.string.religious_site, SpotTypeGroup.TOUR),
    MONUMENT_STATUE("🗿", R.string.monument_statue, SpotTypeGroup.TOUR),

    ACTIVITY("🏀", R.string.activity, SpotTypeGroup.TOUR),
    LEISURE("🎱", R.string.leisure, SpotTypeGroup.TOUR),
    SPORT("⚽️", R.string.sport, SpotTypeGroup.TOUR),
    OUTDOOR("⛰️", R.string.outdoor, SpotTypeGroup.TOUR),
    ADVENTURE("🧭", R.string.adventure, SpotTypeGroup.TOUR),

    ENTERTAINMENT("🎮", R.string.entertainment, SpotTypeGroup.TOUR),
    FESTIVAL("🎉", R.string.festival, SpotTypeGroup.TOUR),
    ART("🎨", R.string.art, SpotTypeGroup.TOUR),
    MUSIC("🎧", R.string.music, SpotTypeGroup.TOUR),
    MOVIE("🎬", R.string.movie, SpotTypeGroup.TOUR),

    MUSEUM("🏺", R.string.museum, SpotTypeGroup.TOUR),
    EXHIBITION("🖼️", R.string.exhibition, SpotTypeGroup.TOUR),

    PARK("🌳", R.string.park, SpotTypeGroup.TOUR),
    GARDEN("🪴", R.string.garden, SpotTypeGroup.TOUR),
    BEACH("🏖️", R.string.beach, SpotTypeGroup.TOUR),

    SHOPPING("🛍️", R.string.shopping, SpotTypeGroup.TOUR),
    STREET_MARKET("🏪", R.string.street_market, SpotTypeGroup.TOUR),
    GIFT_SHOP("🎁", R.string.gift_shop, SpotTypeGroup.TOUR),



    //FOOD
    COFFEE_TEA("☕️", R.string.coffee_tea, SpotTypeGroup.FOOD),
    RESTAURANT("🥘", R.string.restaurant, SpotTypeGroup.FOOD),
    FAST_FOOD("🍔", R.string.fast_food, SpotTypeGroup.FOOD),
    DESSERT("🍰", R.string.dessert, SpotTypeGroup.FOOD),
    SNACK("🍟", R.string.snack, SpotTypeGroup.FOOD),
    BAR_PUB("🍷", R.string.bar_pub, SpotTypeGroup.FOOD),


    //LODGING
    HOTEL("🏨", R.string.hotel, SpotTypeGroup.LODGING),
    MOTEL("🏩", R.string.motel, SpotTypeGroup.LODGING),
    GUEST_HOUSE("🏠", R.string.guest_house, SpotTypeGroup.LODGING),
    RESORT("🏘️", R.string.resort, SpotTypeGroup.LODGING),
    HOSTEL("🏘️", R.string.hostel, SpotTypeGroup.LODGING),
    AIR_BNB("🏠", R.string.air_bnb, SpotTypeGroup.LODGING),
    PENSION("🛏️", R.string.pension, SpotTypeGroup.LODGING),
    CAMPING("⛺️", R.string.camping, SpotTypeGroup.LODGING),


    //ETC
    HOME("🏠", R.string.home, SpotTypeGroup.ETC),

    ;

    fun isMove():Boolean{
        return this.group == SpotTypeGroup.MOVE
    }

    fun isNotMove():Boolean{
        return this.group != SpotTypeGroup.MOVE
    }
}