package com.example.data.dao

import androidx.room.*
import com.example.data.entity.LeaveRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveRequestDao {
    @Query("SELECT * FROM leave_requests WHERE userId = :userId ORDER BY submittalDate DESC")
    fun getLeaveRequestsForUser(userId: Int): Flow<List<LeaveRequest>>

    @Query("SELECT * FROM leave_requests ORDER BY submittalDate DESC")
    fun getAllLeaveRequests(): Flow<List<LeaveRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequest(request: LeaveRequest): Long

    @Update
    suspend fun updateLeaveRequest(request: LeaveRequest)

    @Query("SELECT * FROM leave_requests WHERE id = :id")
    suspend fun getLeaveRequestById(id: Int): LeaveRequest?
}
