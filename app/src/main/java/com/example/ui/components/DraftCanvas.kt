package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConstructionLine
import com.example.data.model.DraftPiece
import com.example.data.model.DraftPoint
import com.example.data.model.MeasurementUnit
import com.example.data.model.NotchMarker
import com.example.data.model.PathCommand
import com.example.data.model.formatLength
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun DraftCanvas(
    piece: DraftPiece,
    unit: MeasurementUnit,
    showGrid: Boolean = true,
    showDimensions: Boolean = true,
    showSeamAllowance: Boolean = true,
    showNotches: Boolean = true,
    showConstructionLines: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val blueprintBg = if (isDark) Color(0xFF0F1724) else Color(0xFFF7FAFC)
    val gridColor = if (isDark) Color(0x1A6082B6) else Color(0x152D3748)
    val chalkColor = if (isDark) Color(0xFFF3F7FA) else Color(0xFF1A2634)
    val chalkFill = if (isDark) Color(0x0CFFFFFF) else Color(0x081A2634)
    val seamColor = if (isDark) Color(0x99FFA07A) else Color(0x99D9534F)
    val constructionColor = if (isDark) Color(0x777FB3D5) else Color(0x663182CE)
    val grainlineColor = if (isDark) Color(0xFFFFC107) else Color(0xFFB7791F)
    val notchColor = if (isDark) Color(0xFFFF5252) else Color(0xFFC53030)
    val textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.DKGRAY

    // Canvas Transformation State (Pan, Zoom, & Rotation)
    var scale by remember { mutableFloatStateOf(4.5f) }
    var offsetX by remember { mutableFloatStateOf(80f) }
    var offsetY by remember { mutableFloatStateOf(80f) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    // Auto-fit to piece when piece type changes
    LaunchedEffect(piece.type) {
        val targetScale = when (piece.type.name) {
            "ALL_PIECES_OVERVIEW" -> 1.5f
            "FULL_LAYOUT" -> 1.8f
            "TROUSER_FRONT", "TROUSER_BACK" -> 3.2f
            else -> 4.5f
        }
        scale = targetScale
        offsetX = 80f
        offsetY = 80f
        rotationAngle = 0f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(blueprintBg)
            .pointerInput(piece.type) {
                detectTransformGestures { _, pan, zoom, rotationChange ->
                    scale = (scale * zoom).coerceIn(0.4f, 15f)
                    rotationAngle = (rotationAngle + rotationChange) % 360f
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("pattern_draft_canvas")
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val pivot = Offset(canvasWidth / 2f, canvasHeight / 2f)

            // 1. Draw Grid in background (static or aligned)
            if (showGrid) {
                drawCadGrid(
                    width = canvasWidth,
                    height = canvasHeight,
                    scale = scale,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    gridColor = gridColor
                )
            }

            // Apply rotation and pattern transformations
            withTransform({
                rotate(degrees = rotationAngle, pivot = pivot)
            }) {
                // 2. Draw Construction Guidelines
                if (showConstructionLines) {
                    val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    piece.constructionLines.forEach { line ->
                        val p1 = transformPoint(line.startX, line.startY, scale, offsetX, offsetY)
                        val p2 = transformPoint(line.endX, line.endY, scale, offsetX, offsetY)
                        drawLine(
                            color = constructionColor,
                            start = p1,
                            end = p2,
                            strokeWidth = 2f,
                            pathEffect = if (line.isDashed) dashedEffect else null
                        )

                        if (showDimensions && line.name.isNotBlank()) {
                            val midX = (p1.x + p2.x) / 2f
                            val midY = (p1.y + p2.y) / 2f - 8f
                            drawContext.canvas.nativeCanvas.drawText(
                                if (line.measurementCm > 0f) "${line.name} [${formatLength(line.measurementCm, unit)}]" else line.name,
                                midX,
                                midY,
                                android.graphics.Paint().apply {
                                    color = textColor
                                    textSize = 24f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    isAntiAlias = true
                                }
                            )
                        }
                    }
                }

                // 3. Draw Seam Allowance (Offset Outline)
                if (showSeamAllowance && piece.seamAllowanceOutline != null) {
                    val seamPath = buildComposePath(piece.seamAllowanceOutline, scale, offsetX, offsetY)
                    drawPath(
                        path = seamPath,
                        color = seamColor,
                        style = Stroke(
                            width = 2.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                        )
                    )
                }

                // 4. Draw Main Pattern Outline
                val mainPath = buildComposePath(piece.outline, scale, offsetX, offsetY)
                // Fill with subtle translucent cloth wash
                drawPath(
                    path = mainPath,
                    color = chalkFill
                )
                // Crisp tailor chalk stroke outline
                drawPath(
                    path = mainPath,
                    color = chalkColor,
                    style = Stroke(
                        width = 4.0f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 5. Draw Grainline Arrow
                val gStart = transformPoint(piece.grainlineStart.x, piece.grainlineStart.y, scale, offsetX, offsetY)
                val gEnd = transformPoint(piece.grainlineEnd.x, piece.grainlineEnd.y, scale, offsetX, offsetY)
                drawLine(
                    color = grainlineColor,
                    start = gStart,
                    end = gEnd,
                    strokeWidth = 3f
                )
                drawGrainlineArrowHead(gStart, gEnd, grainlineColor)
                drawGrainlineArrowHead(gEnd, gStart, grainlineColor)
                val gMid = Offset((gStart.x + gEnd.x) / 2f + 16f, (gStart.y + gEnd.y) / 2f)
                drawContext.canvas.nativeCanvas.drawText(
                    "GRAINLINE",
                    gMid.x,
                    gMid.y,
                    android.graphics.Paint().apply {
                        color = if (isDark) 0xFFFFC107.toInt() else 0xFFB7791F.toInt()
                        textSize = 22f
                        isFakeBoldText = true
                        isAntiAlias = true
                    }
                )

                // 6. Draw Tailoring Balance Notches
                if (showNotches) {
                    piece.notches.forEach { notch ->
                        val notchCenter = transformPoint(notch.x, notch.y, scale, offsetX, offsetY)
                        val rad = Math.toRadians(notch.angleDegrees.toDouble())
                        val notchLength = 16f
                        val nEnd1 = Offset(
                            (notchCenter.x - cos(rad) * notchLength).toFloat(),
                            (notchCenter.y - sin(rad) * notchLength).toFloat()
                        )
                        val nEnd2 = Offset(
                            (notchCenter.x + cos(rad) * notchLength).toFloat(),
                            (notchCenter.y + sin(rad) * notchLength).toFloat()
                        )
                        drawLine(
                            color = notchColor,
                            start = nEnd1,
                            end = nEnd2,
                            strokeWidth = 3.5f,
                            cap = StrokeCap.Square
                        )

                        if (showDimensions && notch.label.isNotBlank()) {
                            drawContext.canvas.nativeCanvas.drawText(
                                notch.label,
                                notchCenter.x + 12f,
                                notchCenter.y - 8f,
                                android.graphics.Paint().apply {
                                    color = notchColor.hashCode()
                                    textSize = 20f
                                    isAntiAlias = true
                                }
                            )
                        }
                    }
                }

                // 7. Draw Key Coordinate Points
                piece.keyPoints.forEach { pt ->
                    val p = transformPoint(pt.x, pt.y, scale, offsetX, offsetY)
                    drawCircle(
                        color = grainlineColor,
                        radius = if (pt.isKeyPoint) 5f else 3.5f,
                        center = p
                    )
                    if (showDimensions && pt.isKeyPoint && pt.label.isNotBlank()) {
                        drawContext.canvas.nativeCanvas.drawText(
                            pt.label,
                            p.x + 8f,
                            p.y - 6f,
                            android.graphics.Paint().apply {
                                color = textColor
                                textSize = 20f
                                isAntiAlias = true
                            }
                        )
                    }
                }
            }
        }

        // Floating Piece Specs Badge (Top Left)
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = piece.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Bound: ${formatLength(piece.boundsWidthCm, unit)} × ${formatLength(piece.boundsHeightCm, unit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                val normalizedAngle = ((rotationAngle.roundToInt() % 360) + 360) % 360
                if (normalizedAngle != 0) {
                    Text(
                        text = "Rotated: $normalizedAngle°",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Canvas Transformation Controls: Rotation & Zooming (Bottom Right)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Rotate Left 90°
            SmallFloatingActionButton(
                onClick = { rotationAngle = (rotationAngle - 90f) % 360f },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                modifier = Modifier.testTag("rotate_left_button")
            ) {
                Icon(Icons.Default.RotateLeft, contentDescription = "Rotate 90° Counter-Clockwise")
            }

            // Rotate Right 90°
            SmallFloatingActionButton(
                onClick = { rotationAngle = (rotationAngle + 90f) % 360f },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                modifier = Modifier.testTag("rotate_right_button")
            ) {
                Icon(Icons.Default.RotateRight, contentDescription = "Rotate 90° Clockwise")
            }

            // Reset Rotation
            val normalizedAngle = ((rotationAngle.roundToInt() % 360) + 360) % 360
            if (normalizedAngle != 0) {
                SmallFloatingActionButton(
                    onClick = { rotationAngle = 0f },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(2.dp),
                    modifier = Modifier.testTag("reset_rotation_button")
                ) {
                    Icon(Icons.Default.Sync, contentDescription = "Reset Rotation to 0°")
                }
            }

            // Zoom In (+)
            SmallFloatingActionButton(
                onClick = { scale = (scale * 1.25f).coerceAtMost(15f) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                modifier = Modifier.testTag("zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }

            // Zoom Out (-)
            SmallFloatingActionButton(
                onClick = { scale = (scale * 0.8f).coerceAtLeast(0.4f) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                elevation = FloatingActionButtonDefaults.elevation(2.dp),
                modifier = Modifier.testTag("zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }

            // Reset Fit to Screen
            SmallFloatingActionButton(
                onClick = {
                    scale = when (piece.type.name) {
                        "ALL_PIECES_OVERVIEW" -> 1.5f
                        "FULL_LAYOUT" -> 1.8f
                        "TROUSER_FRONT", "TROUSER_BACK" -> 3.2f
                        else -> 4.5f
                    }
                    offsetX = 80f
                    offsetY = 80f
                    rotationAngle = 0f
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(3.dp),
                modifier = Modifier.testTag("reset_fit_button")
            ) {
                Icon(Icons.Default.CropFree, contentDescription = "Fit Pattern to Screen")
            }
        }
    }
}

private fun transformPoint(x: Float, y: Float, scale: Float, offsetX: Float, offsetY: Float): Offset {
    return Offset(x * scale + offsetX, y * scale + offsetY)
}

private fun buildComposePath(
    commands: List<PathCommand>,
    scale: Float,
    offsetX: Float,
    offsetY: Float
): Path {
    val path = Path()
    commands.forEach { cmd ->
        when (cmd) {
            is PathCommand.MoveTo -> {
                val p = transformPoint(cmd.x, cmd.y, scale, offsetX, offsetY)
                path.moveTo(p.x, p.y)
            }
            is PathCommand.LineTo -> {
                val p = transformPoint(cmd.x, cmd.y, scale, offsetX, offsetY)
                path.lineTo(p.x, p.y)
            }
            is PathCommand.QuadTo -> {
                val cp = transformPoint(cmd.cx, cmd.cy, scale, offsetX, offsetY)
                val ep = transformPoint(cmd.x, cmd.y, scale, offsetX, offsetY)
                path.quadraticTo(cp.x, cp.y, ep.x, ep.y)
            }
            is PathCommand.CubicTo -> {
                val c1 = transformPoint(cmd.c1x, cmd.c1y, scale, offsetX, offsetY)
                val c2 = transformPoint(cmd.c2x, cmd.c2y, scale, offsetX, offsetY)
                val ep = transformPoint(cmd.x, cmd.y, scale, offsetX, offsetY)
                path.cubicTo(c1.x, c1.y, c2.x, c2.y, ep.x, ep.y)
            }
            PathCommand.Close -> {
                path.close()
            }
        }
    }
    return path
}

private fun DrawScope.drawGrainlineArrowHead(from: Offset, toward: Offset, color: Color) {
    val arrowLen = 14f
    val dx = toward.x - from.x
    val dy = toward.y - from.y
    val angle = Math.atan2(dy.toDouble(), dx.toDouble())
    val a1 = angle + Math.PI / 6.0
    val a2 = angle - Math.PI / 6.0

    drawLine(
        color = color,
        start = from,
        end = Offset((from.x + arrowLen * cos(a1)).toFloat(), (from.y + arrowLen * sin(a1)).toFloat()),
        strokeWidth = 3f
    )
    drawLine(
        color = color,
        start = from,
        end = Offset((from.x + arrowLen * cos(a2)).toFloat(), (from.y + arrowLen * sin(a2)).toFloat()),
        strokeWidth = 3f
    )
}

private fun DrawScope.drawCadGrid(
    width: Float,
    height: Float,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    gridColor: Color
) {
    val gridSpacingCm = 10f
    val gridSpacingPx = gridSpacingCm * scale

    if (gridSpacingPx < 15f) return

    val startX = (offsetX % gridSpacingPx)
    var x = startX
    while (x < width) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
        x += gridSpacingPx
    }

    val startY = (offsetY % gridSpacingPx)
    var y = startY
    while (y < height) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
        )
        y += gridSpacingPx
    }
}
