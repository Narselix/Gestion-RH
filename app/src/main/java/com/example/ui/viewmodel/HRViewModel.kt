package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.LeaveRequest
import com.example.data.entity.TimeLog
import com.example.data.entity.User
import com.example.data.repository.HRRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HRViewModel(
    application: Application,
    private val repository: HRRepository
) : AndroidViewModel(application) {

    // Login UI states
    val loginUsernameText = MutableStateFlow("")
    val loginPasswordText = MutableStateFlow("")
    val loginError = MutableStateFlow<String?>(null)
    val isLoginLoading = MutableStateFlow(false)

    // Register UI states
    val registerUsernameText = MutableStateFlow("")
    val registerPasswordText = MutableStateFlow("")
    val registerFullNameText = MutableStateFlow("")
    val registerDepartmentText = MutableStateFlow("R&D")
    val registerRoleText = MutableStateFlow("Employé")
    val registerEmailText = MutableStateFlow("")
    val registerError = MutableStateFlow<String?>(null)
    val registerSuccess = MutableStateFlow<String?>(null)
    val isRegisterLoading = MutableStateFlow(false)

    // Current session
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Navigation and tabs state
    val currentScreen = MutableStateFlow("LOGIN") // "LOGIN", "MAIN"
    val currentTab = MutableStateFlow("DASHBOARD") // "DASHBOARD", "CLOCKS", "LEAVES", "PROFILE"

    // Time tracking input state
    val currentLogNote = MutableStateFlow("")
    val currentLogLocation = MutableStateFlow("Bureau") // "Bureau", "Télétravail", "Déplacement"

    // Leave request input states
    val leaveTypeSelected = MutableStateFlow("Congé Payé")
    val leaveStartDateText = MutableStateFlow("")
    val leaveEndDateText = MutableStateFlow("")
    val leaveReasonText = MutableStateFlow("")
    val leaveFormError = MutableStateFlow<String?>(null)
    val leaveFormSuccess = MutableStateFlow<String?>(null)

    // Reactive states from Room for logged-in user
    val userLatestTimeLog = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getLatestTimeLogForUser(user.id)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userTimeLogs = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getTimeLogsForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userLeaveRequests = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getLeaveRequestsForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin/Manager global lists
    val allCompanyUsers = repository.getAllUsers().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allCompanyLeaveRequests = repository.getAllLeaveRequests().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allCompanyTimeLogs = repository.getAllTimeLogs().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    init {
        // Seed and prepare database in background
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    // Login logic
    fun attemptLogin() {
        val username = loginUsernameText.value.trim().lowercase()
        val password = loginPasswordText.value.trim()

        if (username.isEmpty() || password.isEmpty()) {
            loginError.value = "Veuillez remplir tous les champs"
            return
        }

        viewModelScope.launch {
            isLoginLoading.value = true
            loginError.value = null
            
            val user = repository.getUserByUsername(username)
            if (user != null && user.passwordHash == password) {
                _currentUser.value = user
                currentScreen.value = "MAIN"
                currentTab.value = "DASHBOARD"
                // Clear fields
                loginUsernameText.value = ""
                loginPasswordText.value = ""
            } else {
                loginError.value = "Identifiants incorrects (ex: thomas / thomas123)"
            }
            isLoginLoading.value = false
        }
    }

    // Register logic
    fun attemptRegister() {
        val username = registerUsernameText.value.trim().lowercase()
        val password = registerPasswordText.value.trim()
        val fullName = registerFullNameText.value.trim()
        val department = registerDepartmentText.value
        val role = registerRoleText.value
        val email = registerEmailText.value.trim()

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            registerError.value = "Veuillez remplir tous les champs obligatoires (nom, identifiant, e-mail et mot de passe)."
            return
        }

        viewModelScope.launch {
            isRegisterLoading.value = true
            registerError.value = null
            registerSuccess.value = null

            val existing = repository.getUserByUsername(username)
            if (existing != null) {
                registerError.value = "Ce nom d'utilisateur est déjà pris."
                isRegisterLoading.value = false
                return@launch
            }

            val randomNum = (100..999).random()
            val employeeId = "EMP-2026-$randomNum"
            val newUser = User(
                username = username,
                passwordHash = password,
                fullName = fullName,
                role = role,
                department = department,
                email = email,
                employeeId = employeeId,
                avatarId = (1..6).random(),
                leaveBalance = 25.0
            )

            repository.insertUser(newUser)
            registerSuccess.value = "Compte créé avec succès ! Connectez-vous avec vos identifiants."
            loginUsernameText.value = username
            loginPasswordText.value = password
            
            // Clear register fields
            registerUsernameText.value = ""
            registerPasswordText.value = ""
            registerFullNameText.value = ""
            registerEmailText.value = ""
            
            currentScreen.value = "LOGIN"
            isRegisterLoading.value = false
        }
    }

    // Demo shortcut logging helpers
    fun selectDemoUser(username: String) {
        loginUsernameText.value = username
        loginPasswordText.value = "${username}123"
        loginError.value = null
    }

    // Log-out
    fun logout() {
        _currentUser.value = null
        currentScreen.value = "LOGIN"
    }

    // Time Tracking Clock In / Out Actions
    fun handleClockInOut() {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val latestLog = repository.getLatestTimeLogForUserSync(user.id)
            val currentDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (latestLog == null || latestLog.checkOutTime != null) {
                // Perform check in
                val newLog = TimeLog(
                    userId = user.id,
                    checkInTime = System.currentTimeMillis(),
                    date = currentDateStr,
                    note = currentLogNote.value.takeIf { it.isNotBlank() },
                    checkInLocation = currentLogLocation.value
                )
                repository.insertTimeLog(newLog)
                currentLogNote.value = "" // Reset text area
            } else {
                // Check if break is currently active, if so finish the break first
                val updatedLog = if (latestLog.isBreakActive) {
                    val breakDuration = System.currentTimeMillis() - (latestLog.breakStartTime ?: System.currentTimeMillis())
                    latestLog.copy(
                        isBreakActive = false,
                        breakStartTime = null,
                        totalBreakDurationMs = latestLog.totalBreakDurationMs + breakDuration,
                        checkOutTime = System.currentTimeMillis(),
                        checkOutLocation = currentLogLocation.value
                    )
                } else {
                    latestLog.copy(
                        checkOutTime = System.currentTimeMillis(),
                        checkOutLocation = currentLogLocation.value
                    )
                }
                repository.updateTimeLog(updatedLog)
            }
        }
    }

    // Toggle Break (Pause active log)
    fun toggleBreak() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val latest = repository.getLatestTimeLogForUserSync(user.id) ?: return@launch
            if (latest.checkOutTime == null) {
                // Currently clocked-in
                if (latest.isBreakActive) {
                    // Stop break, calculate added interval
                    val breakStartTime = latest.breakStartTime ?: System.currentTimeMillis()
                    val addition = System.currentTimeMillis() - breakStartTime
                    val updated = latest.copy(
                        isBreakActive = false,
                        breakStartTime = null,
                        totalBreakDurationMs = latest.totalBreakDurationMs + addition
                    )
                    repository.updateTimeLog(updated)
                } else {
                    // Start break
                    val updated = latest.copy(
                        isBreakActive = true,
                        breakStartTime = System.currentTimeMillis()
                    )
                    repository.updateTimeLog(updated)
                }
            }
        }
    }

    // Leave request operations
    fun submitLeaveRequest() {
        val user = _currentUser.value ?: return
        val startStr = leaveStartDateText.value.trim()
        val endStr = leaveEndDateText.value.trim()
        val reason = leaveReasonText.value.trim()
        val type = leaveTypeSelected.value

        leaveFormError.value = null
        leaveFormSuccess.value = null

        if (startStr.isEmpty() || endStr.isEmpty()) {
            leaveFormError.value = "Veuillez entrer les dates de début et de fin"
            return
        }

        if (reason.isEmpty()) {
            leaveFormError.value = "Veuillez donner un motif descriptif"
            return
        }

        // Validate date order simplest check
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = sdf.parse(startStr)
            val end = sdf.parse(endStr)
            if (end != null && start != null && end.before(start)) {
                leaveFormError.value = "La date de fin doit être après la date de début"
                return
            }
        } catch (e: Exception) {
            leaveFormError.value = "Format de date invalide (Requis: AAAA-MM-JJ)"
            return
        }

        viewModelScope.launch {
            val req = LeaveRequest(
                userId = user.id,
                userName = user.fullName,
                startDate = startStr,
                endDate = endStr,
                type = type,
                reason = reason
            )
            repository.insertLeaveRequest(req)
            leaveFormSuccess.value = "Demande envoyée avec succès !"
            // Reset input values
            leaveStartDateText.value = ""
            leaveEndDateText.value = ""
            leaveReasonText.value = ""
        }
    }

    // Change Leave request status (Admin actions)
    fun updateLeaveStatus(requestId: Int, newStatus: String) {
        viewModelScope.launch {
            val requests = repository.getAllLeaveRequests().firstOrNull() ?: return@launch
            val req = requests.find { it.id == requestId } ?: return@launch
            val updatedReq = req.copy(status = newStatus)
            
            repository.updateLeaveRequest(updatedReq)

            // If request is approved, we deduct it from employee's leave balance in Room!
            if (newStatus == "Approuvé") {
                val users = repository.getAllUsers().firstOrNull() ?: return@launch
                val employee = users.find { it.id == req.userId }
                if (employee != null) {
                    val days = calculateLeaveDays(req.startDate, req.endDate)
                    val newBalance = (employee.leaveBalance - days).coerceAtLeast(0.0)
                    repository.updateUser(employee.copy(leaveBalance = newBalance))
                }
            }
        }
    }

    // Helper: calculate days between dates
    private fun calculateLeaveDays(start: String, end: String): Double {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = sdf.parse(start) ?: return 1.0
            val endDate = sdf.parse(end) ?: return 1.0
            val diff = endDate.time - startDate.time
            val days = (diff / (1000 * 60 * 60 * 24)).toDouble() + 1.0
            if (days < 1.0) 1.0 else days
        } catch (e: Exception) {
            1.0
        }
    }

    // Format millisecond duration with optional current accumulating seconds
    fun formatWorkedDuration(log: TimeLog): String {
        val durationMs = getWorkedDurationMs(log)
        val hrs = durationMs / 3600000
        val mins = (durationMs % 3600000) / 60000
        return String.format(Locale.getDefault(), "%02dh %02dm", hrs, mins)
    }

    fun getWorkedDurationMs(log: TimeLog): Long {
        val end = log.checkOutTime ?: System.currentTimeMillis()
        var runTime = end - log.checkInTime

        // Deduct break duration
        runTime -= log.totalBreakDurationMs

        // If currently on break, deduct current active break slice
        if (log.isBreakActive && log.breakStartTime != null) {
            val ongoingBreak = System.currentTimeMillis() - log.breakStartTime
            runTime -= ongoingBreak
        }
        return if (runTime < 0) 0 else runTime
    }

    fun formatEpochToHour(timestamp: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    }

    fun formatEpochToFullString(timestamp: Long): String {
        return SimpleDateFormat("dd MMM, yyyy 'à' HH:mm", Locale.getDefault()).format(Date(timestamp))
    }

    // Helper factory
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val database = AppDatabase.getDatabase(application)
            val repository = HRRepository(
                database.userDao(),
                database.timeLogDao(),
                database.leaveRequestDao()
            )
            return HRViewModel(application, repository) as T
        }
    }
}
