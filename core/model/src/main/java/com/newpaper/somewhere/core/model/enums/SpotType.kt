package com.newpaper.somewhere.core.model.enums

import com.newpaper.somewhere.core.model.data.MyColor

const val blackInt = 0xff000000.toInt()
const val whiteInt = 0xffffffff.toInt()

//spot type group color
const val tourColor =       0xff493cfa.toInt()
const val moveColor =       0xff47de9f.toInt()
const val movePointColor =  0xff26b502.toInt()
const val foodColor =       0xFFedb25f.toInt()
const val lodgingColor =    0xff901aeb.toInt()
const val etcColor =        0xff8c8c8c.toInt()

const val onTourColor =     whiteInt
const val onMoveColor =     blackInt
const val onMovePointColor = blackInt
const val onFoodColor =     blackInt
const val onLodgingColor =  whiteInt
const val onEtcColor =      blackInt

enum class SpotTypeGroup(
    val color: MyColor,
    val memberList: List<SpotType>
) {
    TOUR(MyColor(tourColor, onTourColor),
        listOf(
            SpotType.TOUR,
            SpotType.TOURIST_ATTRACTION, SpotType.HISTORIC_SITE, SpotType.ARCHITECTURAL_BUILDING, SpotType.LANDMARK, SpotType.RELIGIOUS_SITE, SpotType.MONUMENT_STATUE,
            SpotType.ACTIVITY, SpotType.LEISURE, SpotType.SPORT, SpotType.OUTDOOR, SpotType.ADVENTURE,
            SpotType.ENTERTAINMENT, SpotType.FESTIVAL, SpotType.ART, SpotType.MUSIC, SpotType.MOVIE,
            SpotType.MUSEUM, SpotType.EXHIBITION,
            SpotType.PARK, SpotType.GARDEN, SpotType.BEACH,
            SpotType.SHOPPING, SpotType.STREET_MARKET, SpotType.GIFT_SHOP
        )
    ),

    MOVE(MyColor(moveColor, onMoveColor),
        listOf(
            SpotType.MOVE,
            SpotType.AIRPLANE, SpotType.HELICOPTER, SpotType.HOT_AIR_BALLOON,
            SpotType.TRAIN, SpotType.SUBWAY, SpotType.TRAM,
            SpotType.BUS, SpotType.TAXI, SpotType.CAR,
            SpotType.SHIP, SpotType.YACHT,
            SpotType.BICYCLE, SpotType.ELECTRIC_KICKBOARD,
            SpotType.WALK, SpotType.RUNNING, SpotType.HIKING
        )
    ),

    MOVE_POINT(MyColor(movePointColor, onMovePointColor),
        listOf(
            SpotType.MOVE_POINT,
            SpotType.TERMINAL, SpotType.AIRPORT, SpotType.BUS_TERMINAL, SpotType.BUS_STOP, SpotType.SUBWAY_STATION, SpotType.TRAIN_STATION, SpotType.PORT
        )
    ),

    FOOD(MyColor(foodColor, onFoodColor),
        listOf(
            SpotType.FOOD,
            SpotType.COFFEE_TEA, SpotType.RESTAURANT, SpotType.FAST_FOOD, SpotType.DESSERT, SpotType.SNACK, SpotType.BAR_PUB
        )
    ),

    LODGING(MyColor(lodgingColor, onLodgingColor),
        listOf(
            SpotType.LODGING,
            SpotType.HOTEL, SpotType.MOTEL, SpotType.GUEST_HOUSE, SpotType.RESORT, SpotType.HOSTEL, SpotType.AIR_BNB, SpotType.PENSION, SpotType.CAMPING
        )
    ),

    ETC(MyColor(etcColor, onEtcColor),
        listOf(
            SpotType.ETC,
            SpotType.HOME
        )
    )
}

enum class SpotType(
    val group: SpotTypeGroup
){
    TOUR(SpotTypeGroup.TOUR),
    MOVE(SpotTypeGroup.MOVE),
    MOVE_POINT(SpotTypeGroup.MOVE_POINT),
    FOOD(SpotTypeGroup.FOOD),
    LODGING(SpotTypeGroup.LODGING),
    ETC(SpotTypeGroup.ETC),

    //MOVE
    AIRPLANE(SpotTypeGroup.MOVE),
    HELICOPTER(SpotTypeGroup.MOVE),
    HOT_AIR_BALLOON(SpotTypeGroup.MOVE),

    TRAIN(SpotTypeGroup.MOVE),
    SUBWAY(SpotTypeGroup.MOVE),
    TRAM(SpotTypeGroup.MOVE),

    BUS(SpotTypeGroup.MOVE),
    TAXI(SpotTypeGroup.MOVE),
    CAR(SpotTypeGroup.MOVE),

    SHIP(SpotTypeGroup.MOVE),
    YACHT(SpotTypeGroup.MOVE),
    CABLE_CAR( SpotTypeGroup.MOVE),

    BICYCLE(SpotTypeGroup.MOVE),
    ELECTRIC_KICKBOARD(SpotTypeGroup.MOVE),

    WALK(SpotTypeGroup.MOVE),
    RUNNING(SpotTypeGroup.MOVE),
    HIKING(SpotTypeGroup.MOVE),



    //MOVE POINT
    TERMINAL(SpotTypeGroup.MOVE_POINT),
    AIRPORT(SpotTypeGroup.MOVE_POINT),
    BUS_TERMINAL(SpotTypeGroup.MOVE_POINT),
    BUS_STOP(SpotTypeGroup.MOVE_POINT),
    SUBWAY_STATION(SpotTypeGroup.MOVE_POINT),
    TRAIN_STATION(SpotTypeGroup.MOVE_POINT),
    PORT(SpotTypeGroup.MOVE_POINT),



    //TOUR
    TOURIST_ATTRACTION(SpotTypeGroup.TOUR),
    HISTORIC_SITE(SpotTypeGroup.TOUR),
    ARCHITECTURAL_BUILDING(SpotTypeGroup.TOUR),
    LANDMARK(SpotTypeGroup.TOUR),
    RELIGIOUS_SITE(SpotTypeGroup.TOUR),
    MONUMENT_STATUE(SpotTypeGroup.TOUR),

    ACTIVITY(SpotTypeGroup.TOUR),
    LEISURE(SpotTypeGroup.TOUR),
    SPORT(SpotTypeGroup.TOUR),
    OUTDOOR(SpotTypeGroup.TOUR),
    ADVENTURE(SpotTypeGroup.TOUR),

    ENTERTAINMENT(SpotTypeGroup.TOUR),
    FESTIVAL(SpotTypeGroup.TOUR),
    ART(SpotTypeGroup.TOUR),
    MUSIC(SpotTypeGroup.TOUR),
    MOVIE(SpotTypeGroup.TOUR),

    MUSEUM(SpotTypeGroup.TOUR),
    EXHIBITION(SpotTypeGroup.TOUR),

    PARK(SpotTypeGroup.TOUR),
    GARDEN(SpotTypeGroup.TOUR),
    BEACH(SpotTypeGroup.TOUR),

    SHOPPING(SpotTypeGroup.TOUR),
    STREET_MARKET(SpotTypeGroup.TOUR),
    GIFT_SHOP(SpotTypeGroup.TOUR),



    //FOOD
    COFFEE_TEA(SpotTypeGroup.FOOD),
    RESTAURANT(SpotTypeGroup.FOOD),
    FAST_FOOD(SpotTypeGroup.FOOD),
    DESSERT(SpotTypeGroup.FOOD),
    SNACK(SpotTypeGroup.FOOD),
    BAR_PUB(SpotTypeGroup.FOOD),


    //LODGING
    HOTEL(SpotTypeGroup.LODGING),
    MOTEL(SpotTypeGroup.LODGING),
    GUEST_HOUSE(SpotTypeGroup.LODGING),
    RESORT(SpotTypeGroup.LODGING),
    HOSTEL(SpotTypeGroup.LODGING),
    AIR_BNB(SpotTypeGroup.LODGING),
    PENSION(SpotTypeGroup.LODGING),
    CAMPING(SpotTypeGroup.LODGING),


    //ETC
    HOME(SpotTypeGroup.ETC),

    ;

    fun isMove():Boolean{
        return this.group == SpotTypeGroup.MOVE
    }

    fun isNotMove():Boolean{
        return this.group != SpotTypeGroup.MOVE
    }
}

fun getSpotTypeFromString(value: String): SpotType{
    return try {
        SpotType.valueOf(value)
    } catch(e: IllegalArgumentException) {
        SpotType.TOUR
    }
}