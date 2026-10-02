package com.example.data.model

enum class TargetGender(val label: String, val shortLabel: String) {
    MALE("Gentlemen / Male", "Male"),
    FEMALE("Ladies / Female", "Female")
}

data class BodyTypePreset(
    val id: String,
    val name: String,
    val gender: TargetGender,
    val bodyType: String,
    val subtitle: String,
    val description: String,
    val measurements: BodyMeasurements
)

object BodyTypePresetDirectory {

    // --- MALE BODY TYPE PRESETS ---

    val MALE_ATHLETIC = BodyTypePreset(
        id = "male_athletic",
        name = "Athletic (V-Shape)",
        gender = TargetGender.MALE,
        bodyType = "Athletic / Inverted Triangle",
        subtitle = "Broad shoulders & chest, tapered waist (Drop 9.5)",
        description = "Engineered for an athletic V-taper frame. Features generous shoulder and chest width with aggressive waist suppression and athletic thigh room.",
        measurements = BodyMeasurements(
            profileName = "Male Athletic V-Shape",
            notes = "Broad shoulders, high chest-to-waist drop 9.5",
            unit = MeasurementUnit.CM,
            chest = 106.0f,
            waist = 82.0f,
            hip = 99.0f,
            shoulderWidth = 48.5f,
            backLength = 44.5f,
            jacketLength = 77.0f,
            neckCircumference = 41.0f,
            sleeveLength = 65.5f,
            bicepCircumference = 38.0f,
            wristCircumference = 18.5f,
            inseam = 82.0f,
            outseam = 108.5f,
            thighCircumference = 62.0f,
            kneeCircumference = 45.0f,
            trouserBottomWidth = 21.0f
        )
    )

    val MALE_AVERAGE = BodyTypePreset(
        id = "male_average",
        name = "Average (Classic)",
        gender = TargetGender.MALE,
        bodyType = "Average / Rectangular",
        subtitle = "Standard balanced proportions (Drop 5.5)",
        description = "The quintessential Savile Row proportion baseline. Balanced chest-to-waist drop offering optimal comfort and timeless sartorial balance.",
        measurements = BodyMeasurements(
            profileName = "Male Average Classic",
            notes = "Standard UK 40 / EU 50 balanced proportions",
            unit = MeasurementUnit.CM,
            chest = 100.0f,
            waist = 86.0f,
            hip = 102.0f,
            shoulderWidth = 45.5f,
            backLength = 43.5f,
            jacketLength = 76.0f,
            neckCircumference = 39.5f,
            sleeveLength = 64.0f,
            bicepCircumference = 35.0f,
            wristCircumference = 18.0f,
            inseam = 80.5f,
            outseam = 107.0f,
            thighCircumference = 59.0f,
            kneeCircumference = 44.0f,
            trouserBottomWidth = 21.0f
        )
    )

    val MALE_PEAR = BodyTypePreset(
        id = "male_pear",
        name = "Pear-Shaped (Triangle)",
        gender = TargetGender.MALE,
        bodyType = "Pear-Shaped / Triangle",
        subtitle = "Narrower shoulders, broader hips & thighs",
        description = "Tailored for narrower shoulders and wider hips/seat. Structured chest canvas and shoulder extension create visual balance above a flared hip line.",
        measurements = BodyMeasurements(
            profileName = "Male Pear-Shaped",
            notes = "Narrower upper torso with wider hip and thigh circumference",
            unit = MeasurementUnit.CM,
            chest = 98.0f,
            waist = 90.0f,
            hip = 108.0f,
            shoulderWidth = 43.5f,
            backLength = 43.0f,
            jacketLength = 75.5f,
            neckCircumference = 39.0f,
            sleeveLength = 63.0f,
            bicepCircumference = 34.0f,
            wristCircumference = 17.5f,
            inseam = 79.0f,
            outseam = 105.0f,
            thighCircumference = 64.0f,
            kneeCircumference = 47.0f,
            trouserBottomWidth = 22.0f
        )
    )

    val MALE_APPLE = BodyTypePreset(
        id = "male_apple",
        name = "Apple-Shaped (Oval)",
        gender = TargetGender.MALE,
        bodyType = "Apple-Shaped / Oval",
        subtitle = "Fuller waist & abdomen, softer chest slope",
        description = "Designed for fuller midsections. Front hem has graceful sweep ease, soft waist suppression, and extended trouser rise for maximum sitting ease.",
        measurements = BodyMeasurements(
            profileName = "Male Apple-Shaped",
            notes = "Fuller waist and midsection with comfortable rise",
            unit = MeasurementUnit.CM,
            chest = 110.0f,
            waist = 108.0f,
            hip = 112.0f,
            shoulderWidth = 47.0f,
            backLength = 42.5f,
            jacketLength = 74.0f,
            neckCircumference = 42.5f,
            sleeveLength = 62.5f,
            bicepCircumference = 38.5f,
            wristCircumference = 19.0f,
            inseam = 77.0f,
            outseam = 103.0f,
            thighCircumference = 65.0f,
            kneeCircumference = 48.0f,
            trouserBottomWidth = 22.5f
        )
    )

    val MALE_SLIM = BodyTypePreset(
        id = "male_slim",
        name = "Slim (Lean)",
        gender = TargetGender.MALE,
        bodyType = "Slim / Ectomorph",
        subtitle = "Narrow chest & trim waist, Italian silhouette",
        description = "Sleek, close-to-body Italian cut. Minimal chest ease with tapered sleeves and slim trouser leg line.",
        measurements = BodyMeasurements(
            profileName = "Male Slim Cut",
            notes = "Lean silhouette with sharp tapered lines",
            unit = MeasurementUnit.CM,
            chest = 92.0f,
            waist = 76.0f,
            hip = 94.0f,
            shoulderWidth = 43.0f,
            backLength = 42.0f,
            jacketLength = 73.5f,
            neckCircumference = 37.5f,
            sleeveLength = 63.5f,
            bicepCircumference = 32.5f,
            wristCircumference = 17.0f,
            inseam = 81.0f,
            outseam = 106.0f,
            thighCircumference = 55.0f,
            kneeCircumference = 41.0f,
            trouserBottomWidth = 19.5f
        )
    )

    // --- FEMALE BODY TYPE PRESETS ---

    val FEMALE_HOURGLASS = BodyTypePreset(
        id = "female_hourglass",
        name = "Hourglass (Curved)",
        gender = TargetGender.FEMALE,
        bodyType = "Hourglass / Balanced Curve",
        subtitle = "Defined narrow waist with balanced bust & hips",
        description = "Balanced bust and hip proportions with dramatic waist definition. Sculpted front waist darts and curved side seams accentuate the natural waistline.",
        measurements = BodyMeasurements(
            profileName = "Female Hourglass",
            notes = "Defined waist with proportional bust and hip curves",
            unit = MeasurementUnit.CM,
            chest = 94.0f,
            waist = 70.0f,
            hip = 99.0f,
            shoulderWidth = 40.0f,
            backLength = 40.0f,
            jacketLength = 68.0f,
            neckCircumference = 35.0f,
            sleeveLength = 59.0f,
            bicepCircumference = 29.0f,
            wristCircumference = 15.5f,
            inseam = 76.5f,
            outseam = 101.5f,
            thighCircumference = 58.0f,
            kneeCircumference = 40.0f,
            trouserBottomWidth = 19.5f
        )
    )

    val FEMALE_PEAR = BodyTypePreset(
        id = "female_pear",
        name = "Pear-Shaped (Triangle)",
        gender = TargetGender.FEMALE,
        bodyType = "Pear-Shaped / Triangle",
        subtitle = "Narrower shoulders, fuller hips & thighs",
        description = "Hips significantly wider than bust line. Pattern features gentle shoulder pad reinforcement, flared jacket skirt, and roomy trouser seat curve.",
        measurements = BodyMeasurements(
            profileName = "Female Pear-Shaped",
            notes = "Fuller hips and thighs with neat shoulders",
            unit = MeasurementUnit.CM,
            chest = 89.0f,
            waist = 72.0f,
            hip = 104.0f,
            shoulderWidth = 38.0f,
            backLength = 39.5f,
            jacketLength = 67.0f,
            neckCircumference = 34.0f,
            sleeveLength = 58.5f,
            bicepCircumference = 28.5f,
            wristCircumference = 15.0f,
            inseam = 75.0f,
            outseam = 100.5f,
            thighCircumference = 61.0f,
            kneeCircumference = 42.0f,
            trouserBottomWidth = 20.5f
        )
    )

    val FEMALE_APPLE = BodyTypePreset(
        id = "female_apple",
        name = "Apple-Shaped (Round)",
        gender = TargetGender.FEMALE,
        bodyType = "Apple-Shaped / Round",
        subtitle = "Fuller bust & waist, slender legs & hips",
        description = "Fuller torso and upper body with slim legs. Features elongated lapel gorge, relaxed waist suppression, and slim-cut trousers.",
        measurements = BodyMeasurements(
            profileName = "Female Apple-Shaped",
            notes = "Fuller torso and bust with slender leg proportions",
            unit = MeasurementUnit.CM,
            chest = 102.0f,
            waist = 92.0f,
            hip = 98.0f,
            shoulderWidth = 41.0f,
            backLength = 40.0f,
            jacketLength = 69.0f,
            neckCircumference = 37.0f,
            sleeveLength = 58.0f,
            bicepCircumference = 32.0f,
            wristCircumference = 16.0f,
            inseam = 74.0f,
            outseam = 99.0f,
            thighCircumference = 56.0f,
            kneeCircumference = 39.0f,
            trouserBottomWidth = 19.0f
        )
    )

    val FEMALE_ATHLETIC = BodyTypePreset(
        id = "female_athletic",
        name = "Athletic (Inverted)",
        gender = TargetGender.FEMALE,
        bodyType = "Athletic / Inverted Triangle",
        subtitle = "Broad athletic shoulders, trim hips & waist",
        description = "Wider shoulder-to-hip ratio with muscular back and slender hips. Clean armhole scye scoop and straight tailored trouser fall.",
        measurements = BodyMeasurements(
            profileName = "Female Athletic",
            notes = "Muscular shoulder and back with athletic waist and hips",
            unit = MeasurementUnit.CM,
            chest = 96.0f,
            waist = 76.0f,
            hip = 93.0f,
            shoulderWidth = 42.0f,
            backLength = 41.0f,
            jacketLength = 69.5f,
            neckCircumference = 36.0f,
            sleeveLength = 60.5f,
            bicepCircumference = 31.0f,
            wristCircumference = 16.0f,
            inseam = 78.0f,
            outseam = 103.0f,
            thighCircumference = 56.0f,
            kneeCircumference = 39.0f,
            trouserBottomWidth = 19.5f
        )
    )

    val FEMALE_AVERAGE = BodyTypePreset(
        id = "female_average",
        name = "Average (Rectangle)",
        gender = TargetGender.FEMALE,
        bodyType = "Average / Column Silhouette",
        subtitle = "Uniform silhouette, balanced bust & hips",
        description = "Uniform, straight silhouette with subtle waist indentation. Tailored suit canvas creates crisp definition through the midsection.",
        measurements = BodyMeasurements(
            profileName = "Female Average Rectangle",
            notes = "Balanced column silhouette with clean lines",
            unit = MeasurementUnit.CM,
            chest = 91.0f,
            waist = 77.0f,
            hip = 94.0f,
            shoulderWidth = 39.5f,
            backLength = 40.0f,
            jacketLength = 68.0f,
            neckCircumference = 35.5f,
            sleeveLength = 59.0f,
            bicepCircumference = 28.5f,
            wristCircumference = 15.5f,
            inseam = 76.0f,
            outseam = 101.0f,
            thighCircumference = 55.0f,
            kneeCircumference = 39.0f,
            trouserBottomWidth = 19.5f
        )
    )

    val MALE_PRESETS = listOf(MALE_ATHLETIC, MALE_AVERAGE, MALE_PEAR, MALE_APPLE, MALE_SLIM)
    val FEMALE_PRESETS = listOf(FEMALE_HOURGLASS, FEMALE_PEAR, FEMALE_APPLE, FEMALE_ATHLETIC, FEMALE_AVERAGE)
    val ALL_PRESETS = MALE_PRESETS + FEMALE_PRESETS
}
