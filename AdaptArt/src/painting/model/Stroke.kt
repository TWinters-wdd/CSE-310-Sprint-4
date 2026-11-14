package painting.model

import java.awt.Color
import java.awt.Point

data class Stroke(
    val points: MutableList<Point>,
    val color: Color,
    val width: Float,
    val tool: DrawingTool
)
