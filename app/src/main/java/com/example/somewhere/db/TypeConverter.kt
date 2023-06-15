package com.example.somewhere.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.typeUtils.CurrencyType
import com.example.somewhere.typeUtils.SpotType
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


//enum
@ProvidedTypeConverter
class CurrencyTypeTypeConverter(
    private val moshi: Moshi
) {
    // unitOfCurrency -> json
    @TypeConverter
    fun unitOfCurrencyToJson(value: CurrencyType): String {
        val adapter: JsonAdapter<CurrencyType> = moshi.adapter(CurrencyType::class.java)
        return adapter.toJson(value)
    }

    // json -> unitOfCurrency
    @TypeConverter
    fun jsonToUnitOfCurrency(value: String): CurrencyType? {
        val adapter: JsonAdapter<CurrencyType> = moshi.adapter(CurrencyType::class.java)
        return adapter.fromJson(value)
    }
}

@ProvidedTypeConverter
class SpotTypeTypeConverter(
    private val moshi: Moshi
) {
    // SpotType -> json
    @TypeConverter
    fun spotTypeToJson(value: SpotType): String {
        val adapter: JsonAdapter<SpotType> = moshi.adapter(SpotType::class.java)
        return adapter.toJson(value)
    }

    // json -> SpotType
    @TypeConverter
    fun jsonToSpotType(value: String): SpotType? {
        val adapter: JsonAdapter<SpotType> = moshi.adapter(SpotType::class.java)
        return adapter.fromJson(value)
    }
}

//list
@ProvidedTypeConverter
class StringListTypeConverter(
    private val moshi: Moshi
) {
    // List<String> -> json
    @TypeConverter
    fun stringListToJson(value: List<String>): String {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)
        return adapter.toJson(value)
    }

    // json -> List<String>
    @TypeConverter
    fun jsonToStringList(value: String): List<String>? {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }
}

@ProvidedTypeConverter
class DateListTypeConverter(
    private val moshi: Moshi
) {
    // List<Date> -> json
    @TypeConverter
    fun dateListToJson(value: List<Date>): String {
        val listType = Types.newParameterizedType(List::class.java, Date::class.java)
        val adapter: JsonAdapter<List<Date>> = moshi.adapter(listType)
        return adapter.toJson(value)
    }

    // json -> List<Date>
    @TypeConverter
    fun jsonToDateList(value: String): List<Date>? {
        val listType = Types.newParameterizedType(List::class.java, Date::class.java)
        val adapter: JsonAdapter<List<Date>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }
}

@ProvidedTypeConverter
class SpotListTypeConverter(
    private val moshi: Moshi
) {
    // List<Spot> -> json
    @TypeConverter
    fun spotListToJson(value: List<Spot>): String {
        val listType = Types.newParameterizedType(List::class.java, Spot::class.java)
        val adapter: JsonAdapter<List<Spot>> = moshi.adapter(listType)
        return adapter.toJson(value)
    }

    // json -> List<Spot>
    @TypeConverter
    fun jsonToSpotList(value: String): List<Spot>? {
        val listType = Types.newParameterizedType(List::class.java, Spot::class.java)
        val adapter: JsonAdapter<List<Spot>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }
}

//
@ProvidedTypeConverter
class DateTypeConverter(
    private val moshi: Moshi
){
    //Date -> json
    @TypeConverter
    fun dateToJson(value: Date): String {
        val adapter: JsonAdapter<Date> = moshi.adapter(Date::class.java)
        return adapter.toJson(value)
    }

    //json -> Date
    @TypeConverter
    fun jsonToDate(value: String): Date? {
        val adapter: JsonAdapter<Date> = moshi.adapter(Date::class.java)
        return adapter.fromJson(value)
    }
}

@ProvidedTypeConverter
class SpotTypeConverter(
    private val moshi: Moshi
){
    //Date -> json
    @TypeConverter
    fun spotToJson(value: Spot): String {
        val adapter: JsonAdapter<Spot> = moshi.adapter(Spot::class.java)
        return adapter.toJson(value)
    }

    //json -> Date
    @TypeConverter
    fun jsonToSpot(value: String): Spot? {
        val adapter: JsonAdapter<Spot> = moshi.adapter(Spot::class.java)
        return adapter.fromJson(value)
    }
}

//
@ProvidedTypeConverter
class LatLngTypeConverter(
    //private val moshi: Moshi
) {
//    //LatLng -> json
//    @TypeConverter
//    fun latLngToJson(value: LatLng): String {
//        val adapter: JsonAdapter<LatLng> = moshi.adapter(LatLng::class.java)
//        return adapter.toJson(value)
//    }
//
//    //json -> LatLng
//    @TypeConverter
//    fun jsonToLatLng(value: String): LatLng? {
//        val adapter: JsonAdapter<LatLng> = moshi.adapter(LatLng::class.java)
//        return adapter.fromJson(value)
//    }

    //LatLng -> json
    @TypeConverter
    fun latLngToJson(latLng: LatLng?): String? {
        return latLng?.let { "${it.latitude},${it.longitude}" }
    }

    //json -> LatLng
    @TypeConverter
    fun jsonToLatLng(value: String?): LatLng? {
        return value?.let {
            val parts = it.split(",")
            if (parts.size == 2) {
                val latitude = parts[0].toDoubleOrNull()
                val longitude = parts[1].toDoubleOrNull()
                if (latitude != null && longitude != null) {
                    return LatLng(latitude, longitude)
                }
            }
            return null
        }
    }
}

class LocalDateAdapter {
    @ToJson fun toJson(localDate: LocalDate): String {
        return localDate.toString()
        //2023-06-15
    }

    @FromJson fun fromJson(json: String): LocalDate? {
        val parts = json.split("-")
        return if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            LocalDate.of(year, month, day)
        } else
            null
    }
}

@ProvidedTypeConverter
class LocalDateTypeConverter(
    //private val moshi: Moshi
) {
//    //LocalDate -> json
//    @TypeConverter
//    fun localDateToJson(value: LocalDate): String {
//        val adapter: JsonAdapter<LocalDate> = moshi.adapter(LocalDate::class.java)
//        return adapter.toJson(value)
//    }
//
//    //json -> LocalDate
//    @TypeConverter
//    fun jsonToLocalDate(value: String): LocalDate? {
//        val adapter: JsonAdapter<LocalDate> = moshi.adapter(LocalDate::class.java)
//        return adapter.fromJson(value)
//    }

    //LocalDate -> json
    @TypeConverter
    fun localDateToJson(value: LocalDate): String {
        val year = value.year
        val month = value.monthValue
        val day = value.dayOfMonth
        return "$year.$month.$day"
    }

    //json -> LocalDate
    @TypeConverter
    fun jsonToLocalDate(value: String): LocalDate? {
        val parts = value.split(".")
        return if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            LocalDate.of(year, month, day)
        } else
            null
    }
}

class LocalTimeAdapter {
    @ToJson fun toJson(localTime: LocalTime): String {
        return "${localTime.hour}:${localTime.minute}"
        //15:30
    }

    @FromJson fun fromJson(json: String): LocalTime? {
        val parts = json.split(":")
        return if (parts.size == 2) {
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            LocalTime.of(hour, minute)
        } else
            null
    }
}

@ProvidedTypeConverter
class LocalTimeTypeConverter(
    private val moshi: Moshi
) {
    //LocalTime -> json
    @TypeConverter
    fun localTimeToJson(value: LocalTime): String {
        val adapter: JsonAdapter<LocalTime> = moshi.adapter(LocalTime::class.java)
        return adapter.toJson(value)
    }

    //json -> LocalTime
    @TypeConverter
    fun jsonToLocalTime(value: String): LocalTime? {
        val adapter: JsonAdapter<LocalTime> = moshi.adapter(LocalTime::class.java)
        return adapter.fromJson(value)
    }
}

@ProvidedTypeConverter
class LocalDateTimeTypeConverter(
    //private val moshi: Moshi
) {
    //LocalTime -> json
    @TypeConverter
    fun localDateTimeToJson(value: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return value.format(formatter)

    }

    //json -> LocalTime
    @TypeConverter
    fun jsonToLocalDateTime(value: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return LocalDateTime.parse(value, formatter)
    }

//    //LocalTime -> json
//    @TypeConverter
//    fun localDateTimeToJson(value: LocalDateTime): String {
//        val adapter: JsonAdapter<LocalDateTime> = moshi.adapter(LocalDateTime::class.java)
//        return adapter.toJson(value)
//    }
//
//    //json -> LocalTime
//    @TypeConverter
//    fun jsonToLocalDateTime(value: String): LocalDateTime? {
//        val adapter: JsonAdapter<LocalDateTime> = moshi.adapter(LocalDateTime::class.java)
//        return adapter.fromJson(value)
//    }
}
