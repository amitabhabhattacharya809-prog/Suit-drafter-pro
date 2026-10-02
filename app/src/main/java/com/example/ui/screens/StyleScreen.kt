package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FitCut
import com.example.data.model.JacketButtonStyle
import com.example.data.model.LapelStyle
import com.example.data.model.PocketStyle
import com.example.data.model.TrouserHem
import com.example.data.model.TrouserPleats
import com.example.data.model.VentStyle
import com.example.ui.components.AssemblyGuideList
import com.example.ui.components.FabricEstimatorCard
import com.example.ui.viewmodel.SuitDraftViewModel
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun StyleScreen(
    viewModel: SuitDraftViewModel,
    onNavigateToCanvas: () -> Unit,
    modifier: Modifier = Modifier
) {
    val style by viewModel.currentStyle.collectAsStateWithLifecycle()
    val draftResult by viewModel.draftResult.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("style_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fit Silhouette Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fit_cut_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = "Silhouette & Ease Allowance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FitCut.entries.forEach { cut ->
                            FilterChip(
                                selected = style.fitCut == cut,
                                onClick = { viewModel.setFitCut(cut) },
                                label = { Text(cut.displayName, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("fit_chip_${cut.name}")
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Chest Ease: +${style.fitCut.chestEaseCm} cm | Waist Ease: +${style.fitCut.waistEaseCm} cm",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = style.fitCut.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Jacket Architecture
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("jacket_architecture_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Jacket Architecture",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(12.dp))

                    // Button Styling
                    Text(
                        text = "Button Fastening:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        JacketButtonStyle.entries.forEach { btnStyle ->
                            FilterChip(
                                selected = style.buttonStyle == btnStyle,
                                onClick = { viewModel.setButtonStyle(btnStyle) },
                                label = { Text(btnStyle.displayName) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("button_chip_${btnStyle.name}")
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(Modifier.height(12.dp))

                    // Lapel Style & Width
                    Text(
                        text = "Lapel Style:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LapelStyle.entries.forEach { lapel ->
                            FilterChip(
                                selected = style.lapelStyle == lapel,
                                onClick = { viewModel.setLapelStyle(lapel) },
                                label = { Text(lapel.displayName, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("lapel_chip_${lapel.name}")
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Lapel Width Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lapel Width:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f cm", style.lapelWidthCm),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = style.lapelWidthCm,
                        onValueChange = { viewModel.setLapelWidth((it * 10f).roundToInt() / 10f) },
                        valueRange = 6.0f..12.0f,
                        steps = 11,
                        modifier = Modifier.testTag("lapel_width_slider")
                    )

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(Modifier.height(12.dp))

                    // Vents
                    Text(
                        text = "Vent Style:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        VentStyle.entries.forEach { vent ->
                            FilterChip(
                                selected = style.ventStyle == vent,
                                onClick = { viewModel.setVentStyle(vent) },
                                label = { Text(vent.displayName) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("vent_chip_${vent.name}")
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(Modifier.height(12.dp))

                    // Pockets
                    Text(
                        text = "Hip Pockets:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        PocketStyle.entries.forEach { pocket ->
                            FilterChip(
                                selected = style.pocketStyle == pocket,
                                onClick = { viewModel.setPocketStyle(pocket) },
                                label = { Text(pocket.displayName) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pocket_chip_${pocket.name}")
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Ticket Pocket Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Ticket Pocket",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Extra smaller pocket above right hip flap",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Switch(
                            checked = style.hasTicketPocket,
                            onCheckedChange = { viewModel.toggleTicketPocket() },
                            modifier = Modifier.testTag("ticket_pocket_switch")
                        )
                    }
                }
            }
        }

        // Trouser Details Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trouser_styling_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Trouser Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Waist Pleats:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TrouserPleats.entries.forEach { pleat ->
                            FilterChip(
                                selected = style.trouserPleats == pleat,
                                onClick = { viewModel.setTrouserPleats(pleat) },
                                label = { Text(pleat.displayName, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pleat_chip_${pleat.name}")
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Hem Finish:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TrouserHem.entries.forEach { hem ->
                            FilterChip(
                                selected = style.trouserHem == hem,
                                onClick = { viewModel.setTrouserHem(hem) },
                                label = { Text(hem.displayName, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hem_chip_${hem.name}")
                            )
                        }
                    }
                }
            }
        }

        // Fabric Bolt & Meterage Estimator
        item {
            FabricEstimatorCard(draftResult = draftResult)
        }

        // Master Tailor Assembly Guide
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("assembly_guide_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Savile Row Assembly Sequence",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Master tailoring construction order for custom pattern draft",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(12.dp))
                    AssemblyGuideList(steps = draftResult.assemblySteps)
                }
            }
        }

        // View Pattern CAD CTA
        item {
            Button(
                onClick = onNavigateToCanvas,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("cta_draft_canvas_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Straighten, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Render Vector Draft in CAD Canvas")
            }
        }
    }
}
