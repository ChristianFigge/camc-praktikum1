package com.example.camc_praktikum1

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.camc_praktikum1.ui.screens.AppScreenData
import com.example.camc_praktikum1.ui.screens.CollectDataScreen
import com.example.camc_praktikum1.ui.screens.DataIndexScreen
import com.example.camc_praktikum1.ui.screens.DataPlotScreen
import com.example.camc_praktikum1.ui.screens.HomeScreen
import com.example.camc_praktikum1.ui.screens.components.MyAppBar
import com.example.camc_praktikum1.ui.theme.CAMCpraktikum1Theme
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val HOME_ROUTE = AppScreenData.CollectData.name

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register an event launcher for document creation in the device's shared storage.
        // Note: registerForActivityResult() has to be used BEFORE Activity() creation is finished!
        // i.e. we have to define the Launcher here and use a local reference for dynamic file contents
        // ref https://developer.android.com/training/basics/intents/result#kotlin
        // Thanks a lot to mister Alaa https://stackoverflow.com/questions/64476827/how-to-resolve-the-error-lifecycleowners-must-call-register-before-they-are-sta
        var zipFileContent = byteArrayOf() // overwrite this and call Launcher.launch("filename")
        val zipFileCreationLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("application/zip")) { uri ->
            uri?.let {
                try {
                    val fileOutputStream = contentResolver.openOutputStream(it)
                    fileOutputStream?.write(zipFileContent)
                    fileOutputStream?.close()
                    Toast.makeText(this, "Datei erfolgreich gespeichert", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Fehler beim Speichern der Datei", Toast.LENGTH_SHORT).show()
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            CAMCpraktikum1Theme {
                // Get navigation stack (for the "back" arrow) & current screen
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = AppScreenData.valueOf(
                    backStackEntry?.destination?.route ?: HOME_ROUTE
                )

                // Navigation menu drawer (this object contains the whole screen content)
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
                        // define the Top Bar
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
                                    onGotoSensorsClick = { navController.navigate(AppScreenData.CollectData.name) },
                                    onGotoDataClick = { navController.navigate(AppScreenData.Data.name) }
                                )
                            }
                            composable(route = AppScreenData.Data.name) {
                                DataIndexScreen(
                                    onShowPlotClick = { navController.navigate(AppScreenData.DataPlot.name) },
                                    onGotoSensorsClick = { navController.navigate(AppScreenData.CollectData.name) },
                                    onExportClick = { data, fileName ->
                                        zipFileContent = data
                                        zipFileCreationLauncher.launch(fileName)
                                    },
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
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CAMCpraktikum1Theme {
        Greeting("Android")
    }
}