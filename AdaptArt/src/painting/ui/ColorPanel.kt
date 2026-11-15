package painting.ui

import painting.model.DrawingState
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

class ColorPanel(
    private val state: DrawingState
) : JPanel(FlowLayout(FlowLayout.CENTER, 4, 0)) {
    init {
        val colorOptions = linkedMapOf(
            "Black" to Color.BLACK,
            "White" to Color.WHITE,
            "Red" to Color.RED,
            "Orange" to Color.ORANGE,
            "Yellow" to Color.YELLOW,
            "Green" to Color.GREEN,
            "Cyan" to Color.CYAN,
            "Blue" to Color.BLUE,
            "Purple" to Color.MAGENTA,
            "Gray" to Color.GRAY,
            "Brown" to Color(150, 95, 55)
        )
        val swatches = JPanel(GridLayout(3, 4, 4, 4))
        swatches.preferredSize = Dimension(112, 82)
        var selectedButton: JButton? = null

        colorOptions.forEach { (name, color) ->
            val swatch = JButton().apply {
                preferredSize = Dimension(24, 24)
                background = color
                isOpaque = true
                isFocusPainted = false
                margin = java.awt.Insets(0, 0, 0, 0)
                border = BorderFactory.createLineBorder(Color.DARK_GRAY, 1)
                toolTipText = name
                addActionListener {
                    state.drawingColor = color
                    selectedButton?.border = BorderFactory.createLineBorder(Color.DARK_GRAY, 1)
                    border = BorderFactory.createLineBorder(Color.BLACK, 3)
                    selectedButton = this
                }
            }
            swatches.add(swatch)
            if (color == state.drawingColor) {
                swatch.border = BorderFactory.createLineBorder(Color.BLACK, 3)
                selectedButton = swatch
            }
        }

        add(swatches)
    }
}
