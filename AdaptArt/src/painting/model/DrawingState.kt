package painting.model

import java.awt.Color

class DrawingState {
    var selectedTool: DrawingTool = DrawingTool.PENCIL
    var drawingColor: Color = Color.BLACK
    var strokeWidth: Float = 4f
    var fillShapes: Boolean = false
}
