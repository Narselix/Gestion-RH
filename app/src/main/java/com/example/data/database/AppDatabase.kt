package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.LeaveRequestDao
import com.example.data.dao.TimeLogDao
import com.example.data.dao.UserDao
import com.example.data.dao.TrainingDao
import com.example.data.dao.PayslipDao
import com.example.data.dao.RecruitmentDao
import com.example.data.dao.CollaborationDao
import com.example.data.entity.*

@Database(
    entities = [
        User::class,
        TimeLog::class,
        LeaveRequest::class,
        Training::class,
        TrainingSession::class,
        TrainingEnrollment::class,
        Payslip::class,
        JobOffer::class,
        Candidate::class,
        Interview::class,
        Skill::class,
        EmployeeDoc::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun timeLogDao(): TimeLogDao
    abstract fun leaveRequestDao(): LeaveRequestDao
    abstract fun trainingDao(): TrainingDao
    abstract fun payslipDao(): PayslipDao
    abstract fun recruitmentDao(): RecruitmentDao
    abstract fun collaborationDao(): CollaborationDao

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
