package painting.ui

import painting.model.DrawingState
import painting.model.DrawingTool
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.JSlider
import java.awt.FlowLayout

class BrushPanel(
    private val state: DrawingState
) : JPanel(FlowLayout(FlowLayout.LEFT, 4, 0)) {
    init {
        val brushSelector = JComboBox(arrayOf("Pencil", "Brush", "Pen")).apply {
            toolTipText = "Choose a brush style"
            addActionListener {
                state.selectedTool = when (selectedItem as String) {
                    "Brush" -> DrawingTool.BRUSH
                    "Pen" -> DrawingTool.PEN
                    else -> DrawingTool.PENCIL
                }
            }
        }
        val sizeSlider = JSlider(1, 30, state.strokeWidth.toInt()).apply {
            preferredSize = java.awt.Dimension(120, 28)
            toolTipText = "Brush and shape size"
            addChangeListener { state.strokeWidth = value.toFloat() }
        }
        add(brushSelector)
        add(sizeSlider)
    }
}
