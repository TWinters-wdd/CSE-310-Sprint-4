package painting.ui

import painting.model.DrawingTool
import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.RenderingHints
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.Icon
import javax.swing.JPanel
import javax.swing.JPopupMenu

data class ToolOption(
    val label: String,
    val tool: DrawingTool,
    val icon: Icon
)

enum class ToolbarIcon : Icon {
    PENCIL,
    BRUSH,
    PEN,
    LINE,
    RECTANGLE,
    OVAL;

    override fun getIconWidth(): Int = 28

    override fun getIconHeight(): Int = 28

    override fun paintIcon(component: Component?, graphics: Graphics, x: Int, y: Int) {
        val g2 = graphics.create() as Graphics2D
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.stroke = BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)

            when (this) {
                PENCIL -> {
                    g2.color = Color(246, 198, 72)
                    g2.drawLine(x + 6, y + 21, x + 21, y + 6)
                    g2.color = Color.DARK_GRAY
                    g2.drawLine(x + 5, y + 22, x + 8, y + 19)
                }
                BRUSH -> {
                    g2.color = Color(125, 78, 45)
                    g2.drawLine(x + 8, y + 20, x + 20, y + 8)
                    g2.color = Color(74, 145, 190)
                    g2.fillPolygon(Polygon(
                        intArrayOf(x + 5, x + 9, x + 13),
                        intArrayOf(y + 23, y + 17, y + 21),
                        3
                    ))
                }
                PEN -> {
                    g2.color = Color(52, 165, 190)
                    g2.drawLine(x + 7, y + 20, x + 20, y + 7)
                    g2.color = Color.DARK_GRAY
                    g2.drawLine(x + 6, y + 21, x + 9, y + 18)
                }
                LINE -> {
                    g2.color = Color(45, 50, 55)
                    g2.drawLine(x + 5, y + 22, x + 23, y + 5)
                }
                RECTANGLE -> {
                    g2.color = Color(45, 50, 55)
                    g2.drawRect(x + 5, y + 6, 18, 16)
                }
                OVAL -> {
                    g2.color = Color(45, 50, 55)
                    g2.drawOval(x + 5, y + 6, 18, 16)
                }
            }
        } finally {
            g2.dispose()
        }
    }
}

class IconDropdown(
    private val options: List<ToolOption>,
    selectedIndex: Int = 0,
    private val onSelected: (ToolOption) -> Unit
) : JPanel(BorderLayout(2, 0)) {
    private var selectedOption = options[selectedIndex]
    private val iconButton = JButton()

    init {
        preferredSize = Dimension(72, 36)
        maximumSize = Dimension(72, 36)
        background = Color.WHITE
        border = BorderFactory.createLineBorder(Color(175, 180, 185), 1)

        iconButton.apply {
            icon = selectedOption.icon
            toolTipText = selectedOption.label
            isFocusPainted = false
            isContentAreaFilled = false
            border = BorderFactory.createEmptyBorder()
            addActionListener { showOptions() }
        }

        val arrowButton = JButton("▼").apply {
            preferredSize = Dimension(18, 34)
            isFocusPainted = false
            isContentAreaFilled = false
            border = BorderFactory.createEmptyBorder()
            toolTipText = "Show options"
            addActionListener { showOptions() }
        }

        add(iconButton, BorderLayout.CENTER)
        add(arrowButton, BorderLayout.EAST)
    }

    private fun showOptions() {
        val popup = JPopupMenu()
        popup.border = BorderFactory.createLineBorder(Color(175, 180, 185), 1)
        val optionPanel = JPanel(FlowLayout(FlowLayout.LEFT, 4, 4)).apply {
            background = Color.WHITE
            border = BorderFactory.createEmptyBorder(2, 2, 2, 2)
        }

        options.forEach { option ->
            optionPanel.add(JButton().apply {
                icon = option.icon
                preferredSize = Dimension(38, 38)
                background = if (option == selectedOption) Color(230, 235, 240) else Color.WHITE
                isFocusPainted = false
                isOpaque = true
                border = BorderFactory.createLineBorder(Color(205, 210, 215), 1)
                toolTipText = option.label
                addActionListener {
                    selectedOption = option
                    iconButton.icon = option.icon
                    iconButton.toolTipText = option.label
                    onSelected(option)
                    popup.isVisible = false
                }
            })
        }

        popup.add(optionPanel)
        popup.show(this, 0, height)
    }
}
