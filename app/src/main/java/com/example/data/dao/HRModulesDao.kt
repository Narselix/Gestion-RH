package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
    @Query("SELECT * FROM trainings")
    fun getAllTrainings(): Flow<List<Training>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(training: Training): Long

    @Query("SELECT * FROM training_sessions ORDER BY dateString ASC")
    fun getAllSessions(): Flow<List<TrainingSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSession): Long

    @Update
    suspend fun updateSession(session: TrainingSession)

    @Query("SELECT * FROM training_enrollments")
    fun getAllEnrollments(): Flow<List<TrainingEnrollment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: TrainingEnrollment): Long

    @Update
    suspend fun updateEnrollment(enrollment: TrainingEnrollment)
}

@Dao
interface PayslipDao {
    @Query("SELECT * FROM payslips ORDER BY monthString DESC")
    fun getAllPayslips(): Flow<List<Payslip>>

    @Query("SELECT * FROM payslips WHERE userId = :userId ORDER BY monthString DESC")
    fun getPayslipsForUser(userId: Int): Flow<List<Payslip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayslip(payslip: Payslip): Long

    @Update
    suspend fun updatePayslip(payslip: Payslip)
}

@Dao
interface RecruitmentDao {
    @Query("SELECT * FROM job_offers ORDER BY datePosted DESC")
    fun getAllJobOffers(): Flow<List<JobOffer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobOffer(offer: JobOffer): Long

    @Update
    suspend fun updateJobOffer(offer: JobOffer)

    @Query("SELECT * FROM candidates ORDER BY score DESC")
    fun getAllCandidates(): Flow<List<Candidate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: Candidate): Long

    @Update
    suspend fun updateCandidate(candidate: Candidate)

    @Query("SELECT * FROM interviews ORDER BY dateString ASC")
    fun getAllInterviews(): Flow<List<Interview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterview(interview: Interview): Long

    @Update
    suspend fun updateInterview(interview: Interview)
}

@Dao
interface CollaborationDao {
    @Query("SELECT * FROM skills")
    fun getAllSkills(): Flow<List<Skill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: Skill): Long

    @Query("SELECT * FROM employee_documents ORDER BY uploadDate DESC")
    fun getAllDocuments(): Flow<List<EmployeeDoc>>

    @Query("SELECT * FROM employee_documents WHERE userId = :userId")
    fun getDocumentsForUser(userId: Int): Flow<List<EmployeeDoc>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: EmployeeDoc): Long

    @Update
    suspend fun updateDocument(doc: EmployeeDoc)
}
