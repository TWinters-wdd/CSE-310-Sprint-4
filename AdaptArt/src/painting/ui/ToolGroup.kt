package painting.ui

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

class ToolGroup(
    title: String,
    content: JPanel,
    backgroundColor: Color = Color(245, 247, 250)
) : JPanel(BorderLayout(0, 4)) {
    init {
        background = backgroundColor
        border = BorderFactory.createEmptyBorder(4, 6, 2, 6)
        add(content, BorderLayout.CENTER)
        add(JLabel(title).apply {
            horizontalAlignment = JLabel.CENTER
        }, BorderLayout.SOUTH)
    }
}
