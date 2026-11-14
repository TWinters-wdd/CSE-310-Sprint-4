package painting.model

import java.awt.Color
import java.awt.Point

data class ShapeItem(
    val tool: DrawingTool,
    val start: Point,
    val end: Point,
    val color: Color,
    val width: Float,
    val filled: Boolean = false
)
