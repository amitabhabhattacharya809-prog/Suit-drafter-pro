package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class MeasurementGuideInfo(
    val key: String,
    val title: String,
    val anatomicalLocation: String,
    val stepByStep: List<String>,
    val tailorTip: String
)

object TailorGuideDirectory {
    val guides = mapOf(
        "chest" to MeasurementGuideInfo(
            key = "chest",
            title = "Chest Circumference",
            anatomicalLocation = "Fullest circumference of the thorax",
            stepByStep = listOf(
                "Stand naturally with arms relaxed at your sides.",
                "Wrap the measuring tape horizontally around the fullest part of the chest.",
                "Ensure tape runs under the armpits and across the shoulder blades in back.",
                "Keep tape snug but not compressing soft tissue. Breathe normally."
            ),
            tailorTip = "Savile Row rule: Place two fingers flat under the tape for ideal breathing ease."
        ),
        "waist" to MeasurementGuideInfo(
            key = "waist",
            title = "Natural Waist Circumference",
            anatomicalLocation = "At the natural indentation above hip bones (approx. belly button level)",
            stepByStep = listOf(
                "Locate your natural waist by bending sideways; it is where the torso creases.",
                "Wrap the tape horizontally around this level.",
                "Do not pull your stomach in; measure in a normal relaxed posture."
            ),
            tailorTip = "Traditional suit trousers sit higher than modern jeans—at natural waist, not low hips."
        ),
        "hip" to MeasurementGuideInfo(
            key = "hip",
            title = "Hip / Seat Circumference",
            anatomicalLocation = "Over the maximum projection of the gluteus muscles",
            stepByStep = listOf(
                "Stand with feet together and weight evenly balanced.",
                "Pass the tape around the fullest part of the hips and buttocks.",
                "Ensure tape is parallel to the floor all the way around."
            ),
            tailorTip = "Empty all pockets before measuring to ensure a clean, accurate silhouette."
        ),
        "shoulderWidth" to MeasurementGuideInfo(
            key = "shoulderWidth",
            title = "Across Back Shoulder Width",
            anatomicalLocation = "From left acromion bone to right acromion bone",
            stepByStep = listOf(
                "Locate the bony point at the tip of each shoulder (acromion).",
                "Measure across the upper back following the gentle contour of the neck base.",
                "Keep the tape gently taut without pulling straight like a bowstring."
            ),
            tailorTip = "Crucial for jacket shoulder pitch and preventing divots at the sleeve head."
        ),
        "backLength" to MeasurementGuideInfo(
            key = "backLength",
            title = "Nape to Waist (Back Length)",
            anatomicalLocation = "From 7th cervical vertebra down to natural waistline",
            stepByStep = listOf(
                "Tilt head forward to identify the most prominent neck bone (C7 vertebra).",
                "Measure straight down the spine to the natural waist mark."
            ),
            tailorTip = "Controls the jacket waist suppression position and back balance."
        ),
        "jacketLength" to MeasurementGuideInfo(
            key = "jacketLength",
            title = "Total Jacket Length",
            anatomicalLocation = "From nape of neck down to desired jacket hem line",
            stepByStep = listOf(
                "Measure straight down from the C7 nape bone to the lower hem.",
                "Classic rule: The hem should align with the thumb knuckle when standing straight, or cover the seat."
            ),
            tailorTip = "A classic jacket splits the body into balanced halves from collar to floor."
        ),
        "sleeveLength" to MeasurementGuideInfo(
            key = "sleeveLength",
            title = "Sleeve Length",
            anatomicalLocation = "Shoulder point down to wrist bone",
            stepByStep = listOf(
                "Bend arm slightly at a 45-degree natural angle.",
                "Measure from the shoulder tip bone over the elbow down to the prominent wrist bone."
            ),
            tailorTip = "Leaves 1.2 cm (1/2 inch) of shirt cuff exposed below the jacket sleeve hem."
        ),
        "bicepCircumference" to MeasurementGuideInfo(
            key = "bicepCircumference",
            title = "Bicep Circumference",
            anatomicalLocation = "Widest part of upper arm",
            stepByStep = listOf(
                "Relax arm at side.",
                "Measure around the thickest part of the bicep without flexing."
            ),
            tailorTip = "Determines crown cap width and armhole scye ease."
        ),
        "wristCircumference" to MeasurementGuideInfo(
            key = "wristCircumference",
            title = "Wrist Circumference",
            anatomicalLocation = "Around wrist joint above hand",
            stepByStep = listOf(
                "Measure around the wrist joint over the wrist bone."
            ),
            tailorTip = "Allows sleeve cuff opening to glide smoothly over watch and shirt cuff."
        ),
        "neckCircumference" to MeasurementGuideInfo(
            key = "neckCircumference",
            title = "Neck Circumference",
            anatomicalLocation = "Base of the neck",
            stepByStep = listOf(
                "Wrap tape around the lower base of the neck, just above collarbones.",
                "Insert one finger between neck and tape."
            ),
            tailorTip = "Sets the jacket collar gorge and neck circle diameter."
        ),
        "inseam" to MeasurementGuideInfo(
            key = "inseam",
            title = "Trouser Inseam",
            anatomicalLocation = "From crotch fork down along inner leg to shoe top",
            stepByStep = listOf(
                "Stand straight with feet slightly apart wearing dress shoes.",
                "Measure from the highest point inside the crotch straight down to the shoe instep."
            ),
            tailorTip = "Dictates trouser break (no break, slight break, or full break)."
        ),
        "outseam" to MeasurementGuideInfo(
            key = "outseam",
            title = "Trouser Outseam",
            anatomicalLocation = "From natural waistband down to the bottom of trousers",
            stepByStep = listOf(
                "Measure along the side seam of the body from the waistband level down to the heel/shoe top."
            ),
            tailorTip = "Outseam minus Inseam gives the exact Crotch Depth (Rise)."
        ),
        "thighCircumference" to MeasurementGuideInfo(
            key = "thighCircumference",
            title = "Thigh Circumference",
            anatomicalLocation = "Widest part of upper thigh",
            stepByStep = listOf(
                "Measure around the fullest part of the upper thigh, 2-3 cm below crotch level."
            ),
            tailorTip = "Ensures sufficient ease when sitting down."
        ),
        "kneeCircumference" to MeasurementGuideInfo(
            key = "kneeCircumference",
            title = "Knee Circumference",
            anatomicalLocation = "Across center of kneecap",
            stepByStep = listOf(
                "Measure around the knee joint with leg straight."
            ),
            tailorTip = "Balances the trouser leg taper between crotch and cuff."
        ),
        "trouserBottomWidth" to MeasurementGuideInfo(
            key = "trouserBottomWidth",
            title = "Trouser Leg Opening (Flat)",
            anatomicalLocation = "Flat hem width at ankle",
            stepByStep = listOf(
                "Standard classic trouser bottom is 20-22 cm flat (40-44 cm circumference).",
                "Slim fit typically ranges from 18.5-19.5 cm flat."
            ),
            tailorTip = "Must balance proportionally with shoe size and jacket hem sweep."
        )
    )
}

@Composable
fun HowToMeasureDialog(
    guideKey: String,
    onDismiss: () -> Unit
) {
    val info = TailorGuideDirectory.guides[guideKey] ?: MeasurementGuideInfo(
        key = guideKey,
        title = "Measurement Guide",
        anatomicalLocation = "Standard tailoring placement",
        stepByStep = listOf("Use a flexible tailor's tape snug against the body."),
        tailorTip = "Always stand in a relaxed, natural posture."
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Straighten,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = info.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = info.anatomicalLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "How to Measure:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))

                info.stepByStep.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${index + 1}.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(22.dp)
                        )
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Savile Row Master Tip",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = info.tailorTip,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_guide_dialog_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Got It")
            }
        }
    )
}
