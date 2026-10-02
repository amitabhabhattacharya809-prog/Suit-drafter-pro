package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.MeasurementUnit
import com.example.data.model.cmToInches
import com.example.data.model.formatLength
import com.example.data.model.inchesToCm
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun MeasurementInputCard(
    key: String,
    title: String,
    valueInCm: Float,
    unit: MeasurementUnit,
    onValueChange: (Float) -> Unit,
    onShowGuide: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayValue = if (unit == MeasurementUnit.CM) valueInCm else cmToInches(valueInCm)
    val displayFormatted = String.format(Locale.US, "%.1f", displayValue)

    val stepInCurrentUnit = if (unit == MeasurementUnit.CM) 0.5f else 0.25f

    var textInput by remember(valueInCm, unit) { mutableStateOf(displayFormatted) }
    var isEditing by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("measurement_card_$key"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (unit == MeasurementUnit.CM) {
                            "≈ ${String.format(Locale.US, "%.1f", cmToInches(valueInCm))} in"
                        } else {
                            "≈ ${String.format(Locale.US, "%.1f", valueInCm)} cm"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                IconButton(
                    onClick = { onShowGuide(key) },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("guide_button_$key")
                ) {
                    Icon(
                        Icons.Default.HelpOutline,
                        contentDescription = "How to measure $title",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stepper Decrement
                FilledIconButton(
                    onClick = {
                        val nextVal = (displayValue - stepInCurrentUnit).coerceAtLeast(5.0f)
                        val nextCm = if (unit == MeasurementUnit.CM) nextVal else inchesToCm(nextVal)
                        onValueChange((nextCm * 10f).roundToInt() / 10f)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("stepper_decrement_$key"),
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease $title", modifier = Modifier.size(18.dp))
                }

                // Value Text Field
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { input ->
                        textInput = input
                        val parsed = input.toFloatOrNull()
                        if (parsed != null && parsed > 0f) {
                            val cm = if (unit == MeasurementUnit.CM) parsed else inchesToCm(parsed)
                            onValueChange(cm)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("input_field_$key"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = {
                        Text(
                            text = unit.symbol,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    shape = RoundedCornerShape(8.dp)
                )

                // Stepper Increment
                FilledIconButton(
                    onClick = {
                        val nextVal = (displayValue + stepInCurrentUnit).coerceAtMost(250.0f)
                        val nextCm = if (unit == MeasurementUnit.CM) nextVal else inchesToCm(nextVal)
                        onValueChange((nextCm * 10f).roundToInt() / 10f)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("stepper_increment_$key"),
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase $title", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
