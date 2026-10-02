package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.MeasurementUnit
import com.example.data.model.StandardPresets
import com.example.ui.components.HowToMeasureDialog
import com.example.ui.components.MeasurementInputCard
import com.example.ui.components.PresetSelectorSheet
import com.example.ui.viewmodel.SuitDraftViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementsScreen(
    viewModel: SuitDraftViewModel,
    onNavigateToCanvas: () -> Unit,
    modifier: Modifier = Modifier
) {
    val measurements by viewModel.currentMeasurements.collectAsStateWithLifecycle()
    var activeGuideKey by remember { mutableStateOf<String?>(null) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var showPresetSheet by remember { mutableStateOf(false) }

    val categories = listOf("Upper Body", "Arms & Sleeves", "Trousers / Slacks")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("measurements_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Banner with Bespoke Illustration
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_suit_drafting_1790924854289),
                    contentDescription = "Sartor Bespoke Drafting Studio",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "BESPOKE CAD",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Anatomy & Measurement Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Input client body dimensions for millimeter-precise pattern drafting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Unit Switcher & Quick Save
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Measurement Units",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Segmented Button for CM / INCHES
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.width(180.dp)) {
                            SegmentedButton(
                                selected = measurements.unit == MeasurementUnit.CM,
                                onClick = { viewModel.setUnit(MeasurementUnit.CM) },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                modifier = Modifier.testTag("unit_toggle_cm")
                            ) {
                                Text("CM")
                            }
                            SegmentedButton(
                                selected = measurements.unit == MeasurementUnit.INCHES,
                                onClick = { viewModel.setUnit(MeasurementUnit.INCHES) },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                modifier = Modifier.testTag("unit_toggle_inches")
                            ) {
                                Text("INCHES")
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Client Name input
                    OutlinedTextField(
                        value = measurements.profileName,
                        onValueChange = { viewModel.updateProfileName(it) },
                        label = { Text("Client / Pattern Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_client_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Body Type Presets (Male & Female):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Text(
                            text = "Browse All Types",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { showPresetSheet = true }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("browse_all_presets_button")
                        )
                    }

                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        com.example.data.model.BodyTypePresetDirectory.ALL_PRESETS.forEach { preset ->
                            val isSelected = measurements.profileName == preset.measurements.profileName
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.applyPreset(preset.measurements) },
                                label = {
                                    Text(
                                        "${preset.gender.shortLabel}: ${preset.name}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        if (preset.gender == com.example.data.model.TargetGender.MALE) Icons.Default.Male else Icons.Default.Female,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                modifier = Modifier.testTag("preset_chip_${preset.id}")
                            )
                        }
                    }
                }
            }
        }

        // Category Tabs (Upper Body / Arms / Trousers)
        item {
            PrimaryTabRow(
                selectedTabIndex = selectedCategoryIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                categories.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("category_tab_$index")
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Category Content Items
        when (selectedCategoryIndex) {
            0 -> {
                // Upper Body
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MeasurementInputCard(
                            key = "chest",
                            title = "Chest Circumference",
                            valueInCm = measurements.chest,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("chest", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "waist",
                            title = "Natural Waist Circumference",
                            valueInCm = measurements.waist,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("waist", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "hip",
                            title = "Seat / Hip Circumference",
                            valueInCm = measurements.hip,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("hip", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "shoulderWidth",
                            title = "Shoulder Width (Across Back)",
                            valueInCm = measurements.shoulderWidth,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("shoulderWidth", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "backLength",
                            title = "Nape to Natural Waist (Back Length)",
                            valueInCm = measurements.backLength,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("backLength", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "jacketLength",
                            title = "Total Jacket Length",
                            valueInCm = measurements.jacketLength,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("jacketLength", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "neckCircumference",
                            title = "Neck Circumference",
                            valueInCm = measurements.neckCircumference,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("neckCircumference", it) },
                            onShowGuide = { activeGuideKey = it }
                        )
                    }
                }
            }
            1 -> {
                // Arms & Sleeves
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MeasurementInputCard(
                            key = "sleeveLength",
                            title = "Sleeve Length (Shoulder to Wrist)",
                            valueInCm = measurements.sleeveLength,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("sleeveLength", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "bicepCircumference",
                            title = "Bicep Circumference",
                            valueInCm = measurements.bicepCircumference,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("bicepCircumference", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "wristCircumference",
                            title = "Wrist Circumference",
                            valueInCm = measurements.wristCircumference,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("wristCircumference", it) },
                            onShowGuide = { activeGuideKey = it }
                        )
                    }
                }
            }
            2 -> {
                // Trousers / Slacks
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MeasurementInputCard(
                            key = "inseam",
                            title = "Trouser Inseam Length",
                            valueInCm = measurements.inseam,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("inseam", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "outseam",
                            title = "Trouser Outseam (Waist to Hem)",
                            valueInCm = measurements.outseam,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("outseam", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "thighCircumference",
                            title = "Thigh Circumference",
                            valueInCm = measurements.thighCircumference,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("thighCircumference", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "kneeCircumference",
                            title = "Knee Circumference",
                            valueInCm = measurements.kneeCircumference,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("kneeCircumference", it) },
                            onShowGuide = { activeGuideKey = it }
                        )

                        MeasurementInputCard(
                            key = "trouserBottomWidth",
                            title = "Trouser Leg Opening (Flat Width)",
                            valueInCm = measurements.trouserBottomWidth,
                            unit = measurements.unit,
                            onValueChange = { viewModel.updateMeasurementValue("trouserBottomWidth", it) },
                            onShowGuide = { activeGuideKey = it }
                        )
                    }
                }
            }
        }

        // Action Buttons at bottom
        item {
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.saveCurrentProfile() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("save_profile_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save Client")
                }

                Button(
                    onClick = onNavigateToCanvas,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(50.dp)
                        .testTag("view_pattern_draft_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Straighten, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Draft Pattern CAD")
                }
            }
        }
    }

    // Active Anatomical Guide Dialog
    activeGuideKey?.let { key ->
        HowToMeasureDialog(
            guideKey = key,
            onDismiss = { activeGuideKey = null }
        )
    }

    if (showPresetSheet) {
        PresetSelectorSheet(
            currentPresetName = measurements.profileName,
            unit = measurements.unit,
            onSelectPreset = { preset ->
                viewModel.applyPreset(preset.measurements)
            },
            onDismiss = { showPresetSheet = false }
        )
    }
}
