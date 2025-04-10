package com.example.camc_praktikum1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.camc_praktikum1.ui.screens.AppScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.camc_praktikum1.ui.screens.HomeScreen
import com.example.camc_praktikum1.ui.screens.SensorScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CAMCApp(
    //viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route?: AppScreen.Home.name
    )
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
                    Text("CAMC Praktikum #1", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)

                    HorizontalDivider()

                    AppScreen.entries.forEach { screen ->
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
        },
        drawerState = drawerState
    ) {
        // Screen content (shown indirectly via NavHost below)
        Scaffold(
            topBar = {
                MyAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    drawerScope = drawerScope,
                    drawerState = drawerState,
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            // NavHost defines screen routes and start screen
            NavHost(
                navController = navController,
                startDestination = AppScreen.Home.name,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ) {
                composable(route = AppScreen.Home.name) {
                    HomeScreen()
                }
                composable(route = AppScreen.Sensors.name) {
                    SensorScreen()
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    currentScreen: AppScreen,
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