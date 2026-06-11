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
import com.example.data.entity.Candidate
import com.example.data.entity.Interview
import com.example.data.entity.JobOffer
import com.example.ui.viewmodel.HRViewModel

@Composable
fun RecrutementScreen(viewModel: HRViewModel) {
    val offers by viewModel.allJobOffers.collectAsStateWithLifecycle()
    val candidates by viewModel.allCandidates.collectAsStateWithLifecycle()
    val interviews by viewModel.allInterviews.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf("OFFRES") } // "OFFRES", "CANDIDATS", "ENTRETIENS"
    var showOfferForm by remember { mutableStateOf(false) }

    // Form states
    val newJobTitle by viewModel.jobTitle.collectAsStateWithLifecycle()
    val newJobDept by viewModel.jobDept.collectAsStateWithLifecycle()
    val newJobContract by viewModel.jobContract.collectAsStateWithLifecycle()
    val newJobLoc by viewModel.jobLocationInput.collectAsStateWithLifecycle()
    val newJobDesc by viewModel.jobDescription.collectAsStateWithLifecycle()
    val newJobHc by viewModel.jobHeadcount.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("recrutement_screen")
    ) {
        // Core Header
        Text(
            text = "Gestion des Recrutements & Talents",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = BlueNavy
        )
        Text(
            text = "Publiez vos offres, évaluez les candidats et planifiez les entretiens",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Modules Mini navigation tabs
        TabRow(
            selectedTabIndex = when(activeSubTab) { "OFFRES" -> 0; "CANDIDATS" -> 1; else -> 2 },
            containerColor = Color.Transparent,
            contentColor = BlueNavy
        ) {
            Tab(selected = activeSubTab == "OFFRES", onClick = { activeSubTab = "OFFRES" }, text = { Text("Offres d'Emploi (${offers.size})", fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == "CANDIDATS", onClick = { activeSubTab = "CANDIDATS" }, text = { Text("Candidatures (${candidates.size})", fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == "ENTRETIENS", onClick = { activeSubTab = "ENTRETIENS" }, text = { Text("Entretiens (${interviews.size})", fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when(activeSubTab) {
            "OFFRES" -> {
                // OFFRES TAB
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Catalogue des Offres Actives", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 16.sp)
                    Button(
                        onClick = { showOfferForm = !showOfferForm },
                        colors = ButtonDefaults.buttonColors(containerColor = BlueNavy),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (showOfferForm) Icons.Default.Close else Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showOfferForm) "Annuler" else "Nouvelle Offre", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (showOfferForm) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Publier une Offre d'Emploi", fontWeight = FontWeight.Bold, color = BlueNavy)
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            OutlinedTextField(
                                value = newJobTitle, onValueChange = { viewModel.jobTitle.value = it },
                                modifier = Modifier.fillMaxWidth(), placeholder = { Text("Intitulé du poste (ex: Architecte Kotlin)") },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newJobLoc, onValueChange = { viewModel.jobLocationInput.value = it },
                                    modifier = Modifier.weight(1f), placeholder = { Text("Localisation (ex: Paris)") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = newJobHc, onValueChange = { viewModel.jobHeadcount.value = it },
                                    modifier = Modifier.weight(0.5f), placeholder = { Text("Nb postes") },
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Contract types selectors
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("CDI", "CDD", "Alternance", "Freelance").forEach { c ->
                                    FilterChip(
                                        selected = newJobContract == c,
                                        onClick = { viewModel.jobContract.value = c },
                                        label = { Text(c, fontSize = 10.sp) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newJobDesc, onValueChange = { viewModel.jobDescription.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                placeholder = { Text("Description des tâches, responsabilités et compétences requises...") }
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.publishJobOffer()
                                    showOfferForm = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                            ) {
                                Text("Publier maintenant", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Grid/List of Offers
                if (offers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aucune offre active en cours.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(offers.size) { index ->
                            val offer = offers[index]
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(offer.title, fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 16.sp)
                                        Text("${offer.contractType} • ${offer.location} • ${offer.department}", fontSize = 12.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(offer.description, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, color = Color.DarkGray)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(horizontalAlignment = Alignment.End) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF38A169).copy(alpha = 0.12f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(offer.status, color = Color(0xFF38A169), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("${offer.headcount} poste(s) ouvert(s)", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "CANDIDATS" -> {
                // CANDIDATES TAB
                var selectedOfferFilterId by remember { mutableStateOf<Int?>(null) }
                
                // Horizontal filter chips for job offers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedOfferFilterId == null,
                        onClick = { selectedOfferFilterId = null },
                        label = { Text("Tous les candidats", fontSize = 11.sp) }
                    )
                    offers.forEach { job ->
                        FilterChip(
                            selected = selectedOfferFilterId == job.id,
                            onClick = { selectedOfferFilterId = job.id },
                            label = { Text(job.title, fontSize = 10.sp) }
                        )
                    }
                }

                val filteredCandidates = remember(candidates, selectedOfferFilterId) {
                    if (selectedOfferFilterId == null) candidates
                    else candidates.filter { it.jobOfferId == selectedOfferFilterId }
                }

                if (filteredCandidates.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aucune candidature dans cette section.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredCandidates.size) { idx ->
                            val cand = filteredCandidates[idx]
                            CandidateItemRow(candidate = cand, viewModel = viewModel)
                        }
                    }
                }
            }

            "ENTRETIENS" -> {
                // INTERVIEWS TAB
                if (interviews.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aucun entretien programmé.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(interviews.size) { i ->
                            val iw = interviews[i]
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
                                        Icon(Icons.Default.Send, null, tint = BlueNavy)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(iw.candidateName, fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 14.sp)
                                        Text("Poste : ${iw.jobTitle}", fontSize = 11.sp, color = Color.Gray)
                                        Text("📅 ${iw.dateString} • Type: ${iw.type}", fontSize = 11.sp, color = BlueNavy, fontWeight = FontWeight.Medium)
                                        if (iw.notes.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("📝 Notes: ${iw.notes}", fontSize = 11.sp, color = Color.DarkGray)
                                        }
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (iw.status == "Réalisé") Color(0xFF38A169).copy(alpha = 0.12f) else Color(0xFFED8936).copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(iw.status, color = if (iw.status == "Réalisé") Color(0xFF38A169) else Color(0xFFED8936), fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

// Custom candidate item displaying details & action buttons
@Composable
fun CandidateItemRow(candidate: Candidate, viewModel: HRViewModel) {
    var interviewSchedulerOpen by remember { mutableStateOf(false) }
    
    val iwDate by viewModel.interviewDateString.collectAsStateWithLifecycle()
    val iwInterviewer by viewModel.interviewInterviewer.collectAsStateWithLifecycle()
    val iwType by viewModel.interviewType.collectAsStateWithLifecycle()
    val iwNotes by viewModel.interviewNotes.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(candidate.fullName, fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 15.sp)
                    Text("Candidature : ${candidate.jobTitle}", fontSize = 11.sp, color = Color.Gray)
                    Text("📞 ${candidate.phone} • ✉ ${candidate.email}", fontSize = 11.sp, color = Color.DarkGray)
                }

                // Profile score rating progress indicator circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                candidate.score >= 85 -> Color(0xFF38A169).copy(alpha = 0.15f)
                                candidate.score >= 70 -> Color(0xFFD69E2E).copy(alpha = 0.15f)
                                else -> Color(0xFFE53E3E).copy(alpha = 0.15f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${candidate.score}%",
                        color = when {
                            candidate.score >= 85 -> Color(0xFF38A169)
                            candidate.score >= 70 -> Color(0xFFD69E2E)
                            else -> Color(0xFFE53E3E)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Extra metrics: source and résumé file info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Source: ${candidate.source} • Document: 📝 ${candidate.resumeName}", fontSize = 11.sp, color = Color.Gray)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BlueNavy.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(candidate.currentStatus, color = BlueNavy, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE2E8F0))

            // PIPELINE PROMOTIONS BUTTONS
            Text("Avancement du Pipeline :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueNavy)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // If status is Recue, offer "Planifier un entretien"
                if (candidate.currentStatus == "Reçue" || candidate.currentStatus == "Présélectionnée") {
                    Button(
                        onClick = { interviewSchedulerOpen = !interviewSchedulerOpen },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkOrange),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text("📅 Planifier Entretien", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Hired progress
                if (candidate.currentStatus == "Entretien" || candidate.currentStatus == "Test") {
                    Button(
                        onClick = { viewModel.promoteCandidateStatus(candidate, "Proposition") },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyLight),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text("💼 Faire Proposition", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (candidate.currentStatus == "Proposition") {
                    Button(
                        onClick = { viewModel.promoteCandidateStatus(candidate, "Acceptée") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38A169)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text("✅ Hired (Embaucher)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Refused button is always available in primary states
                if (candidate.currentStatus != "Acceptée" && candidate.currentStatus != "Refusée") {
                    Button(
                        onClick = { viewModel.promoteCandidateStatus(candidate, "Refusée") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(0.6f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text("Refuser", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Inline Interview Scheduler Expand
            if (interviewSchedulerOpen) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Planifier un Entretien de Recrutement", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueNavy)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = iwDate, onValueChange = { viewModel.interviewDateString.value = it },
                            modifier = Modifier.fillMaxWidth(), placeholder = { Text("Date (ex: 2026-06-15 14:00)") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = iwInterviewer, onValueChange = { viewModel.interviewInterviewer.value = it },
                            modifier = Modifier.fillMaxWidth(), placeholder = { Text("Chargé de recrutement / Evaluateur") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Téléphone", "Visio", "Présentiel").forEach { vt ->
                                FilterChip(
                                    selected = iwType == vt,
                                    onClick = { viewModel.interviewType.value = vt },
                                    label = { Text(vt, fontSize = 9.sp) }
                                )
                            }
                        }
                        
                        OutlinedTextField(
                            value = iwNotes, onValueChange = { viewModel.interviewNotes.value = it },
                            modifier = Modifier.fillMaxWidth(), placeholder = { Text("Notes optionnelles pour l'évaluateur...") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.scheduleInterview(candidate.id, candidate.fullName, candidate.jobTitle)
                                    interviewSchedulerOpen = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BlueNavy),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Confirmer le rendez-vous", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { interviewSchedulerOpen = false }) {
                                Text("Annuller", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
