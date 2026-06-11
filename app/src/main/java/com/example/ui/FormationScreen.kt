package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.data.entity.Training
import com.example.data.entity.TrainingEnrollment
import com.example.data.entity.TrainingSession
import com.example.data.entity.User
import com.example.ui.viewmodel.HRViewModel

@Composable
fun FormationScreen(viewModel: HRViewModel) {
    val courses by viewModel.allTrainings.collectAsStateWithLifecycle()
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val enrollments by viewModel.allEnrollments.collectAsStateWithLifecycle()
    val employees by viewModel.allCompanyUsers.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf("CATALOGUE") } // "CATALOGUE", "SESSIONS", "BUDGET"
    var showCourseForm by remember { mutableStateOf(false) }

    // Form states
    val newTitle by viewModel.trainingTitle.collectAsStateWithLifecycle()
    val newCat by viewModel.trainingCategory.collectAsStateWithLifecycle()
    val newDesc by viewModel.trainingDesc.collectAsStateWithLifecycle()
    val newDept by viewModel.trainingDept.collectAsStateWithLifecycle()
    val newDur by viewModel.trainingDuration.collectAsStateWithLifecycle()
    val newCost by viewModel.trainingCost.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("formation_screen")
    ) {
        // Main Header
        Text(
            text = "Catalogue de Formation & Sessions",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = BlueNavy
        )
        Text(
            text = "Pilotez le catalogue, inscrivez les employés et gérez le budget annuel par département",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mini Navigation Tabs
        TabRow(
            selectedTabIndex = when(activeSubTab) { "CATALOGUE" -> 0; "SESSIONS" -> 1; else -> 2 },
            containerColor = Color.Transparent,
            contentColor = BlueNavy
        ) {
            Tab(selected = activeSubTab == "CATALOGUE", onClick = { activeSubTab = "CATALOGUE" }, text = { Text("Cours Cataloge (${courses.size})", fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == "SESSIONS", onClick = { activeSubTab = "SESSIONS" }, text = { Text("Sessions Activités (${sessions.size})", fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == "BUDGET", onClick = { activeSubTab = "BUDGET" }, text = { Text("Analyse Budget RH", fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeSubTab) {
            "CATALOGUE" -> {
                // CATALOGUE Subtab
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Catalogue de Cours Interne & Externe", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 16.sp)
                    Button(
                        onClick = { showCourseForm = !showCourseForm },
                        colors = ButtonDefaults.buttonColors(containerColor = BlueNavy),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (showCourseForm) Icons.Default.Close else Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showCourseForm) "Masquer" else "Créer un Cours", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (showCourseForm) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Ajouter une Formation au Catalogue", fontWeight = FontWeight.Bold, color = BlueNavy)
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            OutlinedTextField(
                                value = newTitle, onValueChange = { viewModel.trainingTitle.value = it },
                                modifier = Modifier.fillMaxWidth(), placeholder = { Text("Intitulé du cours (ex: Programmation Rust)") },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newDur, onValueChange = { viewModel.trainingDuration.value = it },
                                    modifier = Modifier.weight(1f), placeholder = { Text("Durée (ex: 21h)") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = newCost, onValueChange = { viewModel.trainingCost.value = it },
                                    modifier = Modifier.weight(1f), placeholder = { Text("Coût (€)") },
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                listOf("Technique", "Soft Skills", "Sécurité", "Management").forEach { cat ->
                                    FilterChip(
                                        selected = newCat == cat,
                                        onClick = { viewModel.trainingCategory.value = cat },
                                        label = { Text(cat, fontSize = 10.sp) },
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newDesc, onValueChange = { viewModel.trainingDesc.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                placeholder = { Text("Objectifs, compétences clés ciblées, prérequis nécessaires...") }
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.createTrainingCourse()
                                    showCourseForm = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                            ) {
                                Text("Insérer au catalogue", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (courses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aucun cours dans le catalogue.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(courses.size) { i ->
                            val c = courses[i]
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(BlueNavy.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when(c.category) {
                                                "Technique" -> Icons.Default.School
                                                "Management" -> Icons.Default.People
                                                "Sécurité" -> Icons.Default.Business
                                                else -> Icons.Default.School
                                            },
                                            contentDescription = null,
                                            tint = BlueNavy
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(c.title, fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 15.sp)
                                        Text("Catégorie: ${c.category} • Durée: ${c.durationHrs} heures", fontSize = 11.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(c.description, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.DarkGray)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = String.format("%.0f €", c.cost),
                                            fontWeight = FontWeight.Black,
                                            color = DarkOrange,
                                            fontSize = 15.sp
                                        )
                                        Text("par place", fontSize = 9.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "SESSIONS" -> {
                // SESSIONS Subtab
                if (sessions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aucune session planifiée.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(sessions.size) { idx ->
                            val s = sessions[idx]
                            TrainingSessionRowItem(
                                session = s,
                                enrolls = enrollments.filter { it.sessionId == s.id },
                                companyEmployees = employees,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }

            "BUDGET" -> {
                // BUDGET ANALYSIS Subtab
                TrainingBudgetStatsView(courses = courses, sessions = sessions, enrolls = enrollments)
            }
        }
    }
}

// Custom Training Session expansion row with enrollment and scores forms
@Composable
fun TrainingSessionRowItem(
    session: TrainingSession,
    enrolls: List<TrainingEnrollment>,
    companyEmployees: List<User>,
    viewModel: HRViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    var enrollFormOpen by remember { mutableStateOf(false) }
    var evaluationFormOpenEnrollId by remember { mutableStateOf<Int?>(null) }

    val sessDate by viewModel.sessionDate.collectAsStateWithLifecycle()
    val sessLoc by viewModel.sessionLocation.collectAsStateWithLifecycle()
    val sessTrainer by viewModel.sessionTrainer.collectAsStateWithLifecycle()
    val sessCap by viewModel.sessionCapacity.collectAsStateWithLifecycle()

    var selectedEmployeeIdToEnroll by remember { mutableStateOf<Int?>(null) }
    var scoreValueInput by remember { mutableStateOf("5") }
    var commentsValueInput by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Summary header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(session.trainingTitle, fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 15.sp)
                    Text("Session 📅 ${session.dateString} • Formateur: ${session.trainer}", fontSize = 12.sp, color = NavyLight, fontWeight = FontWeight.Medium)
                    Text("Lieu: ${session.location} • Jauge maximum: ${session.capacity} personnes", fontSize = 11.sp, color = Color.Gray)
                }

                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(0.7f)) {
                    val statusColor = when (session.status) {
                        "Terminée" -> Color(0xFF38A169)
                        "En cours" -> Color(0xFFD69E2E)
                        else -> BlueNavy
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(session.status, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = { expanded = !expanded },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(if (expanded) "Réduire ▲" else "Gérer (${enrolls.size}) ▼", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (expanded) {
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                // Actions: Change session status, enroll new participant
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Registre des Participants (${enrolls.size} inscrits)", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (session.status == "Planifiée") {
                            TextButton(onClick = { viewModel.updateSessionStatus(session, "En cours") }) {
                                Text("Lancer ▶", fontSize = 11.sp)
                            }
                        }
                        if (session.status == "En cours") {
                            TextButton(onClick = { viewModel.updateSessionStatus(session, "Terminée") }) {
                                Text("Clôturer ☑", fontSize = 11.sp, color = Color(0xFF38A169))
                            }
                        }
                        TextButton(onClick = { enrollFormOpen = !enrollFormOpen }) {
                            Text("+ Inscrire", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkOrange)
                        }
                    }
                }

                // Inscription inline form
                if (enrollFormOpen) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Inscrire un Collaborateur", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BlueNavy)
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Simple employee selector simulated cycle
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Text("Choisir : ", fontSize = 11.sp, color = Color.Gray)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFE2E8F0))
                                        .clickable {
                                            selectedEmployeeIdToEnroll = companyEmployees
                                                .map { it.id }
                                                .firstOrNull { id -> id != selectedEmployeeIdToEnroll && !enrolls.any { it.userId == id } } 
                                                ?: companyEmployees.firstOrNull()?.id
                                        }
                                        .padding(8.dp)
                                ) {
                                    val empName = companyEmployees.find { it.id == selectedEmployeeIdToEnroll }?.fullName ?: "Cliquer pour cycler"
                                    Text(empName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueNavy)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val emp = companyEmployees.find { it.id == selectedEmployeeIdToEnroll }
                                    if (emp != null) {
                                        viewModel.enrollUserInTraining(session.id, emp.id, emp.fullName)
                                        enrollFormOpen = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                            ) {
                                Text("Confirmer l'inscription", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List of enrolled people
                if (enrolls.isEmpty()) {
                    Text("Aucun inscrit.", fontSize = 11.sp, color = Color.Gray)
                } else {
                    enrolls.forEach { en ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .background(Color(0xFFF7FAFC), RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(en.employeeName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueNavy)
                                if (en.status == "Présent") {
                                    Text("⭐ Éval: ${en.evaluationScore}/5 • ${en.feedbackComment}", fontSize = 10.sp, color = Color.Gray)
                                } else {
                                    Text("Statut: ${en.status}", fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Cert certificate icon issued representation
                                if (en.certificateIssued) {
                                    Icon(Icons.Default.Check, "Certifié", tint = DarkOrange, modifier = Modifier.size(18.dp).padding(end = 4.dp))
                                }

                                if (session.status == "Terminée" && en.status == "Inscrit") {
                                    TextButton(onClick = { evaluationFormOpenEnrollId = en.id }) {
                                        Text("Noter", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueNavy)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(if (en.status == "Présent") Color(0xFF38A169).copy(alpha = 0.12f) else Color.LightGray)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(en.status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if(en.status == "Présent") Color(0xFF38A169) else Color.DarkGray)
                                    }
                                }
                            }
                        }

                        // Evaluation inline inputs
                        if (evaluationFormOpenEnrollId == en.id) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Évaluer l'acquisition des compétences", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarkOrange)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf("1", "2", "3", "4", "5").forEach { num ->
                                            FilterChip(
                                                selected = scoreValueInput == num,
                                                onClick = { scoreValueInput = num },
                                                label = { Text("$num ⭐", fontSize = 10.sp) }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = commentsValueInput,
                                        onValueChange = { commentsValueInput = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Commentaire de satisfaction / compétences...", fontSize = 11.sp) }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row {
                                        Button(
                                            onClick = {
                                                viewModel.completeEnrollment(en, present = true, score = scoreValueInput.toIntOrNull() ?: 5, comment = commentsValueInput)
                                                evaluationFormOpenEnrollId = null
                                                commentsValueInput = ""
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkOrange),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Valider Présence & Certificat", fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        TextButton(onClick = { evaluationFormOpenEnrollId = null }) {
                                            Text("Refuser l'accès")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Depatments budget graphs representation
@Composable
fun TrainingBudgetStatsView(
    courses: List<Training>,
    sessions: List<TrainingSession>,
    enrolls: List<TrainingEnrollment>
) {
    val depts = listOf("R&D", "Service RH", "Design", "Marketing")
    
    // Annual budgets per department
    val annualBudgets = mapOf(
        "R&D" to 15000.0,
        "Service RH" to 8000.0,
        "Design" to 6000.0,
        "Marketing" to 5000.0
    )

    // Calculate consumed expenditure: cost per seat sum of present/inscribed people in the active training course
    val spentByDept = remember(courses, sessions, enrolls) {
        val mapping = depts.associateWith { 0.0 }.toMutableMap()
        enrolls.forEach { en ->
            val sessionOfEnroll = sessions.find { it.id == en.sessionId }
            val courseOfSession = sessionOfEnroll?.let { s -> courses.find { it.id == s.trainingId } }
            
            if (courseOfSession != null) {
                val dept = courseOfSession.department
                val currentSpent = mapping[dept] ?: 0.0
                mapping[dept] = currentSpent + courseOfSession.cost
            }
        }
        mapping
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Budget Formation Consommé par Département", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 15.sp)
            Text("Comparatif des dépenses réelles engagées vs l'enveloppe allouée", fontSize = 12.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(20.dp))

            depts.forEach { d ->
                val limit = annualBudgets[d] ?: 1000.0
                val spent = spentByDept[d] ?: 0.0
                val ratio = (spent / limit).toFloat().coerceIn(0f, 1f)

                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(d, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BlueNavy)
                        Text(
                            text = String.format("%,.0f € / %,.0f € (%d%%)", spent, limit, (ratio * 100).toInt()),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ratio >= 0.85f) Color.Red else DarkOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Simple Progress bar representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .fillMaxHeight()
                                .background(if (ratio >= 0.85f) Color.Red else BlueNavy)
                        )
                    }
                }
            }
        }
    }
}
