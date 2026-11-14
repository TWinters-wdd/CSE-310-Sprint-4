package painting.model

enum class DrawingTool(
    val label: String,
    val icon: String
) {
    PENCIL("Pencil", "✎"),
    BRUSH("Brush", "●"),
    PEN("Pen", "➤"),
    ERASER("Eraser", "⌫"),
    LINE("Line", "╱"),
    RECTANGLE("Rectangle", "□"),
    OVAL("Oval", "○"),
    TEXT("Text", "A");

    override fun toString(): String = "$icon  $label"

    fun isFreehand(): Boolean = this == PENCIL || this == BRUSH || this == PEN || this == ERASER

    fun isShape(): Boolean = this == LINE || this == RECTANGLE || this == OVAL
}
