package com.example.somewhere.enumUtils

//List of ISO 4217 currency codes
enum class CurrencyType(
    val code: String,
    val symbol: String,
    val num: Int,
    val numberOfDecimalPlaces: Int, //소수점 아래 자리 수
    val currencyName: String,
) {
    AUD("AUD", "$", 36, 2, "Australian dollar"),
    CAD("CAD", "$",124, 2, "Canadian dollar"),
    EUR("EUR", "€",978, 2, "Euro"),
    JPY("JPY", "¥",392, 0, "Japanese yen"),
    KRW("KRW", "₩",410, 0, "South Korean won"),
    CNY("CNY", "¥",156, 2, "Renminbi"),
    RUB("RUB", "₽",643, 2, "Russian ruble"),
    THB("THB", "฿",764, 2, "Thai baht"),
    USD("USD", "$",840, 2, "United States dollar")
}