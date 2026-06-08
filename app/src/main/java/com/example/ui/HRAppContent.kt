package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.LeaveRequest
import com.example.data.entity.TimeLog
import com.example.data.entity.User
import com.example.ui.viewmodel.HRViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HRAppContent(
    viewModel: HRViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var forceDarkTheme by remember { mutableStateOf(false) }

    // Use our customized central theme!
    com.example.ui.theme.MyApplicationTheme(darkTheme = forceDarkTheme) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    "LOGIN" -> LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { }
                    )
                    "MAIN" -> {
                        currentUser?.let { user ->
                            MainScreen(
                                viewModel = viewModel,
                                user = user,
                                darkTheme = forceDarkTheme,
                                onThemeToggle = { forceDarkTheme = !forceDarkTheme }
                            )
                        } ?: run {
                            viewModel.currentScreen.value = "LOGIN"
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: HRViewModel,
    onLoginSuccess: () -> Unit
) {
    val username by viewModel.loginUsernameText.collectAsStateWithLifecycle()
    val password by viewModel.loginPasswordText.collectAsStateWithLifecycle()
    val error by viewModel.loginError.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoginLoading.collectAsStateWithLifecycle()

    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Distinct Logo Icon Block
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "RH Logo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Title / Sub-titling
            Text(
                text = "PORTAIL RH HORIZON",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Connexion sécurisée pour les employés",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Inputs Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Identifiez-vous",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Error banner
                    error?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { viewModel.loginUsernameText.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        label = { Text("Nom d'utilisateur") },
                        placeholder = { Text("ex: thomas") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "User Icon")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                        )
                    )

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { viewModel.loginPasswordText.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        label = { Text("Mot de passe") },
                        placeholder = { Text("••••••••") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Lock Icon")
                        },
                        trailingIcon = {
                            Text(
                                text = if (passwordVisible) "Masquer" else "Afficher",
                                modifier = Modifier
                                    .clickable { passwordVisible = !passwordVisible }
                                    .padding(end = 12.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Connect button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.attemptLogin()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Se connecter",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Demo Shortcut Section as useful helper chips
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rôles de démonstration pour essai rapide :",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable { viewModel.selectDemoUser("thomas") }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Thomas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Employé", fontSize = 9.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable { viewModel.selectDemoUser("sophie") }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Sophie", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Admin RH", fontSize = 9.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable { viewModel.selectDemoUser("lucas") }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Lucas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Manager", fontSize = 9.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: HRViewModel,
    user: User,
    darkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == "DASHBOARD",
                    onClick = { viewModel.currentTab.value = "DASHBOARD" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard Icon") },
                    label = { Text("Tableau", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == "CLOCKS",
                    onClick = { viewModel.currentTab.value = "CLOCKS" },
                    icon = { Icon(Icons.Default.Refresh, contentDescription = "Time Tracking Icon") },
                    label = { Text("Temps", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == "LEAVES",
                    onClick = { viewModel.currentTab.value = "LEAVES" },
                    icon = { Icon(Icons.Default.List, contentDescription = "Leaves Icon") },
                    label = { Text("Congés", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = currentTab == "PROFILE",
                    onClick = { viewModel.currentTab.value = "PROFILE" },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile Icon") },
                    label = { Text("Profil", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Elegant Local Header Bar
            val frenchToday = remember {
                try {
                    SimpleDateFormat("EEEE d MMMM", Locale.FRENCH).format(Date()).uppercase()
                } catch (e: Exception) {
                    "LUNDI 8 JUIN"
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = frenchToday,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Bonjour, ${user.fullName.substringBefore(" ")}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ThemeToggleButton(darkTheme = darkTheme, onThemeToggle = onThemeToggle)
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Se déconnecter",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD0E4FF))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.fullName.take(2).uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF004A77)
                        )
                    }
                }
            }

            // Tab navigation switching
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                label = "TabContent"
            ) { tab ->
                when (tab) {
                    "DASHBOARD" -> DashboardTab(viewModel = viewModel, user = user)
                    "CLOCKS" -> TimeTrackingTab(viewModel = viewModel, user = user)
                    "LEAVES" -> LeavesTab(viewModel = viewModel, user = user)
                    "PROFILE" -> ProfileTab(viewModel = viewModel, user = user)
                }
            }
        }
    }
}

@Composable
fun ThemeToggleButton(
    darkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onThemeToggle() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (darkTheme) "🌙" else "☀️",
            fontSize = 16.sp
        )
    }
}

@Composable
fun DashboardTab(
    viewModel: HRViewModel,
    user: User
) {
    val latestLog by viewModel.userLatestTimeLog.collectAsStateWithLifecycle()
    val logs by viewModel.userTimeLogs.collectAsStateWithLifecycle()
    val leaveRequests by viewModel.userLeaveRequests.collectAsStateWithLifecycle()

    val isClockedIn = latestLog != null && latestLog?.checkOutTime == null
    val onBreak = latestLog?.isBreakActive == true

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Elegant Welcome Card with dynamic color depending clocking status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isClockedIn) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                border = if (isClockedIn) {
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                } else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bonjour 👋",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Status Pulse dot
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isClockedIn) {
                                            if (onBreak) Color(0xFFFF9F0A) else Color(0xFF10B981)
                                        } else Color(0xFFEF4444)
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isClockedIn) {
                                    if (onBreak) "En pause" else "En service"
                                } else "Hors service",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isClockedIn) {
                                    if (onBreak) Color(0xFFFF9F0A) else Color(0xFF10B981)
                                } else Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• matricule: ${user.employeeId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }

        // Active Clock Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isClockedIn) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = if (!isClockedIn) {
                    BorderStroke(1.dp, Color(0xFFF0F0F0))
                } else null
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isClockedIn && latestLog != null) {
                        val activeLog = latestLog!!
                        
                        Text(
                            text = "Temps de travail aujourd'hui",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        val workedDurationStr = viewModel.formatWorkedDuration(activeLog)
                        Text(
                            text = workedDurationStr,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-1).sp,
                                fontSize = 42.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.testTag("clock_timer_text")
                        )

                        if (activeLog.totalBreakDurationMs > 0 || onBreak) {
                            Text(
                                text = "Breaks cumulés: ${String.format(Locale.getDefault(), "%dm", activeLog.totalBreakDurationMs / 60000)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Break option button (Pause)
                            Button(
                                onClick = { viewModel.toggleBreak() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    contentColor = Color.White
                                )
                            ) {
                                val label = if (onBreak) "Reprendre" else "Pause"
                                Text(
                                    text = "⏸ $label",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Complete Clock Out (Fin)
                            Button(
                                onClick = { viewModel.handleClockInOut() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("clock_out_button"),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text(
                                    text = "⏹ Fin",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Checked Out View
                        Text(
                            text = "Suivi du temps de travail",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        
                        Text(
                            text = "Vous êtes actuellement hors service",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Input fields for starting notes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                val currentLoc by viewModel.currentLogLocation.collectAsStateWithLifecycle()
                                var showLocMenu by remember { mutableStateOf(false) }

                                OutlinedButton(
                                    onClick = { showLocMenu = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = "Location", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(currentLoc, fontSize = 12.sp)
                                }
                                DropdownMenu(
                                    expanded = showLocMenu,
                                    onDismissRequest = { showLocMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Bureau") },
                                        onClick = {
                                            viewModel.currentLogLocation.value = "Bureau"
                                            showLocMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Télétravail") },
                                        onClick = {
                                            viewModel.currentLogLocation.value = "Télétravail"
                                            showLocMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Déplacement") },
                                        onClick = {
                                            viewModel.currentLogLocation.value = "Déplacement"
                                            showLocMenu = false
                                        }
                                    )
                                }
                            }

                            val currentNote by viewModel.currentLogNote.collectAsStateWithLifecycle()
                            OutlinedTextField(
                                value = currentNote,
                                onValueChange = { viewModel.currentLogNote.value = it },
                                placeholder = { Text("Notes (Optionnel)", fontSize = 12.sp) },
                                modifier = Modifier.weight(1.4f),
                                maxLines = 1,
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.handleClockInOut() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("clock_in_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Clock In", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Démarrer votre journée", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Metrics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card 1: Cumulative Worked hours in the past days (Semaine)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Clock icon square in light pink
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFD8E4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⏰", fontSize = 18.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Semaine",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(2.dp))

                        // Compute average/total from loaded history
                        val totalHrs = if (logs.isNotEmpty()) {
                            val activeHrs = logs.map { viewModel.getWorkedDurationMs(it) }.sum() / 3600000
                            "${activeHrs}h"
                        } else {
                            "32h 45m"
                        }

                        Text(
                            text = totalHrs,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Card 2: Leave balance (Congés)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Calendar icon square in light blue
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFC2E7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📅", fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Congés",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        Text(
                            text = "${user.leaveBalance} Jours",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Historical Preview Header
        item {
            Text(
                text = "Activité Récente",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 3 Compact entries of Log
        if (logs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune activité pour le moment.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            items(logs.take(3)) { log ->
                TimeLogCompactRow(log = log, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TimeLogCompactRow(
    log: TimeLog,
    viewModel: HRViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = log.date,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${viewModel.formatEpochToHour(log.checkInTime)} - ${log.checkOutTime?.let { viewModel.formatEpochToHour(it) } ?: "En cours"}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Location chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = log.checkInLocation ?: "Bureau",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = viewModel.formatWorkedDuration(log),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TimeTrackingTab(
    viewModel: HRViewModel,
    user: User
) {
    val logs by viewModel.userTimeLogs.collectAsStateWithLifecycle()
    val latestLog by viewModel.userLatestTimeLog.collectAsStateWithLifecycle()

    val isClockedIn = latestLog != null && latestLog?.checkOutTime == null
    val currentLoc by viewModel.currentLogLocation.collectAsStateWithLifecycle()
    val currentNote by viewModel.currentLogNote.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Enregistrement du temps de travail",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Detailed Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Paramètres de session active",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isClockedIn) {
                        Text(
                            text = "Vous êtes connecté en ${latestLog?.checkInLocation} depuis ${latestLog?.checkInTime?.let { viewModel.formatEpochToHour(it) }}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        latestLog?.note?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Note: \"$it\"",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        // Location Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Lieu de Travail : ", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Bureau", "Télétravail", "Déplacement").forEach { loc ->
                                    val selected = currentLoc == loc
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable { viewModel.currentLogLocation.value = loc }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = loc,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom note input
                        OutlinedTextField(
                            value = currentNote,
                            onValueChange = { viewModel.currentLogNote.value = it },
                            label = { Text("Que faites-vous aujourd'hui ?") },
                            placeholder = { Text("ex: Réunion client, Code review...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.handleClockInOut() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isClockedIn) Color(0xFFEF4444) else Color(0xFF10B981)
                        )
                    ) {
                        Icon(
                            imageVector = if (isClockedIn) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Clock action"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isClockedIn) "Quitter le service (Clock Out)" else "Commencer le service (Clock In)",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Historial Header
        item {
            Text(
                text = "Historique Complet de Présence",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (logs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun historique de présence disponible.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            items(logs) { log ->
                TimeLogDetailedRow(log = log, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TimeLogDetailedRow(
    log: TimeLog,
    viewModel: HRViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Session duration",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.date,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = log.checkInLocation ?: "Bureau",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ARRIVÉE", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(
                        text = viewModel.formatEpochToHour(log.checkInTime),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text("DÉPART", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(
                        text = log.checkOutTime?.let { viewModel.formatEpochToHour(it) } ?: "En service",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (log.checkOutTime == null) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("DURÉE EFFECTIVE", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(
                        text = viewModel.formatWorkedDuration(log),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            log.note?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: \"$it\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun LeavesTab(
    viewModel: HRViewModel,
    user: User
) {
    val leaveRequests by viewModel.userLeaveRequests.collectAsStateWithLifecycle()
    val error by viewModel.leaveFormError.collectAsStateWithLifecycle()
    val success by viewModel.leaveFormSuccess.collectAsStateWithLifecycle()

    val leaveType by viewModel.leaveTypeSelected.collectAsStateWithLifecycle()
    val startDate by viewModel.leaveStartDateText.collectAsStateWithLifecycle()
    val endDate by viewModel.leaveEndDateText.collectAsStateWithLifecycle()
    val reason by viewModel.leaveReasonText.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${user.leaveBalance}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Congés Disponibles",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Votre solde de jours de congés payés & RTT cumulés.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Submittal Form Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Demander un congé / RTT",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Error & Success indicators
                    error?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                .padding(12.dp)
                        ) {
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    success?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.1f))
                                .padding(12.dp)
                        ) {
                            Text(it, color = Color(0xFF10B981), style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // Leave Type Radio Line
                    Column {
                        Text("Type de congé", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Congé Payé", "Congé Maladie", "RTT").forEach { type ->
                                val active = leaveType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { viewModel.leaveTypeSelected.value = type }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Date range row textfields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { viewModel.leaveStartDateText.value = it },
                            label = { Text("Date début") },
                            placeholder = { Text("AAAA-MM-JJ") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("leave_start_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors()
                        )

                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { viewModel.leaveEndDateText.value = it },
                            label = { Text("Date fin") },
                            placeholder = { Text("AAAA-MM-JJ") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("leave_end_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors()
                        )
                    }

                    // Description textfield
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { viewModel.leaveReasonText.value = it },
                        label = { Text("Motif / Explication") },
                        placeholder = { Text("Pourquoi demandez-vous cette absence ?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("leave_reason_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors()
                    )

                    Button(
                        onClick = { viewModel.submitLeaveRequest() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_leave_button"),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Submit Leave", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Envoyer la demande", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Leave Requests Header
        item {
            Text(
                text = "Vos demandes d'absences",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (leaveRequests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucune demande d'absences soumise.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            items(leaveRequests) { req ->
                LeaveRequestRow(req = req)
            }
        }
    }
}

@Composable
fun LeaveRequestRow(req: LeaveRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = req.type,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Du ${req.startDate} au ${req.endDate}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Render Status badge with adaptive background colors
                val statusColor = when (req.status) {
                    "En attente" -> Color(0xFFF1C40F)
                    "Approuvé" -> Color(0xFF2ECC71)
                    else -> Color(0xFFE74C3C)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = req.status,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Motif : \"${req.reason}\"",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ProfileTab(
    viewModel: HRViewModel,
    user: User
) {
    val companyUsers by viewModel.allCompanyUsers.collectAsStateWithLifecycle()
    val companyLeaveRequests by viewModel.allCompanyLeaveRequests.collectAsStateWithLifecycle()
    val companyTimeLogs by viewModel.allCompanyTimeLogs.collectAsStateWithLifecycle()

    var showHrTab by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simple elegant Profile header card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile big bubble
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.fullName.take(2).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${user.role} • ${user.department}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileDetailRow(label = "Matricule", value = user.employeeId)
                    ProfileDetailRow(label = "Adresse Email", value = user.email)
                    ProfileDetailRow(label = "Date d'embauche", value = user.joiningDate)
                    ProfileDetailRow(label = "Solde congés", value = "${user.leaveBalance} jours restants")
                }
            }
        }

        // CONSOLE ADMIN / MANAGER CHECK-BOX (if role is Admin RH or Manager)
        if (user.role == "Admin RH" || user.role == "Manager") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONSOLE DE GESTION ADMINISTRATIVE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    // Toggle Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { showHrTab = !showHrTab }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (showHrTab) "Cacher Console" else "Afficher Console",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (showHrTab) {
                // List of pending actions
                val pendingRequests = companyLeaveRequests.filter { it.status == "En attente" }

                item {
                    Text(
                        text = "Demandes de congés à valider (${pendingRequests.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (pendingRequests.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aucune demande de congés en attente.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                } else {
                    items(pendingRequests) { req ->
                        AdminApprovalCard(req = req, viewModel = viewModel)
                    }
                }

                // Active employees checkin block
                item {
                    Text(
                        text = "Présence de l'équipe aujourd'hui",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Filter current active logs (no checkout)
                            val activeLogs = companyTimeLogs.filter { it.checkOutTime == null }
                            
                            if (activeLogs.isEmpty()) {
                                Text(
                                    text = "Aucun employé actuellement connecté.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            } else {
                                activeLogs.forEach { log ->
                                    // Match user
                                    val matchedUser = companyUsers.find { it.id == log.userId }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF10B981))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = matchedUser?.fullName ?: "Employé",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }

                                        Text(
                                            text = "Arrivé à ${viewModel.formatEpochToHour(log.checkInTime)} (${log.checkInLocation})",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
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

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    }
}

@Composable
fun AdminApprovalCard(
    req: LeaveRequest,
    viewModel: HRViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = req.userName,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${req.type} • Du ${req.startDate} au ${req.endDate}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "En attente",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Motif : \"${req.reason}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Reject
                OutlinedButton(
                    onClick = { viewModel.updateLeaveStatus(req.id, "Refusé") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                ) {
                    Text("Refuser", fontSize = 12.sp)
                }

                // Approve
                Button(
                    onClick = { viewModel.updateLeaveStatus(req.id, "Approuvé") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Approuver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
