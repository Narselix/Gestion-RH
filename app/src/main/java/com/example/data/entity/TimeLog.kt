package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_logs")
data class TimeLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val checkInTime: Long,
    val checkOutTime: Long? = null,
    val date: String, // format "yyyy-MM-dd"
    val note: String? = null,
    val isBreakActive: Boolean = false,
    val breakStartTime: Long? = null,
    val totalBreakDurationMs: Long = 0L,
    val checkInLocation: String? = null, // e.g. "Paris, France" or "Télétravail"
    val checkOutLocation: String? = null
)
