package com.bignerdranch.android.gustieplanner.eventdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.gustieplanner.Event

@Database(entities = [ Event::class ], version = 1)
@TypeConverters(EventTypeConverters::class)
abstract class EventDatabase: RoomDatabase() {

    abstract fun eventDao(): EventDao

}

//// Migration for adding notification IDs
//val migration_1_2 = object: Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL(
//            "ALTER TABLE Event ADD COLUMN notificationId INTEGER NOT NULL DEFAULT ''"
//        )
//    }
//}