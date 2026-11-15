package painting.ui

import painting.model.DrawingState
import painting.model.DrawingTool
import java.awt.Component
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JPanel

class ShapePanel(
    private val state: DrawingState
) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        preferredSize = Dimension(112, 82)

        val shapeOptions = arrayOf(
            ToolOption("Line", DrawingTool.LINE, ToolbarIcon.LINE),
            ToolOption("Rectangle", DrawingTool.RECTANGLE, ToolbarIcon.RECTANGLE),
            ToolOption("Oval", DrawingTool.OVAL, ToolbarIcon.OVAL)
        )
        val shapeSelector = IconDropdown(shapeOptions.toList()) { option ->
            state.selectedTool = option.tool
        }.apply {
            alignmentX = Component.CENTER_ALIGNMENT
        }

        val fillCheckBox = JCheckBox("Fill").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            toolTipText = "Fill rectangles and ovals"
            addActionListener { state.fillShapes = isSelected }
        }

        add(Box.createVerticalGlue())
        add(shapeSelector)
        add(Box.createVerticalStrut(4))
        add(fillCheckBox)
        add(Box.createVerticalGlue())
    }
}
