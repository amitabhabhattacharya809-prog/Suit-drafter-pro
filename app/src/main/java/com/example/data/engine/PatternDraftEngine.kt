package com.example.data.engine

import com.example.data.model.AssemblyStep
import com.example.data.model.BodyMeasurements
import com.example.data.model.ConstructionLine
import com.example.data.model.DraftPiece
import com.example.data.model.DraftPieceType
import com.example.data.model.DraftPoint
import com.example.data.model.GarmentStyle
import com.example.data.model.JacketButtonStyle
import com.example.data.model.LapelStyle
import com.example.data.model.NotchMarker
import com.example.data.model.PathCommand
import com.example.data.model.SuitDraftResult
import com.example.data.model.TrouserHem
import com.example.data.model.TrouserPleats
import com.example.data.model.VentStyle
import java.util.Locale
import kotlin.math.roundToInt

object PatternDraftEngine {

    fun generateDraft(
        measurements: BodyMeasurements,
        style: GarmentStyle
    ): SuitDraftResult {
        val chest = measurements.chest
        val waist = measurements.waist
        val hip = measurements.hip
        val shoulderWidth = measurements.shoulderWidth
        val backLength = measurements.backLength
        val jacketLength = measurements.jacketLength
        val sleeveLength = measurements.sleeveLength
        val inseam = measurements.inseam
        val outseam = measurements.outseam

        // Proportion calculations (Savile Row & metric system)
        val chestEase = style.fitCut.chestEaseCm
        val waistEase = style.fitCut.waistEaseCm
        val chestFinished = chest + chestEase
        val waistFinished = waist + waistEase
        val hipFinished = hip + chestEase * 0.7f

        // Scye Depth (armhole depth from nape)
        val scyeDepth = (chest / 4.0f) - 1.5f + 1.0f // e.g. 24.5 cm for 102cm chest
        val halfBackWidth = (chest / 6.0f) + 4.5f + 0.8f // e.g. 22.3 cm
        val neckWidth = (chest / 12.0f) + 0.7f // e.g. 9.2 cm

        // Generate draft pieces
        val jacketBack = draftJacketBack(
            measurements = measurements,
            style = style,
            scyeDepth = scyeDepth,
            halfBackWidth = halfBackWidth,
            neckWidth = neckWidth,
            chestFinished = chestFinished,
            waistFinished = waistFinished,
            hipFinished = hipFinished
        )

        val jacketFront = draftJacketFront(
            measurements = measurements,
            style = style,
            scyeDepth = scyeDepth,
            neckWidth = neckWidth,
            chestFinished = chestFinished,
            waistFinished = waistFinished,
            hipFinished = hipFinished
        )

        val sleeveTop = draftSleeveTop(
            measurements = measurements,
            scyeDepth = scyeDepth,
            chest = chest
        )

        val sleeveUnder = draftSleeveUnder(
            measurements = measurements,
            scyeDepth = scyeDepth,
            chest = chest
        )

        val trouserFront = draftTrouserFront(
            measurements = measurements,
            style = style
        )

        val trouserBack = draftTrouserBack(
            measurements = measurements,
            style = style
        )

        val allPiecesOverview = draftAllPiecesOverview(
            jacketFront = jacketFront,
            jacketBack = jacketBack,
            sleeveTop = sleeveTop,
            sleeveUnder = sleeveUnder,
            trouserFront = trouserFront,
            trouserBack = trouserBack
        )

        val fullMarker = draftFullFabricMarker(
            jacketLength = jacketLength,
            sleeveLength = sleeveLength,
            trouserOutseam = outseam,
            chestFinished = chestFinished,
            style = style
        )

        val piecesMap = mapOf(
            DraftPieceType.JACKET_FRONT to jacketFront,
            DraftPieceType.JACKET_BACK to jacketBack,
            DraftPieceType.SLEEVE_TOP to sleeveTop,
            DraftPieceType.SLEEVE_UNDER to sleeveUnder,
            DraftPieceType.TROUSER_FRONT to trouserFront,
            DraftPieceType.TROUSER_BACK to trouserBack,
            DraftPieceType.ALL_PIECES_OVERVIEW to allPiecesOverview,
            DraftPieceType.FULL_LAYOUT to fullMarker
        )

        // Fabric estimates (150cm cloth bolt width)
        val fabricBoltWidth = 150.0f
        // Tailoring formula for 2-piece suit: 2x jacket length + 1x trouser length + pocketing & collar allowance
        val rawClothCm = (jacketLength * 1.8f) + (outseam * 1.15f) + 40.0f
        val fabricMeters = (rawClothCm / 100.0f * 10.0f).roundToInt() / 10.0f
        val liningMeters = ((jacketLength * 1.6f + 25.0f) / 100.0f * 10.0f).roundToInt() / 10.0f
        val interliningCanvasMeters = ((jacketLength + 20.0f) / 100.0f * 10.0f).roundToInt() / 10.0f

        val specifications = listOf(
            "Natural Chest" to String.format(Locale.US, "%.1f cm", chest),
            "Chest Ease (${style.fitCut.displayName})" to String.format(Locale.US, "+%.1f cm", chestEase),
            "Finished Garment Chest" to String.format(Locale.US, "%.1f cm", chestFinished),
            "Finished Garment Waist" to String.format(Locale.US, "%.1f cm", waistFinished),
            "Calculated Scye Depth" to String.format(Locale.US, "%.1f cm", scyeDepth),
            "Half Back Width" to String.format(Locale.US, "%.1f cm", halfBackWidth),
            "Front Overlap" to if (style.buttonStyle.isDoubleBreasted) "8.5 cm (Double Breasted)" else "2.5 cm (Single Breasted)",
            "Lapel Type & Width" to "${style.lapelStyle.displayName} (${style.lapelWidthCm} cm)",
            "Vent Architecture" to style.ventStyle.displayName,
            "Trouser Crotch Depth (Rise)" to String.format(Locale.US, "%.1f cm", outseam - inseam),
            "Standard Seam Allowance" to "${style.seamAllowanceCm} cm (4.0 cm hem)"
        )

        val assemblySteps = buildAssemblySteps(style)

        return SuitDraftResult(
            pieces = piecesMap,
            fabricBoltWidthCm = fabricBoltWidth,
            totalFabricLengthMeters = fabricMeters,
            totalLiningMeters = liningMeters,
            totalInterliningMeters = interliningCanvasMeters,
            scyeDepthCm = scyeDepth,
            chestFinishedCm = chestFinished,
            waistFinishedCm = waistFinished,
            halfBackWidthCm = halfBackWidth,
            specifications = specifications,
            assemblySteps = assemblySteps
        )
    }

    private fun draftJacketBack(
        measurements: BodyMeasurements,
        style: GarmentStyle,
        scyeDepth: Float,
        halfBackWidth: Float,
        neckWidth: Float,
        chestFinished: Float,
        waistFinished: Float,
        hipFinished: Float
    ): DraftPiece {
        val napeY = 0f
        val scyeY = scyeDepth
        val waistY = measurements.backLength
        val hipY = waistY + 19.0f
        val hemY = measurements.jacketLength

        val centerBackX = 0f
        val neckPtX = neckWidth
        val neckPtY = -2.2f

        val shoulderSlopeY = 4.2f
        val shoulderPtX = (measurements.shoulderWidth / 2.0f) + 0.8f
        val shoulderPtY = shoulderSlopeY

        val backPitchY = scyeY - 6.5f
        val backArmholeX = halfBackWidth

        // 1/4 chest width allocated to back piece
        val backChestWidth = (chestFinished / 4.0f) - 1.0f
        val sideScyeX = backChestWidth
        val sideScyeY = scyeY + 1.2f

        // Waist suppression dynamically adapted to body drop (athletic, average, pear, apple)
        val drop = chestFinished - waistFinished
        val cbWaistIndent = (drop / 12.0f).coerceIn(0.5f, 2.2f)
        val sideWaistIndent = (drop / 6.0f).coerceIn(-1.5f, 4.5f)
        val backWaistX = backChestWidth - sideWaistIndent

        // Hip & hem sweep
        val sideHipX = (hipFinished / 4.0f) - 0.5f
        val sideHemX = sideHipX + 0.8f

        // Vent overlap
        val ventTopY = hemY - 24.0f
        val ventOverlapWidth = 4.2f

        // Build main outline commands
        val outline = mutableListOf<PathCommand>()
        // Start at Center Back Nape
        outline.add(PathCommand.MoveTo(centerBackX, napeY))
        // Back neck curve
        outline.add(PathCommand.QuadTo(neckPtX * 0.45f, napeY - 0.5f, neckPtX, neckPtY))
        // Shoulder line
        outline.add(PathCommand.LineTo(shoulderPtX, shoulderPtY))
        // Armhole scye curve
        outline.add(PathCommand.CubicTo(
            shoulderPtX + 0.5f, shoulderPtY + 4.5f,
            backArmholeX, backPitchY,
            backArmholeX, scyeY - 2.0f
        ))
        outline.add(PathCommand.QuadTo(
            backArmholeX + 1.5f, sideScyeY + 0.2f,
            sideScyeX, sideScyeY
        ))
        // Side seam down to waist, hip, and hem
        outline.add(PathCommand.CubicTo(
            sideScyeX - 0.6f, waistY * 0.65f,
            backWaistX + 0.2f, waistY - 2.0f,
            backWaistX, waistY
        ))
        outline.add(PathCommand.CubicTo(
            backWaistX + 0.3f, waistY + 6.0f,
            sideHipX - 0.2f, hipY - 2.0f,
            sideHipX, hipY
        ))
        outline.add(PathCommand.LineTo(sideHemX, hemY))
        // Bottom hem sweep to Center Back
        outline.add(PathCommand.LineTo(centerBackX, hemY))

        // Center Back line with suppression and vent
        if (style.ventStyle == VentStyle.CENTER_VENT) {
            outline.add(PathCommand.LineTo(centerBackX, ventTopY))
            outline.add(PathCommand.LineTo(centerBackX - ventOverlapWidth, ventTopY))
            outline.add(PathCommand.LineTo(centerBackX - ventOverlapWidth, hemY))
            outline.add(PathCommand.LineTo(centerBackX, hemY))
        }

        // Trace CB upward
        outline.add(PathCommand.LineTo(centerBackX + cbWaistIndent, waistY))
        outline.add(PathCommand.QuadTo(centerBackX + 0.4f, scyeY, centerBackX, napeY))
        outline.add(PathCommand.Close)

        // Seam allowance offset outline (1.0cm around, 4cm hem)
        val seamOutline = outline.map { cmd ->
            when (cmd) {
                is PathCommand.MoveTo -> PathCommand.MoveTo(cmd.x - 1.0f, cmd.y - 1.0f)
                is PathCommand.LineTo -> PathCommand.LineTo(cmd.x - 1.0f, cmd.y - 1.0f)
                is PathCommand.QuadTo -> PathCommand.QuadTo(cmd.cx - 1.0f, cmd.cy - 1.0f, cmd.x - 1.0f, cmd.y - 1.0f)
                is PathCommand.CubicTo -> PathCommand.CubicTo(cmd.c1x - 1.0f, cmd.c1y - 1.0f, cmd.c2x - 1.0f, cmd.c2y - 1.0f, cmd.x - 1.0f, cmd.y - 1.0f)
                PathCommand.Close -> PathCommand.Close
            }
        }

        val constructionLines = listOf(
            ConstructionLine(0f, scyeY, sideScyeX, scyeY, "Scye Depth Line", scyeDepth),
            ConstructionLine(halfBackWidth, 0f, halfBackWidth, scyeY, "Back Width Line", halfBackWidth),
            ConstructionLine(0f, waistY, backWaistX, waistY, "Natural Waist Line", backWaistX),
            ConstructionLine(0f, hipY, sideHipX, hipY, "Seat / Hip Line", sideHipX),
            ConstructionLine(0f, hemY, sideHemX, hemY, "Jacket Hem Line", sideHemX)
        )

        val notches = listOf(
            NotchMarker(backArmholeX, backPitchY, 180f, "Back Scye Pitch Notch"),
            NotchMarker(backWaistX, waistY, 0f, "Waist Balance Notch"),
            NotchMarker(sideHipX, hipY, 0f, "Hip Balance Notch")
        )

        val keyPoints = listOf(
            DraftPoint(0f, napeY, "Nape (CB)", true),
            DraftPoint(neckPtX, neckPtY, "Back Neck", true),
            DraftPoint(shoulderPtX, shoulderPtY, "Back Shoulder Pt", true),
            DraftPoint(sideScyeX, sideScyeY, "Back Underarm", true),
            DraftPoint(backWaistX, waistY, "Back Waist", true),
            DraftPoint(sideHemX, hemY, "Back Hem Sweep", true)
        )

        return DraftPiece(
            type = DraftPieceType.JACKET_BACK,
            name = "Jacket Back Canvas & Cloth",
            outline = outline,
            seamAllowanceOutline = seamOutline,
            grainlineStart = DraftPoint(sideScyeX * 0.45f, scyeY - 4.0f),
            grainlineEnd = DraftPoint(sideScyeX * 0.45f, hemY - 6.0f),
            constructionLines = constructionLines,
            notches = notches,
            keyPoints = keyPoints,
            boundsWidthCm = sideHemX + ventOverlapWidth + 4.0f,
            boundsHeightCm = hemY + 6.0f,
            tailoringNotes = listOf(
                "Cut 2 cloth pieces with mirror orientation and 1 back lining.",
                "Ease back shoulder 1.2 cm into front shoulder seam using iron shrinking.",
                "Pad-stitch vent stay if double side vents or center vent are selected.",
                "4.0 cm turn-up allowance included along lower jacket hem."
            )
        )
    }

    private fun draftJacketFront(
        measurements: BodyMeasurements,
        style: GarmentStyle,
        scyeDepth: Float,
        neckWidth: Float,
        chestFinished: Float,
        waistFinished: Float,
        hipFinished: Float
    ): DraftPiece {
        val napeRefY = 0f
        val scyeY = scyeDepth
        val waistY = measurements.backLength
        val hipY = waistY + 19.0f
        val hemY = measurements.jacketLength + 1.5f // Slight front balance drop

        // Front Center Line is at x = 0
        val frontOverlap = if (style.buttonStyle.isDoubleBreasted) 8.5f else 2.5f

        // Front Neck point
        val neckPtX = neckWidth + 1.2f
        val neckPtY = -2.8f

        // Shoulder seam matches back shoulder length (~15cm)
        val shoulderPtX = neckPtX + 13.8f
        val shoulderPtY = 4.8f

        // Front scye (scooped forward)
        val frontScyeWidth = (measurements.chest / 6.0f) + 3.0f // e.g. 20.0 cm
        val sideScyeX = (chestFinished / 4.0f) + 1.0f // front has slightly more chest room
        val sideScyeY = scyeY + 1.2f

        // Lapel break & roll line
        val breakPtY = waistY - (if (style.buttonStyle == JacketButtonStyle.SINGLE_BREASTED_1) 0f else 6.0f)
        val breakPtX = -frontOverlap

        val lapelWidth = style.lapelWidthCm
        val gorgeAngleY = 6.0f
        val gorgeAngleX = neckPtX - 1.0f

        // Lapel peak/notch coordinates
        val lapelPtX = -(frontOverlap + lapelWidth * 0.75f)
        val lapelPtY = gorgeAngleY + 2.5f

        // Waist suppression and darts adapted to chest-waist drop
        val drop = chestFinished - waistFinished
        val frontWaistIndent = (drop / 6.5f).coerceIn(-1.5f, 4.5f)
        val frontWaistX = sideScyeX - frontWaistIndent
        val sideHipX = (hipFinished / 4.0f) + 0.5f
        val sideHemX = sideHipX + 0.5f

        // Front hem sweep (curves gracefully down and across center front)
        val frontHemDropY = hemY + 1.2f

        val outline = mutableListOf<PathCommand>()
        // Start at Neck Gorge
        outline.add(PathCommand.MoveTo(neckPtX, neckPtY))
        // Shoulder line
        outline.add(PathCommand.LineTo(shoulderPtX, shoulderPtY))
        // Front armhole scye (deep scoop for chest muscle)
        outline.add(PathCommand.CubicTo(
            shoulderPtX - 2.5f, shoulderPtY + 5.0f,
            frontScyeWidth + 2.0f, scyeY - 5.0f,
            frontScyeWidth + 1.0f, scyeY - 2.0f
        ))
        outline.add(PathCommand.QuadTo(
            frontScyeWidth + 1.5f, sideScyeY,
            sideScyeX, sideScyeY
        ))
        // Side seam down to waist, hip, and hem
        outline.add(PathCommand.CubicTo(
            sideScyeX - 0.4f, waistY * 0.7f,
            frontWaistX + 0.3f, waistY - 2.0f,
            frontWaistX, waistY
        ))
        outline.add(PathCommand.CubicTo(
            frontWaistX + 0.3f, waistY + 6.0f,
            sideHipX - 0.2f, hipY - 2.0f,
            sideHipX, hipY
        ))
        outline.add(PathCommand.LineTo(sideHemX, hemY))

        // Curved front hem sweep
        outline.add(PathCommand.QuadTo(
            sideHemX * 0.4f, hemY + 0.5f,
            -frontOverlap * 0.3f, frontHemDropY
        ))
        // Front bottom edge curve up to center front overlap
        outline.add(PathCommand.QuadTo(
            -frontOverlap, frontHemDropY - 0.2f,
            -frontOverlap, hemY - 3.5f
        ))

        // Center front edge up through buttons to break point
        outline.add(PathCommand.LineTo(breakPtX, breakPtY))

        // Lapel shape depending on style
        when (style.lapelStyle) {
            LapelStyle.NOTCH -> {
                // Outer lapel roll line out to lapel point
                outline.add(PathCommand.LineTo(lapelPtX, lapelPtY))
                // Notch step into collar gorge
                outline.add(PathCommand.LineTo(lapelPtX + 2.8f, lapelPtY - 1.8f))
                outline.add(PathCommand.LineTo(gorgeAngleX, gorgeAngleY))
                outline.add(PathCommand.LineTo(neckPtX, neckPtY))
            }
            LapelStyle.PEAK -> {
                val peakTipX = lapelPtX - 2.2f
                val peakTipY = lapelPtY - 3.5f
                outline.add(PathCommand.LineTo(peakTipX, peakTipY))
                // Notch cut back down to gorge
                outline.add(PathCommand.LineTo(lapelPtX + 2.0f, lapelPtY - 0.5f))
                outline.add(PathCommand.LineTo(gorgeAngleX, gorgeAngleY))
                outline.add(PathCommand.LineTo(neckPtX, neckPtY))
            }
            LapelStyle.SHAWL -> {
                // Continuous curved lapel
                outline.add(PathCommand.QuadTo(
                    lapelPtX - 1.0f, (breakPtY + gorgeAngleY) * 0.5f,
                    gorgeAngleX, gorgeAngleY
                ))
                outline.add(PathCommand.QuadTo(
                    gorgeAngleX + 1.0f, gorgeAngleY - 2.5f,
                    neckPtX, neckPtY
                ))
            }
        }
        outline.add(PathCommand.Close)

        // Seam allowance outline
        val seamOutline = outline.map { cmd ->
            when (cmd) {
                is PathCommand.MoveTo -> PathCommand.MoveTo(cmd.x + 1.0f, cmd.y - 1.0f)
                is PathCommand.LineTo -> PathCommand.LineTo(cmd.x + 1.0f, cmd.y - 1.0f)
                is PathCommand.QuadTo -> PathCommand.QuadTo(cmd.cx + 1.0f, cmd.cy - 1.0f, cmd.x + 1.0f, cmd.y - 1.0f)
                is PathCommand.CubicTo -> PathCommand.CubicTo(cmd.c1x + 1.0f, cmd.c1y - 1.0f, cmd.c2x + 1.0f, cmd.c2y - 1.0f, cmd.x + 1.0f, cmd.y - 1.0f)
                PathCommand.Close -> PathCommand.Close
            }
        }

        // Pocket markers
        val hipPocketY = waistY + 6.0f
        val pocketStartX = sideScyeX * 0.35f
        val pocketEndX = pocketStartX + 15.5f

        val chestPocketY = scyeY - 3.5f
        val chestPocketStartX = sideScyeX * 0.32f
        val chestPocketEndX = chestPocketStartX + 10.5f

        val constructionLines = listOf(
            ConstructionLine(0f, scyeY, sideScyeX, scyeY, "Chest Scye Line", sideScyeX),
            ConstructionLine(0f, waistY, frontWaistX, waistY, "Front Waist Line", frontWaistX),
            ConstructionLine(0f, hipY, sideHipX, hipY, "Hip Line", sideHipX),
            ConstructionLine(breakPtX, breakPtY, neckPtX, neckPtY, "Lapel Roll / Break Line", 0f, true),
            ConstructionLine(pocketStartX, hipPocketY, pocketEndX, hipPocketY, "${style.pocketStyle.displayName} Placement", 15.5f, false),
            ConstructionLine(chestPocketStartX, chestPocketY, chestPocketEndX, chestPocketY - 1.0f, "Chest Welt Pocket (10.5 cm)", 10.5f, false)
        )

        val notches = listOf(
            NotchMarker(frontScyeWidth + 1.0f, scyeY - 2.0f, 0f, "Front Scye Pitch Notch"),
            NotchMarker(frontWaistX, waistY, 0f, "Waist Seam Notch"),
            NotchMarker(breakPtX, breakPtY, 180f, "Fastening Button Position")
        )

        val keyPoints = listOf(
            DraftPoint(neckPtX, neckPtY, "Front Neck Gorge", true),
            DraftPoint(shoulderPtX, shoulderPtY, "Front Shoulder Pt", true),
            DraftPoint(sideScyeX, sideScyeY, "Front Underarm", true),
            DraftPoint(breakPtX, breakPtY, "Lapel Roll Start", true),
            DraftPoint(lapelPtX, lapelPtY, "Lapel Point", true),
            DraftPoint(pocketStartX, hipPocketY, "Pocket Position", false)
        )

        return DraftPiece(
            type = DraftPieceType.JACKET_FRONT,
            name = "Jacket Front Forepart",
            outline = outline,
            seamAllowanceOutline = seamOutline,
            grainlineStart = DraftPoint(sideScyeX * 0.5f, scyeY - 4.0f),
            grainlineEnd = DraftPoint(sideScyeX * 0.5f, hemY - 6.0f),
            constructionLines = constructionLines,
            notches = notches,
            keyPoints = keyPoints,
            boundsWidthCm = sideHemX + frontOverlap + lapelWidth + 6.0f,
            boundsHeightCm = frontHemDropY + 6.0f,
            tailoringNotes = listOf(
                "Cut 2 mirrored pieces in cloth, 2 full haircloth canvas interfacings, and 2 front facings.",
                "Pad-stitch chest canvas with wool felt and horsehair shoulder reinforcement.",
                "Shape lapel roll with tailor's ham iron manipulation so lapel rolls softly over chest.",
                "Ensure grainline runs true to center front line."
            )
        )
    }

    private fun draftSleeveTop(
        measurements: BodyMeasurements,
        scyeDepth: Float,
        chest: Float
    ): DraftPiece {
        val totalLength = measurements.sleeveLength
        val crownHeight = scyeDepth * 0.72f // e.g. 17.6 cm
        val elbowY = crownHeight + (totalLength - crownHeight) * 0.55f
        val wristY = totalLength

        // Width allocations
        val bicepWidth = (measurements.bicepCircumference / 2.0f) + 6.0f // e.g. 23.5 cm
        val wristWidth = (measurements.wristCircumference / 2.0f) + 5.0f // e.g. 14.0 cm
        val crownApexX = bicepWidth * 0.45f
        val crownApexY = 0f

        val leftArmholePtX = 0f
        val leftArmholePtY = crownHeight

        val rightArmholePtX = bicepWidth
        val rightArmholePtY = crownHeight + 1.2f

        // Elbow shaping (forward pitch bend)
        val leftElbowX = 1.0f
        val rightElbowX = bicepWidth - 1.5f

        val leftWristX = 2.5f
        val rightWristX = leftWristX + wristWidth

        val outline = mutableListOf<PathCommand>()
        outline.add(PathCommand.MoveTo(leftArmholePtX, leftArmholePtY))
        // Crown curve (Sleeve Head): Back pitch curve, Apex curve, Front pitch curve
        outline.add(PathCommand.CubicTo(
            leftArmholePtX + 2.0f, leftArmholePtY - 8.0f,
            crownApexX - 3.5f, crownApexY,
            crownApexX, crownApexY
        ))
        outline.add(PathCommand.CubicTo(
            crownApexX + 3.5f, crownApexY,
            rightArmholePtX - 2.0f, rightArmholePtY - 9.0f,
            rightArmholePtX, rightArmholePtY
        ))
        // Back seam down through elbow to wrist
        outline.add(PathCommand.QuadTo(
            rightElbowX + 2.0f, elbowY,
            rightWristX, wristY
        ))
        // Wrist hem line
        outline.add(PathCommand.LineTo(leftWristX, wristY))
        // Front seam back up through elbow to armhole
        outline.add(PathCommand.QuadTo(
            leftElbowX - 1.0f, elbowY,
            leftArmholePtX, leftArmholePtY
        ))
        outline.add(PathCommand.Close)

        val constructionLines = listOf(
            ConstructionLine(0f, crownHeight, bicepWidth, crownHeight, "Crown Base / Bicep Line", bicepWidth),
            ConstructionLine(0f, elbowY, bicepWidth, elbowY, "Elbow Line", elbowY),
            ConstructionLine(leftWristX, wristY, rightWristX, wristY, "Wrist Hem Line", wristWidth)
        )

        val notches = listOf(
            NotchMarker(crownApexX, crownApexY, 270f, "Shoulder Seam Crown Notch"),
            NotchMarker(rightArmholePtX - 2.5f, rightArmholePtY - 5.0f, 45f, "Back Pitch Balance Notch"),
            NotchMarker(leftArmholePtX + 2.5f, leftArmholePtY - 4.5f, 135f, "Front Pitch Balance Notch")
        )

        val keyPoints = listOf(
            DraftPoint(crownApexX, crownApexY, "Sleeve Crown Apex", true),
            DraftPoint(leftArmholePtX, leftArmholePtY, "Front Underarm Pt", true),
            DraftPoint(rightArmholePtX, rightArmholePtY, "Back Pitch Pt", true),
            DraftPoint(rightWristX, wristY, "Outer Cuff", true)
        )

        return DraftPiece(
            type = DraftPieceType.SLEEVE_TOP,
            name = "Two-Piece Sleeve (Top / Outer)",
            outline = outline,
            seamAllowanceOutline = null,
            grainlineStart = DraftPoint(crownApexX, crownHeight + 2.0f),
            grainlineEnd = DraftPoint(crownApexX, wristY - 6.0f),
            constructionLines = constructionLines,
            notches = notches,
            keyPoints = keyPoints,
            boundsWidthCm = bicepWidth + 6.0f,
            boundsHeightCm = totalLength + 6.0f,
            tailoringNotes = listOf(
                "Cut 2 mirrored pieces in cloth and sleeve lining.",
                "Gather 3.5 cm of fullness into the sleeve head between pitch marks.",
                "Insert wadding / fleece sleeve head roll for a crisp roped shoulder silhouette."
            )
        )
    }

    private fun draftSleeveUnder(
        measurements: BodyMeasurements,
        scyeDepth: Float,
        chest: Float
    ): DraftPiece {
        val totalLength = measurements.sleeveLength
        val crownHeight = scyeDepth * 0.72f
        val elbowY = crownHeight + (totalLength - crownHeight) * 0.55f
        val wristY = totalLength

        // Under sleeve is narrower and has a hollowed underarm scye curve
        val underWidth = (measurements.bicepCircumference / 2.0f) + 1.5f // e.g. 19.0 cm
        val wristWidth = (measurements.wristCircumference / 2.0f) + 2.5f

        val leftArmholePtX = 2.0f
        val leftArmholePtY = crownHeight - 1.5f

        val rightArmholePtX = underWidth
        val rightArmholePtY = crownHeight

        val underScyeDipY = crownHeight + 4.5f

        val outline = mutableListOf<PathCommand>()
        outline.add(PathCommand.MoveTo(leftArmholePtX, leftArmholePtY))
        // Hollowed underarm curve
        outline.add(PathCommand.QuadTo(
            underWidth * 0.5f, underScyeDipY,
            rightArmholePtX, rightArmholePtY
        ))
        // Back under seam down to elbow & wrist
        outline.add(PathCommand.QuadTo(
            rightArmholePtX - 1.2f, elbowY,
            wristWidth + 1.0f, wristY
        ))
        // Wrist hem
        outline.add(PathCommand.LineTo(2.0f, wristY))
        // Front under seam back up to left armhole
        outline.add(PathCommand.QuadTo(
            1.0f, elbowY,
            leftArmholePtX, leftArmholePtY
        ))
        outline.add(PathCommand.Close)

        val constructionLines = listOf(
            ConstructionLine(0f, crownHeight, underWidth, crownHeight, "Under Bicep Line", underWidth),
            ConstructionLine(0f, elbowY, underWidth, elbowY, "Elbow Line", elbowY)
        )

        return DraftPiece(
            type = DraftPieceType.SLEEVE_UNDER,
            name = "Two-Piece Sleeve (Under / Inner)",
            outline = outline,
            seamAllowanceOutline = null,
            grainlineStart = DraftPoint(underWidth * 0.5f, crownHeight + 4.0f),
            grainlineEnd = DraftPoint(underWidth * 0.5f, wristY - 6.0f),
            constructionLines = constructionLines,
            notches = listOf(
                NotchMarker(leftArmholePtX, leftArmholePtY, 90f, "Front Underarm Notch"),
                NotchMarker(rightArmholePtX, rightArmholePtY, 45f, "Back Underarm Notch")
            ),
            keyPoints = listOf(
                DraftPoint(leftArmholePtX, leftArmholePtY, "Underarm Front", true),
                DraftPoint(rightArmholePtX, rightArmholePtY, "Underarm Back", true)
            ),
            boundsWidthCm = underWidth + 6.0f,
            boundsHeightCm = totalLength + 6.0f,
            tailoringNotes = listOf(
                "Cut 2 mirrored pieces in cloth and sleeve lining.",
                "Stitch forearm seam before setting into armhole.",
                "Ensure inner underarm curve sits cleanly without puckering."
            )
        )
    }

    private fun draftTrouserFront(
        measurements: BodyMeasurements,
        style: GarmentStyle
    ): DraftPiece {
        val waist = measurements.waist
        val hip = measurements.hip
        val outseam = measurements.outseam
        val inseam = measurements.inseam

        val crotchDepth = outseam - inseam // e.g. 26.0 cm
        val waistY = 0f
        val hipY = crotchDepth * 0.65f
        val crotchY = crotchDepth
        val kneeY = crotchDepth + (inseam * 0.52f)
        val hemY = outseam

        // Crease / Grainline is positioned in the center of the leg
        val creaseX = 18.0f

        // Front fork extension = Seat / 16
        val frontForkExtension = (hip / 16.0f) + 0.5f // e.g. 7.0 cm

        // Waist width: 1/4 waist + pleat allowance
        val pleatAllowance = when (style.trouserPleats) {
            TrouserPleats.FLAT_FRONT -> 1.0f
            TrouserPleats.SINGLE_PLEAT -> 3.5f
            TrouserPleats.DOUBLE_PLEAT -> 6.0f
        }
        val frontWaistWidth = (waist / 4.0f) + pleatAllowance
        val waistSideX = creaseX - (frontWaistWidth * 0.55f)
        val waistForkX = waistSideX + frontWaistWidth

        // Hip & crotch width
        val sideHipX = creaseX - ((hip / 4.0f) * 0.48f)
        val crotchForkX = creaseX + ((hip / 4.0f) * 0.52f) + frontForkExtension

        // Knee and bottom hem widths (half of circumference)
        val halfKnee = (measurements.kneeCircumference * 0.5f) / 2.0f
        val kneeLeftX = creaseX - halfKnee
        val kneeRightX = creaseX + halfKnee

        val halfBottom = measurements.trouserBottomWidth / 2.0f
        val hemLeftX = creaseX - halfBottom
        val hemRightX = creaseX + halfBottom

        val outline = mutableListOf<PathCommand>()
        // Waistline from side seam to fly front
        outline.add(PathCommand.MoveTo(waistSideX, waistY))
        outline.add(PathCommand.LineTo(waistForkX, waistY))
        // Center front fly line down to fork curve
        outline.add(PathCommand.LineTo(waistForkX, crotchY - 8.0f))
        // Crotch hook (ergonomic curve into inseam)
        outline.add(PathCommand.CubicTo(
            waistForkX + 0.5f, crotchY - 3.0f,
            crotchForkX - 3.0f, crotchY,
            crotchForkX, crotchY
        ))
        // Inseam down to knee and bottom hem
        outline.add(PathCommand.QuadTo(
            (crotchForkX + kneeRightX) * 0.5f - 1.5f, (crotchY + kneeY) * 0.5f,
            kneeRightX, kneeY
        ))
        outline.add(PathCommand.LineTo(hemRightX, hemY))
        // Bottom hem line
        outline.add(PathCommand.LineTo(hemLeftX, hemY))
        // Outseam (side seam) up through knee, hip, to waist
        outline.add(PathCommand.LineTo(kneeLeftX, kneeY))
        outline.add(PathCommand.QuadTo(
            sideHipX - 0.8f, hipY,
            waistSideX, waistY
        ))
        outline.add(PathCommand.Close)

        val constructionLines = listOf(
            ConstructionLine(creaseX, waistY, creaseX, hemY, "Trouser Crease / Center Grainline", outseam, false),
            ConstructionLine(waistSideX, waistY, waistForkX, waistY, "Waist Line", frontWaistWidth),
            ConstructionLine(sideHipX, hipY, waistForkX + 2.0f, hipY, "Hip / Seat Line", hip / 4.0f),
            ConstructionLine(sideHipX, crotchY, crotchForkX, crotchY, "Crotch / Fork Line", crotchForkX - sideHipX),
            ConstructionLine(kneeLeftX, kneeY, kneeRightX, kneeY, "Knee Line", halfKnee * 2.0f),
            ConstructionLine(hemLeftX, hemY, hemRightX, hemY, "Bottom Hem Width", halfBottom * 2.0f)
        )

        val notches = listOf(
            NotchMarker(kneeLeftX, kneeY, 180f, "Outseam Knee Notch"),
            NotchMarker(kneeRightX, kneeY, 0f, "Inseam Knee Notch"),
            NotchMarker(creaseX, waistY, 270f, "Front Crease Pleat Position")
        )

        val keyPoints = listOf(
            DraftPoint(waistForkX, waistY, "Center Front Waist", true),
            DraftPoint(crotchForkX, crotchY, "Front Crotch Fork Pt", true),
            DraftPoint(kneeRightX, kneeY, "Inseam Knee Pt", true),
            DraftPoint(hemLeftX, hemY, "Outseam Hem", true)
        )

        return DraftPiece(
            type = DraftPieceType.TROUSER_FRONT,
            name = "Trouser Forepart (Front)",
            outline = outline,
            seamAllowanceOutline = null,
            grainlineStart = DraftPoint(creaseX, 6.0f),
            grainlineEnd = DraftPoint(creaseX, hemY - 6.0f),
            constructionLines = constructionLines,
            notches = notches,
            keyPoints = keyPoints,
            boundsWidthCm = crotchForkX - sideHipX + 8.0f,
            boundsHeightCm = hemY + 8.0f,
            tailoringNotes = listOf(
                "Cut 2 mirrored pieces in cloth and pocket bag facing.",
                "Fold and press permanent trouser crease along central grainline.",
                if (style.trouserHem == TrouserHem.CUFFED_4CM) "8.5 cm turn-up allowance included for 4.0 cm cuffs." else "4.0 cm standard hem allowance."
            )
        )
    }

    private fun draftTrouserBack(
        measurements: BodyMeasurements,
        style: GarmentStyle
    ): DraftPiece {
        val waist = measurements.waist
        val hip = measurements.hip
        val outseam = measurements.outseam
        val inseam = measurements.inseam

        val crotchDepth = outseam - inseam
        val waistY = 0f
        val hipY = crotchDepth * 0.65f
        val crotchY = crotchDepth
        val kneeY = crotchDepth + (inseam * 0.52f)
        val hemY = outseam

        val creaseX = 18.0f

        // Back fork extension = Seat / 8 + 1 cm (much deeper than front)
        val backForkExtension = (hip / 8.0f) + 1.2f // e.g. 14.2 cm

        // Back waist angle (rise tilted inward 4cm and raised 3.5cm for sitting comfort)
        val backWaistRiseY = -3.8f
        val backWaistCenterPointX = creaseX + 4.5f

        val backWaistWidth = (waist / 4.0f) + 3.0f // + 2.5cm dart allowance
        val backWaistSideX = backWaistCenterPointX - backWaistWidth

        val sideHipX = creaseX - ((hip / 4.0f) * 0.52f) - 1.2f
        val crotchForkX = creaseX + ((hip / 4.0f) * 0.52f) + backForkExtension

        // Back knee and hem are 2cm wider than front for ease
        val halfKnee = (measurements.kneeCircumference * 0.5f) / 2.0f + 1.0f
        val kneeLeftX = creaseX - halfKnee
        val kneeRightX = creaseX + halfKnee

        val halfBottom = (measurements.trouserBottomWidth / 2.0f) + 1.0f
        val hemLeftX = creaseX - halfBottom
        val hemRightX = creaseX + halfBottom

        val outline = mutableListOf<PathCommand>()
        // Back waist line with angle
        outline.add(PathCommand.MoveTo(backWaistSideX, waistY - 0.5f))
        outline.add(PathCommand.LineTo(backWaistCenterPointX, backWaistRiseY))
        // Center back seat line curving into crotch fork
        outline.add(PathCommand.LineTo(backWaistCenterPointX - 1.5f, hipY))
        outline.add(PathCommand.CubicTo(
            backWaistCenterPointX - 2.0f, crotchY - 4.0f,
            crotchForkX - 5.5f, crotchY + 1.5f,
            crotchForkX, crotchY + 1.5f
        ))
        // Back Inseam down to knee & hem
        outline.add(PathCommand.QuadTo(
            (crotchForkX + kneeRightX) * 0.5f - 2.2f, (crotchY + kneeY) * 0.5f,
            kneeRightX, kneeY
        ))
        outline.add(PathCommand.LineTo(hemRightX, hemY))
        // Bottom hem
        outline.add(PathCommand.LineTo(hemLeftX, hemY))
        // Back outseam
        outline.add(PathCommand.LineTo(kneeLeftX, kneeY))
        outline.add(PathCommand.QuadTo(
            sideHipX - 1.0f, hipY,
            backWaistSideX, waistY - 0.5f
        ))
        outline.add(PathCommand.Close)

        val constructionLines = listOf(
            ConstructionLine(creaseX, waistY, creaseX, hemY, "Center Balance Crease", outseam, false),
            ConstructionLine(sideHipX, hipY, backWaistCenterPointX, hipY, "Back Seat Line", hip / 4.0f + 2.0f),
            ConstructionLine(sideHipX, crotchY, crotchForkX, crotchY, "Back Crotch Line", crotchForkX - sideHipX),
            ConstructionLine(kneeLeftX, kneeY, kneeRightX, kneeY, "Back Knee Line", halfKnee * 2.0f)
        )

        val notches = listOf(
            NotchMarker(kneeLeftX, kneeY, 180f, "Outseam Knee Notch"),
            NotchMarker(kneeRightX, kneeY, 0f, "Inseam Knee Notch"),
            NotchMarker(crotchForkX, crotchY + 1.5f, 45f, "Back Fork Assembly Notch")
        )

        val keyPoints = listOf(
            DraftPoint(backWaistCenterPointX, backWaistRiseY, "CB Waist Rise", true),
            DraftPoint(crotchForkX, crotchY + 1.5f, "Back Fork Extension", true),
            DraftPoint(kneeRightX, kneeY, "Back Knee Pt", true)
        )

        return DraftPiece(
            type = DraftPieceType.TROUSER_BACK,
            name = "Trouser Backpart (Back)",
            outline = outline,
            seamAllowanceOutline = null,
            grainlineStart = DraftPoint(creaseX, 6.0f),
            grainlineEnd = DraftPoint(creaseX, hemY - 6.0f),
            constructionLines = constructionLines,
            notches = notches,
            keyPoints = keyPoints,
            boundsWidthCm = crotchForkX - sideHipX + 8.0f,
            boundsHeightCm = hemY + 12.0f,
            tailoringNotes = listOf(
                "Cut 2 mirrored pieces in cloth and waistband curtain lining.",
                "Iron-stretch back inseam curve to mold fabric over the hamstring.",
                "Include 5.0 cm center back in-lay allowance for future waist let-out alterations."
            )
        )
    }

    private fun draftFullFabricMarker(
        jacketLength: Float,
        sleeveLength: Float,
        trouserOutseam: Float,
        chestFinished: Float,
        style: GarmentStyle
    ): DraftPiece {
        // Marker represents 150 cm wide wool cloth bolt layout
        val boltWidth = 150.0f
        val markerTotalLength = (jacketLength * 1.8f) + (trouserOutseam * 1.15f) + 40.0f

        val outline = mutableListOf<PathCommand>()
        outline.add(PathCommand.MoveTo(0f, 0f))
        outline.add(PathCommand.LineTo(boltWidth, 0f))
        outline.add(PathCommand.LineTo(boltWidth, markerTotalLength))
        outline.add(PathCommand.LineTo(0f, markerTotalLength))
        outline.add(PathCommand.Close)

        // Nested block placements within bolt
        val constructionLines = listOf(
            ConstructionLine(0f, 0f, boltWidth, 0f, "Cloth Bolt Width (150 cm)", boltWidth, false),
            ConstructionLine(0f, jacketLength + 5f, boltWidth, jacketLength + 5f, "Jacket Forepart & Backpart Section", boltWidth, true),
            ConstructionLine(0f, (jacketLength * 1.8f), boltWidth, (jacketLength * 1.8f), "Sleeve & Canvas Section", boltWidth, true),
            ConstructionLine(0f, (jacketLength * 1.8f) + trouserOutseam, boltWidth, (jacketLength * 1.8f) + trouserOutseam, "Trouser Forepart & Backpart Section", boltWidth, true),
            ConstructionLine(0f, markerTotalLength, boltWidth, markerTotalLength, "Total Required Cloth Yardage", boltWidth, false)
        )

        return DraftPiece(
            type = DraftPieceType.FULL_LAYOUT,
            name = "Wool Cloth Cutting Marker (150 cm Bolt)",
            outline = outline,
            seamAllowanceOutline = null,
            grainlineStart = DraftPoint(boltWidth * 0.5f, 10f),
            grainlineEnd = DraftPoint(boltWidth * 0.5f, markerTotalLength - 10f),
            constructionLines = constructionLines,
            notches = emptyList(),
            keyPoints = listOf(
                DraftPoint(0f, 0f, "Selvedge Left", true),
                DraftPoint(boltWidth, 0f, "Selvedge Right", true),
                DraftPoint(boltWidth * 0.5f, markerTotalLength, "Marker End (Meterage)", true)
            ),
            boundsWidthCm = boltWidth,
            boundsHeightCm = markerTotalLength,
            tailoringNotes = listOf(
                "Layout calculated for 150 cm (59\") width pure wool suiting fabric.",
                "Ensure nap and twill grain run downwards on all suit pieces.",
                "Includes allowances for collar stand, facings, flap pockets, and waistband curtain."
            )
        )
    }

    private fun buildAssemblySteps(style: GarmentStyle): List<AssemblyStep> {
        return listOf(
            AssemblyStep(
                stepNumber = 1,
                stage = "Preparation & Canvas",
                title = "Chest Canvas & Pad Stitching",
                instructions = "Baste the pure wool-hair canvas to the forepart. Hand pad-stitch the lapels using silk thread at 1/4 inch intervals with slight tension to create a permanent roll roll-over without visible stitches on face.",
                tailorTip = "Roll the lapel over your hand as you pad-stitch to bake the roll directly into the canvas structure."
            ),
            AssemblyStep(
                stepNumber = 2,
                stage = "Pockets & Darts",
                title = "Welt Pocket & Hip Pockets",
                instructions = "Stitch chest welt pocket (10.5 cm) at a 3-degree slant. Construct ${style.pocketStyle.displayName} on the forepart using stay tape to prevent stretching.",
                tailorTip = "Iron press the pocket flaps with a damp cloth before inserting into the forepart cut."
            ),
            AssemblyStep(
                stepNumber = 3,
                stage = "Body Construction",
                title = "Side & Shoulder Seams Bast",
                instructions = "Ease the back shoulder seam (1.2 cm) into the front shoulder seam. Sew side seams matching waist balance notches. Press side seam allowances open flat.",
                tailorTip = "Use an iron tailor's ham to mold the shoulder blade curve into the back cloth."
            ),
            AssemblyStep(
                stepNumber = 4,
                stage = "Collar & Lapel",
                title = "Collar Gorge & Facing Attachment",
                instructions = "Pad-stitch under-collar felt, shape with steam iron, and attach to gorge line. Baste silk or matching cloth facings with ease over chest roll.",
                tailorTip = "Provide +3 mm extra facing cloth over the lapel roll so facing does not pull tight."
            ),
            AssemblyStep(
                stepNumber = 5,
                stage = "Sleeve Setting",
                title = "Two-Piece Sleeve Insertion",
                instructions = "Sew top and under sleeve seams. Distribute 3.5 cm of crown ease across sleeve cap between pitch notches. Hand baste into jacket armhole scye and machine stitch.",
                tailorTip = "Insert horsehair sleeve head wadding to give the crown a crisp English drape."
            ),
            AssemblyStep(
                stepNumber = 6,
                stage = "Trousers",
                title = "Trouser Fork & Leg Assembly",
                instructions = "Stitch trouser front pleats. Steam-shape the back thigh curve. Stitch outseams and inseams. Secure waistband curtain and front zip/button fly.",
                tailorTip = "Apply cotton bias stay tape to curved crotch seam to resist walking stress."
            ),
            AssemblyStep(
                stepNumber = 7,
                stage = "Finishing",
                title = "Hand Pick Stitching & Horn Buttons",
                instructions = "Hand pick-stitch edges along lapels, collar, and pockets at 1/16 inch distance. Cut and hand-work Milanese buttonholes using gimp thread. Sew genuine horn buttons with stem wrap.",
                tailorTip = "Always sew button stems with 4-6 thread wraps to allow thick jacket cloth to button cleanly."
            )
        )
    }

    private fun draftAllPiecesOverview(
        jacketFront: DraftPiece,
        jacketBack: DraftPiece,
        sleeveTop: DraftPiece,
        sleeveUnder: DraftPiece,
        trouserFront: DraftPiece,
        trouserBack: DraftPiece
    ): DraftPiece {
        val spacingX = 14.0f
        val spacingY = 22.0f

        // Top Row: Back Bodice -> Front Bodice -> Top Sleeve -> Under Sleeve
        val p1X = 5.0f
        val p1Y = 5.0f

        val p2X = p1X + jacketBack.boundsWidthCm + spacingX
        val p2Y = 5.0f

        val p3X = p2X + jacketFront.boundsWidthCm + spacingX
        val p3Y = 5.0f

        val p4X = p3X + sleeveTop.boundsWidthCm + spacingX
        val p4Y = 5.0f

        val topRowHeight = maxOf(
            jacketBack.boundsHeightCm,
            jacketFront.boundsHeightCm,
            sleeveTop.boundsHeightCm,
            sleeveUnder.boundsHeightCm
        )

        // Bottom Row: Trouser Forepart -> Trouser Backpart
        val p5X = 5.0f
        val p5Y = topRowHeight + spacingY

        val p6X = p5X + trouserFront.boundsWidthCm + spacingX
        val p6Y = p5Y

        val totalWidth = maxOf(
            p4X + sleeveUnder.boundsWidthCm + 10.0f,
            p6X + trouserBack.boundsWidthCm + 10.0f
        )
        val totalHeight = p5Y + maxOf(trouserFront.boundsHeightCm, trouserBack.boundsHeightCm) + 15.0f

        val allOutlines = mutableListOf<PathCommand>()
        allOutlines.addAll(offsetPathCommands(jacketBack.outline, p1X, p1Y))
        allOutlines.addAll(offsetPathCommands(jacketFront.outline, p2X, p2Y))
        allOutlines.addAll(offsetPathCommands(sleeveTop.outline, p3X, p3Y))
        allOutlines.addAll(offsetPathCommands(sleeveUnder.outline, p4X, p4Y))
        allOutlines.addAll(offsetPathCommands(trouserFront.outline, p5X, p5Y))
        allOutlines.addAll(offsetPathCommands(trouserBack.outline, p6X, p6Y))

        val constructionLines = listOf(
            ConstructionLine(p1X, p1Y + jacketBack.boundsHeightCm + 4f, p1X + jacketBack.boundsWidthCm, p1Y + jacketBack.boundsHeightCm + 4f, "Back Bodice", jacketBack.boundsWidthCm, false),
            ConstructionLine(p2X, p2Y + jacketFront.boundsHeightCm + 4f, p2X + jacketFront.boundsWidthCm, p2Y + jacketFront.boundsHeightCm + 4f, "Front Bodice", jacketFront.boundsWidthCm, false),
            ConstructionLine(p3X, p3Y + sleeveTop.boundsHeightCm + 4f, p3X + sleeveTop.boundsWidthCm, p3Y + sleeveTop.boundsHeightCm + 4f, "Top Sleeve", sleeveTop.boundsWidthCm, false),
            ConstructionLine(p4X, p4Y + sleeveUnder.boundsHeightCm + 4f, p4X + sleeveUnder.boundsWidthCm, p4Y + sleeveUnder.boundsHeightCm + 4f, "Under Sleeve", sleeveUnder.boundsWidthCm, false),
            ConstructionLine(p5X, p5Y + trouserFront.boundsHeightCm + 4f, p5X + trouserFront.boundsWidthCm, p5Y + trouserFront.boundsHeightCm + 4f, "Trouser Forepart (Front)", trouserFront.boundsWidthCm, false),
            ConstructionLine(p6X, p6Y + trouserBack.boundsHeightCm + 4f, p6X + trouserBack.boundsWidthCm, p6Y + trouserBack.boundsHeightCm + 4f, "Trouser Backpart (Back)", trouserBack.boundsWidthCm, false)
        )

        return DraftPiece(
            type = DraftPieceType.ALL_PIECES_OVERVIEW,
            name = "All Pattern Pieces (Proportion Overview)",
            outline = allOutlines,
            seamAllowanceOutline = null,
            grainlineStart = DraftPoint(totalWidth * 0.5f, 10.0f),
            grainlineEnd = DraftPoint(totalWidth * 0.5f, totalHeight - 10.0f),
            constructionLines = constructionLines,
            notches = emptyList(),
            keyPoints = listOf(
                DraftPoint(p1X, p1Y, "Back Bodice", true),
                DraftPoint(p2X, p2Y, "Front Bodice", true),
                DraftPoint(p3X, p3Y, "Top Sleeve", true),
                DraftPoint(p5X, p5Y, "Trouser Front", true)
            ),
            boundsWidthCm = totalWidth,
            boundsHeightCm = totalHeight,
            tailoringNotes = listOf(
                "Full-suit multi-piece overview displaying all 6 primary tailored components together.",
                "Enables visual confirmation of bodily proportions (shoulder slope, chest drop, sleeve head curvature, and trouser rise).",
                "Rotate and zoom to confirm the pattern's basic shape and proportions before cutting cloth."
            )
        )
    }

    private fun offsetPathCommands(commands: List<PathCommand>, dx: Float, dy: Float): List<PathCommand> {
        return commands.map { cmd ->
            when (cmd) {
                is PathCommand.MoveTo -> PathCommand.MoveTo(cmd.x + dx, cmd.y + dy)
                is PathCommand.LineTo -> PathCommand.LineTo(cmd.x + dx, cmd.y + dy)
                is PathCommand.QuadTo -> PathCommand.QuadTo(cmd.cx + dx, cmd.cy + dy, cmd.x + dx, cmd.y + dy)
                is PathCommand.CubicTo -> PathCommand.CubicTo(cmd.c1x + dx, cmd.c1y + dy, cmd.c2x + dx, cmd.c2y + dy, cmd.x + dx, cmd.y + dy)
                PathCommand.Close -> PathCommand.Close
            }
        }
    }
}
