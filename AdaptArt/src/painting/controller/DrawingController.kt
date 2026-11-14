package painting.controller

import painting.canvas.DrawingCanvas
import painting.model.DrawingState
import painting.model.DrawingTool

class DrawingController(
    private val state: DrawingState,
    private val canvas: DrawingCanvas
) {
    fun selectTool(tool: DrawingTool) {
        state.selectedTool = tool
        canvas.repaint()
    }

    fun selectColor(color: java.awt.Color) {
        state.drawingColor = color
        canvas.repaint()
    }

    fun setStrokeWidth(width: Float) {
        state.strokeWidth = width.coerceAtLeast(1f)
        canvas.repaint()
    }

    fun setFillShapes(filled: Boolean) {
        state.fillShapes = filled
        canvas.repaint()
    }

    fun clear() {
        canvas.clearCanvas()
    }
}
