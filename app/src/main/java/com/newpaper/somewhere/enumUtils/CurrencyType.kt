package com.newpaper.somewhere.enumUtils

import androidx.annotation.StringRes
import com.newpaper.somewhere.R

//List of ISO 4217 currency codes
enum class CurrencyType(
    @StringRes val code: Int,
    val symbol: String,
    val num: Int,
    val numberOfDecimalPlaces: Int, //소수점 아래 자리 수
    @StringRes val currencyName: Int,
) {
    AUD(R.string.aud_code, "$", 36, 2, R.string.aud_name),
    CAD(R.string.cad_code, "$",124, 2, R.string.cad_name),
    EUR(R.string.eur_code, "€",978, 2, R.string.eur_name),
    JPY(R.string.jpy_code, "¥",392, 0, R.string.jpy_name),
    KRW(R.string.krw_code, "₩",410, 0, R.string.krw_name),
    CNY(R.string.cny_code, "¥",156, 2, R.string.cny_name),
    RUB(R.string.rub_code, "₽",643, 2, R.string.rub_name),
    VND(R.string.vnd_code, "₫",704, 0, R.string.vnd_name),
    THB(R.string.thb_code, "฿",764, 2, R.string.thb_name),
    USD(R.string.usd_code, "$",840, 2, R.string.usd_name),
    TWD(R.string.twd_code, "$",901, 2, R.string.twd_name)
}