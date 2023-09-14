package com.example.somewhere.db

import android.net.Uri
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.somewhere.model.Date
import com.example.somewhere.ui.theme.MyColor
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter



@ProvidedTypeConverter
class StringListTypeConverter(
    private val moshi: Moshi
) {

    //List<String> -> json
    @TypeConverter
    fun jsonToStringList(value: List<String>): String {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)
        return adapter.toJson(value)
    }

    //json -> List<String>
    @TypeConverter
    fun stringListToJson(value: String): List<String>? {
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
class LocalDateTimeTypeConverter(

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
}

@ProvidedTypeConverter
class UriTypeConverter(
    //private val context: Context
) {
    //Uri -> json
    @TypeConverter
    fun uriToJson(value: Uri?): String?{
        return value?.toString()

//        return if (value == null)
//            null
//        else {
//            val contentResolver = context.contentResolver
//            val inputStream = contentResolver.openInputStream(value)
//
//            val imageFile = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")
//            inputStream?.use { input ->
//                imageFile.outputStream().use { output ->
//                    input.copyTo(output)
//                }
//            }
//
//            imageFile.absolutePath
//        }
    }

    //json -> Uri
    @TypeConverter
    fun jsonToUri(value: String?): Uri? {
        //return Uri.fromFile(File(value))
        //return Uri.parse(value)

        return if (value != null) {
            Uri.fromFile(File(value))
        } else {
            null
        }
    }
}

class LocalDateColorAdapter{
    @ToJson fun toJson(myColor: MyColor): String {
        return myColor.color.toString() + "/" + myColor.onColor.toString()
    }

    @FromJson fun fromJson(json: String): MyColor? {
        val parts = json.split("/")
        return if (parts.size == 2) {
            val color = parts[0].toInt()
            val onColor = parts[1].toInt()

            MyColor(color, onColor)
        } else
            null
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

class UriAdapter {
    @ToJson fun toJson(uri: Uri?): String? {
        return uri?.toString()
    }

    @FromJson fun fromJson(json: String?): Uri?{
        return if (json != null) {
            Uri.fromFile(File(json))
        } else {
            null
        }
    }
}
