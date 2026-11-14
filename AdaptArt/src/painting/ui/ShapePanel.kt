package painting.ui

import painting.model.DrawingState
import painting.model.DrawingTool
import java.awt.FlowLayout
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JPanel

class ShapePanel(
    private val state: DrawingState
) : JPanel(FlowLayout(FlowLayout.LEFT, 4, 0)) {
    init {
        add(JComboBox(arrayOf("Line", "Rectangle", "Oval")).apply {
            toolTipText = "Choose a shape"
            addActionListener {
                state.selectedTool = when (selectedItem as String) {
                    "Rectangle" -> DrawingTool.RECTANGLE
                    "Oval" -> DrawingTool.OVAL
                    else -> DrawingTool.LINE
                }
            }
        })

        add(JCheckBox("Fill").apply {
            toolTipText = "Fill rectangles and ovals"
            addActionListener { state.fillShapes = isSelected }
        })
    }
}
