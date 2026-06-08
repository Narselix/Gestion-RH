package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leave_requests")
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val userName: String,
    val startDate: String, // format "yyyy-MM-dd"
    val endDate: String, // format "yyyy-MM-dd"
    val type: String, // "Congé Payé", "Congé Maladie", "RTT", "Sans Solde"
    val reason: String,
    val status: String = "En attente", // "En attente", "Approuvé", "Refusé"
    val submittalDate: Long = System.currentTimeMillis()
)
