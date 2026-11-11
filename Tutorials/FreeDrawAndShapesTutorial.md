# Kotlin Swing: Freehand Drawing and Set Shapes

This tutorial builds on Oracle's [Creating the Demo Application (Step 2)](https://docs.oracle.com/javase/tutorial/uiswing/painting/step2.html). The original example creates a custom `JPanel`, overrides `paintComponent`, and draws text with a `Graphics` object. The same Swing painting rules apply in Kotlin, but Kotlin lets us use lambdas, `apply`, data classes, and collection operations to make the code smaller and easier to extend.

The complete working example is in [`AdaptArt/src/Main.kt`](../AdaptArt/src/Main.kt).

## 1. Create a custom drawing panel

In Swing, custom painting belongs in a `JPanel` subclass. Use `paintComponent` and call `super.paintComponent(graphics)` first so Swing can paint the background correctly.

```kotlin
private class DrawingCanvas : JPanel() {
    init {
        preferredSize = Dimension(800, 550)
        background = Color.WHITE
    }

    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)
        val g2 = graphics.create() as Graphics2D
        try {
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            )
            // Draw saved strokes and shapes here.
        } finally {
            g2.dispose()
        }
    }
}
```

`Graphics2D` is used instead of the older `Graphics` API because it supports antialiasing, strokes, and geometric shapes. The copied graphics object is disposed after painting so the Swing-managed graphics context is not changed accidentally.

## 2. Store what the user draws

Painting directly inside a mouse event is not enough. Swing may call `paintComponent` again when a window is uncovered or resized, so the program needs to keep the drawing as data and redraw it every time.

```kotlin
private data class Stroke(
    val points: MutableList<Point>,
    val color: Color,
    val width: Float
)

private data class ShapeItem(
    val tool: DrawingTool,
    val start: Point,
    val end: Point,
    val color: Color,
    val width: Float,
    val filled: Boolean
)

private val strokes = mutableListOf<Stroke>()
private val shapes = mutableListOf<ShapeItem>()
```

Each freehand stroke is a list of mouse points. Each preset shape only needs its starting point, ending point, selected color, line width, and whether it should be filled.

## 3. Track mouse input

A press starts a stroke or shape. Dragging adds points to a freehand stroke or updates a temporary shape preview. Releasing the mouse saves the finished item.

```kotlin
addMouseListener(object : MouseAdapter() {
    override fun mousePressed(event: MouseEvent) {
        if (selectedTool == DrawingTool.FREEHAND) {
            activeStroke = Stroke(mutableListOf(event.point), drawingColor, strokeWidth)
        } else {
            shapeStart = event.point
            shapeEnd = event.point
        }
    }

    override fun mouseReleased(event: MouseEvent) {
        if (selectedTool == DrawingTool.FREEHAND) {
            activeStroke?.let(strokes::add)
            activeStroke = null
        } else {
            shapeStart?.let { start ->
                shapes += ShapeItem(
                    selectedTool, start, event.point,
                    drawingColor, strokeWidth, fillShapes
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
        if (selectedTool == DrawingTool.FREEHAND) {
            activeStroke?.points?.add(event.point)
        } else {
            shapeEnd = event.point
        }
        repaint()
    }
})
```

The call to `repaint()` asks Swing to schedule a new paint pass. It is better than calling `paintComponent` yourself because Swing controls when the component should be repainted.

## 4. Draw freehand strokes

The points are connected in order. Rounded caps and joins make the stroke look more natural than a sequence of square-ended lines.

```kotlin
private fun drawStroke(g2: Graphics2D, stroke: Stroke) {
    g2.color = stroke.color
    g2.stroke = BasicStroke(
        stroke.width,
        BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND
    )
    stroke.points.zipWithNext { first, second ->
        g2.drawLine(first.x, first.y, second.x, second.y)
    }
}
```

The full example also draws a small dot for a click that has only one point, so a quick click still leaves visible ink.

## 5. Draw rectangles, ovals, and lines

The user can drag in any direction. Therefore, calculate the top-left corner with `minOf` and use absolute differences for width and height.

```kotlin
val left = minOf(start.x, end.x).toDouble()
val top = minOf(start.y, end.y).toDouble()
val width = abs(end.x - start.x).toDouble()
val height = abs(end.y - start.y).toDouble()

when (item.tool) {
    DrawingTool.LINE -> g2.draw(Line2D.Double(start, end))
    DrawingTool.RECTANGLE -> {
        val rectangle = Rectangle2D.Double(left, top, width, height)
        if (item.filled) g2.fill(rectangle) else g2.draw(rectangle)
    }
    DrawingTool.OVAL -> {
        val oval = Ellipse2D.Double(left, top, width, height)
        if (item.filled) g2.fill(oval) else g2.draw(oval)
    }
    DrawingTool.FREEHAND -> Unit
}
```

This creates a small set of predictable drawing tools while keeping all rendering in one place.

## 6. Add Kotlin-friendly controls

The example uses a `JComboBox` for the tool, `JColorChooser` for color, a `JSlider` for size, a check box for filled shapes, and a clear button. Kotlin's `apply` scope function keeps each component's setup next to its creation:

```kotlin
val clearButton = JButton("Clear").apply {
    addActionListener { canvas.clearCanvas() }
}
```

The window is created on Swing's Event Dispatch Thread, just like the Oracle example:

```kotlin
fun main() {
    SwingUtilities.invokeLater(::createAndShowGUI)
}
```

## 7. Try the application

1. Run `AdaptArt/src/Main.kt` from IntelliJ IDEA.
2. Choose **Freehand** and drag on the white canvas.
3. Choose **Line**, **Rectangle**, or **Oval**, then drag to set its size.
4. Choose a color and line size before drawing.
5. Turn on **Fill shapes** for filled rectangles and ovals.
6. Select **Clear** to remove all saved strokes and shapes.

## Why this survives repainting

The Oracle tutorial emphasizes that Swing can invoke `paintComponent` again when a component is exposed or otherwise needs repainting. This version follows that rule: mouse events only update `strokes` and `shapes`, while `paintComponent` redraws those collections from scratch. As a result, the user's drawing does not disappear after minimizing, resizing, or moving another window over the canvas.
