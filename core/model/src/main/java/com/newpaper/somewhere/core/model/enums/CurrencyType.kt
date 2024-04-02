package com.newpaper.somewhere.core.model.enums

//List of ISO 4217 currency codes
enum class CurrencyType(
    val symbol: String,
    val code: Int,
    val numberOfDecimalPlaces: Int, //소수점 아래 자리 수
){
    AUD("$", 36, 2),
    CAD("$",124, 2),
    CNY("¥",156, 2),
    EGP("£",818, 2),
    EUR("€",978, 2),
    GBP("£",826, 2),
    INR("₹",356, 2),
    JPY("¥",392, 0),
    KRW("₩",410, 0),
    RUB("₽",643, 2),
    THB("฿",764, 2),
    TWD("$",901, 2),
    USD("$",840, 2),
    VND("₫",704, 0)
}

fun getCurrencyTypeFromString(value: String): CurrencyType? {
    return try{
        CurrencyType.valueOf(value)
    } catch (e: IllegalArgumentException){
        null
    }
}