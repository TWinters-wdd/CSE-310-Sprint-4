package painting.canvas

import painting.model.DrawingState
import painting.model.DrawingTool
import painting.model.ShapeItem
import painting.model.Stroke
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Rectangle2D
import javax.swing.BorderFactory
import javax.swing.JPanel
import kotlin.math.abs
import kotlin.math.min

class DrawingCanvas(
    val state: DrawingState = DrawingState()
) : JPanel() {
    private val strokes = mutableListOf<Stroke>()
    private val shapes = mutableListOf<ShapeItem>()
    private var activeStroke: Stroke? = null
    private var shapeStart: Point? = null
    private var shapeEnd: Point? = null

    init {
        preferredSize = Dimension(900, 560)
        background = Color.WHITE
        border = BorderFactory.createLineBorder(Color(210, 214, 220), 1)

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(event: MouseEvent) {
                if (state.selectedTool.isFreehand()) {
                    activeStroke = Stroke(
                        mutableListOf(event.point),
                        effectiveColor(),
                        effectiveWidth(),
                        state.selectedTool
                    )
                } else if (state.selectedTool.isShape()) {
                    shapeStart = event.point
                    shapeEnd = event.point
                }
                repaint()
            }

            override fun mouseReleased(event: MouseEvent) {
                if (state.selectedTool.isFreehand()) {
                    activeStroke?.let(strokes::add)
                    activeStroke = null
                } else if (state.selectedTool.isShape()) {
                    shapeStart?.let { start ->
                        shapes += ShapeItem(
                            state.selectedTool,
                            start,
                            event.point,
                            effectiveColor(),
                            state.strokeWidth,
                            state.fillShapes
                        )
                    }
                    shapeStart = null
                    shapeEnd = null
                }
                repaint()
            }
        })

        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(event: MouseEvent) {
                if (state.selectedTool.isFreehand()) {
                    activeStroke?.points?.add(event.point)
                } else if (state.selectedTool.isShape()) {
                    shapeEnd = event.point
                }
                repaint()
            }
        })
    }

    fun clearCanvas() {
        strokes.clear()
        shapes.clear()
        activeStroke = null
        shapeStart = null
        shapeEnd = null
        repaint()
    }

    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)
        val g2 = graphics.create() as Graphics2D
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            strokes.forEach { drawStroke(g2, it) }
            activeStroke?.let { drawStroke(g2, it) }
            shapes.forEach { drawShape(g2, it) }

            val start = shapeStart
            val end = shapeEnd
            if (start != null && end != null && state.selectedTool.isShape()) {
                drawShape(g2, ShapeItem(
                    state.selectedTool,
                    start,
                    end,
                    effectiveColor(),
                    state.strokeWidth,
                    state.fillShapes
                ))
            }
        } finally {
            g2.dispose()
        }
    }

    private fun effectiveWidth(): Float = when (state.selectedTool) {
        DrawingTool.PENCIL -> min(state.strokeWidth, 3f)
        DrawingTool.PEN -> min(state.strokeWidth, 5f)
        DrawingTool.BRUSH -> state.strokeWidth.coerceAtLeast(6f)
        DrawingTool.ERASER -> state.strokeWidth.coerceAtLeast(8f)
        else -> state.strokeWidth
    }

    private fun effectiveColor(): Color =
        if (state.selectedTool == DrawingTool.ERASER) background else state.drawingColor

    private fun drawStroke(g2: Graphics2D, stroke: Stroke) {
        g2.color = if (stroke.tool == DrawingTool.BRUSH) {
            Color(stroke.color.red, stroke.color.green, stroke.color.blue, 175)
        } else {
            stroke.color
        }
        g2.stroke = BasicStroke(stroke.width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        stroke.points.zipWithNext { first, second -> g2.drawLine(first.x, first.y, second.x, second.y) }
        stroke.points.firstOrNull()?.let { point ->
            val diameter = stroke.width.toInt().coerceAtLeast(1)
            g2.fillOval(point.x - diameter / 2, point.y - diameter / 2, diameter, diameter)
        }
    }

    private fun drawShape(g2: Graphics2D, item: ShapeItem) {
        g2.color = item.color
        g2.stroke = BasicStroke(item.width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        val left = min(item.start.x, item.end.x).toDouble()
        val top = min(item.start.y, item.end.y).toDouble()
        val width = abs(item.end.x - item.start.x).toDouble()
        val height = abs(item.end.y - item.start.y).toDouble()

        when (item.tool) {
            DrawingTool.LINE -> g2.draw(Line2D.Double(item.start, item.end))
            DrawingTool.RECTANGLE -> {
                val rectangle = Rectangle2D.Double(left, top, width, height)
                if (item.filled) g2.fill(rectangle) else g2.draw(rectangle)
            }
            DrawingTool.OVAL -> {
                val oval = Ellipse2D.Double(left, top, width, height)
                if (item.filled) g2.fill(oval) else g2.draw(oval)
            }
            else -> Unit
        }
    }
}
