package painting.ui

import java.awt.Color
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class MenuBar(
    brushesPanel: JPanel,
    shapesPanel: JPanel,
    colorsPanel: JPanel,
    backgroundColor: Color = Color(245, 247, 250)
) : JPanel(GridLayout(1, 3, 8, 0)) {
    init {
        background = backgroundColor
        border = BorderFactory.createEmptyBorder(2, 6, 2, 6)
        add(ToolGroup("Brushes", brushesPanel, backgroundColor))
        add(ToolGroup("Shapes", shapesPanel, backgroundColor))
        add(ToolGroup("Colors", colorsPanel, backgroundColor))
    }
}
