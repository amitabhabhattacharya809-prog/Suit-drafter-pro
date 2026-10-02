package com.example.data.model

import java.util.Locale

enum class MeasurementUnit(val symbol: String, val label: String) {
    CM("cm", "Centimeters (Metric)"),
    INCHES("in", "Inches (Imperial)")
}

fun cmToInches(cm: Float): Float = cm / 2.54f
fun inchesToCm(inches: Float): Float = inches * 2.54f

fun formatLength(cm: Float, unit: MeasurementUnit): String {
    return when (unit) {
        MeasurementUnit.CM -> String.format(Locale.US, "%.1f cm", cm)
        MeasurementUnit.INCHES -> String.format(Locale.US, "%.1f in", cmToInches(cm))
    }
}

enum class FitCut(val displayName: String, val chestEaseCm: Float, val waistEaseCm: Float, val description: String) {
    SLIM("Slim Fit", 7.0f, 5.0f, "Tapered silhouette close to the torso with minimal excess drape"),
    CLASSIC("Classic / Regular", 11.0f, 8.0f, "Traditional Savile Row drape with balanced mobility & comfort"),
    RELAXED("Relaxed / Drape", 15.0f, 12.0f, "Fuller chest and waist ease reminiscent of 1930s drape cut")
}

enum class JacketButtonStyle(val displayName: String, val buttonCount: Int, val isDoubleBreasted: Boolean) {
    SINGLE_BREASTED_1("Single Breasted (1-Button)", 1, false),
    SINGLE_BREASTED_2("Single Breasted (2-Button)", 2, false),
    SINGLE_BREASTED_3("Single Breasted (3-Button)", 3, false),
    DOUBLE_BREASTED_4X2("Double Breasted (4x2)", 4, true),
    DOUBLE_BREASTED_6X2("Double Breasted (6x2)", 6, true)
}

enum class LapelStyle(val displayName: String, val description: String) {
    NOTCH("Notch Lapel", "Standard sartorial notch between lapel and collar"),
    PEAK("Peak Lapel", "Pointed lapel pointing upward toward the shoulder"),
    SHAWL("Shawl Collar", "Continuous rounded lapel curve without gorge seam")
}

enum class VentStyle(val displayName: String) {
    DOUBLE_SIDE_VENTS("Double Side Vents (British)"),
    CENTER_VENT("Center Single Vent (American)"),
    VENTLESS("Ventless (Continental / Formal)")
}

enum class PocketStyle(val displayName: String) {
    FLAP("Standard Flap Pockets"),
    SLANTED_FLAP("Slanted Flap (Hacking)"),
    JETTED("Jetted Pockets (Formal)"),
    PATCH("Patch Pockets (Casual / Sport)")
}

enum class TrouserPleats(val displayName: String) {
    FLAT_FRONT("Flat Front"),
    SINGLE_PLEAT("Single Forward Pleat"),
    DOUBLE_PLEAT("Double Pleats")
}

enum class TrouserHem(val displayName: String, val allowanceCm: Float) {
    PLAIN("Plain Hem", 4.0f),
    CUFFED_4CM("Turn-up Cuffs (4 cm / 1.5 in)", 8.5f)
}

data class BodyMeasurements(
    val id: Long = 0,
    val profileName: String = "Client Bespoke Draft",
    val notes: String = "",
    val unit: MeasurementUnit = MeasurementUnit.CM,
    // Upper body (stored in cm)
    val chest: Float = 102.0f,
    val waist: Float = 86.0f,
    val hip: Float = 104.0f,
    val shoulderWidth: Float = 46.0f,
    val backLength: Float = 43.5f,
    val jacketLength: Float = 76.0f,
    val neckCircumference: Float = 40.0f,
    // Arms
    val sleeveLength: Float = 64.0f,
    val bicepCircumference: Float = 35.0f,
    val wristCircumference: Float = 18.0f,
    // Lower body (Trousers)
    val inseam: Float = 81.0f,
    val outseam: Float = 107.0f,
    val thighCircumference: Float = 60.0f,
    val kneeCircumference: Float = 45.0f,
    val trouserBottomWidth: Float = 21.0f // flat width
)

data class GarmentStyle(
    val fitCut: FitCut = FitCut.CLASSIC,
    val buttonStyle: JacketButtonStyle = JacketButtonStyle.SINGLE_BREASTED_2,
    val lapelStyle: LapelStyle = LapelStyle.NOTCH,
    val lapelWidthCm: Float = 8.5f,
    val ventStyle: VentStyle = VentStyle.DOUBLE_SIDE_VENTS,
    val pocketStyle: PocketStyle = PocketStyle.FLAP,
    val hasTicketPocket: Boolean = false,
    val trouserPleats: TrouserPleats = TrouserPleats.FLAT_FRONT,
    val trouserHem: TrouserHem = TrouserHem.PLAIN,
    val seamAllowanceCm: Float = 1.0f,
    val hemAllowanceCm: Float = 4.0f
)

data class MeasurementCategory(
    val key: String,
    val title: String,
    val subtitle: String,
    val guideTips: String
)

object StandardPresets {
    val CLASSIC_40R = BodyMeasurements(
        profileName = "Savile Row Classic (40R)",
        notes = "Standard UK 40 Regular / EU 50 balanced proportions",
        unit = MeasurementUnit.CM,
        chest = 102.0f,
        waist = 86.0f,
        hip = 104.0f,
        shoulderWidth = 46.0f,
        backLength = 43.5f,
        jacketLength = 76.0f,
        neckCircumference = 40.0f,
        sleeveLength = 64.0f,
        bicepCircumference = 35.0f,
        wristCircumference = 18.0f,
        inseam = 81.0f,
        outseam = 107.0f,
        thighCircumference = 60.0f,
        kneeCircumference = 45.0f,
        trouserBottomWidth = 21.0f
    )

    val SLIM_38R = BodyMeasurements(
        profileName = "Milanese Slim Cut (38R)",
        notes = "Tapered Italian silhouette UK 38 / EU 48",
        unit = MeasurementUnit.CM,
        chest = 96.0f,
        waist = 80.0f,
        hip = 98.0f,
        shoulderWidth = 44.0f,
        backLength = 42.5f,
        jacketLength = 74.0f,
        neckCircumference = 38.5f,
        sleeveLength = 63.0f,
        bicepCircumference = 33.0f,
        wristCircumference = 17.0f,
        inseam = 80.0f,
        outseam = 105.0f,
        thighCircumference = 57.0f,
        kneeCircumference = 42.0f,
        trouserBottomWidth = 19.5f
    )

    val ATHLETIC_42R = BodyMeasurements(
        profileName = "Athletic V-Taper (42R)",
        notes = "Broad shoulders, high chest-to-waist drop 8",
        unit = MeasurementUnit.CM,
        chest = 108.0f,
        waist = 88.0f,
        hip = 106.0f,
        shoulderWidth = 48.5f,
        backLength = 44.5f,
        jacketLength = 77.5f,
        neckCircumference = 42.0f,
        sleeveLength = 65.5f,
        bicepCircumference = 38.0f,
        wristCircumference = 18.5f,
        inseam = 82.5f,
        outseam = 109.0f,
        thighCircumference = 64.0f,
        kneeCircumference = 47.0f,
        trouserBottomWidth = 21.5f
    )

    val PORTLY_44S = BodyMeasurements(
        profileName = "Executive Comfort (44S)",
        notes = "Fuller waist allowance with shorter jacket length",
        unit = MeasurementUnit.CM,
        chest = 112.0f,
        waist = 104.0f,
        hip = 114.0f,
        shoulderWidth = 48.0f,
        backLength = 42.0f,
        jacketLength = 73.0f,
        neckCircumference = 43.0f,
        sleeveLength = 61.5f,
        bicepCircumference = 39.0f,
        wristCircumference = 19.0f,
        inseam = 76.0f,
        outseam = 102.0f,
        thighCircumference = 65.0f,
        kneeCircumference = 48.0f,
        trouserBottomWidth = 22.5f
    )

    val ALL_PRESETS = listOf(CLASSIC_40R, SLIM_38R, ATHLETIC_42R, PORTLY_44S)
}
