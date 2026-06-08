package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val fullName: String,
    val role: String, // "Employé", "Manager", "Admin RH"
    val department: String,
    val email: String,
    val employeeId: String,
    val avatarId: Int = 0, // Profile avatar indicator
    val leaveBalance: Double = 25.0, // Solde de congés restants
    val joiningDate: String = "2024-01-15"
)
