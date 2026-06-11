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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.data.entity.Payslip
import com.example.data.entity.User
import com.example.ui.viewmodel.HRViewModel

@Composable
fun PaieScreen(viewModel: HRViewModel) {
    val payslips by viewModel.allPayslips.collectAsStateWithLifecycle()
    val employees by viewModel.allCompanyUsers.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf("BULLETINS") } // "BULLETINS", "GENERER", "COTISATIONS"
    
    // Cotisation state config
    var retirementFundRate by remember { mutableStateOf(8.2f) }
    var healthInsuranceRate by remember { mutableStateOf(7.5f) }
    var unemploymentFundRate by remember { mutableStateOf(2.4f) }

    val totalContributionRate = remember(retirementFundRate, healthInsuranceRate, unemploymentFundRate) {
        retirementFundRate + healthInsuranceRate + unemploymentFundRate
    }

    // Form inputs state
    var selectedEmployeeForSalaryId by remember { mutableStateOf<Int?>(null) }
    var selectedMonthString by remember { mutableStateOf("2026-05") }
    val baseSalaryInput by viewModel.payslipBaseSalaryInput.collectAsStateWithLifecycle()
    val overtimeInput by viewModel.payslipOvertimeInput.collectAsStateWithLifecycle()
    val primesInput by viewModel.payslipPrimesInput.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("paie_screen")
    ) {
        // Main Header
        Text(
            text = "Gestion de la Paie & Rémunérations",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = BlueNavy
        )
        Text(
            text = "Calculez les salaires, éditez les bulletins de paie et ajustez les cotisations sociales",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Tabs
        TabRow(
            selectedTabIndex = when(activeSubTab) { "BULLETINS" -> 0; "GENERER" -> 1; else -> 2 },
            containerColor = Color.Transparent,
            contentColor = BlueNavy
        ) {
            Tab(selected = activeSubTab == "BULLETINS", onClick = { activeSubTab = "BULLETINS" }, text = { Text("Bulletins de Paie (${payslips.size})", fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == "GENERER", onClick = { activeSubTab = "GENERER" }, text = { Text("Calculateur & Primes", fontWeight = FontWeight.Bold) })
            Tab(selected = activeSubTab == "COTISATIONS", onClick = { activeSubTab = "COTISATIONS" }, text = { Text("Cotisations (${String.format("%.1f%%", totalContributionRate)})", fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when(activeSubTab) {
            "BULLETINS" -> {
                // BULLETINS LOG VIEW
                if (payslips.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aucun bulletin de paie généré dans la base.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(payslips.size) { i ->
                            val p = payslips[i]
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
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(BlueNavy.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Payments, null, tint = BlueNavy)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(p.employeeName, fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 15.sp)
                                        Text("Période : ${p.monthString} • Dép. ${p.department}", fontSize = 11.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = String.format("Base: %.0f € | Primes: %.0f € | Heures Sup: %.1f", p.baseSalary, p.primes, p.overtimeHours),
                                            fontSize = 11.sp,
                                            color = Color.DarkGray
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = String.format("%,.2f €", p.netSalary),
                                            fontWeight = FontWeight.Black,
                                            color = BlueNavy,
                                            fontSize = 16.sp
                                        )
                                        Text("Net à payer", fontSize = 9.sp, color = Color.Gray)
                                        
                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Status badge
                                        val badgeColor = when(p.status) {
                                            "Payé" -> Color(0xFF38A169)
                                            "Validé" -> Color(0xFF3182CE)
                                            else -> Color(0xFFDD6B20)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (p.status == "Brouillon") {
                                                TextButton(
                                                    onClick = { viewModel.validatePayslip(p) },
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Valider", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            if (p.status == "Validé") {
                                                TextButton(
                                                    onClick = { viewModel.payPayslip(p) },
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Payer €", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38A169))
                                                }
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(badgeColor.copy(alpha = 0.12f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(p.status, color = badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "GENERER" -> {
                // GENERATE INDIVIDUAL BULLETINS CALCULATOR
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Simulateur & Éditeur de Bulletins de Paie", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 15.sp)
                        Text("Générez une fiche de paie individuelle pour un collaborateur", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Employee cycler Selector
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Sélectionner employé :", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BlueNavy.copy(alpha = 0.1f))
                                    .clickable {
                                        selectedEmployeeForSalaryId = employees
                                            .map { it.id }
                                            .firstOrNull { id -> id != selectedEmployeeForSalaryId }
                                            ?: employees.firstOrNull()?.id
                                    }
                                    .padding(8.dp)
                            ) {
                                val empName = employees.find { it.id == selectedEmployeeForSalaryId }?.fullName ?: "Choisir Collaborateur"
                                Text(empName, fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Period choosing row cycling
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Période comptable :", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(10.dp))
                            val periodMonths = listOf("2026-04", "2026-05", "2026-06")
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                periodMonths.forEach { m ->
                                    FilterChip(
                                        selected = selectedMonthString == m,
                                        onClick = { selectedMonthString = m },
                                        label = { Text(m, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFE2E8F0))

                        // Dynamic inputs matching base calculations
                        OutlinedTextField(
                            value = baseSalaryInput,
                            onValueChange = { viewModel.payslipBaseSalaryInput.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Salaire de base brut monthly (ex: 3500)") },
                            leadingIcon = { Icon(Icons.Default.Payments, null, tint = BlueNavy) },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = overtimeInput,
                                onValueChange = { viewModel.payslipOvertimeInput.value = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Heures supp (ex: 4.0)") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = primesInput,
                                onValueChange = { viewModel.payslipPrimesInput.value = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Primes exceptionnelles (€)") },
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Math calculation review details
                        val base = baseSalaryInput.toDoubleOrNull() ?: 3000.0
                        val ovTime = overtimeInput.toDoubleOrNull() ?: 0.0
                        val primes = primesInput.toDoubleOrNull() ?: 0.0
                        
                        val overtimePayVal = ovTime * 35.0 // hourly rate
                        val totalGross = base + overtimePayVal + primes
                        val deductionsVal = totalGross * (totalContributionRate / 100f)
                        val calculatedNet = totalGross - deductionsVal

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFC)),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Aperçu Comptable (Détail de simulation)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BlueNavy)
                                Spacer(modifier = Modifier.height(6.dp))
                                DetailRowItem(label = "Total brut de base :", value = String.format("%.2f €", base))
                                DetailRowItem(label = "Majoration heures supp :", value = String.format("%.2f €", overtimePayVal))
                                DetailRowItem(label = "Primes & gratification :", value = String.format("%.2f €", primes))
                                DetailRowItem(label = "Charges & Prélèvements (${String.format("%.1f%%", totalContributionRate)}) :", value = String.format("-%.2f €", deductionsVal))
                                Divider(modifier = Modifier.padding(vertical = 6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Net à payer estimé :", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BlueNavy)
                                    Text(String.format("%.2f €", calculatedNet), fontWeight = FontWeight.Black, fontSize = 14.sp, color = DarkOrange)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val emp = employees.find { it.id == selectedEmployeeForSalaryId }
                                if (emp != null) {
                                    viewModel.calculateAndSavePayslip(emp.id, emp.fullName, emp.department, selectedMonthString)
                                    activeSubTab = "BULLETINS"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                        ) {
                            Text("Enregistrer & éditer bulletin brouillon", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            "COTISATIONS" -> {
                // PARAMETERS OF SOCIAL CONTRIBUTION RATES
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, null, tint = DarkOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Paramètres de Taux des Cotisations Sociales", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 15.sp)
                        }
                        Text("Configurez les curseurs d'imposition déduits du brut pour obtenir le net", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(20.dp))

                        // Retirement slider
                        Text("Assurance Retraite (Vieillesse) : ${String.format("%.2f%%", retirementFundRate)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueNavy)
                        Slider(
                            value = retirementFundRate,
                            onValueChange = { retirementFundRate = it },
                            valueRange = 4f..15f,
                            colors = SliderDefaults.colors(thumbColor = BlueNavy, activeTrackColor = BlueNavy)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Health insurance slider
                        Text("Assurance Maladie & Mutuelle : ${String.format("%.2f%%", healthInsuranceRate)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueNavy)
                        Slider(
                            value = healthInsuranceRate,
                            onValueChange = { healthInsuranceRate = it },
                            valueRange = 3f..12f,
                            colors = SliderDefaults.colors(thumbColor = BlueNavy, activeTrackColor = BlueNavy)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Unemployment slider
                        Text("Assurance Chômage & Cotisations Patronales : ${String.format("%.2f%%", unemploymentFundRate)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueNavy)
                        Slider(
                            value = unemploymentFundRate,
                            onValueChange = { unemploymentFundRate = it },
                            valueRange = 1f..6f,
                            colors = SliderDefaults.colors(thumbColor = BlueNavy, activeTrackColor = BlueNavy)
                        )

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFE2E8F0))

                        // Total review badge recap
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF7ED))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Taux Global des Charges Sociales", fontSize = 12.sp, color = Color.DarkGray)
                                Text(
                                    text = String.format("%.2f %% sur brut", totalContributionRate),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = DarkOrange
                                )
                                Text("Ce taux est automatiquement reporté sur les prochains calculs", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
