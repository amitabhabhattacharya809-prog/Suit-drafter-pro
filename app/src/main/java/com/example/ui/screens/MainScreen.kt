package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MeasurementUnit
import com.example.ui.viewmodel.SuitDraftViewModel

enum class MainDestination(val label: String, val icon: @Composable () -> Unit) {
    CAD("Pattern CAD", { Icon(Icons.Default.Straighten, contentDescription = "Pattern CAD") }),
    MEASUREMENTS("Measurements", { Icon(Icons.Default.AccessibilityNew, contentDescription = "Measurements") }),
    STYLE("Suit Style", { Icon(Icons.Default.Tune, contentDescription = "Suit Style") }),
    PROFILES("Clients", { Icon(Icons.Default.FolderShared, contentDescription = "Clients") })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: SuitDraftViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(MainDestination.CAD) }
    val snackbarHostState = remember { SnackbarHostState() }

    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val measurements by viewModel.currentMeasurements.collectAsStateWithLifecycle()

    // Handle back button: if in a sub-tab, go back to CAD
    BackHandler(enabled = currentDestination != MainDestination.CAD) {
        currentDestination = MainDestination.CAD
    }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Surface(
                        color = Color.Transparent
                    ) {
                        Text(
                            text = "S A R T O R",
                            style = MaterialTheme.typography.titleMedium.copy(
                                letterSpacing = 3.sp,
                                fontWeight = FontWeight.Black
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .testTag("app_unit_badge")
                    ) {
                        Text(
                            text = measurements.unit.symbol.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val spec = viewModel.generateCutterSpecificationSheet()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Sartor Bespoke Pattern", spec))
                            Toast.makeText(context, "Full pattern specification copied!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("top_bar_export_button")
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Export Specs",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                MainDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination },
                        icon = destination.icon,
                        label = {
                            Text(
                                text = destination.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (currentDestination == destination) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_${destination.name}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                MainDestination.CAD -> {
                    DraftCanvasScreen(
                        viewModel = viewModel
                    )
                }
                MainDestination.MEASUREMENTS -> {
                    MeasurementsScreen(
                        viewModel = viewModel,
                        onNavigateToCanvas = { currentDestination = MainDestination.CAD }
                    )
                }
                MainDestination.STYLE -> {
                    StyleScreen(
                        viewModel = viewModel,
                        onNavigateToCanvas = { currentDestination = MainDestination.CAD }
                    )
                }
                MainDestination.PROFILES -> {
                    ProfilesScreen(
                        viewModel = viewModel,
                        onNavigateToCanvas = { currentDestination = MainDestination.CAD }
                    )
                }
            }
        }
    }
}
