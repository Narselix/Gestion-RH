package com.example.data.dao

import androidx.room.*
import com.example.data.entity.TimeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeLogDao {
    @Query("SELECT * FROM time_logs WHERE userId = :userId ORDER BY checkInTime DESC")
    fun getTimeLogsForUser(userId: Int): Flow<List<TimeLog>>

    @Query("SELECT * FROM time_logs ORDER BY checkInTime DESC")
    fun getAllTimeLogs(): Flow<List<TimeLog>>

    @Query("SELECT * FROM time_logs WHERE userId = :userId ORDER BY checkInTime DESC LIMIT 1")
    fun getLatestTimeLogForUser(userId: Int): Flow<TimeLog?>

    @Query("SELECT * FROM time_logs WHERE userId = :userId ORDER BY checkInTime DESC LIMIT 1")
    suspend fun getLatestTimeLogForUserSync(userId: Int): TimeLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeLog(timeLog: TimeLog): Long

    @Update
    suspend fun updateTimeLog(timeLog: TimeLog)

    @Query("SELECT * FROM time_logs WHERE date = :date")
    fun getTimeLogsForDate(date: String): Flow<List<TimeLog>>
}
