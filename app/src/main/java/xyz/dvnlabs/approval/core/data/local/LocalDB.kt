/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class],
    version = 1
)
abstract class LocalDB : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    companion object {
        @Volatile
        private var INSTANCE: LocalDB? = null

        fun getDatabase(context: Context): LocalDB {
            return INSTANCE
                ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDB::class.java,
                        "local.db"
                    )
                        .build()
                    INSTANCE = instance
                    // return instance
                    instance
                }
        }
    }

}