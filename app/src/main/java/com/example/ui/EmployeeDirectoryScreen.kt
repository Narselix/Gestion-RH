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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.shadow
import com.example.data.entity.EmployeeDoc
import com.example.data.entity.Skill
import com.example.data.entity.User
import com.example.ui.viewmodel.HRViewModel

@Composable
fun EmployeeDirectoryScreen(viewModel: HRViewModel) {
    val users by viewModel.allCompanyUsers.collectAsStateWithLifecycle()
    val skills by viewModel.allSkills.collectAsStateWithLifecycle()
    val documents by viewModel.allDocuments.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedDeptFilter by remember { mutableStateOf("Tous") }
    var selectedEmployeeForDetail by remember { mutableStateOf<User?>(null) }
    var activeSubTab by remember { mutableStateOf("ANNUAIRE") } // "ANNUAIRE" or "ORGANIGRAMME"

    val filteredList = remember(users, searchQuery, selectedDeptFilter) {
        users.filter { user ->
            (selectedDeptFilter == "Tous" || user.department == selectedDeptFilter) &&
            (user.fullName.contains(searchQuery, ignoreCase = true) || 
             user.employeeId.contains(searchQuery, ignoreCase = true) ||
             user.department.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("employee_directory_screen")
    ) {
        // Header
        Text(
            text = "Annuaire & Organigramme RH",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = BlueNavy
        )
        Text(
            text = "Consultez, gérez les compétences et structurez vos équipes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle subtabs: Directory vs Org Chart
        TabRow(
            selectedTabIndex = if (activeSubTab == "ANNUAIRE") 0 else 1,
            containerColor = Color.Transparent,
            contentColor = BlueNavy
        ) {
            Tab(
                selected = activeSubTab == "ANNUAIRE",
                onClick = { activeSubTab = "ANNUAIRE" },
                text = { Text("Annuaire Collaborateurs", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubTab == "ORGANIGRAMME",
                onClick = { activeSubTab = "ORGANIGRAMME" },
                text = { Text("Organigramme Hiérarchique", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeSubTab == "ANNUAIRE") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Directory list with filters
                Column(modifier = Modifier.weight(1.2f)) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Rechercher un collaborateur...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BlueNavy)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Department filters scrollable row
                    val depts = listOf("Tous", "R&D", "Service RH", "Design", "Marketing")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        depts.forEach { dept ->
                            FilterChip(
                                selected = selectedDeptFilter == dept,
                                onClick = { selectedDeptFilter = dept },
                                label = { Text(dept, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BlueNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Employee List
                    Box(modifier = Modifier.weight(1f)) {
                        if (filteredList.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Aucun collaborateur trouvé.", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredList.size) { index ->
                                    val emp = filteredList[index]
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedEmployeeForDetail = emp },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedEmployeeForDetail?.id == emp.id) 
                                                BlueNavy.copy(alpha = 0.08f) else 
                                                MaterialTheme.colorScheme.surface
                                        ),
                                        border = if (selectedEmployeeForDetail?.id == emp.id) 
                                            BorderStroke(1.5.dp, BlueNavy) else null
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Avatar
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(BlueNavy.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = emp.fullName.take(2).uppercase(),
                                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                    color = BlueNavy
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = emp.fullName,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = BlueNavy
                                                )
                                                Text(
                                                    text = "${emp.role} • ${emp.department}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }

                                            // Contract tag badge
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(DarkOrange.copy(alpha = 0.12f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(emp.contractType, color = DarkOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Right Column: Advanced detailed profile panel
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    selectedEmployeeForDetail?.let { emp ->
                        EmployeeProfileDetailPanel(
                            employee = emp,
                            skills = skills.filter { it.userId == emp.id },
                            docs = documents.filter { it.userId == emp.id },
                            viewModel = viewModel
                        )
                    } ?: run {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AccountBox, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Sélectionnez un employé pour gérer", color = Color.Gray, fontSize = 13.sp)
                                Text("ses compétences et ses justificatifs", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // ORGANIGRAMME View
            CompanyHierarchyTreeView(users = users)
        }
    }
}

// Side detailed panel with inline Skills & Documents generation forms
@Composable
fun EmployeeProfileDetailPanel(
    employee: User,
    skills: List<Skill>,
    docs: List<EmployeeDoc>,
    viewModel: HRViewModel
) {
    var skillFormOpen by remember { mutableStateOf(false) }
    val newSkillName by viewModel.skillName.collectAsStateWithLifecycle()
    val newSkillLevel by viewModel.skillLevel.collectAsStateWithLifecycle()
    val newSkillCert by viewModel.skillCert.collectAsStateWithLifecycle()

    var docFormOpen by remember { mutableStateOf(false) }
    val newDocName by viewModel.docNameInput.collectAsStateWithLifecycle()
    val newDocType by viewModel.docTypeInput.collectAsStateWithLifecycle()
    val newDocFile by viewModel.docFileName.collectAsStateWithLifecycle()
    val newDocExpiry by viewModel.docExpiryInput.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Upper Profile Card
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(BlueNavy),
                contentAlignment = Alignment.Center
            ) {
                Text(employee.fullName.take(2).uppercase(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(employee.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BlueNavy)
                Text(employee.employeeId, fontSize = 12.sp, color = Color.Gray)
                Text("Inscrit le: ${employee.joiningDate}", fontSize = 11.sp, color = Color.Gray)
            }
        }

        Divider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFE2E8F0))

        // Contract details lists
        Text("Détails Administrateur", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        DetailRowItem(label = "E-mail", value = employee.email)
        DetailRowItem(label = "Département", value = employee.department)
        DetailRowItem(label = "Rôle RH", value = employee.role)
        DetailRowItem(label = "Contrat", value = employee.contractType)
        DetailRowItem(label = "Date Naissance", value = employee.birthDate)
        DetailRowItem(label = "Genre", value = employee.gender)

        Divider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFE2E8F0))

        // 1. COMPETENCES (SKILLS LIST)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Compétences (${skills.size})", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 13.sp)
            TextButton(onClick = { skillFormOpen = !skillFormOpen }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (skillFormOpen) Icons.Default.Close else Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (skillFormOpen) "Fermer" else "Ajouter", fontSize = 11.sp)
                }
            }
        }

        if (skillFormOpen) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Nouvelle Compétence", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BlueNavy)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newSkillName,
                        onValueChange = { viewModel.skillName.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nom (ex: Figma, Kotlin)") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Level scroll Row selector
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Débutant", "Intermédiaire", "Expert").forEach { lvl ->
                            FilterChip(
                                selected = newSkillLevel == lvl,
                                onClick = { viewModel.skillLevel.value = lvl },
                                label = { Text(lvl, fontSize = 10.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newSkillCert,
                        onValueChange = { viewModel.skillCert.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Certification (optionnel)") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.appendSkill(employee.id, employee.fullName)
                            skillFormOpen = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                    ) {
                        Text("Enregistrer compétence", fontSize = 11.sp)
                    }
                }
            }
        }

        if (skills.isEmpty()) {
            Text("Aucune compétence saisie.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
        } else {
            Column {
                skills.forEach { sk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color(0xFFF7FAFC), RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(sk.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueNavy)
                            if (sk.certificationName.isNotEmpty()) {
                                Text("🎓 ${sk.certificationName}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(BlueNavy.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(sk.level, color = BlueNavy, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFE2E8F0))

        // 2. DOCUMENTS / JUSTIFICATIFS (DOCUMENT LIST)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Documents RH (${docs.size})", fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 13.sp)
            TextButton(onClick = { docFormOpen = !docFormOpen }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (docFormOpen) Icons.Default.Close else Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (docFormOpen) "Fermer" else "Ajouter", fontSize = 11.sp)
                }
            }
        }

        if (docFormOpen) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Téléverser un Document", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BlueNavy)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newDocName,
                        onValueChange = { viewModel.docNameInput.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Intitulé (ex: Rib, Diplôme d'ingénieur)") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("CONTRAT", "CNI", "DIPLOME", "RIB").forEach { dType ->
                            FilterChip(
                                selected = newDocType == dType,
                                onClick = { viewModel.docTypeInput.value = dType },
                                label = { Text(dType, fontSize = 9.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newDocExpiry,
                        onValueChange = { viewModel.docExpiryInput.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Date d'expiration AAAA-MM-JJ (optionnel)") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.uploadDoc(employee.id)
                            docFormOpen = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                    ) {
                        Text("Télécharger justificatif", fontSize = 11.sp)
                    }
                }
            }
        }

        if (docs.isEmpty()) {
            Text("Aucun document justificatif téléversé.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
        } else {
            Column {
                docs.forEach { d ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.List, null, tint = BlueNavy, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(d.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueNavy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${d.docType} • Chargé le: ${d.uploadDate}", fontSize = 10.sp, color = Color.Gray)
                                if (d.expiryDate.isNotEmpty()) {
                                    Text("Expire le: ${d.expiryDate}", fontSize = 9.sp, color = Color.Red)
                                }
                            }
                        }
                        // Badge status validation
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF38A169).copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(d.status, color = Color(0xFF38A169), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Compact detail block
@Composable
fun DetailRowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Text(text = value, fontWeight = FontWeight.Bold, color = BlueNavy, fontSize = 12.sp)
    }
}

// Stunning Vertical Company Org Chart matching managers visually
@Composable
fun CompanyHierarchyTreeView(users: List<User>) {
    val roots = remember(users) { users.filter { it.managerId == null } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Arbre Hiérarchique de l'Entreprise",
            fontWeight = FontWeight.Bold,
            color = BlueNavy,
            fontSize = 16.sp
        )
        Text(
            "Lignes directrices de reporting de la direction aux collaborateurs",
            fontSize = 12.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        if (roots.isEmpty()) {
            Text("Aucune racine trouvée pour l'organigramme.", color = Color.Gray)
        } else {
            roots.forEach { root ->
                OrgNodeCard(node = root, allEmployees = users, depth = 0)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun OrgNodeCard(node: User, allEmployees: List<User>, depth: Int) {
    val children = remember(node, allEmployees) { allEmployees.filter { it.managerId == node.id } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = (depth * 20).dp) // indent visual representation
    ) {
        // Line pointing down visual indicator
        if (depth > 0) {
            Box(
                modifier = Modifier
                    .size(width = 2.dp, height = 20.dp)
                    .background(Color.Gray.copy(alpha = 0.5f))
            )
        }

        // Employee Card
        Card(
            modifier = Modifier
                .width(220.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (node.role) {
                    "Admin RH" -> BlueNavy.copy(alpha = 0.12f)
                    "Manager" -> DarkOrange.copy(alpha = 0.12f)
                    else -> MaterialTheme.colorScheme.surface
                }
            ),
            border = BorderStroke(1.dp, BlueNavy.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Short name indicator avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BlueNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Text(node.fullName.take(2).uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        node.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BlueNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        node.role,
                        fontSize = 10.sp,
                        color = Color.DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        node.department,
                        fontSize = 9.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (children.isNotEmpty()) {
            // Visual indicator: branching line pointing down
            Box(
                modifier = Modifier
                    .size(width = 2.dp, height = 16.dp)
                    .background(Color.Gray.copy(alpha = 0.5f))
            )

            // Horizontal connector line
            Box(
                modifier = Modifier
                    .width((children.size * 100).dp.coerceAtMost(250.dp))
                    .height(2.dp)
                    .background(Color.Gray.copy(alpha = 0.5f))
            )

            // Child Nodes rendered recursively in row structure
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                children.forEach { child ->
                    OrgNodeCard(node = child, allEmployees = allEmployees, depth = depth + 1)
                }
            }
        }
    }
}
