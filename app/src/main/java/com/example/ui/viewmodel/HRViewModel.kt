package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.*
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
            try {
                repository.checkAndSeedDatabase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            try {
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
            } catch (e: Exception) {
                e.printStackTrace()
                loginError.value = "Erreur de connexion : ${e.localizedMessage ?: e.message}"
            } finally {
                isLoginLoading.value = false
            }
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

    // --- NEW MODULE STATES AND FLOWS ---

    val allTrainings = repository.getAllTrainings().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allSessions = repository.getAllSessions().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allEnrollments = repository.getAllEnrollments().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allPayslips = repository.getAllPayslips().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allJobOffers = repository.getAllJobOffers().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allCandidates = repository.getAllCandidates().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allInterviews = repository.getAllInterviews().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allSkills = repository.getAllSkills().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allDocuments = repository.getAllDocuments().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Form inputs: Training
    val trainingTitle = MutableStateFlow("")
    val trainingCategory = MutableStateFlow("Technique")
    val trainingDesc = MutableStateFlow("")
    val trainingDept = MutableStateFlow("R&D")
    val trainingDuration = MutableStateFlow("21")
    val trainingCost = MutableStateFlow("1500")

    val sessionDate = MutableStateFlow("")
    val sessionLocation = MutableStateFlow("En ligne")
    val sessionTrainer = MutableStateFlow("")
    val sessionCapacity = MutableStateFlow("15")

    // Form inputs: Payroll
    val payslipBaseSalaryInput = MutableStateFlow("3500")
    val payslipOvertimeInput = MutableStateFlow("4.0")
    val payslipPrimesInput = MutableStateFlow("250")
    val payslipDeductionsInput = MutableStateFlow("850")

    // Form inputs: Recruitment
    val jobTitle = MutableStateFlow("")
    val jobDept = MutableStateFlow("R&D")
    val jobContract = MutableStateFlow("CDI")
    val jobLocationInput = MutableStateFlow("Paris / Télé")
    val jobDescription = MutableStateFlow("")
    val jobHeadcount = MutableStateFlow("1")

    val candidateName = MutableStateFlow("")
    val candidateEmail = MutableStateFlow("")
    val candidatePhone = MutableStateFlow("")
    val candidateSource = MutableStateFlow("LinkedIn")
    val candidateResume = MutableStateFlow("")

    val interviewDateString = MutableStateFlow("")
    val interviewInterviewer = MutableStateFlow("")
    val interviewType = MutableStateFlow("Visio")
    val interviewNotes = MutableStateFlow("")

    // Form inputs: Skills and Documents
    val skillName = MutableStateFlow("")
    val skillLevel = MutableStateFlow("Intermédiaire")
    val skillCert = MutableStateFlow("")

    val docNameInput = MutableStateFlow("")
    val docTypeInput = MutableStateFlow("CONTRAT")
    val docFileName = MutableStateFlow("")
    val docExpiryInput = MutableStateFlow("")


    // --- ACTIONS: TRAINING ---

    fun createTrainingCourse() {
        val title = trainingTitle.value.trim()
        val cat = trainingCategory.value
        val desc = trainingDesc.value.trim()
        val dept = trainingDept.value
        val duration = trainingDuration.value.toIntOrNull() ?: 10
        val cost = trainingCost.value.toDoubleOrNull() ?: 500.0

        if (title.isEmpty() || desc.isEmpty()) return

        viewModelScope.launch {
            repository.insertTraining(
                Training(
                    title = title,
                    category = cat,
                    description = desc,
                    department = dept,
                    durationHrs = duration,
                    cost = cost
                )
            )
            trainingTitle.value = ""
            trainingDesc.value = ""
        }
    }

    fun createTrainingSession(trainingId: Int, trainingTitleStr: String) {
        val date = sessionDate.value.trim()
        val loc = sessionLocation.value.trim()
        val trainer = sessionTrainer.value.trim()
        val cap = sessionCapacity.value.toIntOrNull() ?: 10

        if (date.isEmpty() || trainer.isEmpty()) return

        viewModelScope.launch {
            repository.insertSession(
                TrainingSession(
                    trainingId = trainingId,
                    trainingTitle = trainingTitleStr,
                    dateString = date,
                    location = loc,
                    trainer = trainer,
                    capacity = cap,
                    status = "Planifiée"
                )
            )
            sessionDate.value = ""
            sessionTrainer.value = ""
        }
    }

    fun enrollUserInTraining(sessionId: Int, userId: Int, empName: String) {
        viewModelScope.launch {
            repository.insertEnrollment(
                TrainingEnrollment(
                    sessionId = sessionId,
                    userId = userId,
                    employeeName = empName,
                    status = "Inscrit"
                )
            )
        }
    }

    fun completeEnrollment(enrollment: TrainingEnrollment, present: Boolean, score: Int, comment: String) {
        viewModelScope.launch {
            val updated = enrollment.copy(
                status = if (present) "Présent" else "Absent",
                evaluationScore = score,
                feedbackComment = comment,
                certificateIssued = present
            )
            repository.updateEnrollment(updated)
        }
    }

    fun updateSessionStatus(session: TrainingSession, newStatus: String) {
        viewModelScope.launch {
            repository.updateSession(session.copy(status = newStatus))
        }
    }


    // --- ACTIONS: PAYROLL ---

    fun calculateAndSavePayslip(userId: Int, empName: String, dept: String, month: String) {
        val base = payslipBaseSalaryInput.value.toDoubleOrNull() ?: 3000.0
        val overHours = payslipOvertimeInput.value.toDoubleOrNull() ?: 0.0
        val extraPay = overHours * 35.0 // Flat rate per overtime hour
        val primes = payslipPrimesInput.value.toDoubleOrNull() ?: 0.0
        val deductions = payslipDeductionsInput.value.toDoubleOrNull() ?: 750.0
        val net = (base + extraPay + primes) - deductions

        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = sdf.format(Date())

            repository.insertPayslip(
                Payslip(
                    userId = userId,
                    employeeName = empName,
                    department = dept,
                    monthString = month,
                    baseSalary = base,
                    overtimeHours = overHours,
                    overtimePay = extraPay,
                    primes = primes,
                    deductions = deductions,
                    netSalary = net,
                    status = "Brouillon",
                    dateIssued = dateStr
                )
            )
        }
    }

    fun validatePayslip(payslip: Payslip) {
        viewModelScope.launch {
            repository.updatePayslip(payslip.copy(status = "Validé"))
        }
    }

    fun payPayslip(payslip: Payslip) {
        viewModelScope.launch {
            repository.updatePayslip(payslip.copy(status = "Payé"))
        }
    }


    // --- ACTIONS: RECRUITMENT ---

    fun publishJobOffer() {
        val title = jobTitle.value.trim()
        val dept = jobDept.value
        val contract = jobContract.value
        val loc = jobLocationInput.value.trim()
        val desc = jobDescription.value.trim()
        val hc = jobHeadcount.value.toIntOrNull() ?: 1

        if (title.isEmpty() || desc.isEmpty()) return

        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            repository.insertJobOffer(
                JobOffer(
                    title = title,
                    department = dept,
                    contractType = contract,
                    location = loc,
                    description = desc,
                    headcount = hc,
                    status = "Active",
                    datePosted = sdf.format(Date())
                )
            )
            jobTitle.value = ""
            jobDescription.value = ""
        }
    }

    fun registerCandidate(jobOfferId: Int, offerTitle: String) {
        val name = candidateName.value.trim()
        val email = candidateEmail.value.trim()
        val phone = candidatePhone.value.trim()
        val source = candidateSource.value
        val resume = candidateResume.value.trim().takeIf { it.isNotEmpty() } ?: "CV_$name.pdf"

        if (name.isEmpty() || email.isEmpty()) return

        viewModelScope.launch {
            repository.insertCandidate(
                Candidate(
                    jobOfferId = jobOfferId,
                    jobTitle = offerTitle,
                    fullName = name,
                    email = email,
                    phone = phone,
                    currentStatus = "Reçue",
                    resumeName = resume,
                    coverLetter = "",
                    score = (50..95).random(), // Seed a neat dynamic profile rating
                    source = source
                )
            )
            candidateName.value = ""
            candidateEmail.value = ""
            candidatePhone.value = ""
        }
    }

    fun promoteCandidateStatus(candidate: Candidate, nextStatus: String) {
        viewModelScope.launch {
            val updated = candidate.copy(currentStatus = nextStatus)
            repository.updateCandidate(updated)
            
            // If they are hired (Acceptée), let's onboard them as a new User in the DB!
            if (nextStatus == "Acceptée") {
                val exist = repository.getUserByUsername(candidate.fullName.replace(" ", "").lowercase())
                if (exist == null) {
                    val initialUsername = candidate.fullName.replace(" ", "").lowercase().take(8)
                    val randomId = (100..999).random()
                    val newEmployee = User(
                        username = initialUsername,
                        passwordHash = "${initialUsername}123",
                        fullName = candidate.fullName,
                        role = "Employé",
                        department = "R&D", // Default
                        email = candidate.email,
                        employeeId = "EMP-2026-$randomId",
                        joiningDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        contractType = "CDI",
                        leaveBalance = 25.0
                    )
                    repository.insertUser(newEmployee)
                }
            }
        }
    }

    fun scheduleInterview(candidateId: Int, candName: String, offerTitle: String) {
        val date = interviewDateString.value.trim()
        val iw = interviewInterviewer.value.trim()
        val type = interviewType.value
        val notes = interviewNotes.value.trim()

        if (date.isEmpty() || iw.isEmpty()) return

        viewModelScope.launch {
            repository.insertInterview(
                Interview(
                    candidateId = candidateId,
                    candidateName = candName,
                    jobTitle = offerTitle,
                    dateString = date,
                    interviewer = iw,
                    type = type,
                    status = "Planifié",
                    notes = notes
                )
            )
            // Auto promote candidate status to "Entretien"
            val candidates = repository.getAllCandidates().firstOrNull() ?: emptyList()
            val candidate = candidates.find { it.id == candidateId }
            if (candidate != null && candidate.currentStatus == "Reçue") {
                repository.updateCandidate(candidate.copy(currentStatus = "Entretien"))
            }

            interviewDateString.value = ""
            interviewInterviewer.value = ""
            interviewNotes.value = ""
        }
    }


    // --- ACTIONS: STAFF COLLABORATION (Skills and Doc uploads) ---

    fun appendSkill(userId: Int, empName: String) {
        val name = skillName.value.trim()
        val lvl = skillLevel.value
        val cert = skillCert.value.trim()

        if (name.isEmpty()) return

        viewModelScope.launch {
            repository.insertSkill(
                Skill(
                    userId = userId,
                    employeeName = empName,
                    name = name,
                    level = lvl,
                    certificationName = cert,
                    dateAcquired = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
            )
            skillName.value = ""
            skillCert.value = ""
        }
    }

    fun uploadDoc(userId: Int) {
        val name = docNameInput.value.trim()
        val type = docTypeInput.value
        val file = docFileName.value.trim().takeIf { it.isNotEmpty() } ?: "$name.pdf"
        val expiry = docExpiryInput.value.trim()

        if (name.isEmpty()) return

        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            repository.insertDocument(
                EmployeeDoc(
                    userId = userId,
                    name = name,
                    docType = type,
                    fileName = file,
                    uploadDate = sdf.format(Date()),
                    status = "Valide",
                    expiryDate = expiry
                )
            )
            docNameInput.value = ""
            docExpiryInput.value = ""
        }
    }


    // Helper factory
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val database = AppDatabase.getDatabase(application)
            val repository = HRRepository(
                database.userDao(),
                database.timeLogDao(),
                database.leaveRequestDao(),
                database.trainingDao(),
                database.payslipDao(),
                database.recruitmentDao(),
                database.collaborationDao()
            )
            return HRViewModel(application, repository) as T
        }
    }
}
