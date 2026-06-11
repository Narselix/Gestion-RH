package com.example.data.repository

import com.example.data.dao.LeaveRequestDao
import com.example.data.dao.TimeLogDao
import com.example.data.dao.UserDao
import com.example.data.dao.TrainingDao
import com.example.data.dao.PayslipDao
import com.example.data.dao.RecruitmentDao
import com.example.data.dao.CollaborationDao
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HRRepository(
    private val userDao: UserDao,
    private val timeLogDao: TimeLogDao,
    private val leaveRequestDao: LeaveRequestDao,
    private val trainingDao: TrainingDao,
    private val payslipDao: PayslipDao,
    private val recruitmentDao: RecruitmentDao,
    private val collaborationDao: CollaborationDao
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

    // --- NEW MODULE OPERATIONS ---

    // Training methods
    fun getAllTrainings(): Flow<List<Training>> = trainingDao.getAllTrainings()
    suspend fun insertTraining(t: Training) = trainingDao.insertTraining(t)
    fun getAllSessions(): Flow<List<TrainingSession>> = trainingDao.getAllSessions()
    suspend fun insertSession(s: TrainingSession) = trainingDao.insertSession(s)
    suspend fun updateSession(s: TrainingSession) = trainingDao.updateSession(s)
    fun getAllEnrollments(): Flow<List<TrainingEnrollment>> = trainingDao.getAllEnrollments()
    suspend fun insertEnrollment(e: TrainingEnrollment) = trainingDao.insertEnrollment(e)
    suspend fun updateEnrollment(e: TrainingEnrollment) = trainingDao.updateEnrollment(e)

    // Payslip methods
    fun getAllPayslips(): Flow<List<Payslip>> = payslipDao.getAllPayslips()
    fun getPayslipsForUser(userId: Int): Flow<List<Payslip>> = payslipDao.getPayslipsForUser(userId)
    suspend fun insertPayslip(p: Payslip) = payslipDao.insertPayslip(p)
    suspend fun updatePayslip(p: Payslip) = payslipDao.updatePayslip(p)

    // Recruitment methods
    fun getAllJobOffers(): Flow<List<JobOffer>> = recruitmentDao.getAllJobOffers()
    suspend fun insertJobOffer(j: JobOffer) = recruitmentDao.insertJobOffer(j)
    suspend fun updateJobOffer(j: JobOffer) = recruitmentDao.updateJobOffer(j)
    fun getAllCandidates(): Flow<List<Candidate>> = recruitmentDao.getAllCandidates()
    suspend fun insertCandidate(c: Candidate) = recruitmentDao.insertCandidate(c)
    suspend fun updateCandidate(c: Candidate) = recruitmentDao.updateCandidate(c)
    fun getAllInterviews(): Flow<List<Interview>> = recruitmentDao.getAllInterviews()
    suspend fun insertInterview(i: Interview) = recruitmentDao.insertInterview(i)
    suspend fun updateInterview(i: Interview) = recruitmentDao.updateInterview(i)

    // Collaboration/Skills/Documents methods
    fun getAllSkills(): Flow<List<Skill>> = collaborationDao.getAllSkills()
    suspend fun insertSkill(s: Skill) = collaborationDao.insertSkill(s)
    fun getAllDocuments(): Flow<List<EmployeeDoc>> = collaborationDao.getAllDocuments()
    fun getDocumentsForUser(userId: Int): Flow<List<EmployeeDoc>> = collaborationDao.getDocumentsForUser(userId)
    suspend fun insertDocument(d: EmployeeDoc) = collaborationDao.insertDocument(d)
    suspend fun updateDocument(d: EmployeeDoc) = collaborationDao.updateDocument(d)

    // Seeds initial demo data if the DB is empty
    suspend fun checkAndSeedDatabase() {
        val existingUsers = userDao.getAllUsers().firstOrNull()
        if (existingUsers.isNullOrEmpty()) {
            // Seed a diverse staff of 10 users to display perfect dashboards (pyramid, age, tenure, contracts, etc.)
            val users = listOf(
                User(
                    username = "thomas",
                    passwordHash = "thomas123",
                    fullName = "Thomas Martin",
                    role = "Employé",
                    department = "R&D",
                    email = "thomas.martin@entreprise.com",
                    employeeId = "EMP-2024-042",
                    avatarId = 1,
                    leaveBalance = 18.5,
                    joiningDate = "2024-03-15",
                    gender = "Homme",
                    birthDate = "1994-06-18", // 32 years old
                    contractType = "CDI",
                    managerId = null
                ),
                User(
                    username = "sophie",
                    passwordHash = "sophie123",
                    fullName = "Sophie Dubois",
                    role = "Admin RH",
                    department = "Service RH",
                    email = "sophie.dubois@entreprise.com",
                    employeeId = "EMP-2022-001",
                    avatarId = 2,
                    leaveBalance = 24.0,
                    joiningDate = "2022-01-10", // ~4.5 years tenure
                    gender = "Femme",
                    birthDate = "1981-11-22", // 44 years old
                    contractType = "CDI",
                    managerId = null
                ),
                User(
                    username = "lucas",
                    passwordHash = "lucas123",
                    fullName = "Lucas Morel",
                    role = "Manager",
                    department = "Design",
                    email = "lucas.morel@entreprise.com",
                    employeeId = "EMP-2023-018",
                    avatarId = 3,
                    leaveBalance = 14.0,
                    joiningDate = "2023-09-01", // ~2.8 years tenure
                    gender = "Homme",
                    birthDate = "1988-04-05", // 38 years old
                    contractType = "CDI",
                    managerId = null
                ),
                User(
                    username = "alexandre",
                    passwordHash = "alex123",
                    fullName = "Alexandre Petit",
                    role = "Employé",
                    department = "R&D",
                    email = "alex.petit@entreprise.com",
                    employeeId = "EMP-2025-081",
                    avatarId = 4,
                    leaveBalance = 15.0,
                    joiningDate = "2025-09-01", // < 1 year tenure
                    gender = "Homme",
                    birthDate = "2002-12-05", // 23 years old
                    contractType = "Alternance",
                    managerId = 1 // Thomas is manager
                ),
                User(
                    username = "julie",
                    passwordHash = "julie123",
                    fullName = "Julie Bernard",
                    role = "Employé",
                    department = "R&D",
                    email = "julie.bernard@entreprise.com",
                    employeeId = "EMP-2025-012",
                    avatarId = 5,
                    leaveBalance = 20.0,
                    joiningDate = "2025-02-01", // ~1.3 years tenure
                    gender = "Femme",
                    birthDate = "1992-08-30", // 33 years old
                    contractType = "CDD",
                    managerId = 1
                ),
                User(
                    username = "nicolas",
                    passwordHash = "nicolas123",
                    fullName = "Nicolas Roux",
                    role = "Employé",
                    department = "Marketing",
                    email = "nicolas.roux@entreprise.com",
                    employeeId = "EMP-2012-003",
                    avatarId = 6,
                    leaveBalance = 25.0,
                    joiningDate = "2012-05-15", // ~14 years tenure
                    gender = "Homme",
                    birthDate = "1969-03-10", // 57 years old
                    contractType = "CDI",
                    managerId = null
                ),
                User(
                    username = "emma",
                    passwordHash = "emma123",
                    fullName = "Emma Richard",
                    role = "Employé",
                    department = "Service RH",
                    email = "emma.richard@entreprise.com",
                    employeeId = "EMP-2026-009",
                    avatarId = 2,
                    leaveBalance = 5.0,
                    joiningDate = "2026-04-01", // <1 year tenure
                    gender = "Femme",
                    birthDate = "2004-01-15", // 22 years old
                    contractType = "Stage",
                    managerId = 2 // Sophie is manager
                ),
                User(
                    username = "antoine",
                    passwordHash = "antoine123",
                    fullName = "Antoine Lemaire",
                    role = "Employé",
                    department = "Design",
                    email = "antoine.lemaire@entreprise.com",
                    employeeId = "EMP-2024-118",
                    avatarId = 3,
                    leaveBalance = 0.0,
                    joiningDate = "2024-11-01",
                    gender = "Homme",
                    birthDate = "1995-07-14", // 30 years old
                    contractType = "Freelance",
                    managerId = 3 // Lucas is manager
                ),
                User(
                    username = "marion",
                    passwordHash = "marion123",
                    fullName = "Marion Denis",
                    role = "Manager",
                    department = "R&D",
                    email = "marion.denis@entreprise.com",
                    employeeId = "EMP-2018-055",
                    avatarId = 5,
                    leaveBalance = 22.0,
                    joiningDate = "2018-06-20", // ~8 years tenure
                    gender = "Femme",
                    birthDate = "1971-10-09", // 54 years old
                    contractType = "CDI",
                    managerId = null
                ),
                User(
                    username = "lea",
                    passwordHash = "lea123",
                    fullName = "Léa Faure",
                    role = "Employé",
                    department = "Marketing",
                    email = "lea.faure@entreprise.com",
                    employeeId = "EMP-2024-099",
                    avatarId = 1,
                    leaveBalance = 23.5,
                    joiningDate = "2024-05-10", // ~2 years tenure
                    gender = "Femme",
                    birthDate = "1998-02-14", // 28 years old
                    contractType = "CDI",
                    managerId = 6 // Nicolas is manager
                )
            )

            val insertedIds = mutableListOf<Long>()
            for (u in users) {
                insertedIds.add(userDao.insertUser(u))
            }

            // Adjust managers matching IDs
            val thomasDbId = insertedIds[0].toInt()
            val sophieDbId = insertedIds[1].toInt()
            val lucasDbId = insertedIds[2].toInt()
            val nicolasDbId = insertedIds[5].toInt()

            // Update user managers
            userDao.updateUser(users[3].copy(id = insertedIds[3].toInt(), managerId = thomasDbId)) // Alexandre -> Thomas
            userDao.updateUser(users[4].copy(id = insertedIds[4].toInt(), managerId = thomasDbId)) // Julie -> Thomas
            userDao.updateUser(users[6].copy(id = insertedIds[6].toInt(), managerId = sophieDbId)) // Emma -> Sophie
            userDao.updateUser(users[7].copy(id = insertedIds[7].toInt(), managerId = lucasDbId))  // Antoine -> Lucas
            userDao.updateUser(users[9].copy(id = insertedIds[9].toInt(), managerId = nicolasDbId)) // Lea -> Nicolas

            // Historical Clock-in Logs for Thomas & Lucas
            val cal = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            for (i in 1..5) {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val dayStr = dateFormat.format(cal.time)

                cal.set(Calendar.HOUR_OF_DAY, 9)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                val checkIn = cal.timeInMillis

                cal.set(Calendar.HOUR_OF_DAY, 18)
                cal.set(Calendar.MINUTE, 0)
                val checkOut = cal.timeInMillis

                timeLogDao.insertTimeLog(
                    TimeLog(
                        userId = thomasDbId,
                        checkInTime = checkIn,
                        checkOutTime = checkOut,
                        date = dayStr,
                        note = "Daily Sprint, coding Kotlin & Composable",
                        totalBreakDurationMs = 3600000L,
                        checkInLocation = "Bureau",
                        checkOutLocation = "Bureau"
                    )
                )

                timeLogDao.insertTimeLog(
                    TimeLog(
                        userId = lucasDbId,
                        checkInTime = checkIn + 1800000,
                        checkOutTime = checkOut - 1800000,
                        date = dayStr,
                        note = "Design system review & prototypes",
                        totalBreakDurationMs = 3600000L,
                        checkInLocation = "Télétravail",
                        checkOutLocation = "Télétravail"
                    )
                )
            }

            // Seed Leave Requests
            leaveRequestDao.insertLeaveRequest(
                LeaveRequest(
                    userId = thomasDbId,
                    userName = "Thomas Martin",
                    startDate = "2026-07-10",
                    endDate = "2026-07-17",
                    type = "Congé Payé",
                    reason = "Congés d'été",
                    status = "En attente"
                )
            )
            leaveRequestDao.insertLeaveRequest(
                LeaveRequest(
                    userId = lucasDbId,
                    userName = "Lucas Morel",
                    startDate = "2026-06-25",
                    endDate = "2026-06-26",
                    type = "RTT",
                    reason = "Déménagement",
                    status = "Approuvé"
                )
            )

            // --- SEED MODULE 1: FORMATION ---
            val t1 = Training(title = "Jetpack Compose Expert", category = "Technique", description = "Architecture et programmation réactive de Compose.", department = "R&D", durationHrs = 21, cost = 1200.0)
            val t2 = Training(title = "Management Agile & Leadership", category = "Management", description = "Animer et motiver des équipes à distance.", department = "Service RH", durationHrs = 14, cost = 850.0)
            val t3 = Training(title = "Sécurité & Premier Secours", category = "Sécurité", description = "SST et évacuation incendie.", department = "Service RH", durationHrs = 7, cost = 250.0)
            
            val t1Id = trainingDao.insertTraining(t1).toInt()
            val t2Id = trainingDao.insertTraining(t2).toInt()
            val t3Id = trainingDao.insertTraining(t3).toInt()

            val s1 = TrainingSession(trainingId = t1Id, trainingTitle = t1.title, dateString = "2026-06-12", location = "En ligne", trainer = "Marc Vico", capacity = 10, status = "Planifiée")
            val s2 = TrainingSession(trainingId = t2Id, trainingTitle = t2.title, dateString = "2026-05-18", location = "Salle Bleue", trainer = "Alice Genty", capacity = 5, status = "Terminée")
            val s3 = TrainingSession(trainingId = t3Id, trainingTitle = t3.title, dateString = "2026-06-20", location = "Salle RDC", trainer = "Docteur Schmidt", capacity = 15, status = "Planifiée")

            val s1Id = trainingDao.insertSession(s1).toInt()
            val s2Id = trainingDao.insertSession(s2).toInt()
            val s3Id = trainingDao.insertSession(s3).toInt()

            trainingDao.insertEnrollment(TrainingEnrollment(sessionId = s1Id, userId = thomasDbId, employeeName = "Thomas Martin", status = "Inscrit"))
            trainingDao.insertEnrollment(TrainingEnrollment(sessionId = s1Id, userId = insertedIds[3].toInt(), employeeName = "Alexandre Petit", status = "Inscrit"))
            trainingDao.insertEnrollment(TrainingEnrollment(sessionId = s2Id, userId = lucasDbId, employeeName = "Lucas Morel", status = "Présent", evaluationScore = 5, feedbackComment = "Excellente formation pratique !", certificateIssued = true))
            trainingDao.insertEnrollment(TrainingEnrollment(sessionId = s2Id, userId = sophieDbId, employeeName = "Sophie Dubois", status = "Présent", evaluationScore = 4, feedbackComment = "Utile en gestion quotidienne.", certificateIssued = true))

            // --- SEED MODULE 2: PAIE (Bulletin de Paie payslips for Thomas, Lucas, Sophie) ---
            val months = listOf("2026-04", "2026-05")
            for (m in months) {
                payslipDao.insertPayslip(
                    Payslip(
                        userId = thomasDbId, employeeName = "Thomas Martin", department = "R&D", monthString = m,
                        baseSalary = 3800.0, overtimeHours = 4.0, overtimePay = 150.0, primes = 200.0, deductions = 880.0,
                        netSalary = 3270.0, status = "Payé", dateIssued = "$m-30"
                    )
                )
                payslipDao.insertPayslip(
                    Payslip(
                        userId = lucasDbId, employeeName = "Lucas Morel", department = "Design", monthString = m,
                        baseSalary = 4200.0, overtimeHours = 0.0, overtimePay = 0.0, primes = 150.0, deductions = 940.0,
                        netSalary = 3410.0, status = "Payé", dateIssued = "$m-30"
                    )
                )
                payslipDao.insertPayslip(
                    Payslip(
                        userId = sophieDbId, employeeName = "Sophie Dubois", department = "Service RH", monthString = m,
                        baseSalary = 3600.0, overtimeHours = 2.0, overtimePay = 75.0, primes = 0.0, deductions = 810.0,
                        netSalary = 2865.0, status = "Payé", dateIssued = "$m-30"
                    )
                )
            }

            // --- SEED MODULE 3: RECRUTEMENT (Offers, Candidates, Interviews) ---
            val job1 = JobOffer(title = "Développeur Senior Mobile Kotlin", department = "R&D", contractType = "CDI", location = "Paris / Télé", description = "Recherche d'un expert Android Compose.", headcount = 2, status = "Active", datePosted = "2026-05-10")
            val job2 = JobOffer(title = "Product Designer Designer", department = "Design", contractType = "CDI", location = "Lyon / Bureau", description = "Conception d'outils collaboratifs innovants.", headcount = 1, status = "Active", datePosted = "2026-05-18")
            val job3 = JobOffer(title = "Chargé de recrutement", department = "Service RH", contractType = "CDD", location = "Marseille", description = "Renforcer l'équipe de recrutement estivale.", headcount = 1, status = "Active", datePosted = "2026-06-01")

            val job1Id = recruitmentDao.insertJobOffer(job1).toInt()
            val job2Id = recruitmentDao.insertJobOffer(job2).toInt()
            val job3Id = recruitmentDao.insertJobOffer(job3).toInt()

            val cand1 = Candidate(jobOfferId = job1Id, jobTitle = job1.title, fullName = "Laura Sanchez", email = "laura.sanchez@gmail.com", phone = "0612345678", currentStatus = "Entretien", resumeName = "CV_Laura_Dev.pdf", score = 85, source = "LinkedIn")
            val cand2 = Candidate(jobOfferId = job1Id, jobTitle = job1.title, fullName = "Julien Dupuis", email = "j.dupuis@outlook.fr", phone = "0699887766", currentStatus = "Proposition", resumeName = "CV_Julien_Kotlin.pdf", score = 92, source = "Cooptation")
            val cand3 = Candidate(jobOfferId = job2Id, jobTitle = job2.title, fullName = "Ines Bardi", email = "ines.designer@gmail.com", phone = "0711223344", currentStatus = "Refusée", resumeName = "Portfolio_Ines.pdf", score = 45, source = "Site Carrière")
            val cand4 = Candidate(jobOfferId = job3Id, jobTitle = job3.title, fullName = "Jean Vianney", email = "jean.rh@gmail.com", phone = "0645678912", currentStatus = "Reçue", resumeName = "CV_Vianney_RH.pdf", score = 70, source = "Cabinet")

            val cand1Id = recruitmentDao.insertCandidate(cand1).toInt()
            val cand2Id = recruitmentDao.insertCandidate(cand2).toInt()
            val cand3Id = recruitmentDao.insertCandidate(cand3).toInt()
            val cand4Id = recruitmentDao.insertCandidate(cand4).toInt()

            recruitmentDao.insertInterview(Interview(candidateId = cand1Id, candidateName = cand1.fullName, jobTitle = job1.title, dateString = "2026-06-15 14:00", interviewer = "Thomas Martin", type = "Visio", status = "Planifié"))
            recruitmentDao.insertInterview(Interview(candidateId = cand2Id, candidateName = cand2.fullName, jobTitle = job1.title, dateString = "2026-06-08 10:00", interviewer = "Sophie Dubois", type = "Présentiel", status = "Réalisé", notes = "Candidat exceptionnel, proposition salariale transmise."))

            // --- SEED MODULE 4: SKILLS & DOCUMENTS ---
            collaborationDao.insertSkill(Skill(userId = thomasDbId, employeeName = "Thomas Martin", name = "Jetpack Compose", level = "Expert", certificationName = "Google Associate Android Developer"))
            collaborationDao.insertSkill(Skill(userId = thomasDbId, employeeName = "Thomas Martin", name = "Kotlin Coroutines", level = "Expert"))
            collaborationDao.insertSkill(Skill(userId = thomasDbId, employeeName = "Thomas Martin", name = "Clean Architecture", level = "Intermédiaire"))
            collaborationDao.insertSkill(Skill(userId = lucasDbId, employeeName = "Lucas Morel", name = "Figma", level = "Expert"))
            collaborationDao.insertSkill(Skill(userId = lucasDbId, employeeName = "Lucas Morel", name = "Material Design 3", level = "Expert"))
            collaborationDao.insertSkill(Skill(userId = sophieDbId, employeeName = "Sophie Dubois", name = "Sourcing Recrutement", level = "Expert", certificationName = "Certificat HR-Pulse"))
            collaborationDao.insertSkill(Skill(userId = sophieDbId, employeeName = "Sophie Dubois", name = "Gestion Administrative", level = "Expert"))

            collaborationDao.insertDocument(EmployeeDoc(userId = thomasDbId, name = "Pièce d'Identité National", docType = "CNI", fileName = "Thomas_CNI_2032.pdf", uploadDate = "2024-03-20", status = "Valide", expiryDate = "2032-03-20"))
            collaborationDao.insertDocument(EmployeeDoc(userId = thomasDbId, name = "Contrat de Travail Initial", docType = "CONTRAT", fileName = "Contrat_CDI_Thomas.pdf", uploadDate = "2024-03-15", status = "Valide"))
            collaborationDao.insertDocument(EmployeeDoc(userId = sophieDbId, name = "Diplôme Master 2 RH", docType = "DIPLOME", fileName = "Master_RH_Sophie.pdf", uploadDate = "2022-01-10", status = "Valide"))
        }
    }
}
