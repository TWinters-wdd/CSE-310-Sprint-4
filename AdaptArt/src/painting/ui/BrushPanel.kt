package painting.ui

import painting.model.DrawingState
import painting.model.DrawingTool
import java.awt.Component
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSlider

class BrushPanel(
    private val state: DrawingState
) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        preferredSize = Dimension(112, 82)

        val brushOptions = arrayOf(
            ToolOption("Pencil", DrawingTool.PENCIL, ToolbarIcon.PENCIL),
            ToolOption("Brush", DrawingTool.BRUSH, ToolbarIcon.BRUSH),
            ToolOption("Pen", DrawingTool.PEN, ToolbarIcon.PEN)
        )
        val brushSelector = IconDropdown(brushOptions.toList()) { option ->
            state.selectedTool = option.tool
        }.apply {
            alignmentX = Component.CENTER_ALIGNMENT
        }
        val sizeSlider = JSlider(1, 30, state.strokeWidth.toInt()).apply {
            preferredSize = Dimension(104, 24)
            maximumSize = Dimension(104, 24)
            alignmentX = Component.CENTER_ALIGNMENT
            toolTipText = "Brush and shape size"
            addChangeListener { state.strokeWidth = value.toFloat() }
        }

        add(Box.createVerticalGlue())
        add(brushSelector)
        add(Box.createVerticalStrut(4))
        add(sizeSlider)
        add(Box.createVerticalGlue())
    }
}
