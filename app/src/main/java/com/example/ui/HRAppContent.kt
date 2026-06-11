package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
                    "REGISTER" -> RegisterScreen(
                        viewModel = viewModel,
                        onBackToLogin = { viewModel.currentScreen.value = "LOGIN" }
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
fun AuthBackground() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        backgroundColor
                    )
                )
            )
    ) {
        // Decorative soft glowing circles
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            // Soft top-left colored glow
            drawCircle(
                color = primaryColor.copy(alpha = 0.12f),
                radius = w * 0.4f,
                center = Offset(w * 0.1f, h * 0.1f)
            )
            
            // Soft bottom-right glow
            drawCircle(
                color = tertiaryColor.copy(alpha = 0.08f),
                radius = w * 0.35f,
                center = Offset(w * 0.9f, h * 0.8f)
            )
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthBackground()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .widthIn(max = 450.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Procedural Canvas vector forest drawing (Removing the lock logo completely)
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFFE8F5E9)) // Soft green background
                    .border(2.dp, Color(0xFF2E7D32).copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Left mid green tree
                    val pathLeft = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.35f, h * 0.20f)
                        lineTo(w * 0.12f, h * 0.68f)
                        lineTo(w * 0.58f, h * 0.68f)
                        close()
                    }
                    drawPath(pathLeft, color = Color(0xFF2E7D32))

                    // Left trunk
                    drawRect(
                        color = Color(0xFF5D4037),
                        topLeft = Offset(w * 0.31f, h * 0.68f),
                        size = Size(w * 0.08f, h * 0.15f)
                    )

                    // Right dark green tree
                    val pathRight = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.65f, h * 0.32f)
                        lineTo(w * 0.42f, h * 0.76f)
                        lineTo(w * 0.88f, h * 0.76f)
                        close()
                    }
                    drawPath(pathRight, color = Color(0xFF1B5E20))

                    // Right trunk
                    drawRect(
                        color = Color(0xFF5D4037),
                        topLeft = Offset(w * 0.61f, h * 0.76f),
                        size = Size(w * 0.08f, h * 0.15f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Title / Sub-titling
            Text(
                text = "PORTAIL RH HORIZON",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Gérez votre temps, absences et équipe en toute simplicité",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Inputs Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Identifiez-vous",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Success banner
                    val regSuccess by viewModel.registerSuccess.collectAsStateWithLifecycle()
                    regSuccess?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.dp, Color(0xFF81C784), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✅  ", fontSize = 14.sp)
                                Text(
                                    text = it,
                                    color = Color(0xFF2E7D32),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Error banner
                    error?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                                .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️  ", fontSize = 14.sp)
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
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
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "User Icon", tint = MaterialTheme.colorScheme.primary)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                            unfocusedContainerColor = Color.Transparent
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
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(Icons.Default.Key, contentDescription = "Key Icon", tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { passwordVisible = !passwordVisible }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = if (passwordVisible) "Masquer" else "Afficher",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Connect button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.attemptLogin()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("login_button"),
                        shape = CircleShape,
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Se connecter",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = "S'identifier", modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButton(
                        onClick = { viewModel.currentScreen.value = "REGISTER" },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Nouveau ici ? Créer un profil",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Demo Shortcut Section as useful helper chips with elite design
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rôles de démonstration pour essai rapide :",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                                .clickable { viewModel.selectDemoUser("thomas") }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Thomas 🧑‍💻", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Text("Employé", fontSize = 9.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                                .clickable { viewModel.selectDemoUser("sophie") }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Sophie 💼", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Text("Admin RH", fontSize = 9.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                                .clickable { viewModel.selectDemoUser("lucas") }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Lucas ⭐", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                Text("Manager", fontSize = 9.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: HRViewModel,
    user: User,
    darkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Dialog states
    var showExportDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDeptsDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                // User info header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(BlueNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.fullName.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = user.fullName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = BlueNavy
                    )
                    Text(
                        text = "${user.role} • ${user.department}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Drawer items
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Tableau de bord") },
                    selected = currentTab == "DASHBOARD",
                    onClick = {
                        viewModel.currentTab.value = "DASHBOARD"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    label = { Text("Suivi de Temps / Clocks") },
                    selected = currentTab == "CLOCKS",
                    onClick = {
                        viewModel.currentTab.value = "CLOCKS"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Gestion des Congés") },
                    selected = currentTab == "LEAVES",
                    onClick = {
                        viewModel.currentTab.value = "LEAVES"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Mon Profil Personnel") },
                    selected = currentTab == "PROFILE",
                    onClick = {
                        viewModel.currentTab.value = "PROFILE"
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Settings, Departments, Export
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Business, contentDescription = null) },
                    label = { Text("Gérer les Départements") },
                    selected = false,
                    onClick = {
                        showDeptsDialog = true
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Options & Thèmes") },
                    selected = false,
                    onClick = {
                        showSettingsDialog = true
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.CloudDownload, contentDescription = null) },
                    label = { Text("Exporter les Données (CSV/JSON)") },
                    selected = false,
                    onClick = {
                        showExportDialog = true
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Theme Mode Inline Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeToggle() }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (darkTheme) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (darkTheme) "Mode Sombre" else "Mode Clair")
                    }
                    Switch(checked = darkTheme, onCheckedChange = { onThemeToggle() })
                }

                // Logout
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    label = { Text("Se déconnecter", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        viewModel.logout()
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when(currentTab) {
                                "DASHBOARD" -> "Tableau de bord"
                                "EMPLOYEES" -> "Annuaire Collaborateurs"
                                "RECRUITMENT" -> "Recrutement & Talents"
                                "TRAINING" -> "Catalogue Formation"
                                "PAYROLL" -> "Historique Paye"
                                "CLOCKS" -> "Suivi de Présence"
                                "LEAVES" -> "Mes Congés"
                                else -> "Profil Utilisateur"
                            },
                            fontWeight = FontWeight.Bold,
                            color = BlueNavy,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Ouvrir Menu", tint = BlueNavy)
                        }
                    },
                    actions = {
                        ThemeToggleButton(darkTheme = darkTheme, onThemeToggle = onThemeToggle)
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(Icons.Default.ExitToApp, "Se déconnecter", tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == "DASHBOARD",
                        onClick = { viewModel.currentTab.value = "DASHBOARD" },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Tableau", fontSize = 10.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlueNavy,
                            indicatorColor = BlueNavy.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == "EMPLOYEES",
                        onClick = { viewModel.currentTab.value = "EMPLOYEES" },
                        icon = { Icon(Icons.Default.People, contentDescription = "Employees") },
                        label = { Text("Annuaire", fontSize = 10.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlueNavy,
                            indicatorColor = BlueNavy.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == "RECRUITMENT",
                        onClick = { viewModel.currentTab.value = "RECRUITMENT" },
                        icon = { Icon(Icons.Default.Work, contentDescription = "Recruitment") },
                        label = { Text("Recruter", fontSize = 10.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlueNavy,
                            indicatorColor = BlueNavy.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == "TRAINING",
                        onClick = { viewModel.currentTab.value = "TRAINING" },
                        icon = { Icon(Icons.Default.School, contentDescription = "Training") },
                        label = { Text("Formation", fontSize = 10.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlueNavy,
                            indicatorColor = BlueNavy.copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == "PAYROLL",
                        onClick = { viewModel.currentTab.value = "PAYROLL" },
                        icon = { Icon(Icons.Default.Payments, contentDescription = "Payroll") },
                        label = { Text("Paie", fontSize = 10.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BlueNavy,
                            indicatorColor = BlueNavy.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
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
                        "DASHBOARD" -> DashboardScreen(viewModel = viewModel)
                        "EMPLOYEES" -> EmployeeDirectoryScreen(viewModel = viewModel)
                        "RECRUITMENT" -> RecrutementScreen(viewModel = viewModel)
                        "TRAINING" -> FormationScreen(viewModel = viewModel)
                        "PAYROLL" -> PaieScreen(viewModel = viewModel)
                        "CLOCKS" -> TimeTrackingTab(viewModel = viewModel, user = user)
                        "LEAVES" -> LeavesTab(viewModel = viewModel, user = user)
                        "PROFILE" -> ProfileTab(viewModel = viewModel, user = user)
                    }
                }
            }
        }
    }

    // EXPORT DIALOG
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export des Données SGBD", fontWeight = FontWeight.Bold, color = BlueNavy) },
            text = { Text("Voulez-vous sérialiser et exporter la base de données Room globale vers les formats de rapport d'échange normalisés (CSV d'effectifs & JSON de synthèse d'audit) ?") },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                ) {
                    Text("Oui, Exporter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // SETTINGS DIALOG
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Options Globales & Thèmes", fontWeight = FontWeight.Bold, color = BlueNavy) },
            text = {
                Column {
                    Text("Ajustez les options de comportement du tableau de bord.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Activer notifications d'alertes", fontSize = 12.sp)
                        var checked by remember { mutableStateOf(true) }
                        Switch(checked = checked, onCheckedChange = { checked = it })
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Rafraîchissement automatique", fontSize = 12.sp)
                        var checked by remember { mutableStateOf(true) }
                        Switch(checked = checked, onCheckedChange = { checked = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                ) {
                    Text("Enregistrer")
                }
            }
        )
    }

    // DEPARTMENTS DIALOG
    if (showDeptsDialog) {
        AlertDialog(
            onDismissRequest = { showDeptsDialog = false },
            title = { Text("Gestion des Départements", fontWeight = FontWeight.Bold, color = BlueNavy) },
            text = {
                Column {
                    Text("Départements administratifs enregistrés :", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf("R&D", "Service RH", "Design", "Marketing").forEach { d ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Business, null, tint = BlueNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(d, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDeptsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueNavy)
                ) {
                    Text("Fermer")
                }
            }
        )
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
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${user.leaveBalance.toInt()}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Solde de Congés",
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 0.5.sp)
                        )
                        Text(
                            text = "Jours restants cumulés pour congés payés & RTT",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
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
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Faire une nouvelle demande",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Error & Success indicators
                    error?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                                .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️  ", fontSize = 14.sp)
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    success?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.dp, Color(0xFF81C784), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✅  ", fontSize = 14.sp)
                                Text(
                                    text = it,
                                    color = Color(0xFF2E7D32),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Leave Type Selection
                    Column {
                        Text(
                            text = "Catégorie d'absence",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                                            if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                        .border(
                                            1.dp,
                                            if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.Transparent,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.leaveTypeSelected.value = type }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
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
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
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
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
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
                        Spacer(modifier = Modifier.width(8.dp))
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
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

@Composable
fun RegisterScreen(
    viewModel: HRViewModel,
    onBackToLogin: () -> Unit
) {
    val username by viewModel.registerUsernameText.collectAsStateWithLifecycle()
    val password by viewModel.registerPasswordText.collectAsStateWithLifecycle()
    val fullName by viewModel.registerFullNameText.collectAsStateWithLifecycle()
    val email by viewModel.registerEmailText.collectAsStateWithLifecycle()
    val role by viewModel.registerRoleText.collectAsStateWithLifecycle()
    val department by viewModel.registerDepartmentText.collectAsStateWithLifecycle()
    val error by viewModel.registerError.collectAsStateWithLifecycle()
    val isLoading by viewModel.isRegisterLoading.collectAsStateWithLifecycle()

    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthBackground()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Distinct Logo Icon Block as modern floating premium shield
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                            )
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(22.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "S'enregistrer",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CRÉER UN COMPTE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Enregistrez votre nouveau profil de collaborateur",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Informations professionnelles",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Registration Error banner
                    error?.let {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                                    .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚠️  ", fontSize = 14.sp)
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Full Name Input
                    item {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { viewModel.registerFullNameText.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_fullname_input"),
                            label = { Text("Nom complet") },
                            placeholder = { Text("ex: Julie Dupont") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = "Nom complet", tint = MaterialTheme.colorScheme.primary)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    // Username Input
                    item {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { viewModel.registerUsernameText.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_username_input"),
                            label = { Text("Nom d'utilisateur") },
                            placeholder = { Text("ex: julie") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(Icons.Default.AccountBox, contentDescription = "Utilisateur", tint = MaterialTheme.colorScheme.primary)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    // Email Input
                    item {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.registerEmailText.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_email_input"),
                            label = { Text("Adresse e-mail") },
                            placeholder = { Text("ex: julie.dupont@entreprise.com") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = "Email", tint = MaterialTheme.colorScheme.primary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    // Role Selector
                    item {
                        Column {
                            Text(
                                text = "Rôle dans l'entreprise",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Employé", "Manager", "Admin RH").forEach { r ->
                                    val active = role == r
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            )
                                            .border(
                                                1.dp,
                                                if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent,
                                                RoundedCornerShape(14.dp)
                                            )
                                            .clickable { viewModel.registerRoleText.value = r }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = r,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Department Selector
                    item {
                        Column {
                            Text(
                                text = "Département",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("R&D", "Design", "Service RH", "Sales").forEach { dept ->
                                    val active = department == dept
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (active) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            )
                                            .border(
                                                1.dp,
                                                if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.Transparent,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { viewModel.registerDepartmentText.value = dept }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dept,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Password Input
                    item {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.registerPasswordText.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_password_input"),
                            label = { Text("Mot de passe") },
                            placeholder = { Text("••••••••") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = "Mot de passe", tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon = {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { passwordVisible = !passwordVisible }
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = if (passwordVisible) "Masquer" else "Afficher",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    // Action buttons
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.attemptRegister()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("register_button"),
                            shape = CircleShape,
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Confirmer la création",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Check, contentDescription = "Enregistrer", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    item {
                        TextButton(
                            onClick = onBackToLogin,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Retourner à l'écran de connexion",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
