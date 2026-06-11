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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.User
import com.example.ui.viewmodel.HRViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

// Theme Colors specified as constraints
val BlueNavy = Color(0xFF1A365D)
val DarkOrange = Color(0xFFED8936)
val NavyLight = Color(0xFF2B4C7E)
val LightSage = Color(0xFF81E6D9)
val FemalePink = Color(0xFFEC4899)
val MaleBlue = Color(0xFF3B82F6)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(viewModel: HRViewModel) {
    val users by viewModel.allCompanyUsers.collectAsStateWithLifecycle()
    val payslips by viewModel.allPayslips.collectAsStateWithLifecycle()
    val leaves by viewModel.allCompanyLeaveRequests.collectAsStateWithLifecycle()
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val offers by viewModel.allJobOffers.collectAsStateWithLifecycle()

    var selectedDeptFilter by remember { mutableStateOf("Tous") }
    val uniqueDepts = remember(users) {
        listOf("Tous") + users.map { it.department }.distinct()
    }

    val filteredUsers = remember(users, selectedDeptFilter) {
        if (selectedDeptFilter == "Tous") users else users.filter { it.department == selectedDeptFilter }
    }

    // Calculations for demographics
    val totalWorkforce = users.size
    val currentMonthPayslips = payslips.filter { it.monthString == "2026-05" }
    
    // Monthly payroll
    val monthlyPayroll = remember(users) {
        users.sumOf { 
            when(it.contractType) {
                "Alternance" -> 1350.0
                "Stage" -> 700.0
                "Freelance" -> 4500.0
                else -> 3500.0 // Default CDI Base
            }
        }
    }
    val annualPayroll = monthlyPayroll * 12.0

    // Absenteeism Rate: days of approved leaves divided by total potential workdays
    val absenteeismRate = remember(leaves, users) {
        val approvedLeaves = leaves.filter { it.status == "Approuvé" }
        if (users.isEmpty()) 0.0 
        else {
            val totalLeaveDays = approvedLeaves.sumOf { req ->
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val s = sdf.parse(req.startDate)!!
                    val e = sdf.parse(req.endDate)!!
                    val diff = e.time - s.time
                    val days = (diff / (1000 * 60 * 60 * 24)).toDouble() + 1.0
                    if (days < 1.0) 1.0 else days
                } catch (ex: Exception) {
                    1.0
                }
            }
            // 20 potential workdays per month per employee
            val maxWorkDays = users.size * 21.0
            ((totalLeaveDays / maxWorkDays) * 100.0).coerceIn(0.1, 100.0)
        }
    }

    // Turnover Rate
    val turnoverRate = 2.4 // Typical stable tech percentage

    // Trainings Scheduled
    val scheduledTrainingsCount = sessions.filter { it.status == "Planifiée" }.size

    // Active Jobs
    val activeRecruitmentsCount = offers.filter { it.status == "Active" }.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("dashboard_screen")
    ) {
        // Welcoming header with dynamic department selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tableau de Bord Principal",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = BlueNavy
                )
                Text(
                    text = "Vue consolidée en temps réel de votre capital humain",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Department filter dropdown/row
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable {
                        // Cycle filters
                        val index = uniqueDepts.indexOf(selectedDeptFilter)
                        val nextIndex = (index + 1) % uniqueDepts.size
                        selectedDeptFilter = uniqueDepts[nextIndex]
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Filtrer",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Filtre: $selectedDeptFilter",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // A. KPI CARDS GRILL
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val cardWidth = 140.dp
            KpiCard(
                title = "Effectif Total",
                value = "$totalWorkforce pers",
                subtitle = "Dont ${users.filter { it.contractType != "CDI" }.size} non-CDI",
                icon = Icons.Default.People,
                accentColor = BlueNavy,
                modifier = Modifier.weight(1f).widthIn(min = cardWidth)
            )
            KpiCard(
                title = "Masse Salariale",
                value = String.format("%,.0f €", annualPayroll),
                subtitle = String.format("Mensuel ~ %,.0f €", monthlyPayroll),
                icon = Icons.Default.Payments,
                accentColor = DarkOrange,
                modifier = Modifier.weight(1f).widthIn(min = cardWidth)
            )
            KpiCard(
                title = "Taux Absences",
                value = String.format("%.1f %%", absenteeismRate),
                subtitle = "Congés approuvés",
                icon = Icons.Default.List,
                accentColor = FemalePink,
                modifier = Modifier.weight(1f).widthIn(min = cardWidth)
            )
            KpiCard(
                title = "Turnover",
                value = "$turnoverRate %",
                subtitle = "Seuil optimal de 5%",
                icon = Icons.Default.ArrowForward,
                accentColor = MaleBlue,
                modifier = Modifier.weight(1f).widthIn(min = cardWidth)
            )
            KpiCard(
                title = "Recrutements",
                value = "$activeRecruitmentsCount offres",
                subtitle = "Campagnes actives",
                icon = Icons.Default.Person,
                accentColor = LightSage,
                modifier = Modifier.weight(1f).widthIn(min = cardWidth)
            )
            KpiCard(
                title = "Formations",
                value = "$scheduledTrainingsCount sessions",
                subtitle = "Planifiées ce mois-ci",
                icon = Icons.Default.School,
                accentColor = NavyLight,
                modifier = Modifier.weight(1f).widthIn(min = cardWidth)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // B. GRAPHIQUES OBLIGATOIRES
        
        Text(
            text = "Analyses Démographiques & Structurelles",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = BlueNavy,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 1. Pyramide des âges (stacked horizontal bars, Men vs Women)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pyramide des Âges (Filtre: $selectedDeptFilter)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BlueNavy
                )
                Text(
                    text = "Structure d'âge répartie par genre (Homme / Femme)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                AgePyramidChart(employees = filteredUsers)
            }
        }

        // Two-column layout for screen clarity if space permits
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Pyramide d'ancienneté (Pie/Donut layout)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(340.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ancienneté",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BlueNavy
                    )
                    Text(
                        text = "Tranche d'ancienneté",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TenurePieChart(employees = filteredUsers)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // 4. Répartition par type de contrat (Donut chart & percentages)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(340.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Types de Contrat",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BlueNavy
                    )
                    Text(
                        text = "Rapport de distribution",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ContractDonutChart(employees = filteredUsers)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Évolution des effectifs (Line chart over last 12 months)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Évolution des Effectifs",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BlueNavy
                )
                Text(
                    text = "Arrivées, Départs & Effectif Net consolidé sur 12 mois",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                WorkforceTrendsLineChart()
            }
        }

        // 5. Répartition par département (Horizontal grouped bars)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Répartition par Département",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BlueNavy
                )
                Text(
                    text = "Ventilation opérationnelle et part relative",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                DepartmentDistributionChart(employees = users)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // C. WIDGETS SUPPLÉMENTAIRES (Absences à venir, Anniversaires, Alertes)
        
        Text(
            text = "Opérations & Alertes Quotidiennes",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = BlueNavy,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Absences direct list
            Card(
                modifier = Modifier
                    .weight(1.2f)
                    .height(280.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Absences & Congés à Venir",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BlueNavy
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val pendingLeaves = leaves.filter { it.status == "Approuvé" || it.status == "En attente" }
                    if (pendingLeaves.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Aucune absence à venir.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            pendingLeaves.take(4).forEach { req ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF7FAFC))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(req.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BlueNavy)
                                        Text("${req.type} • ${req.startDate} au ${req.endDate}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                    
                                    val badgeColor = if (req.status == "Approuvé") Color(0xFF38A169) else Color(0xFFDD6B20)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(badgeColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(req.status, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Birthdays & Alerts side col
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Birthday widget
                Card(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, "Anniversaires", tint = DarkOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Anniversaires du Mois", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        val birthdaysThisMonth = users.filter { 
                            try {
                                val birthMonth = it.birthDate.split("-")[1].toInt()
                                // Current mock Gregorian month: June (6)
                                birthMonth == 6
                            } catch(e: Exception) { false }
                        }
                        
                        if (birthdaysThisMonth.isEmpty()) {
                            Text("Aucun anniversaire ce mois-ci.", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                birthdaysThisMonth.forEach { birthdayUser ->
                                    Text(
                                        "🎉 ${birthdayUser.fullName} (${birthdayUser.birthDate.split("-")[2]} Juin)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BlueNavy,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Compliance / HR alerts widget
                Card(
                    modifier = Modifier.weight(1.2f).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, "Alertes", tint = Color.Red, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Alertes de Vigilance", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(Color.Red, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Période d'essai d'Alexandre Petit expire bientôt", fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(Color.Red, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CDD de Julie Bernard à planifier", fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(Color.LightGray, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("1 document expiré : Thomas", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Custom simple KPI Card
@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Black),
                color = BlueNavy
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// 1. Pyramide des ages - Horizontally stacked bars
@Composable
fun AgePyramidChart(employees: List<User>) {
    val tranches = listOf("55+", "45-54", "35-44", "25-34", "<25")
    
    // Group employees by age tranche
    val distributions = remember(employees) {
        val maleDistribution = mutableMapOf<String, Int>().apply { tranches.forEach { put(it, 0) } }
        val femaleDistribution = mutableMapOf<String, Int>().apply { tranches.forEach { put(it, 0) } }
        
        employees.forEach { user ->
            val age = try {
                val birthYear = user.birthDate.split("-")[0].toInt()
                2026 - birthYear
            } catch (e: Exception) { 30 }
            
            val tranche = when {
                age >= 55 -> "55+"
                age >= 45 -> "45-54"
                age >= 35 -> "35-44"
                age >= 25 -> "25-34"
                else -> "<25"
            }
            
            if (user.gender == "Femme") {
                femaleDistribution[tranche] = femaleDistribution[tranche]!! + 1
            } else {
                maleDistribution[tranche] = maleDistribution[tranche]!! + 1
            }
        }
        Pair(maleDistribution, femaleDistribution)
    }

    val maxVal = remember(distributions) {
        val mMax = distributions.first.values.maxOrNull() ?: 1
        val fMax = distributions.second.values.maxOrNull() ?: 1
        maxOf(mMax, fMax).coerceAtLeast(1)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Labels
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("👨 HOMMES (gauche)", color = MaleBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("Tranche d'âge", color = Color.Gray, fontSize = 11.sp)
            Text("👩 FEMMES (droite)", color = FemalePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))

        tranches.forEach { tranche ->
            val mCount = distributions.first[tranche] ?: 0
            val fCount = distributions.second[tranche] ?: 0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Male bar (expands leftwards)
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    val scale = mCount.toFloat() / maxVal.toFloat()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (mCount > 0) {
                            Text("$mCount", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(end = 4.dp))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(scale.coerceAtLeast(0.02f))
                                .height(16.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                                .background(MaleBlue)
                        )
                    }
                }

                // Middle age tranche label
                Box(
                    modifier = Modifier
                        .width(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tranche,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueNavy,
                        textAlign = TextAlign.Center
                    )
                }

                // Female bar (expands rightwards)
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    val scale = fCount.toFloat() / maxVal.toFloat()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(scale.coerceAtLeast(0.02f))
                                .height(16.dp)
                                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                                .background(FemalePink)
                        )
                        if (fCount > 0) {
                            Text("$fCount", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

// 2. Pyramide d'anciennete - Circular/Pie Slice View
@Composable
fun TenurePieChart(employees: List<User>) {
    val tranches = listOf("<1 an", "1-3 ans", "3-5 ans", "5-10 ans", "10+ ans")
    val tenureColors = listOf(
        Color(0xFF81E6D9), // Teal
        Color(0xFF319795), // Dark teal
        Color(0xFF3182CE), // Blue
        Color(0xFF2B6CB0), // Dark blue
        Color(0xFF1A365D)  // Navy
    )

    val distributions = remember(employees) {
        val mapping = mutableMapOf<String, Int>().apply { tranches.forEach { put(it, 0) } }
        employees.forEach { user ->
            val tenureYr = try {
                val joinYr = user.joiningDate.split("-")[0].toInt()
                2026 - joinYr
            } catch (e: Exception) { 1 }

            val tranche = when {
                tenureYr >= 10 -> "10+ ans"
                tenureYr >= 5 -> "5-10 ans"
                tenureYr >= 3 -> "3-5 ans"
                tenureYr >= 1 -> "1-3 ans"
                else -> "<1 an"
            }
            mapping[tranche] = mapping[tranche]!! + 1
        }
        mapping
    }

    val total = remember(distributions) { distributions.values.sum().toFloat() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Draw standard Pie Chart in Canvas
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .weight(1.2f)
        ) {
            if (total == 0f) {
                drawCircle(color = Color.LightGray, style = Stroke(width = 24.dp.toPx()))
                return@Canvas
            }
            var startAngle = -90f
            tranches.forEachIndexed { idx, key ->
                val count = distributions[key] ?: 0
                if (count > 0) {
                    val sweepAngle = (count.toFloat() / total) * 360f
                    drawArc(
                        color = tenureColors[idx],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Legends
        Column(
            modifier = Modifier.weight(1.5f),
            verticalArrangement = Arrangement.Center
        ) {
            tranches.forEachIndexed { idx, key ->
                val count = distributions[key] ?: 0
                val pct = if (total > 0) (count / total) * 100f else 0f
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(tenureColors[idx], CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$key : $count (${String.format("%.0f", pct)}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueNavy
                    )
                }
            }
        }
    }
}

// 4. Contract Types Donut Chart
@Composable
fun ContractDonutChart(employees: List<User>) {
    val contracts = listOf("CDI", "CDD", "Alternance", "Stage", "Freelance")
    val colors = listOf(
        Color(0xFF1A365D), // CDI - Navy
        Color(0xFF3182CE), // CDD - Blue
        Color(0xFFED8936), // Alternance - Orange
        Color(0xFFD69E2E), // Stage - Yellow
        Color(0xFFE53E3E)  // Freelance - Red
    )

    val distributions = remember(employees) {
        val mapping = mutableMapOf<String, Int>().apply { contracts.forEach { put(it, 0) } }
        employees.forEach { user ->
            val cType = user.contractType
            if (mapping.containsKey(cType)) {
                mapping[cType] = mapping[cType]!! + 1
            } else {
                mapping["CDI"] = mapping["CDI"]!! + 1
            }
        }
        mapping
    }

    val total = remember(distributions) { distributions.values.sum().toFloat() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Draw neat Donut Pie Chart
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .weight(1.2f)
        ) {
            if (total == 0f) {
                drawCircle(color = Color.LightGray, style = Stroke(width = 24.dp.toPx()))
                return@Canvas
            }
            var startAngle = -90f
            val strokeWidth = 24.dp.toPx()
            contracts.forEachIndexed { idx, key ->
                val count = distributions[key] ?: 0
                if (count > 0) {
                    val sweepAngle = (count.toFloat() / total) * 360f
                    drawArc(
                        color = colors[idx],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Legends
        Column(
            modifier = Modifier.weight(1.5f),
            verticalArrangement = Arrangement.Center
        ) {
            contracts.forEachIndexed { idx, key ->
                val count = distributions[key] ?: 0
                val pct = if (total > 0) (count / total) * 100f else 0f
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(colors[idx], CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$key : $count (${String.format("%.0f", pct)}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueNavy
                    )
                }
            }
        }
    }
}

// 3. Workforce evolution trends over 12 months (Arrivées, Départs & Net)
@Composable
fun WorkforceTrendsLineChart() {
    val months = listOf("J", "A", "S", "O", "N", "D", "J", "F", "M", "A", "M", "J")
    val dataPointsNet = listOf(5, 5, 6, 6, 7, 7, 8, 8, 8, 9, 9, 10) // Net employees
    val checkIns = listOf(1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1) // Entrées
    val checkOuts = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) // Sorties

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp, 3.dp).background(BlueNavy))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Effectif Net", fontSize = 10.sp, color = BlueNavy, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp, 3.dp).background(DarkOrange))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Entrées (recrutements)", fontSize = 10.sp, color = DarkOrange, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Draw line chart in custom canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFFF7FAFC), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            val widthStep = size.width / (months.size - 1)
            val maxVal = 10f
            val minVal = 0f
            val heightRange = size.height

            // Net curve path
            val pointsNet = dataPointsNet.mapIndexed { idx, valNet ->
                val x = idx * widthStep
                val y = size.height - ((valNet - minVal) / (maxVal - minVal)) * heightRange
                Offset(x, y)
            }

            // Entrées curve path
            val pointsIn = checkIns.mapIndexed { idx, valIn ->
                val x = idx * widthStep
                val y = size.height - ((valIn - minVal) / (maxVal - minVal)) * heightRange
                Offset(x, y)
            }

            // Draw line net
            for (i in 0 until pointsNet.size - 1) {
                drawLine(
                    color = BlueNavy,
                    start = pointsNet[i],
                    end = pointsNet[i + 1],
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Draw points net
            pointsNet.forEach { pt ->
                drawCircle(color = BlueNavy, radius = 4.dp.toPx(), center = pt)
            }

            // Draw line entries
            for (i in 0 until pointsIn.size - 1) {
                drawLine(
                    color = DarkOrange,
                    start = pointsIn[i],
                    end = pointsIn[i + 1],
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Month labels
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            months.forEach { m ->
                Text(m, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.width(16.dp), textAlign = TextAlign.Center)
            }
        }
    }
}

// 5. Répartition par département - Custom horizontal bars
@Composable
fun DepartmentDistributionChart(employees: List<User>) {
    val distributions = remember(employees) {
        val mapping = mutableMapOf<String, Int>()
        employees.forEach { user ->
            mapping[user.department] = (mapping[user.department] ?: 0) + 1
        }
        mapping.toList().sortedByDescending { it.second }
    }

    val total = remember(employees) { employees.size.toFloat() }
    val maxVal = remember(distributions) { distributions.firstOrNull()?.second ?: 1 }

    val deptColors = mapOf(
        "R&D" to Color(0xFF1A365D),
        "Service RH" to Color(0xFFED8936),
        "Design" to Color(0xFF3182CE),
        "Marketing" to Color(0xFF38A169)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        distributions.forEach { (dept, count) ->
            val pct = if (total > 0) (count / total) * 100f else 0f
            val scale = count.toFloat() / maxVal.toFloat()
            val themeColor = deptColors[dept] ?: NavyLight

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dept,
                    modifier = Modifier.width(90.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(scale)
                            .fillMaxHeight()
                            .background(themeColor)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "$count (${String.format("%.0f", pct)}%)",
                    modifier = Modifier.width(48.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueNavy,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
