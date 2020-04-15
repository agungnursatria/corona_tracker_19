package tech.awesome.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tech.awesome.data.R
import tech.awesome.data.local.dao.CountryDAO
import tech.awesome.data.local.entity.Country


@Database(entities = [Country::class], version = 5, exportSchema = false)
abstract class CoronaDB : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: CoronaDB? = null

        fun getDB(context: Context): CoronaDB {
            val temp =
                INSTANCE
            temp?.let { return temp }

            val instance = Room.databaseBuilder(
                context.applicationContext, CoronaDB::class.java,
                context.getString(R.string.db_name)
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            return instance
        }
    }

    abstract fun countryDao(): CountryDAO
}