package com.github.dnaka91.drinkup.kt

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.dnaka91.drinkup.kt.daos.HistoryDao
import com.github.dnaka91.drinkup.kt.daos.IntakeSizeDao
import com.github.dnaka91.drinkup.kt.entities.IntakeSizeEntity
import com.github.dnaka91.drinkup.kt.entities.RecordEntity

@Database(entities = [RecordEntity::class, IntakeSizeEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun intakeSizeDao(): IntakeSizeDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "data")
                .addCallback(CreateDatabase)
                .build()
    }

    object CreateDatabase : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            db.beginTransaction()

            val intakeSizes = listOf(
                IntakeSizeEntity(1, "Small Cup", 150),
                IntakeSizeEntity(2, "Big Cup", 250),
                IntakeSizeEntity(3, "Bottle", 500)
            )

            try {
                for (intakeSize in intakeSizes) {
                    db.insert("intake_sizes", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                        put("id", intakeSize.id)
                        put("name", intakeSize.name)
                        put("amount", intakeSize.amount)
                    })
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
}
