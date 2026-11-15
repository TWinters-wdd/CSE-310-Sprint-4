package painting

import painting.canvas.DrawingCanvas
import painting.model.DrawingState
import painting.ui.BrushPanel
import painting.ui.ColorPanel
import painting.ui.MenuBar
import painting.ui.ShapePanel
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JFrame
import javax.swing.SwingUtilities

private fun createAndShowGUI() {
    // One shared state object keeps the menu and canvas synchronized.
    val state = DrawingState()
    val canvas = DrawingCanvas(state)

    // Each panel owns one group of menu controls.
    val menuBar = MenuBar(
        brushesPanel = BrushPanel(state),
        shapesPanel = ShapePanel(state),
        colorsPanel = ColorPanel(state)
    )

    JFrame("AdaptArt - Kotlin Drawing Demo").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = BorderLayout(8, 8)
        contentPane.background = Color(235, 238, 243)
        add(menuBar, BorderLayout.NORTH)
        add(canvas, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}

fun main() {
    SwingUtilities.invokeLater(::createAndShowGUI)
}
