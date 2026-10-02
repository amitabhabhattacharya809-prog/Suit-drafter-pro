package com.example.data.model

data class DraftPoint(
    val x: Float,
    val y: Float,
    val label: String = "",
    val isKeyPoint: Boolean = false
)

sealed interface PathCommand {
    data class MoveTo(val x: Float, val y: Float) : PathCommand
    data class LineTo(val x: Float, val y: Float) : PathCommand
    data class QuadTo(val cx: Float, val cy: Float, val x: Float, val y: Float) : PathCommand
    data class CubicTo(
        val c1x: Float, val c1y: Float,
        val c2x: Float, val c2y: Float,
        val x: Float, val y: Float
    ) : PathCommand
    data object Close : PathCommand
}

enum class DraftPieceType(val title: String, val shortName: String) {
    JACKET_FRONT("Front Bodice (Jacket Forepart)", "Front Bodice"),
    JACKET_BACK("Back Bodice (Jacket Back)", "Back Bodice"),
    SLEEVE_TOP("Two-Piece Top Sleeve", "Top Sleeve"),
    SLEEVE_UNDER("Two-Piece Under Sleeve", "Under Sleeve"),
    TROUSER_FRONT("Trouser Forepart (Front)", "Trouser Front"),
    TROUSER_BACK("Trouser Backpart (Back)", "Trouser Back"),
    ALL_PIECES_OVERVIEW("All Pattern Pieces (Proportion Overview)", "All Pieces"),
    FULL_LAYOUT("Fabric Bolt Cutting Marker", "Cutting Marker")
}

data class ConstructionLine(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val name: String,
    val measurementCm: Float,
    val isDashed: Boolean = true
)

data class NotchMarker(
    val x: Float,
    val y: Float,
    val angleDegrees: Float,
    val label: String
)

data class DraftPiece(
    val type: DraftPieceType,
    val name: String,
    val outline: List<PathCommand>,
    val seamAllowanceOutline: List<PathCommand>?,
    val grainlineStart: DraftPoint,
    val grainlineEnd: DraftPoint,
    val constructionLines: List<ConstructionLine>,
    val notches: List<NotchMarker>,
    val keyPoints: List<DraftPoint>,
    val boundsWidthCm: Float,
    val boundsHeightCm: Float,
    val tailoringNotes: List<String>
)

data class SuitDraftResult(
    val pieces: Map<DraftPieceType, DraftPiece>,
    val fabricBoltWidthCm: Float,
    val totalFabricLengthMeters: Float,
    val totalLiningMeters: Float,
    val totalInterliningMeters: Float,
    val scyeDepthCm: Float,
    val chestFinishedCm: Float,
    val waistFinishedCm: Float,
    val halfBackWidthCm: Float,
    val specifications: List<Pair<String, String>>,
    val assemblySteps: List<AssemblyStep>
)

data class AssemblyStep(
    val stepNumber: Int,
    val stage: String,
    val title: String,
    val instructions: String,
    val tailorTip: String
)
