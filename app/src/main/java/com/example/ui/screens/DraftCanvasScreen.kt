package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DraftPieceType
import com.example.data.model.formatLength
import com.example.ui.components.DraftCanvas
import com.example.ui.viewmodel.SuitDraftViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftCanvasScreen(
    viewModel: SuitDraftViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val draftResult by viewModel.draftResult.collectAsStateWithLifecycle()
    val measurements by viewModel.currentMeasurements.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedPieceType.collectAsStateWithLifecycle()

    val showGrid by viewModel.showGrid.collectAsStateWithLifecycle()
    val showDimensions by viewModel.showDimensions.collectAsStateWithLifecycle()
    val showSeamAllowance by viewModel.showSeamAllowance.collectAsStateWithLifecycle()
    val showNotches by viewModel.showNotches.collectAsStateWithLifecycle()
    val showConstructionLines by viewModel.showConstructionLines.collectAsStateWithLifecycle()

    val piece = draftResult.pieces[selectedType] ?: draftResult.pieces.values.first()

    var showNotesSheet by remember { mutableStateOf(false) }
    var outlineOnlyMode by remember { mutableStateOf(false) }
    var proportionsConfirmed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("draft_canvas_screen")
    ) {
        // Pattern Pieces Tab Row (Bodices, Sleeves, Trousers, Overview)
        ScrollableTabRow(
            selectedTabIndex = DraftPieceType.entries.indexOf(selectedType).coerceAtLeast(0),
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            DraftPieceType.entries.forEach { type ->
                Tab(
                    selected = selectedType == type,
                    onClick = { viewModel.selectPiece(type) },
                    text = {
                        Text(
                            text = type.shortName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_piece_${type.name}")
                )
            }
        }

        // CAD Overlay Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Outline Only / Shape Inspection Toggle
            FilterChip(
                selected = outlineOnlyMode,
                onClick = { outlineOnlyMode = !outlineOnlyMode },
                label = { Text(if (outlineOnlyMode) "Outline Only (Active)" else "Outline Mode", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Crop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("toggle_outline_mode_chip")
            )

            FilterChip(
                selected = showGrid,
                onClick = { viewModel.toggleGrid() },
                label = { Text("Grid", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        Icons.Default.GridOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("toggle_grid_chip")
            )

            FilterChip(
                selected = showDimensions,
                onClick = { viewModel.toggleDimensions() },
                label = { Text("Labels", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Straighten,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("toggle_dimensions_chip")
            )

            FilterChip(
                selected = showSeamAllowance,
                onClick = { viewModel.toggleSeamAllowance() },
                label = { Text("Seam (1cm)", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        Icons.Default.LinearScale,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("toggle_seam_chip")
            )

            FilterChip(
                selected = showNotches,
                onClick = { viewModel.toggleNotches() },
                label = { Text("Notches", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        if (showNotches) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("toggle_notches_chip")
            )

            FilterChip(
                selected = showConstructionLines,
                onClick = { viewModel.toggleConstructionLines() },
                label = { Text("Guidelines", style = MaterialTheme.typography.labelSmall) },
                leadingIcon = {
                    Icon(
                        if (showConstructionLines) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("toggle_guidelines_chip")
            )

            // Info button for Tailor Notes
            IconButton(
                onClick = { showNotesSheet = true },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("piece_notes_button")
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Piece Tailoring Notes",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Share / Export button
            IconButton(
                onClick = {
                    val specSheet = viewModel.generateCutterSpecificationSheet()
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Sartor Bespoke Spec Sheet", specSheet))
                    Toast.makeText(context, "Specification sheet copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("copy_specs_button")
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share Pattern Spec Sheet",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Shape & Proportion Confirmation Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            color = if (proportionsConfirmed) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            },
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (proportionsConfirmed) "Shape & Proportions Confirmed ✓" else "Confirm Shape & Proportions:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Rotate (⟲ / ⟳) and zoom to verify ${piece.name} outline proportions before drafting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                androidx.compose.material3.TextButton(
                    onClick = {
                        proportionsConfirmed = true
                        Toast.makeText(context, "${piece.name} outline shape confirmed!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("confirm_proportions_button")
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (proportionsConfirmed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (proportionsConfirmed) "Confirmed" else "Confirm",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // CAD Canvas
        Box(modifier = Modifier.weight(1f)) {
            DraftCanvas(
                piece = piece,
                unit = measurements.unit,
                showGrid = if (outlineOnlyMode) false else showGrid,
                showDimensions = if (outlineOnlyMode) false else showDimensions,
                showSeamAllowance = if (outlineOnlyMode) false else showSeamAllowance,
                showNotches = if (outlineOnlyMode) false else showNotches,
                showConstructionLines = if (outlineOnlyMode) false else showConstructionLines
            )
        }
    }

    // Modal Bottom Sheet for Piece Notes & Cutting Instructions
    if (showNotesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotesSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = piece.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Bound Dimensions: ${formatLength(piece.boundsWidthCm, measurements.unit)} × ${formatLength(piece.boundsHeightCm, measurements.unit)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "Master Tailor Cutting Notes:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))

                piece.tailoringNotes.forEachIndexed { i, note ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(18.dp)
                        )
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
