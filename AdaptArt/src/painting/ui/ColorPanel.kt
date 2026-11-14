package painting.ui

import painting.model.DrawingState
import java.awt.FlowLayout
import java.awt.Color
import javax.swing.JComboBox
import javax.swing.JPanel

class ColorPanel(
    private val state: DrawingState
) : JPanel(FlowLayout(FlowLayout.LEFT, 4, 0)) {
    init {
        val colorOptions = linkedMapOf(
            "Black" to Color.BLACK,
            "White" to Color.WHITE,
            "Red" to Color.RED,
            "Orange" to Color.ORANGE,
            "Yellow" to Color.YELLOW,
            "Green" to Color.GREEN,
            "Blue" to Color.BLUE,
            "Purple" to Color.MAGENTA,
            "Gray" to Color.GRAY
        )

        add(JComboBox(colorOptions.keys.toTypedArray()).apply {
            toolTipText = "Choose a color"
            addActionListener {
                colorOptions[selectedItem as String]?.let { color ->
                    state.drawingColor = color
                }
            }
        })
    }
}
