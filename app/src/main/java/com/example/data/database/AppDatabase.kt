package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.LeaveRequestDao
import com.example.data.dao.TimeLogDao
import com.example.data.dao.UserDao
import com.example.data.entity.LeaveRequest
import com.example.data.entity.TimeLog
import com.example.data.entity.User

@Database(
    entities = [User::class, TimeLog::class, LeaveRequest::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun timeLogDao(): TimeLogDao
    abstract fun leaveRequestDao(): LeaveRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hr_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
