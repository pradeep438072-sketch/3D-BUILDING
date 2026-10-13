package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.BuildingViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ColorPickerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FloorConfigScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlanInputScreen
import com.example.ui.screens.ReportDownloadScreen
import com.example.ui.screens.SavedProjectsScreen
import com.example.ui.screens.VisualizationScreen
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode

class MainActivity : ComponentActivity() {

    private val viewModel: BuildingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainApp(viewModel = viewModel, isDarkTheme = isDarkTheme)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: BuildingViewModel, isDarkTheme: Boolean) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentPlan by viewModel.currentPlan.collectAsStateWithLifecycle()
    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    val exportedObjContent by viewModel.exportedObjContent.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect snackbar messages
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Hardware back navigation handler
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.handleBack()
    }

    val isTopLevelScreen = currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.DASHBOARD,
        AppScreen.PLAN_INPUT,
        AppScreen.VISUALIZATION,
        AppScreen.SAVED_PROJECTS
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (!isTopLevelScreen) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentScreen) {
                                AppScreen.FLOOR_CONFIG -> "Floor & Room Layout"
                                AppScreen.COLOR_PICKER -> "Room Colors"
                                AppScreen.REPORT_DOWNLOAD -> "Project Report & Export"
                                AppScreen.AUTH -> "Architect Account"
                                else -> "Building Visualizer"
                            },
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.handleBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.toggleTheme() },
                            modifier = Modifier.testTag("top_bar_theme_toggle")
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        },
        bottomBar = {
            if (isTopLevelScreen) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_home"),
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DASHBOARD,
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        modifier = Modifier.testTag("nav_dashboard"),
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PLAN_INPUT,
                        onClick = { viewModel.navigateTo(AppScreen.PLAN_INPUT) },
                        icon = { Icon(Icons.Default.Edit, contentDescription = "Plan") },
                        label = { Text("Plan") },
                        modifier = Modifier.testTag("nav_plan"),
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.VISUALIZATION,
                        onClick = { viewModel.navigateTo(AppScreen.VISUALIZATION) },
                        icon = { Icon(Icons.Default.ViewInAr, contentDescription = "3D View") },
                        label = { Text("3D View") },
                        modifier = Modifier.testTag("nav_view_3d"),
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SAVED_PROJECTS,
                        onClick = { viewModel.navigateTo(AppScreen.SAVED_PROJECTS) },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Saved") },
                        label = { Text("Saved") },
                        modifier = Modifier.testTag("nav_saved"),
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(
                        currentPlan = currentPlan,
                        activeUser = activeUser,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { viewModel.toggleTheme() },
                        onNavigate = { viewModel.navigateTo(it) },
                        onLoadSample = { viewModel.setPlan(it) }
                    )
                    AppScreen.DASHBOARD -> DashboardScreen(
                        currentPlan = currentPlan,
                        recentProjects = savedProjects,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSelectProject = {
                            viewModel.setPlan(it)
                            viewModel.navigateTo(AppScreen.VISUALIZATION)
                        }
                    )
                    AppScreen.PLAN_INPUT -> PlanInputScreen(
                        currentPlan = currentPlan,
                        onSaveMetadata = { name, type, w, l, fc, rs, bs ->
                            viewModel.updatePlanMetadata(name, type, w, l, fc, rs, bs)
                        },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.FLOOR_CONFIG -> FloorConfigScreen(
                        currentPlan = currentPlan,
                        onUpdateFloorRooms = { fIdx, rooms ->
                            viewModel.updateFloorRooms(fIdx, rooms)
                        },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.VISUALIZATION -> VisualizationScreen(
                        buildingPlan = currentPlan,
                        buildingJson = viewModel.getThreeJsJson(),
                        onSavePlan = { viewModel.saveCurrentPlan() },
                        onExportReady = { viewModel.setExportedObj(it) },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.COLOR_PICKER -> ColorPickerScreen(
                        currentPlan = currentPlan,
                        onUpdateRoomColor = { type, hex ->
                            viewModel.updateRoomColor(type, hex)
                        },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.SAVED_PROJECTS -> SavedProjectsScreen(
                        projects = savedProjects,
                        onSelectProject = {
                            viewModel.setPlan(it)
                            viewModel.navigateTo(AppScreen.VISUALIZATION)
                        },
                        onDeleteProject = { viewModel.deletePlan(it) },
                        onDuplicateProject = { viewModel.duplicatePlan(it) },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.REPORT_DOWNLOAD -> ReportDownloadScreen(
                        currentPlan = currentPlan,
                        exportedObjContent = exportedObjContent,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AppScreen.AUTH -> AuthScreen(
                        activeUser = activeUser,
                        onLogin = { u, e, f -> viewModel.login(u, e, f) },
                        onLogout = { viewModel.logout() },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            }
        }
    }
}
