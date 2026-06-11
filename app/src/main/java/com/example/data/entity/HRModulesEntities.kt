package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// --- MODULE 1: GESTION DES FORMATIONS ---

@Entity(tableName = "trainings")
data class Training(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Technique", "Soft Skills", "Sécurité", "Management"
    val description: String,
    val department: String, // Department budget owner
    val durationHrs: Int,
    val cost: Double
)

@Entity(tableName = "training_sessions")
data class TrainingSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trainingId: Int,
    val trainingTitle: String,
    val dateString: String, // "YYYY-MM-DD"
    val location: String,
    val trainer: String,
    val capacity: Int,
    val status: String // "Planifiée", "En cours", "Terminée"
)

@Entity(tableName = "training_enrollments")
data class TrainingEnrollment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val userId: Int,
    val employeeName: String,
    val status: String, // "Inscrit", "Présent", "Absent"
    val evaluationScore: Int = 0, // 1 to 5 rating
    val feedbackComment: String = "",
    val certificateIssued: Boolean = false
)


// --- MODULE 2: GESTION DE PAIE ---

@Entity(tableName = "payslips")
data class Payslip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val employeeName: String,
    val department: String,
    val monthString: String, // "YYYY-MM"
    val baseSalary: Double,
    val overtimeHours: Double,
    val overtimePay: Double,
    val primes: Double,
    val deductions: Double,
    val netSalary: Double,
    val status: String, // "Brouillon", "Validé", "Payé"
    val dateIssued: String
)


// --- MODULE 3: GESTION DES RECRUTEMENTS ---

@Entity(tableName = "job_offers")
data class JobOffer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val department: String,
    val contractType: String, // "CDI", "CDD", "Alternance", "Stage", "Freelance"
    val location: String,
    val description: String,
    val headcount: Int, // Number of positions open
    val status: String, // "Active", "Archivée", "Brouillon"
    val datePosted: String
)

@Entity(tableName = "candidates")
data class Candidate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jobOfferId: Int,
    val jobTitle: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val currentStatus: String, // "Reçue", "Présélectionnée", "Entretien", "Test", "Proposition", "Acceptée", "Refusée"
    val resumeName: String,
    val coverLetter: String = "",
    val score: Int = 0, // Rating 0-100
    val source: String // "LinkedIn", "Site Carrière", "Cabinet", "Cooptation"
)

@Entity(tableName = "interviews")
data class Interview(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val candidateId: Int,
    val candidateName: String,
    val jobTitle: String,
    val dateString: String, // "YYYY-MM-DD HH:MM"
    val interviewer: String,
    val type: String, // "Téléphone", "Visio", "Présentiel"
    val status: String, // "Planifié", "Réalisé", "Annulé"
    val notes: String = ""
)


// --- MODULE 4: COLLABORATEUR / SKILLS & DOCUMENTS ---

@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val employeeName: String,
    val name: String,
    val level: String, // "Débutant", "Intermédiaire", "Expert"
    val certificationName: String = "",
    val dateAcquired: String = ""
)

@Entity(tableName = "employee_documents")
data class EmployeeDoc(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val name: String, // "CV", "Contrat", "Diplôme de Master", "RIB", "Pièce d'Identité"
    val docType: String,
    val fileName: String,
    val uploadDate: String,
    val status: String, // "Valide", "Expiré", "À renouveler"
    val expiryDate: String = ""
)
