package com.example.data.repository

import com.example.data.dao.LeaveRequestDao
import com.example.data.dao.TimeLogDao
import com.example.data.dao.UserDao
import com.example.data.entity.LeaveRequest
import com.example.data.entity.TimeLog
import com.example.data.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HRRepository(
    private val userDao: UserDao,
    private val timeLogDao: TimeLogDao,
    private val leaveRequestDao: LeaveRequestDao
) {
    // User functions
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    fun getUserById(userId: Int): Flow<User?> {
        return userDao.getUserById(userId)
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    // TimeLog functions
    fun getTimeLogsForUser(userId: Int): Flow<List<TimeLog>> {
        return timeLogDao.getTimeLogsForUser(userId)
    }

    fun getAllTimeLogs(): Flow<List<TimeLog>> {
        return timeLogDao.getAllTimeLogs()
    }

    fun getLatestTimeLogForUser(userId: Int): Flow<TimeLog?> {
        return timeLogDao.getLatestTimeLogForUser(userId)
    }

    suspend fun getLatestTimeLogForUserSync(userId: Int): TimeLog? {
        return timeLogDao.getLatestTimeLogForUserSync(userId)
    }

    suspend fun insertTimeLog(timeLog: TimeLog): Long {
        return timeLogDao.insertTimeLog(timeLog)
    }

    suspend fun updateTimeLog(timeLog: TimeLog) {
        timeLogDao.updateTimeLog(timeLog)
    }

    fun getTimeLogsForDate(date: String): Flow<List<TimeLog>> {
        return timeLogDao.getTimeLogsForDate(date)
    }

    // LeaveRequest functions
    fun getLeaveRequestsForUser(userId: Int): Flow<List<LeaveRequest>> {
        return leaveRequestDao.getLeaveRequestsForUser(userId)
    }

    fun getAllLeaveRequests(): Flow<List<LeaveRequest>> {
        return leaveRequestDao.getAllLeaveRequests()
    }

    suspend fun insertLeaveRequest(request: LeaveRequest): Long {
        return leaveRequestDao.insertLeaveRequest(request)
    }

    suspend fun updateLeaveRequest(request: LeaveRequest) {
        leaveRequestDao.updateLeaveRequest(request)
    }

    // Seeds initial demo data if the DB is empty
    suspend fun checkAndSeedDatabase() {
        val existingUsers = userDao.getAllUsers().firstOrNull()
        if (existingUsers.isNullOrEmpty()) {
            // Seed Users
            val userThomas = User(
                username = "thomas",
                passwordHash = "thomas123", // secure password in local demo sandbox
                fullName = "Thomas Martin",
                role = "Employé",
                department = "R&D",
                email = "thomas.martin@entreprise.com",
                employeeId = "EMP-2024-042",
                avatarId = 1,
                leaveBalance = 18.5
            )
            val userSophie = User(
                username = "sophie",
                passwordHash = "sophie123",
                fullName = "Sophie Dubois",
                role = "Admin RH",
                department = "Service RH",
                email = "sophie.dubois@entreprise.com",
                employeeId = "EMP-2022-001",
                avatarId = 2,
                leaveBalance = 24.0
            )
            val userLucas = User(
                username = "lucas",
                passwordHash = "lucas123",
                fullName = "Lucas Morel",
                role = "Manager",
                department = "Design",
                email = "lucas.morel@entreprise.com",
                employeeId = "EMP-2023-018",
                avatarId = 3,
                leaveBalance = 14.0
            )

            val thomasId = userDao.insertUser(userThomas).toInt()
            val sophieId = userDao.insertUser(userSophie).toInt()
            val lucasId = userDao.insertUser(userLucas).toInt()

            // Seed historical worked hours for Thomas over the last 5 days
            val cal = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Define work sessions: e.g., 9h00 to 18h00, 1h break (total 8h worked)
            for (i in 1..5) {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val dayStr = dateFormat.format(cal.time)

                // 9:00 AM in millis
                cal.set(Calendar.HOUR_OF_DAY, 9)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                val checkIn = cal.timeInMillis

                // 6:00 PM in millis
                cal.set(Calendar.HOUR_OF_DAY, 18)
                cal.set(Calendar.MINUTE, 0)
                val checkOut = cal.timeInMillis

                // Insert TimeLog
                timeLogDao.insertTimeLog(
                    TimeLog(
                        userId = thomasId,
                        checkInTime = checkIn,
                        checkOutTime = checkOut,
                        date = dayStr,
                        note = "Sprint planning & Coding",
                        totalBreakDurationMs = 3600000L, // 1h break
                        checkInLocation = "Bureau",
                        checkOutLocation = "Bureau"
                    )
                )

                // Lucas also has some records
                timeLogDao.insertTimeLog(
                    TimeLog(
                        userId = lucasId,
                        checkInTime = checkIn + 1800000, // 9h30
                        checkOutTime = checkOut - 1800000, // 17h30
                        date = dayStr,
                        note = "Design Review & Wireframes",
                        totalBreakDurationMs = 3600000L,
                        checkInLocation = "Télétravail",
                        checkOutLocation = "Télétravail"
                    )
                )
            }

            // Also seed a couple of leave requests
            leaveRequestDao.insertLeaveRequest(
                LeaveRequest(
                    userId = thomasId,
                    userName = "Thomas Martin",
                    startDate = "2026-07-10",
                    endDate = "2026-07-17",
                    type = "Congé Payé",
                    reason = "Vacances d'été en famille",
                    status = "En attente"
                )
            )
            leaveRequestDao.insertLeaveRequest(
                LeaveRequest(
                    userId = lucasId,
                    userName = "Lucas Morel",
                    startDate = "2026-06-25",
                    endDate = "2026-06-26",
                    type = "RTT",
                    reason = "Déménagement personnel",
                    status = "Approuvé"
                )
            )
        }
    }
}
