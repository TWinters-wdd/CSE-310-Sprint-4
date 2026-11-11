package painting

import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridLayout
import java.awt.Point
import java.awt.RenderingHints
import java.awt.Window
import java.awt.Dialog
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JSlider
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.event.ChangeEvent
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

private enum class DrawingTool(val label: String, val icon: String) {
    PENCIL("Pencil", "✎"),
    BRUSH("Brush", "●"),
    PEN("Pen", "➤"),
    LINE("Line", "╱"),
    RECTANGLE("Rectangle", "□"),
    OVAL("Oval", "○");

    override fun toString() = "$icon  $label"

    fun isFreehand() = this == PENCIL || this == BRUSH || this == PEN
}

private data class Stroke(
    val points: MutableList<Point>,
    val color: Color,
    val width: Float,
    val tool: DrawingTool
)

private data class ShapeItem(
    val tool: DrawingTool,
    val start: Point,
    val end: Point,
    val color: Color,
    val width: Float,
    val filled: Boolean
)

private class DrawingCanvas : JPanel() {
    var selectedTool = DrawingTool.PENCIL
    var drawingColor = Color.BLACK
    var strokeWidth = 4f
    var fillShapes = false

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
                if (selectedTool.isFreehand()) {
                    activeStroke = Stroke(
                        mutableListOf(event.point),
                        drawingColor,
                        effectiveWidth(),
                        selectedTool
                    )
                } else {
                    shapeStart = event.point
                    shapeEnd = event.point
                }
                repaint()
            }

            override fun mouseReleased(event: MouseEvent) {
                if (selectedTool.isFreehand()) {
                    activeStroke?.let(strokes::add)
                    activeStroke = null
                } else {
                    shapeStart?.let { start ->
                        shapes += ShapeItem(
                            selectedTool,
                            start,
                            event.point,
                            drawingColor,
                            strokeWidth,
                            fillShapes
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
                if (selectedTool.isFreehand()) {
                    activeStroke?.points?.add(event.point)
                } else {
                    shapeEnd = event.point
                }
                repaint()
            }
        })
    }

    private fun effectiveWidth(): Float = when (selectedTool) {
        DrawingTool.PENCIL -> min(strokeWidth, 3f)
        DrawingTool.PEN -> min(strokeWidth, 5f)
        DrawingTool.BRUSH -> strokeWidth.coerceAtLeast(6f)
        else -> strokeWidth
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
            if (start != null && end != null) {
                drawShape(g2, ShapeItem(selectedTool, start, end, drawingColor, strokeWidth, fillShapes))
            }
        } finally {
            g2.dispose()
        }
    }

    private fun drawStroke(g2: Graphics2D, stroke: Stroke) {
        val color = if (stroke.tool == DrawingTool.BRUSH) {
            Color(stroke.color.red, stroke.color.green, stroke.color.blue, 175)
        } else {
            stroke.color
        }
        g2.color = color
        g2.stroke = BasicStroke(stroke.width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        stroke.points.zipWithNext { first, second ->
            g2.drawLine(first.x, first.y, second.x, second.y)
        }
        stroke.points.firstOrNull()?.let { point ->
            val diameter = stroke.width.toInt().coerceAtLeast(1)
            g2.fillOval(point.x - diameter / 2, point.y - diameter / 2, diameter, diameter)
        }
    }

    private fun drawShape(g2: Graphics2D, item: ShapeItem) {
        g2.color = item.color
        g2.stroke = BasicStroke(item.width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        val left = minOf(item.start.x, item.end.x).toDouble()
        val top = minOf(item.start.y, item.end.y).toDouble()
        val width = kotlin.math.abs(item.end.x - item.start.x).toDouble()
        val height = kotlin.math.abs(item.end.y - item.start.y).toDouble()

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

private class ColorWheel(initialColor: Color) : JPanel() {
    private val diameter = 240
    private val wheelImage = createWheelImage()
    private var hue: Float
    private var saturation: Float
    private var brightness: Float

    var selectedColor: Color
        private set

    var onColorChanged: ((Color) -> Unit)? = null

    init {
        val hsb = Color.RGBtoHSB(initialColor.red, initialColor.green, initialColor.blue, null)
        hue = hsb[0]
        saturation = hsb[1]
        brightness = hsb[2].coerceAtLeast(0.05f)
        selectedColor = Color.getHSBColor(hue, saturation, brightness)
        preferredSize = Dimension(diameter, diameter)
        toolTipText = "Click or drag on the color wheel"

        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(event: MouseEvent) = chooseColor(event.point)
        })
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(event: MouseEvent) = chooseColor(event.point)
        })
    }

    fun setBrightness(value: Int) {
        brightness = (value / 100f).coerceIn(0.05f, 1f)
        updateColor()
    }

    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)
        val g2 = graphics as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val x = (width - diameter) / 2
        val y = (height - diameter) / 2
        g2.drawImage(wheelImage, x, y, null)

        val radius = diameter / 2.0
        val markerX = x + diameter / 2 + cos(hue * 2 * PI) * saturation * radius
        val markerY = y + diameter / 2 - sin(hue * 2 * PI) * saturation * radius
        g2.color = Color.WHITE
        g2.stroke = BasicStroke(3f)
        g2.drawOval((markerX - 7).toInt(), (markerY - 7).toInt(), 14, 14)
        g2.color = Color.DARK_GRAY
        g2.stroke = BasicStroke(1f)
        g2.drawOval((markerX - 8).toInt(), (markerY - 8).toInt(), 16, 16)
    }

    private fun chooseColor(point: Point) {
        val center = diameter / 2.0
        val dx = point.x - width / 2.0
        val dy = point.y - height / 2.0
        val distance = hypot(dx, dy)
        if (distance > center) return

        saturation = (distance / center).toFloat().coerceIn(0f, 1f)
        hue = ((atan2(-dy, dx) / (2 * PI)) + 1.0).mod(1.0).toFloat()
        updateColor()
    }

    private fun updateColor() {
        selectedColor = Color.getHSBColor(hue, saturation, brightness)
        onColorChanged?.invoke(selectedColor)
        repaint()
    }

    private fun createWheelImage(): BufferedImage {
        val image = BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_RGB)
        val center = diameter / 2.0
        for (x in 0 until diameter) {
            for (y in 0 until diameter) {
                val dx = x - center
                val dy = y - center
                val distance = hypot(dx, dy)
                if (distance <= center) {
                    val saturation = (distance / center).toFloat()
                    val hue = ((atan2(-dy, dx) / (2 * PI)) + 1.0).mod(1.0).toFloat()
                    image.setRGB(x, y, Color.getHSBColor(hue, saturation, 1f).rgb)
                } else {
                    image.setRGB(x, y, Color.WHITE.rgb)
                }
            }
        }
        return image
    }
}

private fun showCustomColorDialog(owner: Window?, initialColor: Color, onSelected: (Color) -> Unit) {
    val dialog = JDialog(owner, "Choose a custom color", Dialog.ModalityType.APPLICATION_MODAL)
    val wheel = ColorWheel(initialColor)
    val preview = JPanel().apply {
        preferredSize = Dimension(44, 44)
        background = initialColor
        border = BorderFactory.createLineBorder(Color.DARK_GRAY)
    }
    val brightnessSlider = JSlider(SwingConstants.HORIZONTAL, 5, 100, 100).apply {
        toolTipText = "Adjust brightness"
    }
    val hsb = Color.RGBtoHSB(initialColor.red, initialColor.green, initialColor.blue, null)
    brightnessSlider.value = (hsb[2] * 100).toInt().coerceIn(5, 100)
    wheel.setBrightness(brightnessSlider.value)
    wheel.onColorChanged = { color ->
        preview.background = color
    }
    brightnessSlider.addChangeListener { event: ChangeEvent ->
        wheel.setBrightness((event.source as JSlider).value)
    }

    val buttons = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
        add(JButton("Cancel").apply { addActionListener { dialog.dispose() } })
        add(JButton("Use color").apply {
            addActionListener {
                onSelected(wheel.selectedColor)
                dialog.dispose()
            }
        })
    }
    val content = JPanel(BorderLayout(12, 12)).apply {
        border = BorderFactory.createEmptyBorder(14, 14, 14, 14)
        add(wheel, BorderLayout.CENTER)
        add(JPanel(BorderLayout(8, 8)).apply {
            add(JLabel("Brightness"), BorderLayout.NORTH)
            add(brightnessSlider, BorderLayout.CENTER)
            add(preview, BorderLayout.SOUTH)
        }, BorderLayout.SOUTH)
        add(buttons, BorderLayout.NORTH)
    }

    dialog.contentPane = content
    dialog.pack()
    dialog.setLocationRelativeTo(owner)
    dialog.isVisible = true
}

private fun showColorMenu(button: JButton, currentColor: Color, onSelected: (Color) -> Unit) {
    val menu = JPopupMenu()
    val presets = listOf(
        "Black" to Color(30, 30, 34),
        "White" to Color.WHITE,
        "Red" to Color(220, 55, 55),
        "Orange" to Color(240, 140, 45),
        "Yellow" to Color(245, 205, 50),
        "Green" to Color(70, 170, 95),
        "Teal" to Color(45, 175, 170),
        "Blue" to Color(65, 115, 220),
        "Purple" to Color(135, 85, 200),
        "Pink" to Color(225, 100, 165),
        "Brown" to Color(145, 90, 55),
        "Gray" to Color(130, 135, 145)
    )
    val presetPanel = JPanel(GridLayout(3, 4, 5, 5)).apply {
        border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
    }
    presets.forEach { (name, color) ->
        presetPanel.add(JButton(name).apply {
            background = color
            foreground = if (color.red + color.green + color.blue < 360) Color.WHITE else Color.DARK_GRAY
            isFocusPainted = false
            addActionListener {
                onSelected(color)
                menu.isVisible = false
            }
        })
    }
    menu.add(presetPanel)
    menu.addSeparator()
    menu.add(JButton("Custom color wheel...").apply {
        font = font.deriveFont(Font.BOLD)
        addActionListener {
            menu.isVisible = false
            showCustomColorDialog(SwingUtilities.getWindowAncestor(button), currentColor, onSelected)
        }
    })
    menu.show(button, 0, button.height)
}

private fun createAndShowGUI() {
    val canvas = DrawingCanvas()
    val toolSelector = JComboBox(DrawingTool.values()).apply {
        toolTipText = "Choose a pencil, brush, pen, or shape tool"
        selectedItem = DrawingTool.PENCIL
        addActionListener { canvas.selectedTool = selectedItem as DrawingTool }
    }
    val colorButton = JButton("Color").apply {
        preferredSize = Dimension(100, 32)
        background = canvas.drawingColor
        foreground = Color.WHITE
        isFocusPainted = false
        addActionListener { showColorMenu(this, canvas.drawingColor) { color ->
            canvas.drawingColor = color
            background = color
            foreground = if (color.red + color.green + color.blue < 360) Color.WHITE else Color.DARK_GRAY
        } }
    }
    val sizeSlider = JSlider(1, 30, canvas.strokeWidth.toInt()).apply {
        preferredSize = Dimension(130, 32)
        toolTipText = "Brush and shape size"
        addChangeListener { _: ChangeEvent -> canvas.strokeWidth = value.toFloat() }
    }
    val fillCheckBox = JCheckBox("Fill shapes").apply {
        toolTipText = "Fill rectangles and ovals"
        addActionListener { canvas.fillShapes = isSelected }
    }
    val clearButton = JButton("Clear").apply {
        addActionListener { canvas.clearCanvas() }
    }
    val controls = JPanel(FlowLayout(FlowLayout.LEFT, 8, 8)).apply {
        background = Color(245, 247, 250)
        border = BorderFactory.createEmptyBorder(2, 6, 2, 6)
        add(JLabel("Tool:"))
        add(toolSelector)
        add(colorButton)
        add(JLabel("Size:"))
        add(sizeSlider)
        add(fillCheckBox)
        add(clearButton)
    }

    javax.swing.JFrame("AdaptArt - Kotlin Drawing Demo").apply {
        defaultCloseOperation = javax.swing.JFrame.EXIT_ON_CLOSE
        layout = BorderLayout(8, 8)
        contentPane.background = Color(235, 238, 243)
        add(controls, BorderLayout.NORTH)
        add(canvas, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}

fun main() {
    SwingUtilities.invokeLater(::createAndShowGUI)
}
