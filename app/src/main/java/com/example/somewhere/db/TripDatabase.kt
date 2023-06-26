package com.example.somewhere.db

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.somewhere.model.Trip
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.addAdapter
import java.time.LocalDate

@Database(entities = [Trip::class], version = 1, exportSchema = false)
@TypeConverters(value = [
    DateListTypeConverter::class,
    LocalDateTimeTypeConverter::class,
    StringListTypeConverter::class
    //UriTypeConverter::class
])
abstract class TripDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao


    companion object {

        @OptIn(ExperimentalStdlibApi::class)
        private val moshi = Moshi.Builder()
            //.addLast(KotlinJsonAdapterFactory())
            .add(LocalDateAdapter())
            .add(LocalTimeAdapter())
            //.add(UriAdapter())
            .build()

        @Volatile
        private var Instance: TripDatabase? = null

        fun getDatabase(context: Context): TripDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TripDatabase::class.java, "trip_database.db")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .addTypeConverter(DateListTypeConverter(moshi))
                    .addTypeConverter(LocalDateTimeTypeConverter())
                    .addTypeConverter(StringListTypeConverter(moshi))
                    //.addTypeConverter(UriTypeConverter())
                    .build()
                    .also { Instance = it }
            }
        }
    }
}