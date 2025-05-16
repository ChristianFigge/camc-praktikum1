package com.example.camc_praktikum1

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.camc_praktikum1.ui.screens.AllSensorsScreen
import com.example.camc_praktikum1.ui.screens.AppScreenData
import com.example.camc_praktikum1.ui.screens.CollectDataScreen
import com.example.camc_praktikum1.ui.screens.DataIndexScreen
import com.example.camc_praktikum1.ui.screens.DataPlotScreen
import com.example.camc_praktikum1.ui.screens.HomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val HOME_ROUTE = AppScreenData.CollectData.name

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CAMCApp(
    navController: NavHostController = rememberNavController()
) {
    // Get navigation stack & current screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreenData.valueOf(
        backStackEntry?.destination?.route ?: HOME_ROUTE
    )

    // Nav menu drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))

                    // Header
                    Box(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column {
                            Text(
                                "CAMC Praktikum",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text("© Datenkraken, 2025",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    HorizontalDivider()

                    // Create menu item for each AppScreen
                    AppScreenData.entries.forEach { screen ->
                        if(screen.showInDrawer) {
                            NavigationDrawerItem(
                                label = { Text(screen.title) },
                                selected = false,
                                icon = { Icon(screen.icon, contentDescription = null) },
                                onClick = {
                                    navController.navigate(screen.name)
                                    drawerScope.launch {
                                        drawerState.close()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        drawerState = drawerState
    ) {
        // Screen content (shown indirectly via NavHost below)
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            topBar = {
                MyAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    drawerScope = drawerScope,
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->

            // NavHost defines screen routes and start screen
            NavHost(
                navController = navController,
                startDestination = HOME_ROUTE,
                modifier = Modifier
                    .fillMaxSize()
                    //.verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ) {
                composable(route = AppScreenData.Home.name) {
                    HomeScreen(
                        onGotoSensorsClick = { navController.navigate(AppScreenData.AllSensors.name) },
                        onGotoDataClick = { navController.navigate(AppScreenData.Data.name) }
                    )
                }
                composable(route = AppScreenData.AllSensors.name) {
                    AllSensorsScreen()
                }
                composable(route = AppScreenData.Data.name) {
                    DataIndexScreen(
                        onShowPlotClick = { navController.navigate(AppScreenData.DataPlot.name) },
                        onGotoSensorsClick = { navController.navigate(AppScreenData.AllSensors.name) }
                    )
                }
                composable(route = AppScreenData.DataPlot.name) {
                    DataPlotScreen()
                }
                composable(route = AppScreenData.CollectData.name) {
                    CollectDataScreen()
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    currentScreen: AppScreenData,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    drawerScope: CoroutineScope,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                currentScreen.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        // LEFT Button (Back-arrow if applicable, or empty)
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        // RIGHT Button(s) - Menu icon
        actions = {
            IconButton(
                onClick = {
                    drawerScope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}