package com.newpaper.somewhere.core.model.enums

import androidx.annotation.StringRes
import com.newpaper.somewhere.core.model.R

//List of ISO 4217 currency codes
enum class CurrencyType(
    val symbol: String,
    val num: Int,
    val numberOfDecimalPlaces: Int, //소수점 아래 자리 수
    @StringRes val currencyName: Int,
) {
    AUD("$", 36, 2, R.string.aud_name),
    CAD("$",124, 2, R.string.cad_name),
    CNY("¥",156, 2, R.string.cny_name),
    EGP("£",818, 2, R.string.egp_name),
    EUR("€",978, 2, R.string.eur_name),
    GBP("£",826, 2, R.string.gbp_name),
    INR("₹",356, 2, R.string.inr_name),
    JPY("¥",392, 0, R.string.jpy_name),
    KRW("₩",410, 0, R.string.krw_name),
    RUB("₽",643, 2, R.string.rub_name),
    THB("฿",764, 2, R.string.thb_name),
    TWD("$",901, 2, R.string.twd_name),
    USD("$",840, 2, R.string.usd_name),
    VND("₫",704, 0, R.string.vnd_name)
}

fun getCurrencyTypeFromString(value: String): CurrencyType{
    return try{
        CurrencyType.valueOf(value)
    }catch (e: IllegalArgumentException){
        CurrencyType.USD
    }
}