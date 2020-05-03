package com.bignerdranch.android.gustieplanner.coursedatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.gustieplanner.Course

@Database(entities = [ Course::class ], version = 1)
@TypeConverters(CourseTypeConverters::class)
abstract class CourseDatabase: RoomDatabase() {

    abstract fun courseDao(): CourseDao

}

//// Migration for adding color integer value
//val migration_1_2 = object: Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL(
//            "ALTER TABLE Course ADD COLUMN color INTEGER NOT NULL DEFAULT ''"
//        )
//    }
//}
//
//// Migration for adding course length and removing endtime
//val migration_2_3 = object: Migration(2, 3) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("CREATE TABLE backup (id TEXT NOT NULL, name TEXT NOT NULL, departmentCode TEXT NOT NULL, numberCode TEXT NOT NULL, startTime INTEGER NOT NULL, isOnM INTEGER NOT NULL, isOnT INTEGER NOT NULL, isOnW INTEGER NOT NULL, isOnR INTEGER NOT NULL, isOnF INTEGER NOT NULL, color INTEGER NOT NULL);")
//        database.execSQL("INSERT INTO backup SELECT id, name, departmentCode, numberCode, startTime, isOnM, isOnT, isOnW, isOnR, isOnF, color FROM Course;")
//        database.execSQL("ALTER TABLE backup ADD COLUMN courseTime REAL NOT NULL DEFAULT 1.0")
//        database.execSQL("DROP TABLE Course;")
//        database.execSQL("ALTER TABLE backup RENAME TO Course;")
//    }
//}